package com.enterprise.appbooks.localdatasource.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.enterprise.appbooks.interfaces.bookdatabase.BigImageDao
import com.enterprise.appbooks.interfaces.bookdatabase.BookDao
import com.enterprise.appbooks.interfaces.bookdatabase.FavoriteBookLabelDao
import com.enterprise.appbooks.interfaces.bookdatabase.SmallImageDao
import com.enterprise.appbooks.model.AppBook
import com.enterprise.appbooks.model.BigImage
import com.enterprise.appbooks.model.FavoriteBookLabel
import com.enterprise.appbooks.model.SmallImage
import com.enterprise.appbooks.utils.database.AppConverter


@Database(entities = [AppBook::class, SmallImage::class, BigImage::class, FavoriteBookLabel::class], version = 1, exportSchema = false)
@TypeConverters(AppConverter::class)
abstract class BookDatabase: RoomDatabase() {

    abstract fun getBookDao(): BookDao
    abstract fun getSmallImageDao(): SmallImageDao
    abstract fun getBigImageDao(): BigImageDao
    abstract fun getFavoriteBookLabelDao(): FavoriteBookLabelDao

    companion object {

        private val databaseName = "book_database"

        @Volatile
        private var INSTANCE: BookDatabase? = null

        fun getDatabase(context: Context): BookDatabase{
            val tempInstance = INSTANCE

            if(tempInstance != null){
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    databaseName)
                    .build()

                INSTANCE = instance

                return instance
            }

        }

    }


}