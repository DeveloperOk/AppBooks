package com.enterprise.appbooks.presentation.viewmodel.bookdetail

import androidx.lifecycle.ViewModel
import com.enterprise.appbooks.domain.model.FavoriteBookLabel
import com.enterprise.appbooks.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class BookDetailScreenViewModel @Inject constructor(private val appRepository: AppRepository)
    : ViewModel(){

    init {
        println("BookDetailScreenViewModel initialized")
    }

    override fun onCleared() {
        super.onCleared()
        println("BookDetailScreenViewModel cleared")
    }

    fun getFavoriteBookLabel(primaryIsbn13: String): FavoriteBookLabel? {
        return appRepository.getFavoriteBookLabel(primaryIsbn13)
    }

    fun addFavoriteBookLabel(favoriteBookLabel: FavoriteBookLabel){

        appRepository.addFavoriteBookLabel(favoriteBookLabel)

    }

}