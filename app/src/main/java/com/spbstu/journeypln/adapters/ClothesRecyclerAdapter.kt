package com.spbstu.journeypln.adapters

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spbstu.journeypln.R
import com.spbstu.journeypln.data.room.entities.Cloth

class ClothesRecyclerAdapter(
    tasks: List<Cloth>,
    private val onClickListener: (View, Cloth) -> Unit,
    private val onClickMoreButtonListener: (View, Cloth) -> Unit
): RecyclerView.Adapter<ClothesRecyclerAdapter.MyViewHolder>(), View.OnCreateContextMenuListener {

    private var mClothes: MutableList<Cloth> = tasks.toMutableList()


    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.cloth_checkBox)
        val moreButton: ImageButton = view.findViewById(R.id.more_btn)
        val weightTextView: TextView = view.findViewById(R.id.cloth_weight)
        val countTextView: TextView = view.findViewById(R.id.cloth_count)
    }

    fun setData(newTasksList: List<Cloth>) {
        mClothes = newTasksList.toMutableList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesRecyclerAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_cloth, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClothesRecyclerAdapter.MyViewHolder, position: Int) {
        val currentTask = mClothes[position]

        holder.checkBox.text = currentTask.name
        holder.checkBox.isChecked = currentTask.isChecked

        holder.checkBox.setOnClickListener { view ->
            onClickListener(view, currentTask)
        }

        holder.weightTextView.text = "${String.format("%.2f", currentTask.weight * currentTask.count)} кг"
        holder.countTextView.text = "x ${currentTask.count}"

        holder.moreButton.setOnClickListener { view ->
            onClickMoreButtonListener(view, currentTask)
        }
    }

    override fun getItemCount(): Int {
        return mClothes.size
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {

    }

}