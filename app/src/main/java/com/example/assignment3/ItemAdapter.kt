package com.example.assignment3

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(private val context: Context, private val dataset: List<PromptOutput>): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    class ItemViewHolder (private val view: View, private val context: Context): RecyclerView.ViewHolder(view){
        //private val textView: TextView = view.findViewById(R.id.promptTypeName)
        private val draftTitle: TextView = view.findViewById(R.id.draftTitle)
        private val draftText: TextView = view.findViewById(R.id.draftText)

        private val copyButton: TextView = view.findViewById(R.id.copyButton)
        //private val imageView: ImageView = view.findViewById(R.id.promptInputBackground)

        private var currPromptOutput: PromptOutput? = null;

        fun bind(promptOutput: PromptOutput) {
            draftTitle.text = context.resources.getString(promptOutput.titleResourceId)
            draftText.text = promptOutput.output
            copyButton.setOnClickListener {
                val textToCopy = draftText.text.toString()
                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.Item(textToCopy)
                val clipData = ClipData("draft", arrayOf("text/plain"), clip)
                clipboardManager.setPrimaryClip(clipData)
            }
            //textView.text = context.resources.getString(promptInput.stringResourceId)
            //imageView.setImageResource(promptInput.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(adapterLayout, context)
    }

    override fun getItemCount() = dataset.size
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.bind(item)
    }
}