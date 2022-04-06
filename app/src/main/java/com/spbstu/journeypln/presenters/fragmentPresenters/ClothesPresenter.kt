package com.spbstu.journeypln.presenters.fragmentPresenters

import android.view.MenuItem
import android.widget.CheckBox
import android.widget.PopupMenu
import com.spbstu.journeypln.R
import com.spbstu.journeypln.adapters.ClothesRecyclerAdapter
import com.spbstu.journeypln.data.room.dao.CategoriesDao
import com.spbstu.journeypln.data.room.dao.ClothesDao
import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.data.room.entities.Category
import com.spbstu.journeypln.data.room.entities.Cloth
import com.spbstu.journeypln.views.ClothesView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import moxy.MvpPresenter

class ClothesPresenter: MvpPresenter<ClothesView>() {
    private var tripId: Long = 0
    private lateinit var mClothesAdapter: ClothesRecyclerAdapter
    private var clothesList: List<Cloth> = listOf()
    private lateinit var db: TripsDb
    private lateinit var categoriesDao: CategoriesDao
    private lateinit var clothesDao: ClothesDao

    fun initDb(db: TripsDb) {
        this.db = db
        this.categoriesDao = db.categoriesDao()
        this.clothesDao = db.clothesDao()
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }

    fun setTripId(id: Long) {
        this.tripId = id
    }

    fun addNewCategory(categoryName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val isCategoryExist = categoriesDao.isCategoryExist(categoryName)

            if (isCategoryExist == 0) {
                categoriesDao.insertCategory(Category(name = categoryName, tripId = tripId))
            }
            else {
                launch(Dispatchers.Main) {
                    viewState.showToast("Such category already exist!")
                }
            }
        }
    }

    fun loadCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            val categories = categoriesDao.getCategories(tripId)

            launch(Dispatchers.Main) {
                viewState.updateCategories(categories)
            }
        }
    }

    fun addNewCloth(clothName: String, category: String, weight: String, count: Int) {

        runBlocking {
            val job = addNewClothToDB(clothName, category, weight, count)
            job.join()

            updateRecyclerByCategory(category)
            viewState.hideClothCard()
        }
    }

    private suspend fun addNewClothToDB(clothName: String, category: String, weight: String, count: Int) = CoroutineScope(Dispatchers.IO).launch {
        val categoryId = categoriesDao.getIdByName(category, tripId)
        val clothExist = clothesDao.ifClothWithSuchNameExist(tripId, clothName, weight.toDouble())

        if (clothExist == 0) {
            clothesDao.insertCloth(
                Cloth(
                    name = clothName, isChecked = false, weight = weight.toDouble(),
                    count = count, categoryId = categoryId, tripId = tripId
                )
            )
        }
        else {
            clothesDao.incrementCounter(clothName, count, tripId)
            viewState.showToast("Field \"$clothName\" was incremented by $count.")
        }
    }

    fun updateRecyclerByCategory(category: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val categoryId = categoriesDao.getIdByName(category, tripId)

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
                    clothesDao.updateCheckup(b, cloth.uid)
                }
            },
            onClickMoreButtonListener = { view, cloth ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.inflate(R.menu.cloth_menu)
                popupMenu.setOnMenuItemClickListener {
                    menuActionsSetup(it, cloth)
                }
                popupMenu.show()
            })
        viewState.setUpAdapter(mClothesAdapter)
    }

    private fun menuActionsSetup(item: MenuItem, cloth: Cloth): Boolean {
        when (item.itemId) {
            R.id.action_cloth_edit -> {
                inEditionClothId = cloth.uid

                CoroutineScope(Dispatchers.IO).launch {
                    val categoryName = categoriesDao.getNameById(cloth.categoryId)

                    launch(Dispatchers.Main) {
                        viewState.showEditClothCard(
                            name = cloth.name, category = categoryName,
                            count = cloth.count, weight = cloth.weight
                        )
                    }
                }
                return true
            }
            R.id.action_cloth_delete -> {

                CoroutineScope(Dispatchers.IO).launch {
                    clothesDao.deleteCloth(cloth)
                }
                return true
            }
            else ->
                return false
        }
    }

    private var inEditionClothId: Long = 0

    fun updateCloth(name: String, category: String, count: Int, weight: String) {
        CoroutineScope(Dispatchers.IO).launch {
            updateClothForDB(name, category, count, weight)

            launch(Dispatchers.Main) {
                viewState.hideEditClothCard()
            }
        }
    }

    private fun updateClothForDB(name: String, category: String, count: Int, weight: String) {
        val categoryId = categoriesDao.getIdByName(category, tripId)

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
    }

    fun deleteCategory(categoryName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val categoryId = categoriesDao.getIdByName(categoryName, tripId)

            clothesDao.deleteByCategory(categoryId)
            categoriesDao.deleteByUid(categoryId)


            launch(Dispatchers.Main) {
                viewState.hideEditClothCard()
            }
        }
    }
}