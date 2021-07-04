package com.spbstu.journeypln.animations

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.spbstu.journeypln.R

abstract class SwipeToDeleteCallback(val context: Context): ItemTouchHelper.Callback() {

    private val paint: Paint = Paint()
    private val gradDrawable = GradientDrawable()
    private val backgroundColor: Int = Color.RED
    private val deleteDrawable: Drawable? = ContextCompat.getDrawable(
        context,
        R.drawable.ic_baseline_delete_24
    )
    private val width: Int = deleteDrawable!!.intrinsicWidth
    private val height: Int = deleteDrawable!!.intrinsicHeight

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
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

        val view = viewHolder.itemView as MaterialCardView
        val itemHeight = view.height
        val isCanceled = dX == 0.0F && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(c, view.right + dX, view.top.toFloat(), view.right.toFloat(), view.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }
        gradDrawable.setColor(backgroundColor)
        gradDrawable.setBounds(view.right + dX.toInt(), view.top, view.right, view.bottom)
        gradDrawable.cornerRadius = 42F

        gradDrawable.draw(c)

        val deleteIconTop = view.top + (view.height - height) / 2
        val deleteIconMargin = (itemHeight - height) / 2
        val deleteIconLeft = view.right - deleteIconMargin - width
        val deleteIconRight = view.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + height

        deleteDrawable!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteDrawable.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, paint)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7F
    }

}