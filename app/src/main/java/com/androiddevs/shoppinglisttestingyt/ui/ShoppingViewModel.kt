package com.androiddevs.shoppinglisttestingyt.ui

import android.provider.MediaStore.Images
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.shoppinglisttestingyt.data.local.ShoppingItem
import com.androiddevs.shoppinglisttestingyt.data.remote.ImageResponse
import com.androiddevs.shoppinglisttestingyt.other.Constants.MAX_NAME_LENGTH
import com.androiddevs.shoppinglisttestingyt.other.Constants.MAX_PRICE_LENGTH
import com.androiddevs.shoppinglisttestingyt.other.Event
import com.androiddevs.shoppinglisttestingyt.other.Resource
import com.androiddevs.shoppinglisttestingyt.repositories.ShoppingRepository
import kotlinx.coroutines.launch
import java.lang.Exception


class ShoppingViewModel @ViewModelInject constructor(
    private val repository: ShoppingRepository
): ViewModel() {
    val shoppingItem = repository.observeAllShoppingItems()
    val total = repository.observeTotalPrice()
    private val _image = MutableLiveData<Event<Resource<ImageResponse>>>()
    val image : LiveData<Event<Resource<ImageResponse>>> =_image

    private val _curImageUrl = MutableLiveData<String>()
    val curImageUrl : LiveData<String> =_curImageUrl

    private val _insertShoppingItemStatus = MutableLiveData<Event<Resource<ShoppingItem>>>()
    val insertShoppingItemStatus : LiveData<Event<Resource<ShoppingItem>>> =_insertShoppingItemStatus

    fun setCurImageURL(url : String){
        _curImageUrl.postValue(url)
    }
    fun deleteShoppingItem(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.deleteShoppingItem(shoppingItem = shoppingItem)
    }
    fun insertShoppingItemIntoDB(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.insertShoppingItem(shoppingItem = shoppingItem)
    }
    fun insertShoppingItem(name : String, amountString : String, priceString: String){
        if(name.isEmpty() || amountString.isEmpty() || priceString.isEmpty()){
            _insertShoppingItemStatus.postValue(Event(Resource.error("Field where?",null)))
        }
        if (name.length > MAX_NAME_LENGTH){
            _insertShoppingItemStatus.postValue(Event(Resource.error("Too Long?",null)))

        }
        if (priceString.length > MAX_PRICE_LENGTH){
            _insertShoppingItemStatus.postValue(Event(Resource.error("Too Long?",null)))

        }
        val amount = try {
            amountString.toInt()
        }catch (
            e:Exception
        ){
            _insertShoppingItemStatus.postValue(Event(Resource.error(e.toString(),null)))
            return

        }
        val shoppingItem = ShoppingItem(
            name = name,
            amount = amount,
            price = priceString.toFloat(),
            imageUrl = _curImageUrl.value?:""
        )
        insertShoppingItemIntoDB(shoppingItem)
        setCurImageURL("")
        _insertShoppingItemStatus.postValue(Event(Resource.success(shoppingItem)))


    }
    fun searchForImages(imagesQuery : String){
        if(imagesQuery.isEmpty()){
            return
        }
        _image.value= Event(Resource.loading(null))
        viewModelScope.launch {
            val response = repository.searchForImage(imagesQuery)
            _image.value = Event(response)
        }
    }
}