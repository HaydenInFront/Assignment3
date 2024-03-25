package com.example.assignment3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MenuFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val myDataset = Datasource().loadPromptInputs()

        val recyclerView = view.findViewById<RecyclerView>(R.id.promptRecycler)
        recyclerView.adapter = ItemAdapter(requireContext(), myDataset)

        recyclerView.setHasFixedSize(true)*/
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }
}