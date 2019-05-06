package c.feature.recyclerview.swipemenu

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.moveToPosition(position: Int) {
    adapter?.let {
        if (position >= 0 && it.itemCount > position)
            if (layoutManager is LinearLayoutManager) {
                layoutManager?.let { lm ->
                    val first = (lm as LinearLayoutManager).findFirstVisibleItemPosition()
                    val last = lm.findLastVisibleItemPosition()

                    if (position <= first) {
                        scrollToPosition(position)
                    } else if (position <= last) {
                        val top = getChildAt(position - first).top
                        scrollBy(0, top)
                    } else {
                        scrollToPosition(position)
                        post {
                            val n = position - (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                            if (n in 0 until childCount) {
                                //获取要置顶的项顶部离RecyclerView顶部的距离
                                val top = getChildAt(n).top
                                //最后的移动
                                scrollBy(0, top)
                            }
                        }
                    }
                }
            }
    }

}