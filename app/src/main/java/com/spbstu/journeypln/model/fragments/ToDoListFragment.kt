package com.spbstu.journeypln.model.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM
import com.spbstu.journeypln.R
import com.spbstu.journeypln.adapters.TodoRecyclerAdapter
import com.spbstu.journeypln.animations.SwipeToDeleteCallback
import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.presenters.fragmentPresenters.ToDoListPresenter
import com.spbstu.journeypln.views.ToDoListView
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import kotlin.properties.Delegates

class ToDoListFragment : MvpAppCompatFragment(), ToDoListView {

    @InjectPresenter
    lateinit var presenter: ToDoListPresenter

    private lateinit var newTaskName: TextInputLayout
    private lateinit var mRecyclerView: RecyclerView

    private var tripId by Delegates.notNull<Long>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_to_do_list, container, false)
        init(view)
        presenter.loadAllTasks()
        presenter.setAdapter()
        enableSwipeToDelete()

        newTaskName.setEndIconOnClickListener{
            val taskName = newTaskName.editText?.text.toString()
            presenter.addNewTask(taskText = taskName)
        }

        newTaskName.editText?.doAfterTextChanged {
            if (newTaskName.editText.toString().isNotEmpty()) {
                newTaskName.error = null
            }
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        val id = bundle?.getLong("id")
        if (id != null) {
            this.tripId = id
            presenter.setTripId(tripId)
        }

        val db = Room.databaseBuilder(
            requireActivity().applicationContext,
            TripsDb::class.java, "database"
        )
            .fallbackToDestructiveMigration()
            .build()

        presenter.setContext(requireContext(), db)
    }

    private fun init(view: View) {
        newTaskName = view.findViewById(R.id.new_task_name)
        newTaskName.error = null

        mRecyclerView = view.findViewById(R.id.todo_list_recycler)
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun enableSwipeToDelete() {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                presenter.deleteElement(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(mRecyclerView)
    }

    override fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun setUpAdapter(adapter: TodoRecyclerAdapter) {
        mRecyclerView.adapter = adapter
    }

    override fun showInputError() {
        newTaskName.error = "Поле пустое!"
    }

    override fun hideInputErrorAndClear() {
        newTaskName.editText?.setText("")
        newTaskName.error = null
    }
}