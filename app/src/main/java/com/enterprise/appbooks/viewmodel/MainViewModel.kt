package com.enterprise.appbooks.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.appbooks.R
import com.enterprise.appbooks.constants.nytimes.ImageConstants
import com.enterprise.appbooks.model.AppBook
import com.enterprise.appbooks.model.BigImage
import com.enterprise.appbooks.model.Book
import com.enterprise.appbooks.model.BooksData
import com.enterprise.appbooks.model.FavoriteBookLabel
import com.enterprise.appbooks.model.SmallImage
import com.enterprise.appbooks.repository.AppRepository
import com.squareup.picasso.Picasso
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    private val TAG = "MainViewModel"
    private val failureText = "Failure: "
    private val onFailureText = "onFailure: "

    var mainScreenShowProgressIndicator = mutableStateOf(false)
    var mainScreenProgressBarFactor: MutableState<Float> = mutableStateOf(0.0f)
    var mainScreenProgressBarPercent: MutableState<Int> = mutableStateOf(0)

    var isMainScreenButtonsEnabled = mutableStateOf(true)

    var mutableStateAllAppBooks = mutableStateListOf<AppBook>()

    var selectedAppBook: AppBook? = null

    fun getBooks(context: Context) {

        viewModelScope.launch(Dispatchers.IO) {

            try {

                val booksData = appRepository.getBooks()

                val books = booksData?.results?.books
                addBooksToDatabase(books, context)

            }catch (exception: Exception){

                handleError(exception, context)

            }

        }

    }

    private fun handleError(exception: Exception, context: Context) {

        Log.d(TAG, onFailureText + exception.message)

        viewModelScope.launch(Dispatchers.Main) {

            Toast.makeText(
                context,
                R.string.retrofit_error_message,
                Toast.LENGTH_LONG
            ).show()

            mainScreenShowProgressIndicator.value = false
            isMainScreenButtonsEnabled.value = true

        }

    }

    private fun addBooksToDatabase(
        books: ArrayList<Book>?,
        context: Context
    ) {
        if (books.isNullOrEmpty()) {

            handleEndOfProcess(R.string.main_screen_no_books_message, context)

        } else {

            val mutex = Mutex()

            val sizeOfbooks = books.size
            var index = 0

            viewModelScope.launch(Dispatchers.IO) {
                for (book in books) {

                    if (book.primaryIsbn13 != null) {

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

                        appRepository.addAppBook(appBook)

                        val bitmapBig: Bitmap =
                            Picasso.get().load(appBook.bookImage).resize(
                                ImageConstants.BigImageWidth, 0
                            ).get()
                        bigImage.bigImage = bitmapBig
                        appRepository.addBigImage(bigImage)

                        val bitmapSmall: Bitmap = Bitmap.createScaledBitmap(
                            bitmapBig,
                            ImageConstants.SmallImageWidth,
                            bitmapBig.height * ImageConstants.SmallImageWidth / bitmapBig.width,
                            false
                        )
                        smallImage.smallImage = bitmapSmall
                        appRepository.addSmallImage(smallImage)

                        favoriteBookLabel.favorite = false
                        appRepository.insertFavoriteBookLabel(favoriteBookLabel)

                        mutex.withLock {
                            index++
                            viewModelScope.launch(Dispatchers.Main) {
                                Log.d(
                                    TAG,
                                    "Factor: " + mainScreenProgressBarFactor.value.toString()
                                )
                                mainScreenProgressBarFactor.value =
                                    index.toFloat() / sizeOfbooks.toFloat()
                                mainScreenProgressBarPercent.value =
                                    (mainScreenProgressBarFactor.value * 100).toInt()

                            }

                            if (index == sizeOfbooks) {

                                handleEndOfProcess(R.string.main_activity_books_downloaded_message, context)

                            }

                        }
                    }
                }
            }


        }
    }

    private fun handleEndOfProcess(messageTextId: Int, context: Context) {
        viewModelScope.launch(Dispatchers.Main) {

            mainScreenProgressBarFactor.value = 1.0f
            mainScreenProgressBarPercent.value = 100

            Toast.makeText(
                context,
                messageTextId,
                Toast.LENGTH_LONG
            ).show()

            mainScreenShowProgressIndicator.value = false
            isMainScreenButtonsEnabled.value = true

        }
    }

    fun getAllAppBooks() {

        viewModelScope.launch(Dispatchers.IO) {

            val listOfAppBooks = appRepository.getAllAppBooks()

            viewModelScope.launch(Dispatchers.Main) {

                var tempMutableStateAllAppBooks = mutableStateListOf<AppBook>()
                tempMutableStateAllAppBooks.addAll(listOfAppBooks)

                mutableStateAllAppBooks = tempMutableStateAllAppBooks

            }

        }

    }

    fun getFavoriteBookLabel(primaryIsbn13: String): FavoriteBookLabel? {
        return appRepository.getFavoriteBookLabel(primaryIsbn13)
    }

    fun getSmallImage(primaryIsbn13: String): SmallImage?{

        return appRepository.getSmallImage(primaryIsbn13)

    }

    fun getBigImage(primaryIsbn13: String): BigImage?{

        return appRepository.getBigImage(primaryIsbn13)

    }

    fun addFavoriteBookLabel(favoriteBookLabel: FavoriteBookLabel){

        appRepository.addFavoriteBookLabel(favoriteBookLabel)

    }

}