package diamondcraftdevs.startups.speakingApp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import diamondcraftdevs.startups.speakingApp.fragment.PracticeFragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import diamondcraftdevs.startups.speakingApp.databinding.ActivityHolderBinding

class HolderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHolderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val get: String = intent.extras!!.getString("from")!!
        if (get == "practice") {
            supportFragmentManager.beginTransaction()
                .add(R.id.practice_fragment, PracticeFragment()).commit()
        }
        binding.holderBack.setOnClickListener {
            onBackPressed()
        }
//        MobileAds.initialize(this) {}
//        val adRequest = AdRequest.Builder().build()

      //  binding.holderBanner.loadAd(adRequest)
       /* binding.holderBanner.adListener = object : AdListener() {
            override fun onAdLoaded() {
                binding.holderBanner.visibility = View.VISIBLE
            }
        }*/
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
            binding.holderRelative.setBackgroundColor(Color.parseColor("#e9967a"))
            binding.holderName.text = "Preparation"
        }

    }

}