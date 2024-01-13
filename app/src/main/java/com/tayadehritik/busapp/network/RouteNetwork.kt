package com.tayadehritik.busapp.network

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.tayadehritik.busapp.models.Route
import com.tayadehritik.busapp.models.Routes
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import java.util.Base64

class RouteNetwork(userId:String) {
    val userId = userId

    private val client = HttpClient() {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun allRoutes(): List<Route> {
        val allroutes:Routes = client.get("https://www.punebusapp.live/route"){
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization,"Bearer "+ Base64.getEncoder().encodeToString(userId.toByteArray()))
            }
        }.body()

        return allroutes.routes
    }
}