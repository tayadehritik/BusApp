package com.tayadehritik.busapp.network

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.tayadehritik.busapp.models.User
import com.tayadehritik.busapp.models.Users
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.Base64

class UserNetwork(userId: String) {

   private val userId = userId

    private val client = HttpClient() {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    public suspend fun addUser()
    {
        val response:HttpResponse = client.post("https://www.punebusapp.live/user") {
            contentType(ContentType.Application.Json)
            setBody(User(userId,false,"100","",0.0,0.0))
        }

        Log.d("userresponse", response.status.toString())


    }

    @RequiresApi(Build.VERSION_CODES.O)
    public suspend fun getUsersTravellingOn(bus:String): List<User>
    {
        val allUsers:Users = client.get("https://www.punebusapp.live/user/$bus") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization,"Bearer "+ Base64.getEncoder().encodeToString(userId.toByteArray()))
            }
        }.body()
        return allUsers.users
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public suspend fun updateUser(user:User)
    {
        val response = client.post("https://www.punebusapp.live/user/update/$userId") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + Base64.getEncoder().encodeToString(userId.toByteArray()))
            }
            setBody(user)
        }

        Log.d("user updated", response.status.toString())
    }


}