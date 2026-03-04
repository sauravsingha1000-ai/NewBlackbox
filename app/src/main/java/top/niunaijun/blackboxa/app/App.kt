package top.niunaijun.blackboxa.app

import android.app.Application
import android.content.Context
import top.niunaijun.blackbox.BlackBoxCore

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
        BlackBoxCore.get().doCreate(this)
        AppManager.init(this)
    }
}
