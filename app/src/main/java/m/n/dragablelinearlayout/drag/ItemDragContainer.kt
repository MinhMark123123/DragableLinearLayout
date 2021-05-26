package m.n.dragablelinearlayout.drag

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.get
import kotlin.math.abs
import kotlin.math.roundToInt


class ItemDragContainer(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private var scrollView: HorizontalScrollView? = null
    private var listenerOnViewDrop: ((View, Int) -> Unit)? = null
    private var listenerOnViewSelected: ((View, Int) -> Unit)? = null
    private var isExited = false
    private var originX = 0f
    private var originY = 0f
    private var listBarrier = ArrayList<Int>()

    fun setOnViewSelectedListener(onViewSelected: (((View, Int) -> Unit))) {
        listenerOnViewSelected = onViewSelected
    }

    fun sycToScrollView(scrollView: HorizontalScrollView) {
        this.scrollView = scrollView
        setOnDragListener(onDragListener)
    }

    fun addItemViewVertical(view: ItemDragViewHolder) {
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
        view.setOnViewSelectedListener { itemView, i ->
            originX = itemView.x
            originY = itemView.y
            listenerOnViewSelected?.invoke(
                itemView,
                i
            )
        }
        listBarrier.add(params.topMargin)
        addView(view, params)
        view.viewIndex = childCount - 1
    }

    fun addItemViewHorizontal(view: ItemDragViewHolder) {
        val params = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (childCount != 0) {
            val item = getChildAt(childCount - 1)
            params.topMargin = (item.y).toInt()
            params.leftMargin = ((item.x + item.width) + 16).toInt()
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
            val params: LayoutParams = itView.layoutParams as LayoutParams
            params.topMargin = params.topMargin - itView.height - 16
            itView.layoutParams = params
            itView.invalidate()
        }
        if (listBarrier.isNotEmpty()) {
            listBarrier.removeLast()
        }
    }

    fun onViewDrop(listener: (view: View, index: Int) -> Unit) {
        listenerOnViewDrop = listener
    }

    var deltaX: Float = 0f
    var deltaY: Float = 0f
    var isHoldingLeftSide = false
    var xMaxToScale = -1
    private val onDragListener: OnDragListener = object : OnDragListener {
        override fun onDrag(view: View?, dragEvent: DragEvent?): Boolean {
            if (view == null) return false
            if (dragEvent == null) return false
            var localView = dragEvent.localState
            if (localView is ItemDragViewHolder) {
                localView = dragEvent.localState as ItemDragViewHolder
                when (dragEvent.action) {
                    DragEvent.ACTION_DRAG_EXITED -> {
                        if (localView.visibility != View.VISIBLE) {
                            localView.invalidate()
                        }
                        isExited = true
                    }
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        if (localView.visibility == View.VISIBLE) {
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
                        var isOverlappingChildItem = false
                        for (i in 0 until childCount) {
                            val child: View = getChildAt(i)
                            Log.d("mmm", "is overlap loop")
                            if (child != localView) {
                                Log.d("mmm", "is overlap action")
                                if (isOverlap(localView, child)) {
                                    Log.d("mmm", "is overlap action = true")
                                    isOverlappingChildItem = true
                                    break
                                }
                            }
                        }
                        Log.d("mmm", "is overlap $isOverlappingChildItem")
                        if (isOverlappingChildItem) {
                            swapBackOrigin(localView)
                        } else {
                            val x: Float = dragEvent.x
                            val y: Float = dragEvent.y
                            localView.x = (x - localView.width / 2)
                            localView.y = (y - localView.height / 2)
                            val correctedValue = findClosedBarrier(dragEvent.y)
                            if (localView.y.roundToInt() != correctedValue) {
                                localView.animate().setDuration(100L).y(correctedValue.toFloat())
                                    .start()
                            }
                            listenerOnViewDrop?.invoke(
                                localView,
                                localView.viewIndex
                            )
                        }

                    }
                }
            } else if (localView is View) {
                if (dragEvent == null) return false

                val parentView = (localView.parent as LinearLayout)[1]

                when (dragEvent.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        deltaX = dragEvent.x
                        deltaY = dragEvent.y
                        val viewItem = localView.parent as ItemDragViewHolder
                        xMaxToScale = findMaxScaleX(viewItem, viewItem.viewIndex)
                        isHoldingLeftSide =
                            localView.x <= (localView.parent as ItemDragViewHolder).x.roundToInt()
                        if (isHoldingLeftSide) {
                            parentView.pivotX = parentView.pivotX + parentView.width
                        }
                    }
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        val x: Float = dragEvent.x
                        Log.e("mmmm", "debug max scale $xMaxToScale")
                        if (x > deltaX) {
                            //drag right
                            val amountIncrease = (x / abs(
                                x
                            )).roundToInt()
                            if (isHoldingLeftSide) {
                                parentView.layoutParams.width -= amountIncrease
                                (localView.parent as ItemDragViewHolder).x -= amountIncrease
                            } else {
                                val viewFather = (localView.parent as ItemDragViewHolder)
                                val valueCompare =
                                    (viewFather.x + viewFather.width - localView.width * 2 + amountIncrease)
                                Log.e(
                                    "mmmm",
                                    "debug drag right hold right value $valueCompare and $xMaxToScale"
                                )
                                if (xMaxToScale != -1) {
                                    if (valueCompare < abs(xMaxToScale)) {
                                        parentView.layoutParams.width += amountIncrease
                                    }
                                } else {
                                    parentView.layoutParams.width += amountIncrease
                                }
                            }

                        } else {
                            //drag left
                            if (isHoldingLeftSide) {
                                if (x != 0f) {
                                    val amountIncrease = (x / abs(
                                        x
                                    )).roundToInt()
                                    val valueCompare =
                                        ((localView.parent as ItemDragViewHolder).x - localView.x - amountIncrease)
                                    Log.e(
                                        "mmmm",
                                        "debug drag left hold left value $valueCompare and $xMaxToScale"
                                    )
                                    if (valueCompare > abs(xMaxToScale)) {
                                        parentView.layoutParams.width += amountIncrease
                                        (localView.parent as LinearLayout).x -= amountIncrease
                                    }
                                    if (xMaxToScale != -1) {
                                        if (valueCompare > abs(xMaxToScale)) {
                                            parentView.layoutParams.width += amountIncrease
                                            (localView.parent as LinearLayout).x -= amountIncrease
                                        }
                                    } else {
                                        parentView.layoutParams.width += amountIncrease
                                        (localView.parent as LinearLayout).x -= amountIncrease
                                    }
                                }
                            } else {
                                parentView.layoutParams.width -= (x / Math.abs(
                                    x
                                )).roundToInt()
                            }
                        }

                        parentView.requestLayout()
                        deltaX = x
                    }
                    DragEvent.ACTION_DRAG_EXITED -> {
                        parentView.pivotX = 0f
                        /* if(isReachBand){
                             localView.animate().setDuration(100L).x((localView.parent as ItemDragViewHolder).x + localView.width)
                                 .start()
                             isReachBand = false
                         }*/
                    }
                    DragEvent.ACTION_DROP -> {
                        parentView.pivotX = 0f
                        /* if (isReachBand) {
                             (localView.parent as ItemDragViewHolder).animate().setDuration(100L)
                                 .x((localView.parent as ItemDragViewHolder).x + localView.width)
                                 .start()
                             isReachBand = false
                         }
                         isReachBand = false*/
                    }
                }
                return true
            }
            return true
        }
    }

    private fun findMaxScaleX(viewRow: View, indexInclude: Int): Int {
        val xV = viewRow.x.roundToInt()
        val yV = viewRow.y.roundToInt()
        (0 until childCount).forEach { index ->
            if (get(index).y.roundToInt() == yV && index != indexInclude) {
                if (get(index).x < xV) {
                    //xV is on the right of view , band left side
                    return -(get(index).x + get(index).width).roundToInt()
                } else {
                    //xV is on the right of view , band right side
                    return get(index).x.roundToInt()
                }
            }
        }
        return -1
    }

    private fun findClosedBarrier(y: Float): Int {
        if (listBarrier.isEmpty()) return y.roundToInt()
        if (listBarrier.size == 1) return listBarrier[0]
        var minIndex = 0
        var minValue = abs(y - listBarrier.first())
        listBarrier.forEachIndexed { index, barrier ->
            if (abs(y - barrier) <= minValue) {
                minIndex = index
                minValue = abs(y - barrier)
            }
        }
        return listBarrier[minIndex]
    }

    private fun isOverlap(v1: View, v2: View): Boolean {
        if (v1.top >= v2.top &&
            v1.left >= v2.left &&
            v1.right <= v2.right &&
            v1.bottom <= v2.bottom
        ) {
            return true
        }
        if (v2.top >= v1.top &&
            v2.left >= v1.left &&
            v2.right <= v1.right &&
            v2.bottom <= v1.bottom
        ) {
            return true
        }
        val rect1 =
            Rect(v1.x.toInt(), v1.y.toInt(), (v1.x.toInt() + v1.width), (v1.y.toInt() + v1.height))
        val rect2 = Rect(v2.left, v2.top, v2.right, v2.bottom)
        Log.d("mmm", "is overlap $rect1 $rect2")
        return Rect.intersects(rect1, rect2) or Rect.intersects(rect2, rect1)
    }

    private fun swapBackOrigin(view: View) {
        view.animate().x(originX.toFloat()).y(originY.toFloat()).setDuration(200).start()
    }
}