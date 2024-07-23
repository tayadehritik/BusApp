package com.tayadehritik.busapp.di

import com.tayadehritik.busapp.data.remote.GoogleRoadsAPI
import com.tayadehritik.busapp.data.remote.PhoneAuthentication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesPhoneAuthentication(): PhoneAuthentication {
        return PhoneAuthentication()
    }

    @Provides
    @Singleton
    fun providesGoogleRoadsAPI(): GoogleRoadsAPI {
        return Retrofit.Builder()
            .baseUrl("https://roads.googleapis.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleRoadsAPI::class.java)
    }
}