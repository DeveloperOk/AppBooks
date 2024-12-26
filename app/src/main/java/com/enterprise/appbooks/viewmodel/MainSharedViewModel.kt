package com.enterprise.appbooks.viewmodel

import androidx.lifecycle.ViewModel
import com.enterprise.appbooks.model.AppBook
import com.enterprise.appbooks.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainSharedViewModel @Inject constructor(private val appRepository: AppRepository)
    : ViewModel(){

    var selectedAppBook: AppBook? = null

}