package com.example.data.remote

import com.example.data.model.AccountEntryDto
import com.example.data.model.PushResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface FirebaseRtdbApi {

    @GET("mas_accounts.json")
    suspend fun getAllEntries(): Response<Map<String, AccountEntryDto>?>

    @POST("mas_accounts.json")
    suspend fun addEntry(@Body entry: AccountEntryDto): Response<PushResponse>

    @PUT("mas_accounts/{id}.json")
    suspend fun updateEntry(
        @Path("id") id: String,
        @Body entry: AccountEntryDto
    ): Response<AccountEntryDto>

    @DELETE("mas_accounts/{id}.json")
    suspend fun deleteEntry(@Path("id") id: String): Response<Unit>
}

object FirebaseClient {
    const val DEFAULT_BASE_URL = "https://mas-accounts-default-rtdb.firebaseio.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    fun createService(baseUrl: String = DEFAULT_BASE_URL): FirebaseRtdbApi {
        val sanitizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(sanitizedUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FirebaseRtdbApi::class.java)
    }
}
