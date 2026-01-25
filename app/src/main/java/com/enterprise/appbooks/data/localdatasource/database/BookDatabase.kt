package com.enterprise.appbooks.data.localdatasource.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.enterprise.appbooks.data.interfaces.bookdatabase.BookDao
import com.enterprise.appbooks.data.interfaces.bookdatabase.FavoriteBookLabelDao
import com.enterprise.appbooks.domain.model.AppBook
import com.enterprise.appbooks.domain.model.FavoriteBookLabel


@Database(entities = [AppBook::class, FavoriteBookLabel::class], version = 1, exportSchema = false)
abstract class BookDatabase: RoomDatabase() {

    abstract fun getBookDao(): BookDao
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