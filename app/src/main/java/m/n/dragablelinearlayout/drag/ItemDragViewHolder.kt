package m.n.dragablelinearlayout.drag

import android.content.ClipData
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import m.n.dragablelinearlayout.R
import kotlin.math.abs
import kotlin.math.roundToInt


class ItemDragViewHolder(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private var _isEnableDragging = false
    lateinit var myShadow: CustomViewShadow
    lateinit var imageViewShadow: ImageView
    private var listenerOnViewSelected: ((View, Int) -> Unit)? = null
    private var isInSelectedMode = false
    private var isHold = false
    private var isLeft = false
    private var isAdjust = true

    private var mWidth = 0
    private var mDelta = 0
    private var mHeight = 0

    fun setAdjust(adjust: Boolean) {
        isAdjust = adjust
    }

    fun setOnViewSelectedListener(onViewSelected: (((View, Int) -> Unit))) {
        listenerOnViewSelected = onViewSelected
    }

    var viewIndex: Int = 0
        set(value: Int) {
            field = value
        }
        get() = field

    init {
//        orientation = LinearLayout.HORIZONTAL
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


    private val mGestureDetector: GestureDetector =
        GestureDetector(context, object : GestureDetector.OnGestureListener {
            override fun onDown(p0: MotionEvent?): Boolean {
                return true
            }

            override fun onShowPress(p0: MotionEvent?) {
                //no-op
            }

            override fun onSingleTapUp(p0: MotionEvent?): Boolean {
                unSelectedMode()
                return true
            }

            override fun onScroll(
                p0: MotionEvent?,
                p1: MotionEvent?,
                p2: Float,
                p3: Float
            ): Boolean {
                return true
            }

            override fun onLongPress(p0: MotionEvent?) {
                //no-op
            }

            override fun onFling(
                p0: MotionEvent?,
                p1: MotionEvent?,
                p2: Float,
                p3: Float
            ): Boolean {
                return true
            }

        })


    fun selectedMode() {
        if (isInSelectedMode) return
        isInSelectedMode = true
//        val leftView = LeftViewDrag(context = context)
//        val rightView = RightViewDrag(context = context)
        val rightView = MaskViewExpand(context = context)

        val params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

//        setOnTouchListener(this)
//        leftView.setOnTouchListener(onTouchListener)
        rightView.setOnTouchListener(onTouchListener)
        addView(rightView, params)
//        addView(rightView, childCount)
//        requestLayout()
    }

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        if (event == null) return false
//        Log.e("ttt", "else:  ${event.action} ok")
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                enableScroll(false)
//
//                if (isAdjust) {
//                    mLastX = event.rawX
//
//                    if (mLastX > left + 2 * width / 3f) {
//                        isHold = true
//                        isLeft = false
//                    } else if (mLastX > left && mLastX < left + width / 3f) {
//                        isHold = true
//                        isLeft = true
//                    }
//                }
//            }
//            MotionEvent.ACTION_MOVE -> {
//                Log.e("ttt", "MOVE: ok $isHold")
//                if (isHold) {
//                    val d = event.rawX - mLastX
//                    enableScroll(false)
//                    Log.e("ttt", "MOVE delta: ok $d")
//                    if (isLeft) {
//                        get(0).layoutParams.width = (width - d).roundToInt()
//                    } else {
//                        get(0).layoutParams.width = (width + d).roundToInt()
//                    }
//                    requestLayout()
//                }
//                mLastX = event.rawX
//            }
//            MotionEvent.ACTION_UP -> {
////                    if (isHold) {
////                        isHold = false
////                        enableScroll(true)
////                    }
//            }
//            MotionEvent.ACTION_CANCEL -> {
//                enableScroll(true)
//            }
//            else -> {
////                    enableScroll(true)
//            }
//        }
//        Log.e("ttt", "return: ok")
//        return true
//    }

    fun unSelectedMode() {
        if (!isInSelectedMode) return
        isInSelectedMode = false
        enableScroll(true)
        removeViewAt(1)
//        removeViewAt(childCount - 1)
        requestLayout()
    }

    private val onDragEvent = object : OnDragListener {
        override fun onDrag(p0: View?, dragEvent: DragEvent?): Boolean {
            if (dragEvent == null) return false
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_LOCATION -> {
                    get(1).layoutParams.width += (x / abs(x)).roundToInt()
                    get(1).requestLayout()
                }
            }
            return true
        }
    }

    private var mLastX = 0f

    private val onTouchListener = object : OnTouchListener {
        override fun onTouch(view: View?, event: MotionEvent?): Boolean {
            if (view == null) return false
            if (event == null) return false
            mGestureDetector.onTouchEvent(event)
            Log.e("ttt", "else:  ${event.action} ok")
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    enableScroll(false)
                    if (isAdjust) {
                        mLastX = event.rawX

                        if (mLastX > view.left + 2 * view.width / 3f) {
                            isHold = true
                            isLeft = false
                        } else if (mLastX > view.left && mLastX < view.left + view.width / 3f) {
                            isHold = true
                            isLeft = true
                        }
                    }

                    mLastX = event.rawX
                    Log.e("ttt", "down: ok $mLastX")
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.e("ttt", "MOVE: ok $isHold")
                    mWidth = width
                    mHeight = height
                    if (isHold) {
                        val d = event.rawX - mLastX
                        enableScroll(false)
                        Log.e("ttt", "MOVE delta: ok $d")
                        if (isLeft) {
                            mDelta = -d.roundToInt()
                            get(0).layoutParams.width = (width - d).roundToInt()
                        } else {
                            mDelta = d.roundToInt()
                            get(0).layoutParams.width = (width + d).roundToInt()
                        }
                        requestLayout()
                    }
                    mLastX = event.rawX

//                    if (isHold) {
//                        val d = event.rawX
//                        enableScroll(false)
//                        Log.e("ttt", "MOVE: ok ${(d - mLastX)}")
//                        if (isLeft) {
//                            view.x = (x - view.width / 2)
//                            get(1).layoutParams.width += (mLastX - d).roundToInt()
//                        } else {
//                            get(1).layoutParams.width += (d - mLastX).roundToInt()
//                        }
//                        requestLayout()
//                    }
                }
                MotionEvent.ACTION_UP -> {
//                    if (isHold) {
//                        isHold = false
//                        enableScroll(true)
//                    }
                }
                MotionEvent.ACTION_CANCEL -> {
//                    enableScroll(true)
//                    if (isHold) {
//                        isHold = false
//                        enableScroll(true)
//                    }
                }
                else -> {
//                    enableScroll(true)
                }
            }
            Log.e("ttt", "return: ok")
            return true
        }
    }

    private fun enableScroll(enable: Boolean) {
        if (parent == null) return
        if (parent is ViewGroup) {
            if (parent.parent is HorizontalScrollView) {
                (parent.parent as HorizontalScrollView).setOnTouchListener { _, _ -> !enable }
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

class MaskViewExpand(context: Context) : ConstraintLayout(context) {
    init {
        inflate(context, R.layout.item_mask_button, this)
        setBackgroundResource(R.drawable.shape_stroke_gray)
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
