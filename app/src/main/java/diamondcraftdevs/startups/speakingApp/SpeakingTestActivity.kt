package diamondcraftdevs.startups.speakingApp

import android.Manifest.permission.RECORD_AUDIO
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.text.Html
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.transition.TransitionManager
import diamondcraftdevs.startups.speakingApp.MainActivity.Companion.t1
import diamondcraftdevs.startups.speakingApp.const.StringExtractor.RequestPermissionCode
import diamondcraftdevs.startups.speakingApp.const.StringExtractor.part1Speech
import diamondcraftdevs.startups.speakingApp.const.StringExtractor.part2Speech
import diamondcraftdevs.startups.speakingApp.const.StringExtractor.part3Speech
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import diamondcraftdevs.startups.speakingApp.databinding.ActivitySpeakingTestBinding
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class   SpeakingTestActivity : AppCompatActivity() {
    private var store: String = "welcome"
    private var data = arrayListOf<String>()
    var position: Int = 0
    private var itemName: String = ""
    private var partName: String = ""
    private var partName1: String = ""
    private var saveItemName: String = ""
    private var extractor = 0
    private var pdfId: Int = R.raw.introquestions
    private val pre = Array(1) { false }
    private var countDownTimer: CountDownTimer? = null
    private var countDownTimer2: CountDownTimer? = null
    private var timeMillis = 20
    private var interval = 1
    private var increment: Int = 0
    private var millisUntilFinished: Long = 0L
    private lateinit var recorder: MediaRecorder
    private var recordPermissionGranted = false
    private var saveOrDelete: Boolean = false
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var binding: ActivitySpeakingTestBinding
//    private var mInterstitialAd: InterstitialAd? = null
//    private lateinit var interstitialAdRequest: AdRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpeakingTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        extractor = intent.extras?.getInt("item_data")!!
        itemName = intent.extras?.getString("item_name")!!
        partName = intent.extras?.getString("part_name")!!
        saveItemName = itemName + "_" + UUID.randomUUID()
      //  interstitialAdRequest = AdRequest.Builder().build()
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                recordPermissionGranted =
                    permissions[RECORD_AUDIO] ?: recordPermissionGranted
            }
        recorder = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            MediaRecorder(applicationContext)
        } else {
            MediaRecorder()
        }
        dataSetter()
        requestPermission()
        //loadAds()

        lifecycleScope.launch {
            extractPDF(extractor, itemName)
        }
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.inst_dialog, null)
        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )
        // Set an elevation for the popup window
        val slideIn = Slide()
        slideIn.slideEdge = Gravity.TOP
        popupWindow.enterTransition = slideIn
        // Slide animation for popup window exit transition
        val slideOut = Slide()
        slideOut.slideEdge = Gravity.END
        popupWindow.exitTransition = slideOut
        // Get the widgets reference from custom view
        val buttonPopup = view.findViewById<Button>(R.id.inst_okay)
        // Set a click listener for popup's button widget
        buttonPopup.setOnClickListener {
            // Dismiss the popup window
            popupWindow.dismiss()
        }
        // Set a dismiss listener for popup window
        popupWindow.setOnDismissListener {
            Toast.makeText(applicationContext, "Press Start", Toast.LENGTH_SHORT).show()
        }
        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(binding.speakingRelative)
        binding.speakingRelative.post {
            popupWindow.showAtLocation(
                binding.speakingRelative, // Location to display popup window
                Gravity.CENTER, // Exact position of layout to display popup
                0, // X offset
                0 // Y offset
            )
        }

        binding.imgStartBtn.setOnClickListener {
            if (recordPermissionGranted) {
                when (partName) {
                    "part1" -> speak()
                    "part2" -> speakPart2()
                    "part3" -> speakPart3()
                }
                binding.imgStartBtn.setImageResource(R.drawable.icon_user)
                startRecord()
                it.isClickable = false
                binding.incrementBtn.isEnabled = true
            } else
                requestPermission()
        }

        binding.btnNext.setOnClickListener {
            Log.d("clicked", "clicked1")
            countDownTimer?.cancel()
            countDownTimer2?.cancel()
            Log.d("clicked", "clicked1")
            pre[0] = false
            when (partName) {
                "part1" -> speak()
                "part3" -> speakPart3()
            }
            Log.d("clicked", "clicked2")
        }
        binding.btnPre.setOnClickListener {
            if (position > 1) {
                countDownTimer?.cancel()
                countDownTimer2?.cancel()
                pre[0] = true
                when (partName) {
                    "part1" -> speak()
                    "part3" -> speakPart3()
                }
            }
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.incrementBtn.setOnClickListener {
            increment = 6
            if (partName != "part2") {
                countDownTimer!!.cancel()
                countDownTimer2!!.cancel()
                timeSetter((millisUntilFinished / 1000).toInt(), 1, increment)
            }
        }
    }

    private fun dataSetter() {
        when (partName) {
            "part1" -> {
                timeMillis =
                    PreferenceManager.getDefaultSharedPreferences(this).getString("intro", "20")!!
                        .toInt()
                interval = 1
                data.addAll(part1Speech)

            }
            "part2" -> {
                timeMillis = 80
                interval = 1
                data.addAll(part2Speech)
                pdfId = R.raw.cuecardsquestions

            }
            "part3" -> {
                timeMillis =
                    PreferenceManager.getDefaultSharedPreferences(this).getString("follow", "30")!!
                        .toInt()
                interval = 1
                pdfId = R.raw.followupquestions


            }
            "mock" -> {
                partName1 = "mock"
                partName = "part1"
                timeMillis =
                    PreferenceManager.getDefaultSharedPreferences(this).getString("intro", "20")!!
                        .toInt()
                saveItemName = "mockTest_" + UUID.randomUUID()
                interval = 1
                data.addAll(part1Speech)
                binding.btnPre.isEnabled = true
                binding.btnNext.isEnabled = true
            }
        }
    }

    private suspend fun extractPDF(extractor: Int, itemName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reader = PdfReader(resources.openRawResource(pdfId))
                val extractedText =
                    PdfTextExtractor.getTextFromPage(reader, extractor + 1)
                store = extractedText
                when (partName) {
                    "part1" -> data.addAll(stringSplitter("\nLet's talk about $itemName .\n$extractedText"))
                    "part2" -> data.add("\nNow you can speak for 2 minutes. Your time start Now")
                    "part3" -> data.addAll(stringSplitter("$part3Speech\n$extractedText"))
                }
                withContext(Dispatchers.Main) {
                    Log.d("extractor", "done2")
                    binding.tvCaption.text = "Caption is On\n"
                }
                reader.close()
            } catch (e: IOException) {
            }
        }
    }

    private fun speak(){
        millisUntilFinished = timeMillis.toLong()
        binding.btnPre.isEnabled = true
        binding.btnNext.isEnabled = true
        if (pre[0]) {
            position -= 2
            pre[0] = false
        }
        countDownTimer =
            object : CountDownTimer(
                ((data.size - (position + 1)) * timeMillis * 1000).toLong(),
                (timeMillis * 1000).toLong()
            ) {
                override fun onTick(millisUntilFinished: Long) {
                    increment = 0
                    binding.tvCaption.apply {
                        append(
                            data[position] + " ?"
                        )
                    }
                    binding.tvScroll.apply {
                        post {
                            fullScroll(View.FOCUS_DOWN)
                        }
                    }
                    kotlin.run {
                        t1!!.speak(
                            data[position] + " ?",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                        timeSetter(timeMillis, 1, increment)
                    }
                    position += 1
                }

                override fun onFinish() {
                    binding.tvCaption.text = "This Is The End Of Speaking part 1"
                    binding.tvTimer.text = "0"
                    countDownTimer2?.cancel()
                    t1!!.speak(
                        "This Is The End Of Speaking part 1",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                    if (partName1 == "mock") {
                        mockSetUp()
                        lifecycleScope.launch {
                            extractPDF(extractor, itemName)
                            delay(4000L)
                            speakPart2()
                        }

                    }
                }
            }.start()

    }

    private fun speakPart2() {
        binding.btnPre.isEnabled = false
        binding.btnNext.isEnabled = false
        countDownTimer = object : CountDownTimer(230000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished > 229000L) {
                    binding.tvCaption.append(data[0])
                    binding.tvCaption.append("\n\n$store")
                    kotlin.run {
                        t1!!.speak(
                            data[0],
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                    }
                    binding.tvScroll.apply {
                        post {
                            fullScroll(View.FOCUS_DOWN)
                        }
                    }
                } else if (millisUntilFinished in 150001..150999) {
                    binding.tvCaption.append("\n${data[1]}")
                    kotlin.run {
                        t1!!.speak(
                            data[1],
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                    }
                    binding.tvScroll.apply {
                        post {
                            fullScroll(View.FOCUS_DOWN)
                        }
                    }
                } else {
                    if (millisUntilFinished > 150000L)
                        binding.tvTimer.text = (millisUntilFinished / 1000 - 150).toString()
                    else
                        binding.tvTimer.text = (millisUntilFinished / 1000).toString()

                }

            }

            override fun onFinish() {
                binding.tvCaption.text = "This Is The End Of Speaking part 2"
                binding.tvTimer.text = "0"
                t1!!.speak(
                    "This Is The End Of Speaking part 2",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
                if (partName1 == "mock") {
                    mockSetUp()
                    Log.d("extractor", "done1")
                    lifecycleScope.launch {
                        Log.d("extractor", "done")
                        extractPDF(extractor, itemName)
                        delay(4000L)
                        speakPart3()
                    }

                }
            }
        }.start()
    }

    private fun speakPart3() {
        binding.btnPre.isEnabled = true
        binding.btnNext.isEnabled = true
        if (pre[0]) {
            position -= 2
            pre[0] = false
        }

        countDownTimer =
            object : CountDownTimer(
                ((data.size - (position + 1)) * timeMillis * 1000).toLong(),
                (timeMillis * 1000).toLong()
            ) {
                @RequiresApi(api = Build.VERSION_CODES.N)
                override fun onTick(millisUntilFinished: Long) {
                    binding.tvCaption.append(data[position] + " ?")
                    binding.tvScroll.apply {
                        post {
                            fullScroll(View.FOCUS_DOWN)
                        }
                    }
                    kotlin.run {
                        t1!!.speak(
                            data[position] + " ?",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                        timeSetter(timeMillis, 1, 0)
                    }
                    position += 1
                }

                override fun onFinish() {
                    binding.tvCaption.text = "This Is The End Of Speaking part 3"
                    binding.tvTimer.text = "0"
                    countDownTimer2?.cancel()
                    t1!!.speak(
                        "This Is The End Of Speaking part 3",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )

                }
            }.start()

    }

    private fun mockSetUp() {
        if (partName == "part1") {
            binding.btnPre.isEnabled = false
            binding.btnNext.isEnabled = false
            extractor = (0..65).random()
            partName = "part2"
            timeMillis = 80
            interval = 1
            position = 0
            data.clear()
            data.addAll(part2Speech)
            pdfId = R.raw.cuecardsquestions
        } else {
            binding.btnPre.isEnabled = true
            binding.btnNext.isEnabled = true
            partName = "part3"
            timeMillis =
                PreferenceManager.getDefaultSharedPreferences(this).getString("follow", "30")!!
                    .toInt()
            interval = 1
            position = 0
            data = arrayListOf()
            pdfId = R.raw.followupquestions
        }
    }

    private fun timeSetter(time: Int, interval: Int, increment: Int) {
        countDownTimer2 =
            object :
                CountDownTimer(
                    ((time + increment) * 1000).toLong(),
                    (interval * 1000).toLong()
                ) {
                private var time = time + increment
                override fun onTick(millisUntilFinished: Long) {
                    binding.tvTimer.text = this.time.toString()
                    this@SpeakingTestActivity.millisUntilFinished = millisUntilFinished
                    this.time -= interval
                }

                override fun onFinish() {
                    if (increment == 6) {
                        when (partName) {
                            "part1" -> speak()
                            "part2" -> speakPart2()
                            "part3" -> speakPart3()
                        }
                    }
                }

            }.start()
    }

    private fun stringSplitter(data: String): ArrayList<String> =
        data.split("?").toCollection(ArrayList())


    private fun requestPermission() {
        val hasRecordPermission = ContextCompat.checkSelfPermission(
            this,
            RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        recordPermissionGranted = hasRecordPermission
        val permissionsToRequests = mutableListOf<String>()
        if (!recordPermissionGranted) {
            permissionsToRequests.add(RECORD_AUDIO)
        }
        if (permissionsToRequests.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequests.toTypedArray())
        }
    }

    @Throws(IllegalStateException::class, IOException::class)
    private fun startRecord() {
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder.setOutputFile(getExternalFilesDir(null)?.absolutePath + "/$saveItemName.3gp")
        try {
            recorder.prepare()
            recorder.start()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Recorder not started", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteFileFromInternalStorage(fileName: String): Boolean {
        return try {
            File(fileName).delete()
            Log.d("delete", "yes")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("delete", "not")
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RequestPermissionCode ->
                if (grantResults.isNotEmpty()) {
                    val recordPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if (recordPermission) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
        }
    }

    override fun onBackPressed() {
        if (!binding.imgStartBtn.isClickable) {
            AlertDialog.Builder(this)
                .setMessage("Do You Want To Save Recording..?")
                .setNegativeButton(
                    Html.fromHtml(
                        "<font color='red'>" + "NO" + "</font>",
                        Color.RED
                    )
                ) { _, _ ->
                    saveOrDelete = true
                    releaseThings()
                }
                .setNeutralButton(
                    Html.fromHtml(
                        "<font color='green'>" + "Cancel" + "</font>",
                        Color.GREEN
                    )
                ) { _, _ ->
                }
                .setPositiveButton(
                    Html.fromHtml(
                        "<font color='green'>" + "YES" + "</font>",
                        Color.GREEN
                    )
                ) { _, _ ->
                    //mInterstitialAd?.show(this@SpeakingTestActivity)
                    finish()
                }.show()
        } else
            finish()
    }

    override fun onPause() {
        super.onPause()
        pauseThings()
    }

    override fun onResume() {
        super.onResume()
        if (countDownTimer != null) {
            countDownTimer?.start()
        }
        try {
            recorder.resume()
        } catch (e: java.lang.RuntimeException) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseThings()
    }

    private fun pauseThings() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
        if (countDownTimer2 != null) {
            countDownTimer2?.cancel()
        }
        try {
            recorder.pause()
        } catch (e: java.lang.RuntimeException) {
        }
        if (t1 != null)
            t1!!.stop()
    }

    private fun releaseThings() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
        try {
            recorder.stop()
        } catch (e: RuntimeException) {
        } finally {
            recorder.release()
        }
        if (t1 != null)
            t1!!.stop()
        if (saveOrDelete)
            CoroutineScope(Dispatchers.IO).launch {
                val isSuccessful =
                    deleteFileFromInternalStorage("${getExternalFilesDir(null)}/$saveItemName.3gp")

                withContext(Dispatchers.Main) {
                    if (isSuccessful) {
                        //mInterstitialAd?.show(this@SpeakingTestActivity)
                        finish()
                    }
                }
            }
    }

   /*private fun loadAds() {
        InterstitialAd.load(
            applicationContext,
            "ca-app-pub-3940256099942544/1033173712",
            interstitialAdRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {

                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            }

            override fun onAdShowedFullScreenContent() {
                mInterstitialAd = null
            }
        }
    }*/
}