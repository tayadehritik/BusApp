package com.tayadehritik.busapp.network

import android.util.Log
import com.tayadehritik.busapp.models.User
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class User(userId: String) {

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

        //set headers
        //set authorization
        //set json

    }
}