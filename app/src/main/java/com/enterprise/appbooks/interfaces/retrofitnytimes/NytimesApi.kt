package com.enterprise.appbooks.interfaces.retrofitnytimes

import com.enterprise.appbooks.model.BooksData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NytimesApi {

    @GET("lists/{date}/{list}.json")
    suspend fun getBooks(@Path("date") date: String,
                 @Path("list") list: String,
                 @Query("api-key") apiKey: String): Response<BooksData?>

}