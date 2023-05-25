package com.androiddevs.shoppinglisttestingyt.ui


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.androiddevs.shoppinglisttestingyt.LiveDataUtilAndroidTest.getOrAwaitValue
import com.androiddevs.shoppinglisttestingyt.MainCourotinesRules
import com.androiddevs.shoppinglisttestingyt.other.Constants
import com.androiddevs.shoppinglisttestingyt.other.Resource
import com.androiddevs.shoppinglisttestingyt.other.Status
import com.androiddevs.shoppinglisttestingyt.repositories.FakeShoppingRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi


import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ShoppingViewModelTest {
    private lateinit var shoppingViewModel: ShoppingViewModel
    @get:Rule
    var instantTaskExecutorRule  = InstantTaskExecutorRule()


    @get:Rule
    var mainCourotinesRules  = MainCourotinesRules()
    @Before
    fun setUp() {
        shoppingViewModel = ShoppingViewModel(FakeShoppingRepository())
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `insert shopping item  with empty field return error`(){
        shoppingViewModel.insertShoppingItem("name","","3.0")
        val result =shoppingViewModel.insertShoppingItemStatus.getOrAwaitValue()

        assertThat(result.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)

    }

    @Test
    fun `insert shopping item  to Long Name `(){
        val string = buildString {
            for (i in 1..Constants.MAX_NAME_LENGTH +1){
                append("s")
            }
        }
        shoppingViewModel.insertShoppingItem(name = string,"5","3.0")
        val result =shoppingViewModel.insertShoppingItemStatus.getOrAwaitValue()

        assertThat(result.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)

    }

    @Test
    fun `insert shopping item  to Long Price `(){
        val price = buildString {
            for (i in 1..Constants.MAX_PRICE_LENGTH +1){
                append(1)
            }
        }
        shoppingViewModel.insertShoppingItem("name","3",price)
        val result =shoppingViewModel.insertShoppingItemStatus.getOrAwaitValue()

        assertThat(result.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)

    }
    @Test
    fun `insert shopping item  to Long Amount `(){

        shoppingViewModel.insertShoppingItem("name","999999999999999999999","3.0")
        val result =shoppingViewModel.insertShoppingItemStatus.getOrAwaitValue()

        assertThat(result.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)

    }

    @Test
    fun `insert shopping item with valide input `(){

        shoppingViewModel.insertShoppingItem("name","2","3.0")
        val result =shoppingViewModel.insertShoppingItemStatus.getOrAwaitValue()

        assertThat(result.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)

    }

}