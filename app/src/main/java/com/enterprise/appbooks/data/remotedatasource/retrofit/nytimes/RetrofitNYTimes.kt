package com.enterprise.appbooks.data.remotedatasource.retrofit.nytimes

import android.content.Context
import com.enterprise.appbooks.domain.constants.nytimes.NytimesApiConstants
import com.enterprise.appbooks.data.interfaces.retrofitnytimes.NytimesApi
import com.enterprise.appbooks.data.remotedatasource.retrofit.interceptor.NetworkInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNYTimes {

    companion object {


        private fun getOkHttpClient(context: Context): OkHttpClient {

            val client = OkHttpClient.Builder()
                .addInterceptor(NetworkInterceptor(context = context))
                .build()

            return client

        }


        private fun getRetrofitNewYorkTimes(context: Context): Retrofit {

            val client = getOkHttpClient(context = context)

            val retrofit =
                Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NytimesApiConstants.BaseUrl)
                    .client(client)
                    .build()

            return retrofit

        }


        fun getRetrofitNewYorkTimesApi(context: Context): NytimesApi{

            val retrofitNewYorkTimes = getRetrofitNewYorkTimes(context = context)

            val nYTimesApi = retrofitNewYorkTimes.create(NytimesApi::class.java)

            return nYTimesApi

        }


    }

}