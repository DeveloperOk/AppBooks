package com.enterprise.appbooks.model.screens

import com.enterprise.appbooks.model.AppBook
import kotlinx.serialization.Serializable


@Serializable
data class BookDetailScreenData(val appBookSerialized: String)