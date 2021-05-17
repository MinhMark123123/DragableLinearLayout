package m.n.dragablelinearlayout

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.HorizontalScrollView
import androidx.fragment.app.Fragment
import m.n.dragablelinearlayout.drag.ItemDragContainer
import m.n.dragablelinearlayout.drag.ItemDragViewHolder

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    lateinit var containerLayout: ItemDragContainer
    lateinit var button: Button
    lateinit var buttonRemove: Button
    private var listView: ArrayList<View> = ArrayList()
    private var listViewId = arrayListOf(
        R.layout.item_drag_red,
        R.layout.item_drag_blue,
        R.layout.item_drag_black,
        R.layout.item_drag_primary
    )
    private var currentPosition = 0
    lateinit var horizontalScrollView: HorizontalScrollView
    private var mScrollDistance = 0f
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = ArrayList(4)
        (0..3).forEach {
            listView.add(LayoutInflater.from(requireContext()).inflate(listViewId[it], null, false))
        }
        view.apply {
            horizontalScrollView = findViewById(R.id.horizontal_scroller)
            containerLayout = findViewById(R.id.rev_container)
            button = findViewById(R.id.button_first)
            buttonRemove = findViewById(R.id.button_remove)
            containerLayout.sycToScrollView(horizontalScrollView)
        }
        button.setOnClickListener {
            if (containerLayout.childCount >= listView.size) {
                return@setOnClickListener
            }
            val itemView = listView[currentPosition]
            containerLayout.addItemViewVertical(itemView as ItemDragViewHolder)
            currentPosition++
        }
        buttonRemove.setOnClickListener {
            containerLayout.removeItemView(0)
        }
        containerLayout.onViewDrop { view, index -> Log.e("mmm", "on item : $index") }
        containerLayout.setOnViewSelectedListener { view, i -> Log.e("mmm", "on item : $i") }
    }


/*
    private fun updateListener() {
        containerLayout.forEach {
            it.setOnClickListener { }
        }
    }

    private val onLongClickListener = View.OnLongClickListener { view ->
        // Create a new ClipData.
        // This is done in two steps to provide clarity. The convenience method
        // ClipData.newPlainText() can create a plain text ClipData in one step.
        // Create a new ClipData.Item from the ImageView object's tag
        val item = ClipData.Item(view.tag as? CharSequence)
        // Create a new ClipData using the tag as a label, the plain text MIME type, and
        // the already-created item. This will create a new ClipDescription object within the
        // ClipData, and set its MIME type entry to "text/plain"
        val dragData = ClipData(
            view.tag as? CharSequence,
            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
            item
        )
        // Instantiates the drag shadow builder.
        val myShadow = View.DragShadowBuilder(view)//MyDragShadowBuilder(view)
        // Starts the drag
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            view.startDragAndDrop(dragData, myShadow, null, 0)
        } else {
            view.startDrag(
                dragData,   // the data to be dragged
                myShadow,   // the drag shadow builder
                null,       // no need to use local data
                0           // flags (not currently used, set to 0)
            )
        }
        view.visibility = View.INVISIBLE
        true
    }

    private val onDragListener = View.OnDragListener { view, event ->
        var result = false
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                Log.e(
                    "mmm",
                    "item position new position start : ${view.x}, ${view.y}"
                )
                view.invalidate()
                result = true
            }
            // Once the drag gesture enters a certain area, we want to elevate it even more.
            DragEvent.ACTION_DRAG_ENTERED -> result = true
            // No need to handle this for our use case.
            DragEvent.ACTION_DRAG_LOCATION -> result = true
            // Once the drag gesture exits the area, we lower the elevation down to the previous one.
            DragEvent.ACTION_DRAG_EXITED ->{
                view.invalidate()
                result = true
            }
            // Once the color is dropped on the area, we want to paint it in that color.
            DragEvent.ACTION_DROP -> {
                // Read color data from the clip data and apply it to the card view background.
                val item: ClipData.Item = event.clipData.getItemAt(0)
                Log.e("mmm", "item position new position end : ${view.x}, ${view.y}")
                view.invalidate()
                event.localState?.let {
                    val v = it as View
                    v.visibility = View.VISIBLE
                }
                //it.setCardBackgroundColor(Color.parseColor(colorHex.toString()))
                result = true
            }
            // Once the drag has ended, revert card views to the default elevation.
            DragEvent.ACTION_DRAG_ENDED -> {
                //it.cardElevation = CARD_ELEVATION_DEFAULT_DP.toDp(resources.displayMetrics)
                Log.e("mmm", "item position new position end : ${view.x}, ${view.y}")
                result = true
            }
            else -> result = false
        }
        result
    }*/
}

/*
class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {

    private val shadow = ColorDrawable(Color.LTGRAY)

    // Defines a callback that sends the drag shadow dimensions and touch point back to the
    // system.
    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        // Sets the width of the shadow to half the width of the original View
        val width: Int = view.width / 2

        // Sets the height of the shadow to half the height of the original View
        val height: Int = view.height / 2

        // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
        // Canvas that the system will provide. As a result, the drag shadow will fill the
        // Canvas.
        shadow.setBounds(0, 0, width, height)

        // Sets the size parameter's width and height values. These get back to the system
        // through the size parameter.
        size.set(width, height)

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(width / 2, height / 2)
    }

    // Defines a callback that draws the drag shadow in a Canvas that the system constructs
    // from the dimensions passed in onProvideShadowMetrics().
    override fun onDrawShadow(canvas: Canvas) {
        // Draws the ColorDrawable in the Canvas passed in from the system.
        shadow.draw(canvas)
    }
}*/
