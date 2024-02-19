package com.tayadehritik.busapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.tayadehritik.busapp.R
import com.tayadehritik.busapp.home.Home
import com.tayadehritik.busapp.models.Route

class RoutesAdapter(
    private var dataSet: List<Route>,
    private var homeActivity:Home
    )  :
    RecyclerView.Adapter<RoutesAdapter.ViewHolder>(), Filterable {

        private var filteredDataSet: List<Route> = dataSet
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
        viewHolder.busName.text = filteredDataSet[position].bus
        viewHolder.routeName.text = filteredDataSet[position].route

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
                    val filteredRoutes = arrayListOf<Route>()
                    dataSet.filter { (it.bus.contains(searchString) or (it.route.contains(searchString))) }.forEach {
                        filteredRoutes.add(it)
                    }
                    results.count = filteredRoutes.size
                    results.values = filteredRoutes.toList()
                    return results

                }

            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredDataSet = results!!.values as List<Route>
                notifyDataSetChanged()
            }

        }
    }

}
