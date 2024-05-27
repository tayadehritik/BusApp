package com.tayadehritik.busapp.data.remote

import android.util.Log
import com.tayadehritik.busapp.data.User
import com.tayadehritik.busapp.data.Users
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.sendSerialized
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
            setBody(User(userId,false,0.0,0.0,"null","null","null","null"))
        }

        Log.d("userresponse", response.status.toString())

    }



    public suspend fun openGetUsersTravellingOnBusConnection(): DefaultClientWebSocketSession
    {
        return client.webSocketSession(method = HttpMethod.Get, host="www.punebusapp.live", port=8080, path = "/user/bus", block = {
            headers {
                append(HttpHeaders.Authorization, "Bearer " + Base64.getEncoder().encodeToString(userId.toByteArray()))
            }
        })
    }

    public suspend fun stopGetUsersTravellingOnBusConnection()
    {
        println("stopping connection")
        client.close()
    }
    public suspend fun getUsersTravellingOn(bus:String): List<User>
    {
        val allUsers: Users = client.get("https://www.punebusapp.live/user/$bus") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization,"Bearer "+ Base64.getEncoder().encodeToString(userId.toByteArray()))
            }
        }.body()
        return allUsers.users
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

    public suspend fun stopUserUpdateConnection()
    {
        client.close()
    }
    public suspend fun updateUser(user: User)
    {

        if(this::userUpdateSession.isInitialized)
        {
            userUpdateSession.sendSerialized(user)
        }

    }

    suspend fun getUser():User {

        val user:User = client.post("https://www.punebusapp.live/user/$userId") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization,"Bearer "+ Base64.getEncoder().encodeToString(userId.toByteArray()))
            }
        }.body()

        return user
    }

}