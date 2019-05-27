package c.feature.extension;

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast


fun Activity.transparentStatus() {
    val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    window.decorView.systemUiVisibility = option;
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.statusBarColor = Color.TRANSPARENT;
}


fun isLightColor(color: Int): Boolean {
    val r = (color shr 16) and 0xff
    val g = (color shr 8) and 0xff
    val b = color and 0xff
    return r * 0.299 + g * 0.587 + b * 0.114 >= 192
}

fun Activity.transparentStatus(colorRes: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        var option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isLightColor(colorRes)) {
                option = (option or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
        }
        window.decorView.systemUiVisibility = option;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.statusBarColor = Color.TRANSPARENT;
        }
    }
}

fun Activity.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(text: String, textSize: Float) {
    val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
    val linearLayout = toast.view as LinearLayout
    val childAt = linearLayout.getChildAt(0) as TextView
    childAt.setTextSize(textSize)
    toast.show()
}
