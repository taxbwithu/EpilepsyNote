package com.mmoson9.epilepsynote.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mmoson.epilepsynote.R
import com.mmoson9.epilepsynote.mvp.interactor.impl.Seizure


class SeizureListAdapter(val seizureList: ArrayList<Seizure>, var onItemButtonClicked: onItemButtonClick) : RecyclerView.Adapter<SeizureListAdapter.ViewHolder>(){
    interface onItemButtonClick{
        fun startEditFragment(seizure : Seizure)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_seizures,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return seizureList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val seizure: Seizure = seizureList[position]

        holder.listDate?.text = seizure.date
        holder.listSize?.text = seizure.size
        holder.listDescription?.text = seizure.description

        holder.cardView.setOnClickListener{
            onItemButtonClicked.startEditFragment(seizure)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val listDate = itemView.findViewById<TextView>(R.id.list_date)
        val listSize = itemView.findViewById<TextView>(R.id.list_size)
        val listDescription = itemView.findViewById<TextView>(R.id.list_description)
        val cardView = itemView.findViewById<CardView>(R.id.cardView)
    }
}