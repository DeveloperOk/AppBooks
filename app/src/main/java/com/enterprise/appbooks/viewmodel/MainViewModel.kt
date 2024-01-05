package com.enterprise.appbooks.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.appbooks.R
import com.enterprise.appbooks.constants.nytimes.ImageConstants
import com.enterprise.appbooks.model.AppBook
import com.enterprise.appbooks.model.BigImage
import com.enterprise.appbooks.model.BooksData
import com.enterprise.appbooks.model.FavoriteBookLabel
import com.enterprise.appbooks.model.SmallImage
import com.enterprise.appbooks.repository.AppRepository
import com.squareup.picasso.Picasso
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val appRepository: AppRepository)
    : ViewModel(){

    fun getBooks(context: Context) {

        val mutex = Mutex()

        val callBooksData = appRepository.getBooks()

        callBooksData.enqueue(object : Callback<BooksData?> {
            override fun onResponse(call: Call<BooksData?>, response: Response<BooksData?>) {

                if(response.isSuccessful){

                    val responseBody = response?.body()
                    val books = responseBody?.results?.books

                    if(books != null){

                        val sizeOfbooks = books.size
                        var index = 0
                        for (book in books){

                            if(book.primaryIsbn13 != null) {

                                var appBook = AppBook()
                                var smallImage = SmallImage()
                                var bigImage = BigImage()
                                var favoriteBookLabel = FavoriteBookLabel()

                                appBook.primaryIsbn13 = book.primaryIsbn13!!
                                smallImage.primaryIsbn13 = book.primaryIsbn13!!
                                bigImage.primaryIsbn13 = book.primaryIsbn13!!
                                favoriteBookLabel.primaryIsbn13 = book.primaryIsbn13!!

                                appBook.bookImage = book.bookImage
                                appBook.bookImageWidth = book.bookImageWidth
                                appBook.bookImageHeight = book.bookImageHeight
                                appBook.title = book.title
                                appBook.author = book.author
                                appBook.rank = book.rank
                                appBook.description = book.description
                                appBook.publisher = book.publisher

                                viewModelScope.launch(Dispatchers.IO) {

                                    appRepository.addAppBook(appBook)

                                    val bitmapSmall: Bitmap =
                                        Picasso.get().load(appBook.bookImage).resize(
                                            ImageConstants.SmallImageWidth, 0
                                        ).get()
                                    smallImage.smallImage = bitmapSmall
                                    appRepository.addSmallImage(smallImage)

                                    val bitmapBig: Bitmap =
                                        Picasso.get().load(appBook.bookImage).resize(
                                            ImageConstants.BigImageWidth, 0
                                        ).get()
                                    bigImage.bigImage = bitmapBig
                                    appRepository.addBigImage(bigImage)

                                    favoriteBookLabel.favorite = false
                                    appRepository.insertFavoriteBookLabel(favoriteBookLabel)

                                    mutex.withLock {
                                        index++
                                    }

                                    if (index == sizeOfbooks) {
                                        viewModelScope.launch(Dispatchers.Main) {
                                            Toast.makeText(
                                                context,
                                                R.string.main_activity_books_downloaded_message,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }


                            }

                            }
                        }


                    }

                }else{

                    Log.d("MainViewModel", "Failure: "+ response.message().toString())

                }

            }

            override fun onFailure(call: Call<BooksData?>, t: Throwable) {
                Log.d("MainViewModel", "onFailure: "+ t.message)
            }
        })

    }



}