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
    lateinit var horizontalButton: Button
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
            horizontalButton = findViewById(R.id.button_horizontal)
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
        horizontalButton.setOnClickListener {
            if (containerLayout.childCount >= listView.size) {
                return@setOnClickListener
            }
            val itemView = listView[currentPosition]
            containerLayout.addItemViewHorizontal(itemView as ItemDragViewHolder)
            currentPosition++
        }
        buttonRemove.setOnClickListener {
            containerLayout.removeItemView(0)
        }
        containerLayout.onViewDrop { view, index -> Log.e("mmm", "on item dropped : $index") }
        containerLayout.setOnViewSelectedListener { view, i ->
            Log.e(
                "mmm",
                "on selected item : $i"
            )
        }
    }
}
