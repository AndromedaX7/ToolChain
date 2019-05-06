package c.feature.autosize

import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics

/**
 * AutoSizeManager
 * AutoSize控制器
 */
class AutoSizeManager private constructor(var application: Application) {


    private object adapter : ActivityLifeCircleAdapter() {
        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            AutoSize.processor(activity!!)
        }
    }

    /**
     * 初始化 AutoSizeManager
     * 获取 AndroidManifest.xml  Application节点中 meta-data信息
     * 启动适配
     */
    fun init() {
        AutoSize.defaultDesignSize(application)
        application.registerActivityLifecycleCallbacks(adapter)
    }

    /**
     * 重启屏幕适配
     */
    fun restart(current: Activity) {
        application.registerActivityLifecycleCallbacks(adapter)
        AutoSize.restart(current)
    }

    /**
     * 停止屏幕适配
     */
    fun stop() {
        if (AutoSize.run)
            application.unregisterActivityLifecycleCallbacks(adapter)
        AutoSize.run = false
    }

    /**
     * AutoSizeManager单例模式
     */
    companion object {
        private var instance: AutoSizeManager? = null
        fun instance(app: Application): AutoSizeManager {
            if (instance == null) {
                instance = AutoSizeManager(app)
            }
            return instance!!
        }
    }
}

/**
 * AutoSize功能模块
 */
private object AutoSize {
    /**
     * 是否运行
     */
    var run: Boolean = false
    /**
     * 设计稿默认宽度
     */
    private var designWidth: Int = 0
    /**
     * 设计稿默认高度
     */
    private var designHeight: Int = 0

    /**
     * 屏幕适配关键信息 density
     * density = application.resources.displayMetrics.density
     * scaledDensity = application.resources.displayMetrics.scaledDensity
     * densityDpi = application.resources.displayMetrics.densityDpi
     */
    private var density: Float = 0f
    private var scaledDensity: Float = 0f
    private var densityDpi: Int = 0;
    private var xdpi: Float = 0f


    /**
     * 缓存已适配过的信息
     */
    private val informationCache: HashMap<String, AutoSizeInformation> = HashMap()

    /**
     * 获取系统信息,AndroidManifest.xml 中定义设计稿尺寸
     */
    internal fun defaultDesignSize(application: Application) {
        if (run) return
        val applicationInfo =
            application.packageManager.getApplicationInfo(application.packageName, PackageManager.GET_META_DATA)
        applicationInfo.metaData?.let {
            designWidth = it.getInt("design_width")
            designHeight = it.getInt("design_height")
        }
        density = application.resources.displayMetrics.density
        scaledDensity = application.resources.displayMetrics.scaledDensity
        densityDpi = application.resources.displayMetrics.densityDpi
        xdpi = application.resources.displayMetrics.xdpi
        run = true
    }

    /**
     * 分析匹配模式，进行适配
     */
    internal fun processor(context: Activity) {
        if (run)
            if (context is AutoAdaptSize)
                processorAutoSize(context)
            else if (context is CancelAdaptSize)
                processorCancelAdaptSize(context)
            else processorDefault(context)
    }

    /**
     * 没有设置适配信息 用系统默认数据
     */
    private fun processorDefault(context: Activity) {
        val displayMetrics = context.resources.displayMetrics
        displayMetrics.let {
            it.density = density
            it.scaledDensity = scaledDensity
            it.densityDpi = densityDpi
            it.xdpi = xdpi
        }
    }

    /**
     * 对不必适配的区域使用默认数据
     */
    private fun processorCancelAdaptSize(context: Activity) {
        val displayMetrics = context.resources.displayMetrics
        displayMetrics.let {
            it.density = density
            it.scaledDensity = scaledDensity
            it.densityDpi = densityDpi
            it.xdpi = xdpi
        }
    }

    /**
     * 获取适配选项
     * 获取设计宽高以及适配方向(基于宽度或高度)
     * 设计稿尺寸读取优先级：适配源>androidManifest.xml>系统默认
     */
    private fun processorAutoSize(context: Activity) {
        if (context is AutoAdaptSize) {
            val metrics = context.resources.displayMetrics
            val basedWidth = context.basedWidth()
            if (context.designSize() <= 0) {
                val basedDesignSize = designSize(basedWidth)
                if (basedDesignSize != 0)
                    adaptSized(context::class.java.name, metrics, basedDesignSize, basedWidth, context.complexUnit())
                else {
                    processorDefault(context)
                }
            } else {
                adaptSized(context::class.java.name, metrics, context.designSize(), basedWidth, context.complexUnit())
            }
        }
    }

    private fun designSize(basedWidth: Boolean) = if (basedWidth) designWidth else designHeight


    /**
     * 适配
     * context 适配源
     * size 设计稿尺寸
     * basedWidth 是否基于宽度适配
     * complexUnit 设计尺寸单位 默认dp
     */
    private fun adaptSized(
        name: String,
        metrics: DisplayMetrics,
        size: Int,
        basedWidth: Boolean,
        complexUnit: ComplexUnit = ComplexUnit.DP
    ) {
        if (informationCache[name] == null) {
            val pxSize = if (basedWidth) {
                metrics.widthPixels
            } else {
                metrics.heightPixels
            }

            val targetDensity = if (complexUnit == ComplexUnit.DP) pxSize * 1.0f / size else density
            val targetDensityDpi = if (complexUnit == ComplexUnit.DP) (targetDensity * 160).toInt() else densityDpi
            val targetScaleDensity =
                if (complexUnit == ComplexUnit.DP) targetDensity * (scaledDensity / density) else scaledDensity
            val targetXdpi: Float =
                when (complexUnit) {
                    ComplexUnit.DP -> xdpi
                    ComplexUnit.MM -> pxSize * 1.0f / size * 25.4f
                    ComplexUnit.PT -> pxSize * 1.0f / size * 72
                    ComplexUnit.INCH -> pxSize * 1.0f / size
                }
            metrics.let {
                it.density = targetDensity
                it.scaledDensity = targetScaleDensity
                it.densityDpi = targetDensityDpi.toInt()
                it.xdpi = targetXdpi
            }
            informationCache[name] =
                AutoSizeInformation(targetDensity, targetScaleDensity, targetDensityDpi.toInt(), targetXdpi)
        } else {
            metrics.let {
                it.density = informationCache[name]!!.density
                it.scaledDensity = informationCache[name]!!.scaleDensity
                it.densityDpi = informationCache[name]!!.densityDpi
                it.xdpi = informationCache[name]!!.xdpi
            }
        }
    }

    /**
     * 重启适配
     */
    internal fun restart(current: Activity) {
        if (!run) {
            processor(current)
            current.window.decorView.invalidate()
            run = true
        }
    }
}

private data class AutoSizeInformation(
    var density: Float,
    var scaleDensity: Float,
    var densityDpi: Int,
    var xdpi: Float
)

/**
 * 屏幕适配识别接口
 * 其中定义适配方向和当前设计稿尺寸
 */
interface AutoAdaptSize {
    fun basedWidth(): Boolean = true

    fun designSize(): Int = 0

    fun complexUnit(): ComplexUnit = ComplexUnit.DP
}

enum class ComplexUnit constructor(val value: Int) {
    DP(0),
    INCH(1),
    PT(2),
    MM(4)
}

/**
 * 不适配的区域
 */
interface CancelAdaptSize

open class ActivityLifeCircleAdapter : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }
}
