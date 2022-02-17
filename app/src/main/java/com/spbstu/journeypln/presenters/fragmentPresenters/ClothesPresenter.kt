package com.spbstu.journeypln.presenters.fragmentPresenters

import android.content.Context
import android.widget.CheckBox
import android.widget.PopupMenu
import com.spbstu.journeypln.R
import com.spbstu.journeypln.adapters.ClothesRecyclerAdapter
import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.data.room.entities.Category
import com.spbstu.journeypln.data.room.entities.Cloth
import com.spbstu.journeypln.views.ClothesView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moxy.MvpPresenter

class ClothesPresenter: MvpPresenter<ClothesView>() {

    private lateinit var context: Context

    private var tripId: Long = 0
    private lateinit var mClothesAdapter: ClothesRecyclerAdapter
    private var clothesList: List<Cloth> = listOf()
    private lateinit var db: TripsDb

    fun setApplicationContext(context: Context, db: TripsDb) {
        this.context = context
        this.db = db
    }

    fun setTripId(id: Long) {
        this.tripId = id
    }

    fun addNewCategory(categoryName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val categoriesDao = db.categoriesDao()
            categoriesDao.insertCategory(Category(name = categoryName, tripId = tripId))
        }
    }

    fun loadCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            val categoriesDao = db.categoriesDao()
            val categories = categoriesDao.getCategories(tripId)

            launch(Dispatchers.Main) {
                viewState.updateCategories(categories)
            }
        }
    }

    fun addNewCloth(clothName: String, category: String, weight: String, count: Int) {

        CoroutineScope(Dispatchers.IO).launch {
            val categoriesDao = db.categoriesDao()
            val categoryId = categoriesDao.getIdByName(category, tripId)

            val clothesDao = db.clothesDao()
            val clothExist = clothesDao.ifClothWithSuchNameExist(tripId, clothName, weight.toDouble())

            if (clothExist == 0) {
                clothesDao.insertCloth(
                    Cloth(
                        name = clothName, isChecked = false, weight = weight.toDouble(),
                        count = count, categoryId = categoryId, tripId = tripId
                    )
                )
                launch(Dispatchers.Main) {
                    updateRecyclerByCategory(category)
                    viewState.hideClothCard()
                }
            }
            else {
                clothesDao.incrementCounter(clothName, count, tripId)
                viewState.showToast("Field \"$clothName\" was incremented by $count.")

                launch(Dispatchers.Main) {
                    updateRecyclerByCategory(category)
                    viewState.hideClothCard()
                }
            }
        }
    }

    fun updateRecyclerByCategory(category: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val categoriesDao = db.categoriesDao()
            val categoryId = categoriesDao.getIdByName(category, tripId)

            val clothesDao = db.clothesDao()

            val clothes: List<Cloth> = if (category != "") {
                clothesDao.getClothesByCategoryId(categoryId, tripId)
            } else {
                clothesDao.getAllClothes(tripId)
            }

            clothesList = clothes
            mClothesAdapter.setData(clothesList)
            launch(Dispatchers.Main) {
                mClothesAdapter.notifyDataSetChanged()
            }
        }
    }

    fun setAdapter() {
        mClothesAdapter = ClothesRecyclerAdapter(tasks = clothesList,
            onClickListener = { compoundButton, cloth ->
                val b = (compoundButton as CheckBox).isChecked

                CoroutineScope(Dispatchers.IO).launch {
                    val clothesDao = db.clothesDao()
                    clothesDao.updateCheckup(b, cloth.uid)
                }
            },
            onClickMoreButtonListener = { view, cloth ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.inflate(R.menu.cloth_menu)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_cloth_edit -> {
                            inEditionClothId = cloth.uid

                            CoroutineScope(Dispatchers.IO).launch {
                                val categoriesDao = db.categoriesDao()
                                val categoryName = categoriesDao.getNameById(cloth.categoryId)

                                launch(Dispatchers.Main) {
                                    viewState.showEditClothCard(
                                        name = cloth.name, category = categoryName,
                                        count = cloth.count, weight = cloth.weight
                                    )
                                }
                            }
                            return@setOnMenuItemClickListener true
                        }
                        R.id.action_cloth_delete -> {

                            CoroutineScope(Dispatchers.IO).launch {
                                val clothesDao = db.clothesDao()
                                clothesDao.deleteCloth(cloth)
                            }
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

    private var inEditionClothId: Long = 0

    fun updateCloth(name: String, category: String, count: Int, weight: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val categoriesDao = db.categoriesDao()
            val categoryId = categoriesDao.getIdByName(category, tripId)

            val clothesDao = db.clothesDao()
            val cloth = clothesDao.getClothById(inEditionClothId)
            clothesDao.updateCloth(
                Cloth(
                    uid = inEditionClothId,
                    name = name,
                    isChecked = cloth.isChecked,
                    weight = weight.toDouble(),
                    count = count,
                    categoryId = categoryId,
                    tripId = cloth.tripId
                )
            )

            launch(Dispatchers.Main) {
                viewState.hideEditClothCard()
            }
        }
    }

    fun deleteCategory(categoryName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val categoriesDao = db.categoriesDao()
            val categoryId = categoriesDao.getIdByName(categoryName, tripId)

            val clothesDao = db.clothesDao()
            clothesDao.deleteByCategory(categoryId)
            categoriesDao.deleteByUid(categoryId)


            launch(Dispatchers.Main) {
                viewState.hideEditClothCard()
            }
        }
    }
}