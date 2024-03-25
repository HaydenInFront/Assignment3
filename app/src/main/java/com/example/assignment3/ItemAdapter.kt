package com.example.assignment3

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/*class ItemAdapter(private val context: Context, private val dataset: List<PromptInput>): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    class ItemViewHolder (private val view: View, private val context: Context): RecyclerView.ViewHolder(view){
        //private val textView: TextView = view.findViewById(R.id.promptTypeName)

        //private val imageView: ImageView = view.findViewById(R.id.promptInputBackground)

        private var currPromptInput: PromptInput? = null;

        fun bind(promptInput: PromptInput) {
            //textView.text = context.resources.getString(promptInput.stringResourceId)
            //imageView.setImageResource(promptInput.image)
        }
    }

    //override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        //val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        //return ItemViewHolder(adapterLayout, context)
    //}

    override fun getItemCount() = dataset.size
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.bind(item)
    }
}
*/