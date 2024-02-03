package com.enterprise.appbooks.interfaces.bookdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.enterprise.appbooks.model.AppBook

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAppBook(appBook: AppBook)

    @Query("SELECT * FROM book_table ORDER BY rank ASC" )
    fun getAllAppBooks(): List<AppBook>

    @Query("SELECT * FROM book_table WHERE primaryIsbn13 =:primaryIsbn13" )
    fun getAppBook(primaryIsbn13: String): AppBook?

}