package com.enterprise.appbooks.interfaces.bookdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.enterprise.appbooks.model.BigImage


@Dao
interface BigImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addBigImage(bigImage: BigImage)

    @Query("SELECT * FROM big_image_table WHERE primaryIsbn13 =:primaryIsbn13" )
    fun getBigImage(primaryIsbn13: String): BigImage?

}