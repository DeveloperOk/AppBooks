package com.enterprise.appbooks.presentation.viewmodel.booklist


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.appbooks.domain.model.AppBook
import com.enterprise.appbooks.domain.model.FavoriteBookLabel
import com.enterprise.appbooks.data.repository.AppRepository
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

}