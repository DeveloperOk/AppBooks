package com.enterprise.appbooks.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "book_table")
data class AppBook (

    @PrimaryKey
    var primaryIsbn13      : String             = "",

    var bookImage          : String?             = null,
    var bookImageWidth     : Int?                = null,
    var bookImageHeight    : Int?                = null,
    var title              : String?             = null,
    var author             : String?             = null,
    var rank               : Int?                = null,
    var description        : String?             = null,
    var publisher          : String?             = null

)