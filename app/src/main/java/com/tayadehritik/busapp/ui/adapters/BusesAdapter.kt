package com.tayadehritik.busapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tayadehritik.busapp.R
import com.tayadehritik.busapp.ui.home.Home
import com.tayadehritik.busapp.model.Bus

class BusesAdapter(
    private var dataSet: List<Bus>,
    private var homeActivity: Home
    )  :
    RecyclerView.Adapter<BusesAdapter.ViewHolder>(), Filterable {

        private var filteredDataSet: List<Bus> = dataSet
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val busName: TextView
        val routeName: TextView
        val item: LinearLayout
        init {
            // Define click listener for the ViewHolder's View
            busName = view.findViewById(R.id.busName)
            routeName = view.findViewById(R.id.routeName)
            item = view.findViewById(R.id.item)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.busName.text = filteredDataSet[position].route_short_name
        viewHolder.routeName.text = filteredDataSet[position].trip_headsign

        viewHolder.item.setOnClickListener{
            homeActivity.updateLocationOfBusOnMap(filteredDataSet[position])
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = filteredDataSet.size
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val searchString = constraint?.toString() ?: ""
                if(searchString == "")
                {
                    results.count = dataSet.size
                    results.values = dataSet
                    return results
                }
                else
                {
                    val filteredRoutes = arrayListOf<Bus>()
                    dataSet.filter { (it.route_short_name.contains(searchString) or (it.trip_headsign.contains(searchString))) }.forEach {
                        filteredRoutes.add(it)
                    }
                    results.count = filteredRoutes.size
                    results.values = filteredRoutes.toList()
                    return results

                }

            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredDataSet = results!!.values as List<Bus>
                notifyDataSetChanged()
            }

        }
    }

}
