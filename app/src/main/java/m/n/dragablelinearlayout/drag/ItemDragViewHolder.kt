package m.n.dragablelinearlayout.drag

import android.content.ClipData
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView


class ItemDragViewHolder(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var _isEnableDragging = false
    lateinit var myShadow: CustomViewShadow
    lateinit var imageViewShadow: ImageView
    private var listenerOnViewSelected: ((View, Int) -> Unit)? = null
    fun setOnViewSelectedListener(onViewSelected: (((View, Int) -> Unit))) {
        listenerOnViewSelected = onViewSelected
    }

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

