package diamondcraftdevs.startups.speakingApp

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import diamondcraftdevs.startups.speakingApp.const.StringExtractor.holderList
import diamondcraftdevs.startups.speakingApp.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*


class MainActivity : AppCompatActivity() {
    companion object {
        @JvmField
        var t1: TextToSpeech? = null
    }

    private lateinit var binding: ActivityMainBinding
    private var part1position: Int = 0
    private var part2position: Int = 0
    private var speechRate: Float = 0.8f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        when (PreferenceManager.getDefaultSharedPreferences(this).getString("speed", "1x")) {
            "0.8x" -> speechRate = 0.8f
            "0.6x" -> speechRate = 0.6f
            "1x" -> speechRate = 1.0f
            "1.2x" -> speechRate = 1.2f
        }
         t1 = TextToSpeech(applicationContext, { status: Int ->
            t1!!.setSpeechRate(speechRate)
            if (status == TextToSpeech.SUCCESS) {
                val a: MutableSet<String> = HashSet()
                a.add("male")
                val locale = Locale("en", "US")
                val voice = Voice(
                    "en-us-x-sfg#male_2-local",
                    locale,
                    Voice.QUALITY_HIGH,
                    Voice.LATENCY_LOW,
                    true, a
                )
                t1!!.voice = voice
                t1!!.setSpeechRate(speechRate)
                val result = t1!!.setVoice(voice)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.d("lang", "lang")
                    t1!!.speak("hello", TextToSpeech.QUEUE_FLUSH, null, null)

                } else {
                    Log.d("lang2", "lang")
                    t1!!.speak("hello", TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }, "com.google.android.tts")
        runBlocking {
            delay(2000)
        }



        binding.practiceTests.setOnClickListener {
            startActivity(Intent(this, HolderActivity::class.java).putExtra("from", "practice"))
        }

        binding.homeSetting.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.btnSetting.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.speakingSamples.setOnClickListener {
            startActivity(Intent(this, SampleBookActivity::class.java))
        }
        binding.mockTests.setOnClickListener {
            part1position = (0..143).random()
            part2position = (0..66).random()

            startActivity(
                Intent(this, SpeakingTestActivity::class.java).putExtra("item_data", part1position)
                    .putExtra("item_name", holderList[part1position]).putExtra("part_name", "mock")
            )
        }
        binding.results.setOnClickListener {
            startActivity(Intent(this, RecordPlayActivity::class.java))
        }
    }
}

