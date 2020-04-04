package com.example.parkinglot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ParqueoListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<ParqueoListAdapter.WordViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var parqueos = emptyList<Parqueo>() // Cached copy of words

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parqueoItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val current = parqueos[position]
        holder.parqueoItemView.text = current.parqueadero
    }

    internal fun setWords(parqueos: List<Parqueo>) {
        this.parqueos = parqueos
        notifyDataSetChanged()
    }

    override fun getItemCount() = parqueos.size
}