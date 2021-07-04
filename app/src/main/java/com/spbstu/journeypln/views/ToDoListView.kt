package com.spbstu.journeypln.views

import com.spbstu.journeypln.adapters.TodoRecyclerAdapter
import moxy.MvpView
import moxy.viewstate.strategy.SingleStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface ToDoListView: MvpView {

    @StateStrategyType(value = SingleStateStrategy::class)
    fun showToast(text: String)

    @StateStrategyType(value = SingleStateStrategy::class)
    fun setUpAdapter(adapter: TodoRecyclerAdapter)

    @StateStrategyType(value = SingleStateStrategy::class)
    fun showInputError()

    @StateStrategyType(value = SingleStateStrategy::class)
    fun hideInputErrorAndClear()

}