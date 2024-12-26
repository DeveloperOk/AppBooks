package com.enterprise.appbooks.viewmodel.bookdetail

import androidx.lifecycle.ViewModel
import com.enterprise.appbooks.model.BigImage
import com.enterprise.appbooks.model.FavoriteBookLabel
import com.enterprise.appbooks.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class BookDetailScreenViewModel @Inject constructor(private val appRepository: AppRepository)
    : ViewModel(){

    fun getFavoriteBookLabel(primaryIsbn13: String): FavoriteBookLabel? {
        return appRepository.getFavoriteBookLabel(primaryIsbn13)
    }

    fun getBigImage(primaryIsbn13: String): BigImage?{

        return appRepository.getBigImage(primaryIsbn13)

    }

    fun addFavoriteBookLabel(favoriteBookLabel: FavoriteBookLabel){

        appRepository.addFavoriteBookLabel(favoriteBookLabel)

    }

}