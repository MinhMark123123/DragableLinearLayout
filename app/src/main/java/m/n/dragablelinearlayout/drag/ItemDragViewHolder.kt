package m.n.dragablelinearlayout.drag

import android.content.ClipData
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.get
import m.n.dragablelinearlayout.R
import kotlin.math.roundToInt


class ItemDragViewHolder(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private var _isEnableDragging = false
    lateinit var myShadow: CustomViewShadow
    lateinit var imageViewShadow: ImageView
    private var listenerOnViewSelected: ((View, Int) -> Unit)? = null
    private var isInSelectedMode = false
    fun setOnViewSelectedListener(onViewSelected: (((View, Int) -> Unit))) {
        listenerOnViewSelected = onViewSelected
    }

    var viewIndex: Int = 0
        set(value: Int) {
            field = value
        }
        get() = field

    init {
        orientation = LinearLayout.HORIZONTAL
        setOnLongClickListener { it ->
            val data = ClipData.newPlainText(
                "dot",
                "Dot : $this"
            )
            imageViewShadow = ImageView(getContext())
            imageViewShadow.setImageBitmap(loadBitmapFromView(it))
            myShadow = CustomViewShadow(imageViewShadow)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                startDragAndDrop(data, myShadow, this, 0)
            } else {
                startDrag(data, myShadow, this, 0)
            }
            listenerOnViewSelected?.invoke(this, viewIndex)
            _isEnableDragging = true
            false
        }
        setOnClickListener {
            if (!isInSelectedMode) {
                selectedMode()
            } else {
                unSelectedMode()
            }
        }
    }

    fun selectedMode() {
        if (isInSelectedMode) return
        isInSelectedMode = true
        val leftView = LeftViewDrag(context = context)
        val rightView = RightViewDrag(context = context)
        leftView.setOnTouchListener(dragExpand)
        rightView.setOnTouchListener(dragExpand)
        addView(leftView, 0)
        addView(rightView, childCount)
        requestLayout()
    }

    fun unSelectedMode() {
        if (!isInSelectedMode) return
        isInSelectedMode = false
        removeViewAt(0)
        removeViewAt(childCount - 1)
        requestLayout()
    }

    private val dragExpand = object : OnTouchListener {
        override fun onTouch(view: View?, event: MotionEvent?): Boolean {
            if (view == null) return false
            if (event == null) return false
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    get(1).getLayoutParams().width += (x / Math.abs(x)).roundToInt()
                    get(1).requestLayout();
                    return true
                }
                else -> {
                    return true
                }
            }
        }
    }


    fun loadBitmapFromView(v: View): Bitmap {
        val b = Bitmap.createBitmap(
            v.width,
            v.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return b
    }
}

class CustomViewShadow(var oView: View) : View.DragShadowBuilder(oView) {
    override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
        outShadowSize?.let {
            it.set(1, 1)
            outShadowTouchPoint?.let { point ->
                point.set(0, 0)
            }
        }
    }
}

class LeftViewDrag(context: Context) : FrameLayout(context) {
    init {
        inflate(context, R.layout.left_button, this)
    }
}

class RightViewDrag(context: Context) : FrameLayout(context) {
    init {
        inflate(context, R.layout.right_button, this)
    }
}
