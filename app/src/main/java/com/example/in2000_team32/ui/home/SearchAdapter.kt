package com.example.in2000_team32.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.in2000_team32.R
import com.example.in2000_team32.api.NominatimLocationFromString

class SearchAdapter(searchQueryElements : MutableList<NominatimLocationFromString>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    //Listen med alpacaparties
    var searchQueryElements : MutableList<NominatimLocationFromString> = searchQueryElements

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.search_query_element, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.searchQueryName.text = searchQueryElements[position].address?.city
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
        }
    }
}
