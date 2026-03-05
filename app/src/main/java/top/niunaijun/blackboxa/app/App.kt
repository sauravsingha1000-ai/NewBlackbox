package top.niunaijun.blackboxa.app

import android.app.Application
import android.content.Context
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackboxa.util.CrashHandler

class App : Application() {

companion object {
    lateinit var instance: App
        private set
}

override fun attachBaseContext(base: Context) {
    super.attachBaseContext(base)
    BlackBoxCore.get().doAttachBaseContext(base)
}

override fun onCreate() {
    super.onCreate()
    instance = this

    // Enable crash logger
    CrashHandler.get().init(this)

    BlackBoxCore.get().doCreate(this)
    AppManager.init(this)
}

}
