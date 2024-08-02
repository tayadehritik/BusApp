package com.tayadehritik.busapp.data

import android.app.Application
import android.util.Xml
import com.tayadehritik.busapp.R
import java.io.File
import java.io.StringWriter

class KMLHandler(private val appContext:Application) {

    val xmlSerializer = Xml.newSerializer()
    fun createFile() {
        val file = File(appContext.filesDir, "test.kml")
        val writer = StringWriter()
        xmlSerializer.setOutput(writer)


    }

}