package com.example.clocks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val cities = listOf("London", "Moscow", "Beijing", "Chicago")
    private val description = listOf("Here you can find the oldest subway in the world",
        "A record for the number of baked pancakes has been set in Moscow",
        "The former Imperial Palace in Beijing, or the \"Forbidden City\"," +
                " is the largest palace complex in the world.",
        "Chicago pizza is the most famous dish in the city, although it's hard to eat more than one hefty slice.")
    private val utc = listOf(0, 3, 8, -6)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CustomCitiesAdapter(cities, utc, description)
    }
}