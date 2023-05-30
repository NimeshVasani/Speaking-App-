package diamondcraftdevs.startups.speakingApp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import diamondcraftdevs.startups.speakingApp.databinding.ActivitySampleBookBinding


class SampleBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySampleBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySampleBookBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        binding.holderBack.setOnClickListener {
            finish()
        }
        binding.pdfv.fromAsset("samplebook.pdf").load()

    }
}