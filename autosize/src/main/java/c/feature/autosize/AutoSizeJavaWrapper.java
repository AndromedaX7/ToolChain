package c.feature.autosize;

import android.app.Activity;
import android.app.Application;

/**
 * 对AutoSizeManager 进行java 调用封装
 * Kotlin 请直接调用 {@link AutoSizeManager}
 * 框架会自动适配 继承{@link AutoAdaptSize}或 {@link AdaptAction}的子类
 */
public class AutoSizeJavaWrapper {
    private static AutoSizeManager instance;

    /**
     * 初始化
     *
     * @param app 当前应用 application
     */
    public static void init(Application app) {
        instance = AutoSizeManager.Companion.instance(app);
        instance.init();
    }

    /**
     * 重启框架
     *
     * @param activity
     */
    public static void restart(Activity activity) {
        instance.restart(activity);
    }

    /**
     * 停止适配功能
     */
    public static void stop() {
        instance.stop();
    }
}
