package com.example.cow_cow.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.R

class GamesAdapter(
    private val gamesList: List<String>,
    private val onGameClicked: (String) -> Unit
) : RecyclerView.Adapter<GamesAdapter.GamesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game, parent, false)
        return GamesViewHolder(view)
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        val game = gamesList[position]
        holder.gameTitle.text = game
        holder.itemView.setOnClickListener {
            onGameClicked(game)
        }
    }

    override fun getItemCount(): Int = gamesList.size

    class GamesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameTitle: TextView = view.findViewById(R.id.gameTitle)
    }
}
