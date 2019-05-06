package c.feature.recyclerview.swipemenu

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


abstract class BaseRecyclerViewAdapter<T, VH : BaseRecyclerViewAdapter.RecyclerViewHolder> :
    RecyclerView.Adapter<VH>(), View.OnClickListener {

    private var openPosition: Int = -1

    val data: ArrayList<T> = ArrayList()

    private var onItemClickListener: ((Int, T) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return createViewHolder(LayoutInflater.from(parent.context).inflate(layoutId(), parent, false))
    }


    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(this)
        holder.expand()
    }

    abstract fun createViewHolder(view: View): VH
    abstract fun layoutId(): Int

    open class RecyclerViewHolder constructor(protected val adapter: BaseRecyclerViewAdapter<*, *>, itemView: View) :
        RecyclerView.ViewHolder(itemView)/*, OnItemExpandListener*/ {
        fun expand() {
            if (adapter.openPosition == layoutPosition) {
                openCurrent()
            } else {
                closeCurrent()
            }
        }


        open fun onOpened() {
            when (adapter.openPosition) {
                -1 -> {
                    adapter.openPosition = layoutPosition
                    openCurrent()
                    adapter.notifyItemChanged(adapter.openPosition)
                }
                layoutPosition -> {
                    adapter.openPosition = -1
                    closeCurrent()
                    adapter.notifyItemChanged(layoutPosition)
                }
                else -> {
                    val last: Int = adapter.openPosition
                    adapter.openPosition = layoutPosition

                    adapter.notifyItemChanged(last)
                    adapter.notifyItemChanged(adapter.openPosition)
                }
            }
        }

        open fun openCurrent() {

        }

        open fun closeCurrent() {

        }
    }

    override fun onClick(view: View) {
        val position: Int = view.tag as Int
        onItemClickListener?.let {
            it(position, data[position])
        }
    }

    fun appendNewData(data: ArrayList<T>) {
        val srcSize = this.data.size
        val dstSize = data.size
        this.data.addAll(data)
        notifyItemRangeInserted(srcSize, dstSize)
    }

    fun removeItem(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clearAdd(data: ArrayList<T>) {
        clear()
        Log.e("clearAdd", "data size:${data.size}")
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun clear() {
        val size = data.size
        data.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun setOnItemClickListener(x: (position: Int, item: T) -> Unit) {
        onItemClickListener =  x
    }

    interface OnItemClickListener<T> {
        fun onItemClick(position: Int, item: T)
    }

//    interface OnItemExpandListener {
//        fun callOpen(position: Int)
//        fun callClose(position: Int)
//    }
}