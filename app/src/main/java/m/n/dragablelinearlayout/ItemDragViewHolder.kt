package m.n.dragablelinearlayout

import android.content.ClipData
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.HorizontalScrollView


class ItemDragViewHolder(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var _isEnableDragging = false
    private var _onStartDrag: (() -> Unit)? = null
    private var scrollView: HorizontalScrollView? = null
    private var mScrollDistance = 0
    private var _onEndDrag: (() -> Unit)? = null

    fun setOnEndDrag(value: (() -> Unit)) {
        _onEndDrag = value
    }

    fun setOnStartDrag(value: (() -> Unit)) {
        _onStartDrag = value
    }

    fun sycToScrollView(scrollView: HorizontalScrollView) {
        this.scrollView = scrollView
    }

    init {
        setOnLongClickListener { it ->
            val data = ClipData.newPlainText(
                "dot",
                "Dot : $this"
            )
            val myShadow: DragShadowBuilder = DragShadowBuilder(this)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                startDragAndDrop(data, myShadow, this, 0)
            } else {
                startDrag(data, myShadow, this, 0)
            }
            _isEnableDragging = true
            false
        }
    }


    private var dY: Float = 0f
    private var dX: Float = 0f


}