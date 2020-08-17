package com.example.sicreditest.Controller.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sicreditest.Model.EventItem
import com.example.sicreditest.R
import kotlinx.android.synthetic.main.item_view.view.*
import java.text.SimpleDateFormat


class MyAdapter(val eventList: List<EventItem>, val clickListener: (EventItem) -> Unit) : RecyclerView.Adapter<MyAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(eventList[position], clickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindItems(event: EventItem, clickListener: (EventItem) -> Unit){
            val textView_Title = itemView.textView_Title
            val imageView_Image = itemView.imageView_EventImage
            val format = SimpleDateFormat("dd/MM/yyyy  HH:mm ")

            textView_Title.text = event.title

            Glide.with(itemView.context)
                .load(event.image)
                .error(R.drawable.img_error_loading)
                .fitCenter()
                .into(imageView_Image);

            itemView.setOnClickListener { clickListener(event) }
        }

    }


}
