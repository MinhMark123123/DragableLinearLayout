package m.n.dragablelinearlayout

import android.view.MotionEvent
import android.view.View

open class DragExperimentTouchListener(var lastX: Float, var lastY: Float) : View.OnTouchListener {
    var isDragging = false
    var deltaX = 0f
    override fun onTouch(arg0: View, arg1: MotionEvent): Boolean {
        val action = arg1.action
        if (action == MotionEvent.ACTION_DOWN && !isDragging) {
            isDragging = true
            deltaX = arg1.x
            return true
        } else if (isDragging) {
            if (action == MotionEvent.ACTION_MOVE) {
                arg0.x = arg0.x + arg1.x - deltaX
                arg0.y = arg0.y
                return true
            } else if (action == MotionEvent.ACTION_UP) {
                isDragging = false
                lastX = arg1.x
                lastY = arg1.y
                return true
            } else if (action == MotionEvent.ACTION_CANCEL) {
                arg0.x = lastX
                arg0.y = lastY
                isDragging = false
                return true
            }
        }
        return false
    }
}