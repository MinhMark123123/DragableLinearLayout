package m.n.dragablelinearlayout.drag

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
    var viewIndex: Int = 0
        set(value: Int) {
            field = value
        }
        get() = field

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
            visibility = View.INVISIBLE
            _isEnableDragging = true
            false
        }
    }


}