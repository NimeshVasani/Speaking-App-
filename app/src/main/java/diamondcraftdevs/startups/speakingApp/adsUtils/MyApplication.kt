package diamondcraftdevs.startups.speakingApp.adsUtils

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus

class MyApplication : Application() {
 //   lateinit var appOpenManager: AppOpenManager
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {
            var initializationStatus: InitializationStatus
        }
      //  appOpenManager = AppOpenManager(this)

    }
}