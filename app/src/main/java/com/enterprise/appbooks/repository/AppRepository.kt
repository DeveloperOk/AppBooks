package com.enterprise.appbooks.repository


import com.enterprise.appbooks.constants.nytimes.NytimesApiConstants
import com.enterprise.appbooks.interfaces.retrofitnytimes.NytimesApi
import com.enterprise.appbooks.localdatasource.database.BookDatabase
import com.enterprise.appbooks.model.AppBook
import com.enterprise.appbooks.model.BigImage
import com.enterprise.appbooks.model.BooksData
import com.enterprise.appbooks.model.FavoriteBookLabel
import com.enterprise.appbooks.model.SmallImage
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class AppRepository @Inject constructor(private val localDataSourceBookDatabase:BookDatabase,
                                        private val remoteDataSourceNytimesApi: NytimesApi ) {


    suspend fun getBooks(): Response<BooksData?> {
        return remoteDataSourceNytimesApi.getBooks(NytimesApiConstants.Date, NytimesApiConstants.List, NytimesApiConstants.ApiKey)
    }

    fun addAppBook(appBook: AppBook) {
        localDataSourceBookDatabase.getBookDao().addAppBook(appBook)
    }

    fun addSmallImage(smallImage: SmallImage) {
        localDataSourceBookDatabase.getSmallImageDao().addSmallImage(smallImage)
    }

    fun addBigImage(bigImage: BigImage) {
        localDataSourceBookDatabase.getBigImageDao().addBigImage(bigImage)
    }


    fun insertFavoriteBookLabel(favoriteBookLabel: FavoriteBookLabel) {
        localDataSourceBookDatabase.getFavoriteBookLabelDao().insertFavoriteBookLabel(favoriteBookLabel)
    }

    fun getAllAppBooks(): List<AppBook> {
        return localDataSourceBookDatabase.getBookDao().getAllAppBooks()
    }

    fun getFavoriteBookLabel(primaryIsbn13: String): FavoriteBookLabel? {
        return localDataSourceBookDatabase.getFavoriteBookLabelDao()
            .getFavoriteBookLabel(primaryIsbn13)
    }

    fun getSmallImage(primaryIsbn13: String): SmallImage?{

        return localDataSourceBookDatabase.getSmallImageDao().getSmallImage(primaryIsbn13)

    }

    fun getBigImage(primaryIsbn13: String): BigImage?{

        return localDataSourceBookDatabase.getBigImageDao().getBigImage(primaryIsbn13)

    }

    fun addFavoriteBookLabel(favoriteBookLabel: FavoriteBookLabel){

        localDataSourceBookDatabase.getFavoriteBookLabelDao().addFavoriteBookLabel(favoriteBookLabel)

    }




}