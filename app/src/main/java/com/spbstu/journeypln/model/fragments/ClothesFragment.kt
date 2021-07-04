package com.spbstu.journeypln.model.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.spbstu.journeypln.R
import com.spbstu.journeypln.adapters.ClothesRecyclerAdapter
import com.spbstu.journeypln.adapters.TodoRecyclerAdapter
import com.spbstu.journeypln.presenters.fragmentPresenters.ClothesPresenter
import com.spbstu.journeypln.views.ClothesView
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter

class ClothesFragment : MvpAppCompatFragment(), ClothesView {

    @InjectPresenter
    lateinit var presenter: ClothesPresenter

    private lateinit var recycler: RecyclerView
    private lateinit var categoryFAB: ExtendedFloatingActionButton
    private lateinit var clothFAB: ExtendedFloatingActionButton

    private lateinit var categoryCardView: CardView
    private lateinit var clothCardView: CardView
    private lateinit var editClothCardView: CardView

    private lateinit var newCategoryNameTxt: TextInputLayout
    private lateinit var newClothNameTxt: TextInputLayout
    private lateinit var newClothCategoryPick: TextInputLayout
    private lateinit var applyNewClothBtn: Button
    private lateinit var globalCategoryPick: TextInputLayout
    private lateinit var weightPicker: NumberPicker
    private lateinit var clothCountPicker: NumberPicker

    private lateinit var editClothNameTxt: TextInputLayout
    private lateinit var editClothCategoryPick: TextInputLayout
    private lateinit var applyEditedClothBtn: Button
    private lateinit var discardEditedClothBtn: Button
    private lateinit var editWeightPicker: NumberPicker
    private lateinit var editClothCountPicker: NumberPicker

    private lateinit var deleteCategoryBtn: ImageButton

    private lateinit var tripId: String
    private val ar = Array(50) { String.format("%.1f", (it * 0.1)).replace(",", ".") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        val id = bundle?.getString("id")
        if (id != null) {
            this.tripId = id
            presenter.setTripId(this.tripId)
        }
        presenter.setApplicationContext(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_clothes, container, false)
        init(view)

        categoryFAB.setOnClickListener {
            if (categoryCardView.visibility == View.GONE) {
                if (clothCardView.visibility == View.VISIBLE) {
                    hideClothCard()
                }
                showCategoryCard()
            } else if (categoryCardView.visibility == View.VISIBLE) {
                hideCategoryCard()
            }
        }

        clothFAB.setOnClickListener {
            if (clothCardView.visibility == View.GONE) {
                if (categoryCardView.visibility == View.VISIBLE) {
                    hideCategoryCard()
                }
                showClothCard()
            } else if (clothCardView.visibility == View.VISIBLE) {
                hideClothCard()
            }
        }

        newCategoryNameTxt.setEndIconOnClickListener{
            val taskName = newCategoryNameTxt.editText?.text.toString()
            if (taskName.isNotEmpty()) {
                presenter.addNewCategory(categoryName = taskName)
                newCategoryNameTxt.editText?.setText("")
                hideCategoryCard()
            } else {
                newCategoryNameTxt.error = "Заполните поле!"
            }
        }

        newCategoryNameTxt.editText?.doAfterTextChanged {
            if (newCategoryNameTxt.editText.toString().isNotEmpty()) {
                newCategoryNameTxt.error = null
            }
        }

        presenter.loadCategories()

        applyNewClothBtn.setOnClickListener {
            val tempStr = newClothNameTxt.editText?.text.toString()
            val catStr = newClothCategoryPick.editText?.text.toString()
            val weightStr = ar[weightPicker.value]
            val count = clothCountPicker.value
            if (tempStr.isNotEmpty()) {
                presenter.addNewCloth(clothName = tempStr, category = catStr, weight = weightStr, count = count)
                newClothNameTxt.editText?.setText("")
                hideClothCard()
            } else {
                newClothNameTxt.editText?.error = ""
            }
        }

        presenter.setAdapter()
        presenter.updateRecyclerByCategory("")

        (globalCategoryPick.editText as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                println(parent?.getItemAtPosition(position) as String)
                val text = parent.getItemAtPosition(position) as String
                presenter.updateRecyclerByCategory(text)
            }

        applyEditedClothBtn.setOnClickListener {
            val name = editClothNameTxt.editText?.text.toString()
            val category = editClothCategoryPick.editText?.text.toString()
            val weight = ar[editWeightPicker.value]
            val count = editClothCountPicker.value
            presenter.updateCloth(
                    name, category, count, weight
            )
        }

        discardEditedClothBtn.setOnClickListener {
            hideEditClothCard()
        }

        deleteCategoryBtn.setOnClickListener {
            val categoryName = globalCategoryPick.editText?.text.toString()

            if (categoryName.isNotEmpty()) {
                val builder = AlertDialog.Builder(requireParentFragment().requireContext())
                builder.setTitle("Удалить категорию?")
                        .setMessage("Все вещи из данной категории будут также удалалены!")
                        .setCancelable(true)
                        .setPositiveButton("Да") { _, _ ->
                            presenter.deleteCategory(categoryName = categoryName)
                        }
                        .setNegativeButton("Нет") { dialog, _ ->
                            dialog.cancel()
                        }

                builder.create().show()
            }
        }

        return view
    }

