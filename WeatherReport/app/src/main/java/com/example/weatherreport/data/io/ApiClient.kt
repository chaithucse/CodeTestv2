package com.example.weatherreport.data.io

import com.example.weatherreport.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Rest API client for network requests
 */
class ApiClient {

    companion object {
        private var instance: Retrofit? = null

        fun getRetrofitInstance(): Retrofit {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            //Creating Auth Interceptor to add api_key query in front of all the requests.
            val authInterceptor = Interceptor { chain ->
                val newUrl = chain.request().url()
                    .newBuilder()
                    .addQueryParameter("appid", "5ad7218f2e11df834b0eaf3a33a39d2a")
                    .build()

                val newRequest = chain.request()
                    .newBuilder()
                    .url(newUrl)
                    .build()

                chain.proceed(newRequest)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(interceptor).build()

            instance = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return instance!!
        }
    }
}