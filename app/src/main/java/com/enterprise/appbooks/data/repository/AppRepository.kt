package com.enterprise.appbooks.data.repository


import com.enterprise.appbooks.domain.constants.nytimes.NytimesApiConstants
import com.enterprise.appbooks.data.interfaces.retrofitnytimes.NytimesApi
import com.enterprise.appbooks.data.localdatasource.database.BookDatabase
import com.enterprise.appbooks.domain.model.AppBook
import com.enterprise.appbooks.domain.model.BigImage
import com.enterprise.appbooks.domain.model.BooksData
import com.enterprise.appbooks.domain.model.FavoriteBookLabel
import com.enterprise.appbooks.domain.model.SmallImage
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