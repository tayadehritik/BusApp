package com.tayadehritik.busapp.di

import android.app.Application
import androidx.room.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tayadehritik.busapp.data.KMLHandler
import com.tayadehritik.busapp.data.local.AppDatabase
import com.tayadehritik.busapp.data.locationstuff.LocationClient
import com.tayadehritik.busapp.data.remote.GoogleRoadsAPI
import com.tayadehritik.busapp.data.remote.PhoneAuthentication
import com.tayadehritik.busapp.data.remote.UserNetwork
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

    @Provides
    @Singleton
    fun providesDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,"app-database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providesKMLHandler(app:Application):KMLHandler {
        return KMLHandler(app)
    }

    @Provides
    @Singleton
    fun providesLocationClient(app:Application,appDatabase: AppDatabase):LocationClient {
        return LocationClient(app,appDatabase)
    }

}