package com.tayadehritik.busapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.tayadehritik.busapp.R
import com.tayadehritik.busapp.models.Route


class ListAdapter(routes:List<Route>): BaseAdapter() {

    val routes = routes
    val layoutInflater:LayoutInflater? = null
    override fun getCount(): Int {
        return routes.size
    }

    override fun getItem(p0: Int): Any {
        return routes.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val localview = LayoutInflater.from(viewGroup!!.context)
            .inflate(R.layout.row_item, viewGroup, false)
        val busName = localview.findViewById<TextView>(R.id.busName)
        val routeName = localview.findViewById<TextView>(R.id.routeName)
        busName.text = routes[position].bus
        routeName.text = routes[position].route
        return localview
    }


}