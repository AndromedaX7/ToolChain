package c.feature.component.common

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.WindowManager

fun Activity.transparentStatus(): Unit {
    val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    window.decorView.systemUiVisibility = option;
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.statusBarColor = Color.TRANSPARENT;
}



