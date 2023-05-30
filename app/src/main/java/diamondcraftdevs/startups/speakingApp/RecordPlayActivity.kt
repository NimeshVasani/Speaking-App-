package diamondcraftdevs.startups.speakingApp

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import diamondcraftdevs.startups.speakingApp.adapter.RecordedListAdapter
import diamondcraftdevs.startups.speakingApp.const.InternalStorageRecordFileList
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import diamondcraftdevs.startups.speakingApp.databinding.ActivityRecordPlayBinding
import java.util.*
import java.util.concurrent.TimeUnit


class RecordPlayActivity : AppCompatActivity() {
    private lateinit var files: List<InternalStorageRecordFileList>
    private var recordedListAdapter: RecordedListAdapter? = null
    private lateinit var binding: ActivityRecordPlayBinding
    private var mediaPlayer = MediaPlayer()
    private lateinit var updateSeekBar: Thread
    private var position: Int = 0
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var interstitialAdRequest: AdRequest
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val file = getExternalFilesDir(null)!!.listFiles()
        files = file?.filter {
            it.canRead() && it.isFile
        }?.map {
            InternalStorageRecordFileList(it.name, it, Uri.fromFile(it))
        } ?: listOf()
        recordedListAdapter =
            RecordedListAdapter(applicationContext, files)
        binding.latestAddedSongs.layoutManager = LinearLayoutManager(applicationContext)
        binding.latestAddedSongs.adapter = recordedListAdapter
        binding.recordSeek.isEnabled = false

        interstitialAdRequest = AdRequest.Builder().build()
      //  loadAds()
        binding.recordSeek.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
        binding.control.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                binding.btnPause.setImageResource(android.R.drawable.ic_media_play)
                binding.control.setImageResource(android.R.drawable.ic_media_play)
            } else {
                binding.btnPause.setImageResource(android.R.drawable.ic_media_pause)
                binding.control.setImageResource(android.R.drawable.ic_media_pause)
                mediaPlayer.start()
            }
        }
        binding.btnPause.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                binding.btnPause.setImageResource(android.R.drawable.ic_media_play)
                binding.control.setImageResource(android.R.drawable.ic_media_play)
            } else {
                binding.btnPause.setImageResource(android.R.drawable.ic_media_pause)
                binding.control.setImageResource(android.R.drawable.ic_media_pause)
                mediaPlayer.start()
            }
        }
        binding.btnNext.setOnClickListener {
            if (position < recordedListAdapter!!.itemCount - 1) {
                position += 1
                playAudio(position)
                initializeSeekBar()
            }
        }
        binding.btnPre.setOnClickListener {
            if (position > 0) {
                position -= 1
                playAudio(position)
                initializeSeekBar()
            }
        }
        recordedListAdapter?.apply {
            setOnItemClickListener(object :
                RecordedListAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, view: View) {
                    this@RecordPlayActivity.position = position
                    playAudio(position)
                    initializeSeekBar()
                }

                override fun onItemDeleteClick(position: Int, view: View) {
                    files[position].file.delete()
                    val file1 = getExternalFilesDir(null)!!.listFiles()
                    files = file1?.filter {
                        it.canRead() && it.isFile
                    }?.map {
                        InternalStorageRecordFileList(it.name, it, Uri.fromFile(it))
                    } ?: listOf()
                    notifyItemRemoved(position)

                }

                override fun onItemShareClick(position: Int, view: View) {
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "audio/*"
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, files[position].file)
                    startActivity(Intent.createChooser(sharingIntent, "Share Audio File"))
                }
            })
        }

    }


    fun initializeSeekBar() {
        val duration = mediaPlayer.duration
        val time = String.format(
            Locale.US,
            "%02d : %02d ",
            TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    duration.toLong()
                )
            )
        )
        binding.tvRecordTotalTime.text = time

        binding.recordSeek.max = mediaPlayer.duration
        updateSeekBar = object : Thread() {
            override fun run() {
                val totalDuration: Int = mediaPlayer.duration
                Log.d("duration", totalDuration.toString())
                var currentPosition = 0
                while (currentPosition < totalDuration) {
                    try {
                        sleep(1000)
                        currentPosition = mediaPlayer.currentPosition
                        binding.recordSeek.progress = currentPosition
                        runOnUiThread {
                            val currentDuration = mediaPlayer.currentPosition
                            val currentTime = String.format(
                                Locale.US,
                                "%02d : %02d ",
                                TimeUnit.MILLISECONDS.toMinutes(currentDuration.toLong()),
                                TimeUnit.MILLISECONDS.toSeconds(currentDuration.toLong()) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(
                                        currentDuration.toLong()
                                    )
                                )
                            )
                            binding.tvRecordCurrentTime.text = currentTime
                        }


                    } catch (e: InterruptedException) {
                        Log.d("duration", e.toString())
                        e.printStackTrace()
                    }
                }
            }
        }.apply { start() }
        binding.btnRecordPlayBack.setOnClickListener { finish() }
    }

    fun playAudio(position: Int) {
        binding.btnPause.setImageResource(android.R.drawable.ic_media_pause)
        binding.control.setImageResource(android.R.drawable.ic_media_pause)
        binding.musicTitle.text = files[position].fileName.split("_")[0]
        binding.musicArtistName.text = "Speaking Mentor"
        binding.recordSeek.isEnabled = true
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
            Log.d("play", "playing")
            mediaPlayer = MediaPlayer.create(this, Uri.fromFile(files[position].file))
            mediaPlayer.start()


        } else {
            Log.d("play", "not playing")
            mediaPlayer = MediaPlayer.create(this, Uri.fromFile(files[position].file))
            mediaPlayer.start()

        }
    }

    private fun stopAudio() {
        mediaPlayer.stop()

    }

    private fun pauseAudio() {
        mediaPlayer.pause()
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            updateSeekBar.interrupt()
            pauseAudio()
        }
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            updateSeekBar.interrupt()
            stopAudio()
        }
        mInterstitialAd?.show(this@RecordPlayActivity)
    }

    private fun loadAds() {
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
    }
}