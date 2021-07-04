package com.spbstu.journeypln.presenters.fragmentPresenters

import android.content.Context
import android.widget.CheckBox
import android.widget.PopupMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.spbstu.journeypln.R
import com.spbstu.journeypln.adapters.ClothesRecyclerAdapter
import com.spbstu.journeypln.adapters.TodoRecyclerAdapter
import com.spbstu.journeypln.data.firebase.pojo.Cloth
import com.spbstu.journeypln.data.firebase.pojo.TodoTask
import com.spbstu.journeypln.views.ClothesView
import moxy.MvpPresenter
import moxy.MvpView

class ClothesPresenter: MvpPresenter<ClothesView>() {

    private lateinit var context: Context
    private val database = Firebase.database
    private val signInAccount = FirebaseAuth.getInstance()

    private lateinit var tripId: String
    private lateinit var mClothesAdapter: ClothesRecyclerAdapter
    private var clothesList: ArrayList<Cloth> = arrayListOf()

    fun setApplicationContext(context: Context) {
        this.context = context
    }

    fun setTripId(id: String) {
        this.tripId = id
    }

    fun addNewCategory(categoryName: String) {
        val databaseReference = database.getReference("users/${signInAccount.uid}/$tripId/categories")
        databaseReference.push().setValue(categoryName)
    }

    fun loadCategories() {
        val databaseReference = database.getReference("users/${signInAccount.uid}/$tripId/categories")
        databaseReference.orderByValue().addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val categoriesList = arrayListOf<String>()

                        if (snapshot.exists()) {
                            for (element in snapshot.children) {
                                val newCat = element.getValue(String::class.java)
                                if (newCat != null) {
                                    categoriesList.add(newCat)
                                }
                            }

                            viewState.updateCategories(categoriesList)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        viewState.showToast(error.toString())
                    }

                }
        )
    }

    fun addNewCloth(clothName: String, category: String, weight: String, count: Int) {
        val databaseReference = database.getReference("users/${signInAccount.uid}/$tripId/clothes")

        val snap = databaseReference.addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val dataList = arrayListOf<Cloth>()

                                if (snapshot.exists()){
                                    val tempList = snapshot.children.filter {
                                        val tmp = it.getValue(Cloth::class.java)
                                        tmp?.name == clothName && tmp.category == category
                                                && tmp.weight == weight.toDouble()
                                    }

                                    for (element in tempList) {
                                        val cloth = element.getValue(Cloth::class.java)
                                        if (cloth != null) {
                                            cloth.setKey(element.key.toString())
                                            dataList.add(cloth)
                                        }
                                    }

                                    if (dataList.isEmpty()) {
                                        val newCloth = Cloth(
                                                name = clothName, isChecked = false, weight = weight.toDouble(),
                                                count = count, category = category
                                        )
                                        databaseReference.push().setValue(newCloth)
                                    } else {
                                        databaseReference.child(dataList[0].getKey())
                                                .updateChildren(mapOf("count" to dataList[0].count + count))
                                    }
                                } else {
                                    val newCloth = Cloth(
                                            name = clothName, isChecked = false, weight = weight.toDouble(),
                                            count = count, category = category
                                    )
                                    databaseReference.push().setValue(newCloth)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                viewState.showToast(error.toString())
                            }

                        }
                )
    }

    fun updateRecyclerByCategory(category: String) {
        val databaseReference = database.getReference("users/${signInAccount.uid}/$tripId/clothes")

        databaseReference.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newList = arrayListOf<Cloth>()
                    if (snapshot.exists()) {
                        if (category != "") {
                            val tempList = snapshot.children.filter {
                                val element = it.getValue(Cloth::class.java)
                                element?.category == category
                            }.toList()

                            for (element in tempList) {
                                val upload = element.getValue(Cloth::class.java)
                                if (upload != null) {
                                    upload.setKey(element.key.toString())
                                    newList.add(upload)
                                }
                            }
                        } else {
                            for (element in snapshot.children) {
                                val upload = element.getValue(Cloth::class.java)
                                if (upload != null) {
                                    upload.setKey(element.key.toString())
                                    newList.add(upload)
                                }
                            }
                        }
                    }
                    clothesList = newList
                    mClothesAdapter.setData(clothesList)
                    println(clothesList.size)
                    mClothesAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    viewState.showToast(error.message)
                }
            }
        )
    }

    fun setAdapter() {
        val databaseReference = database.getReference("users/${signInAccount.uid}/$tripId/clothes")

        mClothesAdapter = ClothesRecyclerAdapter(tasks = clothesList,
            onClickListener = { compoundButton, cloth ->
                val b = (compoundButton as CheckBox).isChecked
                databaseReference.child(cloth.getKey()).updateChildren(mapOf("isChecked" to b))
            },
            onClickMoreButtonListener = { view, cloth ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.inflate(R.menu.cloth_menu)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_cloth_edit -> {
                            inEditionClothKey = cloth.getKey()
                            viewState.showEditClothCard(
                                    name = cloth.name, category = cloth.category,
                                    count = cloth.count, weight = cloth.weight
                            )
                            return@setOnMenuItemClickListener true
                        }
                        R.id.action_cloth_delete -> {
                            databaseReference.child(cloth.getKey()).removeValue()
                            return@setOnMenuItemClickListener true
                        }
                        else ->
                            return@setOnMenuItemClickListener false
                    }
                }
                popupMenu.show()
            })
        viewState.setUpAdapter(mClothesAdapter)
    }

    private var inEditionClothKey: String = ""

    fun updateCloth(name: String, category: String, count: Int, weight: String) {
        val databaseReference = database.getReference("users/${signInAccount.uid}/$tripId/clothes/$inEditionClothKey")

        val cloth = Cloth(
                name = name, category = category, count = count,
                weight = weight.toDouble()
        )
        databaseReference.updateChildren(cloth.toMap()).addOnSuccessListener {
            viewState.hideEditClothCard()
        }
    }

    fun deleteCategory(categoryName: String) {
        val databaseReferenceClothes = database.getReference("users/${signInAccount.uid}/$tripId/clothes")
        databaseReferenceClothes.orderByChild("category").equalTo(categoryName)
                .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {

                                    snapshot.children.forEach {
                                        it.ref.removeValue()
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                viewState.showToast(error.toString())
                            }

                        }
                )

        val databaseReferenceCategory = database.getReference("users/${signInAccount.uid}/$tripId/categories")
        databaseReferenceCategory.orderByValue().equalTo(categoryName)
                .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {

                                    snapshot.children.forEach {
                                        it.ref.removeValue()
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                viewState.showToast(error.toString())
                            }

                        }
                )
    }
}