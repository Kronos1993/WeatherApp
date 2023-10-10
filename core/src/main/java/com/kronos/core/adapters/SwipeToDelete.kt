package com.kronos.core.adapters

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeToDelete(
    var leftIcon: Drawable,
    private val backgroundLeft: ColorDrawable,
    var rightIcon: Drawable,
    private val backgroundRight: ColorDrawable,
    var itemTouchHelper: ItemTouchHelper.Callback,
    direction: Int
) : ItemTouchHelper.SimpleCallback(0, direction) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        itemTouchHelper.onSwiped(viewHolder, direction);
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20 //so background is behind the rounded corners of itemView
        when {
            dX > 0 -> {
                // Swiping to the right
                val iconTop = itemView.top + (itemView.height - rightIcon.intrinsicHeight) / 2
                val iconMargin = (itemView.height - rightIcon.intrinsicHeight) / 2
                val iconLeft = itemView.left + iconMargin
                val iconRight = iconLeft + rightIcon.intrinsicWidth
                val iconBottom = iconTop + rightIcon.intrinsicHeight
                rightIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                backgroundRight.setBounds(
                    itemView.left, itemView.top,
                    itemView.left + dX.toInt() + backgroundCornerOffset, itemView.bottom
                )
                backgroundRight.draw(c)
                rightIcon.draw(c)
            }
            dX < 0 -> {
                // Swiping to the left
                val iconMargin: Int = (itemView.height - leftIcon.intrinsicHeight) / 2
                val iconTop: Int = itemView.top + (itemView.height - leftIcon.intrinsicHeight) / 2
                val iconBottom = iconTop + leftIcon.intrinsicHeight
                val iconLeft: Int = itemView.right - iconMargin - leftIcon.intrinsicWidth
                val iconRight: Int = itemView.right - iconMargin
                leftIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                backgroundLeft.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top, itemView.right, itemView.bottom
                )
                backgroundLeft.draw(c)
                leftIcon.draw(c)
            }
            else -> { // view is unSwiped
                backgroundLeft.setBounds(0, 0, 0, 0)
                backgroundRight.setBounds(0, 0, 0, 0)
            }
        }
    }
}