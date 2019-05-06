package c.feature.autosize;

import android.app.Application;
import android.util.TypedValue;

/**
 * java 示例
 */
public class AppJava extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AutoSizeJavaWrapper.init(this);

    }
}
