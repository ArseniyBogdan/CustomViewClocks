package com.example.clocks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.clock.CustomClock

class CustomCitiesAdapter(private val cityNames: List<String>,
                          private val offsets: List<Int>,
                          private val interestingFacts: List<String>):
    RecyclerView.Adapter<CustomCitiesAdapter.MyViewHolder>(){

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val clock: CustomClock = itemView.findViewById(R.id.clock)
        val title: TextView = itemView.findViewById(R.id.title)
        val timeOffset: TextView = itemView.findViewById(R.id.utc)
        val intrestingFact: TextView = itemView.findViewById(R.id.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.city_element, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = cityNames[position]
        holder.timeOffset.text =
            if(offsets[position] >= 0) "UTC+${offsets[position]}" else "UTC${offsets[position]}"
        holder.intrestingFact.text = interestingFacts[position]
        holder.clock.setTimeZone(offsets[position])
    }

    override fun getItemCount(): Int = cityNames.size

}