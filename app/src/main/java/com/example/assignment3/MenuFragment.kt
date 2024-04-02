package com.example.assignment3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController

//import androidx.recyclerview.widget.RecyclerView

class MenuFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuButton = view.findViewById<Button>(R.id.menuStart)

        menuButton.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_menuFragment_to_promptFragment)
        }
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