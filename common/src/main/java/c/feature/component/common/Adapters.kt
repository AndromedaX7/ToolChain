package c.feature.component.common

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import butterknife.ButterKnife

open class PagerAdapter(fragmentManager: FragmentManager, var list: List<Fragment>) :
    FragmentPagerAdapter(fragmentManager) {
    override fun getCount() = list.size
    override fun getItem(position: Int) = list[position]

    fun setListData(list: List<Fragment>) {
        this.list = ArrayList<Fragment>(list)
        notifyDataSetChanged()
    }
}


abstract class BaseListAdapter<T, VH : BaseListAdapter.ViewHolder> : BaseAdapter() {

    protected val data: ArrayList<T> = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vh: VH;
        val cvtView: View
        if (convertView == null) {
            cvtView = LayoutInflater.from(parent!!.context).inflate(layoutId(), parent, false)
            vh = createViewHolder(cvtView)
            cvtView.tag = vh;
        } else {
            cvtView = convertView;
            vh = cvtView.tag as VH
        }

        onBindView(vh, position, data[position])
        return cvtView
    }

    abstract fun createViewHolder(view: View): VH
    abstract fun onBindView(vh: VH, position: Int, item: T)

    abstract fun layoutId(): Int
    override fun getItem(position: Int): T = data[position]

    override fun getItemId(position: Int): Long = position.toLong()
    override fun getCount() = data.size

    open class ViewHolder(protected val adapter: BaseListAdapter<*, *>, view: View) {
        init {
            ButterKnife.bind(this, view)
        }
    }

    fun setData(data: ArrayList<T>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }
}

