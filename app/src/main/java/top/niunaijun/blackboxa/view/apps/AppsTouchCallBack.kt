package top.niunaijun.blackboxa.view.apps

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class AppsTouchCallBack(
    private val onSwipeLeft: (Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder,
                         target: RecyclerView.ViewHolder) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.LEFT) onSwipeLeft(viewHolder.adapterPosition)
    }
}
