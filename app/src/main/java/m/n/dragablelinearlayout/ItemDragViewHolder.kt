package m.n.dragablelinearlayout

import android.content.ClipData
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ScrollView


class ItemDragViewHolder(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var _isEnableDragging = false
    private var _onStartDrag: (() -> Unit)? = null
    private var scrollView:HorizontalScrollView? = null
    fun setOnStartDrag(value: (() -> Unit)) {
        _onStartDrag = value
    }

    private var _onEndDrag: (() -> Unit)? = null
    fun setOnEndDrag(value: (() -> Unit)) {
        _onEndDrag = value
    }

    fun sycToScrollView(scrollView: HorizontalScrollView){
        this.scrollView = scrollView
    }

    init {
        setOnLongClickListener { it ->
            val data = ClipData.newPlainText(
                "dot",
                "Dot : $this"
            )
            val myShadow: DragShadowBuilder = View.DragShadowBuilder(this)
            startDrag(data, myShadow, this, 0)
            it.setOnDragListener { view, dragEvent ->
                when (dragEvent.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        Log.e(
                            "mmm",
                            "item position new position start : ${view.x}, ${view.y}"
                        )
                        view.visibility = View.INVISIBLE
                        dX = dragEvent.x
                        dY = dragEvent.y
                        invalidate()
                        true
                    }
                    // Once the drag gesture enters a certain area, we want to elevate it even more.
                    DragEvent.ACTION_DRAG_ENTERED -> true
                    // No need to handle this for our use case.
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        Log.e(
                            "mmm",
                            "item position new position end : ${dragEvent.x}, ${dragEvent.y}"
                        )
                        scrollView?.let {
                            val topOfDropZone: Int = it.top
                            val bottomOfDropZone: Int = it.bottom

                            val scrollY: Int = it.scrollY
                            val scrollViewHeight: Int = it.measuredHeight


                            if (bottomOfDropZone > scrollY + scrollViewHeight - 100) it.smoothScrollBy(
                                0,
                                30
                            )

                            if (topOfDropZone < scrollY + 100) it.smoothScrollBy(0, -30)
                        }
                        true
                    }
                    // Once the drag gesture exits the area, we lower the elevation down to the previous one.
                    DragEvent.ACTION_DRAG_EXITED -> {
                        invalidate()
                        true
                    }
                    // Once the color is dropped on the area, we want to paint it in that color.
                    DragEvent.ACTION_DROP -> {
                        Log.e("mmm", "item position new position end : ${view.x}, ${view.y}")
                        //it.setCardBackgroundColor(Color.parseColor(colorHex.toString()))
                        true
                    }
                    // Once the drag has ended, revert card views to the default elevation.
                    DragEvent.ACTION_DRAG_ENDED -> {
                        //it.cardElevation = CARD_ELEVATION_DEFAULT_DP.toDp(resources.displayMetrics)
                        Log.e(
                            "mmm",
                            "item position new position end : ${dragEvent.x}, ${dragEvent.y}"
                        )
                        x = dragEvent.x - dX
                        visibility = View.VISIBLE
                        it.setOnDragListener(null)
                        true
                    }
                    else -> false
                }
            }
            //enableDrag()
            false
        }

    }

    /* fun enableDrag() {
         _isEnableDragging = true
         setOnTouchListener(this)
         _onStartDrag?.invoke()
     }

     fun disableDrag() {
         setOnTouchListener(null)
         _isEnableDragging
         _onEndDrag?.invoke()
     }*/

    private var dY: Float = 0f
    private var dX: Float = 0f

    /* override fun onTouch(view: View?, event: MotionEvent?): Boolean {
         if (event == null) return false
         if (view == null) return false
         return when (event.action) {
             MotionEvent.ACTION_DOWN -> {
                 dX = x - event.x
                 dY = y - event.y
                 Log.e("mmm", "x : $x y : $y dx : $dX dy: $dY")
                 true
             }
             MotionEvent.ACTION_MOVE -> {
                 dX = event.rawX + x
                 animate()
                     .x(event.x + x)
                     .y(event.y + y)
                     .setDuration(0)
                     .start()
                 Log.e("mmm", "x : $x y : $y dx : $dX  dy: $dY")
                 true
             }
             MotionEvent.ACTION_UP -> {
                 animate()
                     .x(dX)
                     .y(dY)
                     .setDuration(0)
                     .start()
                 disableDrag()
                 true
             }
             else -> false
         }
     }*/
}