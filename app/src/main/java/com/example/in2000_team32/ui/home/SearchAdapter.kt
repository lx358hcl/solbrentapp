package com.example.in2000_team32.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.in2000_team32.R
import com.example.in2000_team32.api.ChosenLocation
import com.example.in2000_team32.api.DataSourceRepository
import com.example.in2000_team32.api.NominatimLocationFromString
import com.example.in2000_team32.contextOfApplication

class SearchAdapter(searchQueryElements : MutableList<NominatimLocationFromString>, context : Context?) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    var searchQueryElements : MutableList<NominatimLocationFromString> = searchQueryElements
    var dataSourceRepository = DataSourceRepository(contextOfApplication)
    var context : Context? = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.search_query_element, parent, false)
        val dataSourceRepository = DataSourceRepository(contextOfApplication)
        var chosenLocation : ChosenLocation? = dataSourceRepository.getChosenLocation()
        if(chosenLocation != null) {
            if(chosenLocation.lon == searchQueryElements[viewType].lon?.toDouble() && chosenLocation.lat == searchQueryElements[viewType].lat?.toDouble()) {
                v.setBackgroundColor(v.context.resources.getColor(R.color.light_green))
            }
        }
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        if(searchQueryElements[position].address?.city != null) {
            holder.searchQueryName.text = searchQueryElements[position].address?.city
        }
        else if(searchQueryElements[position].address?.municipality != null){
            holder.searchQueryName.text = searchQueryElements[position].address?.municipality
        }
        else if(searchQueryElements[position].address?.town != null){
            holder.searchQueryName.text = searchQueryElements[position].address?.country
        }
        else{
            holder.searchQueryName.text = "Unknown"
        }
        holder.searchQueryCountry.text = searchQueryElements[position].address?.country
    }

    override fun getItemCount(): Int {
        return searchQueryElements.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var searchQueryName : TextView
        var searchQueryCountry : TextView

        init {
            searchQueryName = itemView.findViewById(R.id.searchQueryCountry)
            searchQueryCountry = itemView.findViewById(R.id.searchQueryName)

            //Set click listener for each item in the recycler view
            itemView.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    //Save selected item in sharedPreferences for later use
                    val chosenLocation = searchQueryElements[position]
                    val chosenLocationObject = ChosenLocation(chosenLocation.address?.city ?: "", chosenLocation.lat?.toDouble() ?: 0.0, chosenLocation.lon?.toDouble() ?: 0.0)

                    dataSourceRepository.setChosenLocation(chosenLocationObject)

                    //Color the entire card green
                    itemView.setBackgroundColor(itemView.context.resources.getColor(R.color.light_green))
                }
            }
        }
    }
}
