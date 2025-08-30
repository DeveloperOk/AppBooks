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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BookListScreenViewModel @Inject constructor(private val appRepository: AppRepository)
    : ViewModel(){


    var mutableStateAllAppBooks = MutableStateFlow(arrayListOf<AppBook>())


    fun getAllAppBooks() {

        viewModelScope.launch(Dispatchers.IO) {

            val listOfAppBooks = appRepository.getAllAppBooks()

            mutableStateAllAppBooks.update { currentValue -> listOfAppBooks as ArrayList }

        }

    }

    fun getFavoriteBookLabel(primaryIsbn13: String): FavoriteBookLabel? {
        return appRepository.getFavoriteBookLabel(primaryIsbn13)
    }

    fun getSmallImage(primaryIsbn13: String): SmallImage?{

        return appRepository.getSmallImage(primaryIsbn13)

    }

}