package com.enterprise.appbooks.viewmodel.booklist


import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.appbooks.model.AppBook
import com.enterprise.appbooks.model.FavoriteBookLabel
import com.enterprise.appbooks.model.SmallImage
import com.enterprise.appbooks.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BookListScreenViewModel @Inject constructor(private val appRepository: AppRepository)
    : ViewModel(){

    var mutableStateAllAppBooks = mutableStateListOf<AppBook>()

    fun getAllAppBooks() {

        viewModelScope.launch(Dispatchers.IO) {

            val listOfAppBooks = appRepository.getAllAppBooks()

            viewModelScope.launch(Dispatchers.Main) {

                var tempMutableStateAllAppBooks = mutableStateListOf<AppBook>()
                tempMutableStateAllAppBooks.addAll(listOfAppBooks)

                mutableStateAllAppBooks = tempMutableStateAllAppBooks

            }

        }

    }

    fun getFavoriteBookLabel(primaryIsbn13: String): FavoriteBookLabel? {
        return appRepository.getFavoriteBookLabel(primaryIsbn13)
    }

    fun getSmallImage(primaryIsbn13: String): SmallImage?{

        return appRepository.getSmallImage(primaryIsbn13)

    }

}