package com.spbstu.journeypln.views

import com.spbstu.journeypln.adapters.ClothesRecyclerAdapter
import moxy.MvpView
import moxy.viewstate.strategy.SingleStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface ClothesView: MvpView {

    @StateStrategyType(value = SingleStateStrategy::class)
    fun updateCategories(list: ArrayList<String>)

    @StateStrategyType(value = SingleStateStrategy::class)
    fun setUpAdapter(adapter: ClothesRecyclerAdapter)

    @StateStrategyType(value = SingleStateStrategy::class)
    fun showToast(text: String)

    @StateStrategyType(value = SingleStateStrategy::class)
    fun showEditClothCard(name: String, category: String, count: Int, weight: Double)

    @StateStrategyType(value = SingleStateStrategy::class)
    fun hideEditClothCard()
}