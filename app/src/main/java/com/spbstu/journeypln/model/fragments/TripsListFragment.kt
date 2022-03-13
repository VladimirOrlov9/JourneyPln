package com.spbstu.journeypln.model.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.spbstu.journeypln.R
import com.spbstu.journeypln.adapters.TripsRecyclerAdapter
import com.spbstu.journeypln.animations.SwipeToDeleteCallback
import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.presenters.fragmentPresenters.TripsListPresenter
import com.spbstu.journeypln.views.TripsListView
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter

class TripsListFragment: MvpAppCompatFragment(), TripsListView {

    private var position: Int = 0

    @InjectPresenter
    lateinit var presenter:  TripsListPresenter

    private lateinit var mRecyclerView: RecyclerView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        val pos = bundle?.getInt("pos")
        if (pos != null) {
            this.position = pos
        }

        val db = Room.databaseBuilder(
            requireActivity().applicationContext,
            TripsDb::class.java, "database"
        )
            .fallbackToDestructiveMigration()
            .build()

        presenter.setApplicationContext(requireContext(), position, db)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trips_list, container, false)
        init(view)

        presenter.initList()

        enableSwipeToDeleteAndUndo()
        return view
    }


    private fun init(view: View) {
        mRecyclerView = view.findViewById(R.id.recycler)
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun setUpAdapter(adapter: TripsRecyclerAdapter) {
        mRecyclerView.adapter = adapter
    }

    override fun showSnackBarDeletedItem(text: String) {
        var isRestorePressed = false
        Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG)
            .setAction("Отмена", View.OnClickListener {
                isRestorePressed =true
                presenter.restoreItem()
            })
            .addCallback(object : Snackbar.Callback(){
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if (!isRestorePressed) {
                        presenter.removeElementForever()
                    }
                }
            })
            .show()
    }

    override fun openClickedTrip(id: Long, name: String) {
        val bundle = Bundle()
        bundle.putLong("id", id)
        bundle.putString("name", name)
        Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.thisTripFragment, bundle)
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                presenter.deleteElement(position)
            }

        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(mRecyclerView)
    }


}