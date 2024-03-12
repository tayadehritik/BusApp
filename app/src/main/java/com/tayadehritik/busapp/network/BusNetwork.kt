package com.tayadehritik.busapp.network

import com.tayadehritik.busapp.models.Bus
import com.tayadehritik.busapp.models.BusShape
import com.tayadehritik.busapp.models.Buses
import com.tayadehritik.busapp.models.Shape
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import java.util.Base64

class BusNetwork(userId:String) {
    val userId = userId

    private val client = HttpClient() {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }


    suspend fun allBuses(): List<Bus> {
        val allbuses: Buses = client.get("https://www.punebusapp.live/buses"){
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization,"Bearer "+ Base64.getEncoder().encodeToString(userId.toByteArray()))
            }
        }.body()

        return allbuses.buses
    }

    suspend fun getBusShape(shape_id:String): List<Shape> {
        val busShape: BusShape = client.get("https://www.punebusapp.live/busShape/$shape_id") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization,"Bearer "+ Base64.getEncoder().encodeToString(userId.toByteArray()))
            }
        }.body()

        return busShape.busShape
    }
}