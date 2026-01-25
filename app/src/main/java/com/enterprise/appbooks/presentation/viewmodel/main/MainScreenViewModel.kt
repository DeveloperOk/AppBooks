package com.enterprise.appbooks.presentation.viewmodel.main

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.appbooks.R
import com.enterprise.appbooks.domain.constants.nytimes.ImageConstants
import com.enterprise.appbooks.data.remotedatasource.retrofit.exception.NoInternetConnectionException
import com.enterprise.appbooks.domain.model.AppBook
import com.enterprise.appbooks.domain.model.Book
import com.enterprise.appbooks.domain.model.FavoriteBookLabel
import com.enterprise.appbooks.data.repository.AppRepository
import com.enterprise.appbooks.presentation.utils.ImageFileUtil
import com.squareup.picasso.Picasso
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject


@HiltViewModel
class MainScreenViewModel @Inject constructor(private val appRepository: AppRepository)
    : ViewModel(){

    private val TAG = "MainViewModel"
    private val onFailureText = "onFailure: "

    var mainScreenShowProgressIndicator = MutableStateFlow(false)
    var mainScreenProgressBarFactor = MutableStateFlow<Float>(0.0f)
    var mainScreenProgressBarPercent = MutableStateFlow<Int>(0)

    var isMainScreenButtonsEnabled = MutableStateFlow(true)

    val isNoInternetConnectionDialogVisible = MutableStateFlow(false)

    fun getBooks(context: Context) {

        viewModelScope.launch(Dispatchers.Main) {

            mainScreenProgressBarFactor.update{ currentValue -> 0.0f }
            mainScreenProgressBarPercent.update{ currentValue -> 0 }
            isMainScreenButtonsEnabled.update{ currentValue -> false }
            mainScreenShowProgressIndicator.update{ currentValue ->true }

            viewModelScope.launch(Dispatchers.IO) {

                try {

                    val booksDataResponse = appRepository.getBooks()

                    if (booksDataResponse.isSuccessful) {

                        val booksData = booksDataResponse.body()

                        val books = booksData?.results?.books
                        addBooksToDatabase(books, context)

                    } else {

                        val failureMessageToLog = "Response Error!"
                        handleError(failureMessageToLog, context, false)

                    }

                } catch (exception: Exception) {

                    val failureMessageToLog = exception.message.toString()

                    if (exception is NoInternetConnectionException) {
                        handleError(failureMessageToLog, context, true)
                    } else {
                        handleError(failureMessageToLog, context, false)
                    }

                }

            }
        }
    }

    private fun handleError(failureMessageToLog: String, context: Context, showNoInternetConnectionPopup: Boolean) {

        Log.d(TAG, onFailureText + failureMessageToLog)

        viewModelScope.launch(Dispatchers.Main) {

            if(!showNoInternetConnectionPopup){
                Toast.makeText(
                    context,
                    R.string.retrofit_error_message,
                    Toast.LENGTH_LONG
                ).show()
            }

            mainScreenShowProgressIndicator.update{ currentValue -> false }
            isMainScreenButtonsEnabled.update{ currentValue ->  true }
            isNoInternetConnectionDialogVisible.update{ currentValue -> showNoInternetConnectionPopup }


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
                        var favoriteBookLabel = FavoriteBookLabel()

                        appBook.primaryIsbn13 = book.primaryIsbn13!!
                        favoriteBookLabel.primaryIsbn13 = book.primaryIsbn13!!

                        appBook.bookImage = book.bookImage
                        appBook.bookImageWidth = book.bookImageWidth
                        appBook.bookImageHeight = book.bookImageHeight
                        appBook.title = book.title
                        appBook.author = book.author
                        appBook.rank = book.rank
                        appBook.description = book.description
                        appBook.publisher = book.publisher


                        val imageBitmap: Bitmap = Picasso.get().load(appBook.bookImage).get()

                        val imageFileName = ImageConstants.imageFileNamePrefix + appBook.primaryIsbn13 + ImageConstants.imageFileExtension

                        val imageFile = ImageFileUtil.saveBitmapToInternalFolder(context = context,
                            bitmap = imageBitmap,
                            folderName = ImageConstants.folderNameOfImages,
                            fileName = imageFileName)

                        appBook.imageLocalPath = imageFile.absolutePath

                        appRepository.addAppBook(appBook)

                        favoriteBookLabel.favorite = false
                        appRepository.insertFavoriteBookLabel(favoriteBookLabel)

                        mutex.withLock {
                            index++
                            viewModelScope.launch(Dispatchers.Main) {
                                Log.d(
                                    TAG,
                                    "Factor: " + mainScreenProgressBarFactor.value.toString()
                                )
                                mainScreenProgressBarFactor.update{ currentValue ->
                                    index.toFloat() / sizeOfbooks.toFloat() }
                                mainScreenProgressBarPercent.update{ currentValue ->
                                    (mainScreenProgressBarFactor.value * 100).toInt() }

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

            mainScreenProgressBarFactor.update{ currentValue -> 1.0f }
            mainScreenProgressBarPercent.update{ currentValue -> 100 }

            Toast.makeText(
                context,
                messageTextId,
                Toast.LENGTH_LONG
            ).show()

            mainScreenShowProgressIndicator.update{ currentValue -> false }
            isMainScreenButtonsEnabled.update{ currentValue -> true }

        }
    }




}