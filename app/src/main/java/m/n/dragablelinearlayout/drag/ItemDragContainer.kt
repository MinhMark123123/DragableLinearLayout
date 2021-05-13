package m.n.dragablelinearlayout.drag

import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.RelativeLayout
import androidx.core.view.children
import androidx.core.view.get
import kotlin.math.roundToInt

class ItemDragContainer(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private var scrollView: HorizontalScrollView? = null
    private var listenerOnViewDrop: ((View, Int) -> Unit)? = null
    private var isExited = false

    fun sycToScrollView(scrollView: HorizontalScrollView) {
        this.scrollView = scrollView
        setOnDragListener(onDragListener)
    }

    fun addItemView(view: ItemDragViewHolder) {
        val params = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = 16
        if (childCount != 0) {
            val item = getChildAt(childCount - 1)
            params.topMargin = (item.y + item.height + 16).toInt()
        } else {
            params.topMargin = 16
        }
        addView(view, params)
        view.viewIndex = childCount - 1
    }

    fun removeItemView(index: Int) {
        if (childCount <= 1) return
        if (index in childCount..-1) return
        removeViewAt(index)
        if (index in childCount..-1) return
        for (viewIndex in index until childCount) {
            val itView = getChildAt(viewIndex)
            val params: RelativeLayout.LayoutParams = itView.layoutParams as LayoutParams
            params.topMargin = params.topMargin - itView.height - 16
            itView.layoutParams = params
            itView.invalidate()
        }
    }

    fun onViewDrop(listener: (view: View, index: Int) -> Unit) {
        listenerOnViewDrop = listener
    }

    private val onDragListener: OnDragListener = object : OnDragListener {
        override fun onDrag(view: View?, dragEvent: DragEvent?): Boolean {
            if (view == null) return false
            if (dragEvent == null) return false
            val localView = dragEvent.localState as ItemDragViewHolder
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_EXITED -> {
                    if (localView.visibility != View.VISIBLE) {
                        //localView.visibility = View.VISIBLE
                        localView.hideShadow()
                        localView.invalidate()
                    }
                    isExited = true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    if (localView.visibility == View.VISIBLE) {
                        //localView.visibility = View.INVISIBLE
                        localView.showShadow()
                        localView.invalidate()
                    }
                    isExited = false
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    val x = dragEvent.x.roundToInt()
                    val y: Float = dragEvent.y
                    val translatedX = (x - (scrollView?.scrollX ?: 0)).toInt()
                    val threshold = (dragEvent.localState as View).width / 2
                    // make a scrolling up due the y has passed the threshold
                    if (translatedX < threshold) {
                        // make a scroll up by 30 px
                        scrollView?.smoothScrollBy(-20, 0)
                    } else if (translatedX + threshold > 500) {
                        // make a scroll down by 30 px
                        scrollView?.smoothScrollBy(20, 0)
                    }
                    if (!isExited) {
                        localView.x = (x.toFloat() - localView.width / 2)
                        localView.y = (y - localView.height / 2)
                    }
                }
                DragEvent.ACTION_DROP -> {
                    val x: Float = dragEvent.x
                    val y: Float = dragEvent.y
                    localView.x = (x - localView.width / 2)
                    localView.y = (y - localView.height / 2)
                    //localView.visibility = View.VISIBLE
                    listenerOnViewDrop?.invoke(
                        localView,
                        (localView as ItemDragViewHolder).viewIndex
                    )
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    val localView = dragEvent.localState as View
                    //localView.visibility = View.VISIBLE
                }
            }
            return true
        }
    }
}