package com.enterprise.appbooks.remotedatasource.retrofit.interceptor

import android.content.Context
import com.enterprise.appbooks.exception.NoInternetConnectionException
import com.enterprise.appbooks.utils.internet.InternetManager
import okhttp3.Interceptor
import okhttp3.Response


class NetworkInterceptor(val context: Context): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val currentRequest = chain.request().newBuilder()

        if(!InternetManager.isInternetAvailable(context = context)){

            throw NoInternetConnectionException(context = context)

        }

        val newRequest = currentRequest.build()
        return chain.proceed(newRequest)
    }
}