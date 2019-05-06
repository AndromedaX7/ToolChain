package c.feature.autosize

import android.app.Application

/**
 * kotlin 示例
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AutoSizeManager.instance(this).init()
    }

}