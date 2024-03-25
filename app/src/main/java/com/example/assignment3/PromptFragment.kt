package com.example.assignment3

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView

class PromptFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val promptInput = view.findViewById<EditText>(R.id.promptInput)

        val addressInput = view.findViewById<EditText>(R.id.addressInput)

        val customInput = view.findViewById<EditText>(R.id.customCheckInput)

        val checkList: ArrayList<CheckBoxBundle> = ArrayList()
        checkList += CheckBoxBundle(view.findViewById<CheckBox>(R.id.businessCheck), "business")
        checkList+= CheckBoxBundle(view.findViewById<CheckBox>(R.id.friendlyCheck), "friendly")
        checkList+= CheckBoxBundle(view.findViewById<CheckBox>(R.id.teacherCheck),"student to teacher")
        checkList+= CheckBoxBundle(view.findViewById<CheckBox>(R.id.bossCheck),"employee to boss")
        checkList+= CheckBoxBundle(view.findViewById<CheckBox>(R.id.coworkerCheck),"coworker to coworker")
        checkList+= CheckBoxBundle(view.findViewById<CheckBox>(R.id.customCheck),"custom")

        val proceedButton = view.findViewById<Button>(R.id.proceedButton)
        proceedButton.setOnClickListener {
            val checkedList: ArrayList<CheckBoxBundle> = ArrayList()
            for (index in 0 until checkList.size) {
                if (checkList[index].checkBox.isChecked){
                    checkedList+=checkList[index]
                }
            }

            if (checkedList.size > 1) {
                val text = "Only check one box!"
                val toast = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
                toast.show()
            } else if (checkedList.size == 0 || promptInput.text.isEmpty() || addressInput.text.isEmpty()){
                val text = "At least one field is left empty"
                val toast = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
                toast.show()
            } else {
                val prompt = promptInput.text.toString()
                val address = addressInput.text.toString()
                val tone: String
                if (checkedList[0].tone == "custom"){
                    tone = customInput.text.toString()
                }

                
            }
        }

        /*val myDataset = Datasource().loadPromptInputs()

        val recyclerView = view.findViewById<RecyclerView>(R.id.promptRecycler)
        recyclerView.adapter = ItemAdapter(requireContext(), myDataset)

        recyclerView.setHasFixedSize(true)*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prompt, container, false)
    }
}

class CheckBoxBundle(val checkBox: CheckBox, val tone: String);