package com.tayadehritik.busapp.network

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.tayadehritik.busapp.models.User
import com.tayadehritik.busapp.models.Users
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.ClientWebSocketSession
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.converter
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import java.util.Base64

class UserNetwork(userId: String) {

    private val userId = userId
    private lateinit var userUpdateSession:DefaultClientWebSocketSession

    private val client = HttpClient() {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
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




    public suspend fun openUserUpdateConnection()
    {
        userUpdateSession = client.webSocketSession(method = HttpMethod.Get, host="www.punebusapp.live", port=8080, path = "/user/update", block = {
            headers {
                append(HttpHeaders.Authorization, "Bearer " + Base64.getEncoder().encodeToString(userId.toByteArray()))
            }
        })

        userUpdateSession.incoming.consumeEach {
            if(it is Frame.Text)
            {
                println(it.readText())
            }
        }

    }

    public suspend fun openGetUsersTravellingOnBusConnection(): DefaultClientWebSocketSession
    {
        return client.webSocketSession(method = HttpMethod.Get, host="www.punebusapp.live", port=8080, path = "/user/bus", block = {
            headers {
                append(HttpHeaders.Authorization, "Bearer " + Base64.getEncoder().encodeToString(userId.toByteArray()))
            }
        })
    }
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

    public suspend fun updateUser(user:User)
    {

        /*val response = client.post("https://www.punebusapp.live/user/update/$userId") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + Base64.getEncoder().encodeToString(userId.toByteArray()))
            }
            setBody(user)
        }

        Log.d("user updated", response.status.toString())*/
        if(this::userUpdateSession.isInitialized)
        {
            userUpdateSession.sendSerialized(user)
        }


    }


}