package com.tayadehritik.busapp.data

import android.app.Application
import android.util.Xml
import com.google.android.gms.maps.model.LatLng
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import java.io.StringWriter
import java.io.Writer
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class KMLHandler(private val appContext:Application) {

    val xmlSerializer = Xml.newSerializer()
    fun createFile(coords: List<LatLng>, name:String) {
        val file = File(appContext.filesDir, "$name.kml")
        file.writeText(createKmlContent(coords))
    }

    fun createKmlContent(coords: List<LatLng>): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n")
        sb.append("<Document>\n")
        sb.append("\t<name>Coordinates</name>\n")
        sb.append("\t<Placemark>\n")
        sb.append("\t\t<LineString>\n")
        sb.append("\t\t\t<coordinates>\n")
        coords.forEach { location ->
            sb.append("\t\t\t\t${location.longitude},${location.latitude} ")
        }
        sb.append("\n\t\t\t</coordinates>\n")
        sb.append("\t\t</LineString>\n")
        sb.append("\t</Placemark>\n")
        sb.append("</Document>\n")
        sb.append("</kml>\n")

        return sb.toString()
    }



}