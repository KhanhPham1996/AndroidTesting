package com.androiddevs.shoppinglisttestingyt.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.androiddevs.shoppinglisttestingyt.LiveDataUtilAndroidTest.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class ShoppingDaoTest {

    @get:Rule
    var instantTaskExecutorRule  = InstantTaskExecutorRule()
    private lateinit var database: ShoppingItemDatabase
    private lateinit var dao : ShoppingDao
    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShoppingItemDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.shoppingDao()
    }
    @After
    fun teardown(){
        database.close()
    }

    @Test
    fun insertShoppingItem() = runBlockingTest {
        val shoppingItem = ShoppingItem(
            name = "Name",
            amount = 1, price = 1f, imageUrl = "url",id=1
        )
        dao.insertShoppingItem(shoppingItem)
        val allShoppingItem = dao.observeAllShoppingItems().getOrAwaitValue()

        assertThat(allShoppingItem.contains(shoppingItem))
    }
    @Test
    fun deleteShoppingItem() = runBlockingTest {
        val shoppingItem = ShoppingItem(
            name = "Name",
            amount = 1, price = 1f, imageUrl = "url",id=1
        )
        dao.insertShoppingItem(shoppingItem)
        dao.deleteShoppingItem(shoppingItem)
        val allShoppingItem = dao.observeAllShoppingItems().getOrAwaitValue()

        assertThat(allShoppingItem.isEmpty())
    }

    @Test
    fun observeTotalPrice() = runBlockingTest {
        val shoppingItem = ShoppingItem(
            name = "Name",
            amount = 1, price = 1f, imageUrl = "url",id=1
        )
        val shoppingItem2 = ShoppingItem(
            name = "Name",
            amount = 1, price = 2f, imageUrl = "url",id=2
        )
        val shoppingItem3 = ShoppingItem(
            name = "Name",
            amount = 1, price = 3f, imageUrl = "url",id=3
        )
        dao.insertShoppingItem(shoppingItem)
        dao.insertShoppingItem(shoppingItem2)
        dao.insertShoppingItem(shoppingItem3)

        val totalPrice = dao.observeTotalPrice().getOrAwaitValue()

        assertThat(totalPrice).isEqualTo(1*1f+1*2f+1*3f)
    }
}