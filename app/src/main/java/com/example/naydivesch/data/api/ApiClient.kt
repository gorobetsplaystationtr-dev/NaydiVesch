package com.example.naydivesch.data.api

import com.example.naydivesch.model.Location
import com.example.naydivesch.model.Thing
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val DEFAULT_BASE_URL = "http://10.0.2.2:5000/"

    private var _baseUrl: String = DEFAULT_BASE_URL
    private var _apiKey: String? = null

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateJsonAdapter())
        .setLenient()
        .create()

    private fun buildOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(logging)

        _apiKey?.let { key ->
            builder.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Authorization", "Bearer $key")
                    .header("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
        }

        return builder.build()
    }

    fun updateConfig(baseUrl: String, apiKey: String?) {
        _baseUrl = baseUrl
        _apiKey = apiKey
    }

    fun create(): NaydiVeschApi {
        return Retrofit.Builder()
            .baseUrl(_baseUrl)
            .client(buildOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(NaydiVeschApi::class.java)
    }
}

/**
 * Simple adapter: Date <-> epoch-ms (seconds * 1000), used by the Flask backend.
 */
private class DateJsonAdapter :
    com.google.gson.JsonSerializer<Date>,
    com.google.gson.JsonDeserializer<Date> {

    override fun serialize(src: Date, typeOfSrc: java.lang.reflect.Type, context: com.google.gson.JsonSerializationContext): com.google.gson.JsonElement {
        return com.google.gson.JsonPrimitive(src.time)
    }

    @Throws(com.google.gson.JsonParseException::class)
    override fun deserialize(json: com.google.gson.JsonElement, typeOfT: java.lang.reflect.Type, context: com.google.gson.JsonDeserializationContext): Date {
        return Date(json.asLong)
    }
}