package com.example.realtimedatabase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(var itemArray: ArrayList<ItemData>,
    var reyclerInterface: RecyclerInterface) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){
    class ViewHolder (private var view: View):RecyclerView.ViewHolder(view){
        var className : TextView = view.findViewById(R.id.tvClass)
        var rollNumber : TextView = view.findViewById(R.id.tvRoll)
        var subject : TextView = view.findViewById(R.id.tvSub)
        var cardView : CardView = view.findViewById(R.id.cvCad)
        var deleteBtn : Button = view.findViewById(R.id.deleteBtn)
        var updateBtn : Button = view.findViewById(R.id.updateBtn)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.dynamic_recycler_view,parent,false))
    }

    override fun getItemCount(): Int {
        return itemArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.className.setText(itemArray[position].className)
        holder.rollNumber.setText(itemArray[position].rollNumber.toString())
       holder.subject.setText(itemArray[position].subject.toString())

        holder.deleteBtn.setOnClickListener {
            reyclerInterface.deleteBtn(position)

        }

        holder.updateBtn.setOnClickListener {
            reyclerInterface.updateBtn(position)
        }





    }

}