    private fun hideCategoryCard() {
        categoryCardView.visibility = View.GONE
        categoryFAB.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_add_24)
    }

    private fun hideClothCard() {
        clothCardView.visibility = View.GONE
        clothFAB.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_add_24)
    }

    private fun showCategoryCard() {
        categoryCardView.visibility = View.VISIBLE
        categoryFAB.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_keyboard_arrow_down_24)
    }

    private fun showClothCard() {
        clothCardView.visibility = View.VISIBLE
        clothFAB.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_keyboard_arrow_down_24)
    }

    override fun hideEditClothCard() {
        editClothCardView.visibility = View.GONE
        clothFAB.isClickable = true
        categoryFAB.isClickable = true
    }

    override fun showEditClothCard(name: String, category: String, count: Int, weight: Double) {
        hideClothCard()
        hideCategoryCard()

        editClothNameTxt.editText?.setText(name)
        (editClothCategoryPick.editText as? AutoCompleteTextView)?.setText(category, false)
        editClothCountPicker.value = count
        editWeightPicker.value = ar.indexOf(weight.toString())

        editClothCardView.visibility = View.VISIBLE
        clothFAB.isClickable = false
        categoryFAB.isClickable = false
    }

    private fun init(view: View) {
        recycler = view.findViewById(R.id.recycler)
        recycler.hasFixedSize()
        recycler.layoutManager = LinearLayoutManager(requireContext())
        categoryFAB = view.findViewById(R.id.category_fab)
        categoryFAB.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_add_24)
        clothFAB = view.findViewById(R.id.cloth_fab)
        clothFAB.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_add_24)
        categoryCardView = view.findViewById(R.id.category_cardview)
        clothCardView = view.findViewById(R.id.cloth_cardview)
        newCategoryNameTxt = view.findViewById(R.id.new_category_name)
        newClothNameTxt = view.findViewById(R.id.new_cloth_name)
        newClothCategoryPick = view.findViewById(R.id.categories_set)
        applyNewClothBtn = view.findViewById(R.id.apply_new_cloth_btn)
        globalCategoryPick = view.findViewById(R.id.category)
        weightPicker = view.findViewById(R.id.weight_picker)
        weightPicker.minValue = 0
        weightPicker.maxValue = 49
        weightPicker.displayedValues = ar
        clothCountPicker = view.findViewById(R.id.cloth_count)
        clothCountPicker.minValue = 1
        clothCountPicker.maxValue = 50

        editClothCardView = view.findViewById(R.id.edit_cloth_cardview)
        editClothNameTxt = view.findViewById(R.id.edit_cloth_name)
        editClothCategoryPick = view.findViewById(R.id.edit_categories_set)
        applyEditedClothBtn = view.findViewById(R.id.edit_apply_new_cloth_btn)
        discardEditedClothBtn = view.findViewById(R.id.edit_revert_new_cloth_btn)

        editWeightPicker = view.findViewById(R.id.edit_weight_picker)
        editWeightPicker.minValue = 0
        editWeightPicker.maxValue = 49
        editWeightPicker.displayedValues = ar
        editClothCountPicker = view.findViewById(R.id.edit_cloth_count)
        editClothCountPicker.minValue = 1
        editClothCountPicker.maxValue = 50

        deleteCategoryBtn = view.findViewById(R.id.delete_category)
    }

    override fun updateCategories(list: ArrayList<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)
        (newClothCategoryPick.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        (globalCategoryPick.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        (editClothCategoryPick.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    override fun setUpAdapter(adapter: ClothesRecyclerAdapter) {
        recycler.adapter = adapter
    }

    override fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}