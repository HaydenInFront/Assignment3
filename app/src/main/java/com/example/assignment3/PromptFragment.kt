package com.example.assignment3

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.example.assignment3.databinding.FragmentPromptBinding

class PromptFragment : Fragment() {

    private lateinit var binding: FragmentPromptBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //tracks what the current background color is
        var isColorWhite = false

        //adds menu buttons specifically for menu fragment
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    //changes background color
                    R.id.settingsButton -> {
                        //changes background and reassigns isColorWhite
                        isColorWhite = if (isColorWhite) {
                            //color is white, so changes to black and updates isColorWhite
                            view.setBackgroundColor(Color.parseColor("#222222"))
                            !isColorWhite
                        } else {
                            //color is black, so changes to white and updates isColorWhite
                            view.setBackgroundColor(Color.parseColor("#EBEBEB"))
                            !isColorWhite
                        }
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        //email summary edit text
        val promptInput = binding.promptInput

        //email addressed to edit text
        val addressInput = binding.addressInput

        //custom input edit text
        val customInput = binding.customCheckInput

        //creates list of all checkboxes as checkbox bundles
        //adds string correlating to tone of checkbox to use for chatgpt prompt
        val checkList: ArrayList<CheckBoxBundle> = ArrayList()
        checkList+= CheckBoxBundle(binding.businessCheck, "business")
        checkList+= CheckBoxBundle(binding.friendlyCheck, "friendly")
        checkList+= CheckBoxBundle(binding.teacherCheck,"student to teacher")
        checkList+= CheckBoxBundle(binding.bossCheck,"employee to boss")
        checkList+= CheckBoxBundle(binding.coworkerCheck,"coworker to coworker")
        checkList+= CheckBoxBundle(binding.customCheck,"custom")

        //clicked after done filling out prompt info
        val proceedButton = binding.proceedButton
        proceedButton.setOnClickListener {
            //list of checkboxes which are actively checked
            val checkedList: ArrayList<CheckBoxBundle> = ArrayList()

            //goes through all the checkboxes and adds checkboxes that are actively checked
            for (index in 0 until checkList.size) {
                if (checkList[index].checkBox.isChecked){
                    checkedList+=checkList[index]
                }
            }

            //if there is more than one checked box
            if (checkedList.size > 1) {
                //tells user to only keep one box checked
                val text = "Only check one box!"
                val toast = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
                toast.show()
            }
            //if any of the fields are left empty
            else if (checkedList.size == 0 || promptInput.text.isEmpty() || addressInput.text.isEmpty()){
                //lets user know all fields must be filed
                val text = "At least one field is left empty"
                val toast = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
                toast.show()
            }
            //all fields are filled and there is only one checked box
            else {
                //grabs text from the prompt and address edit texts
                val prompt = promptInput.text.toString()
                val address = addressInput.text.toString()
                //checks what checkbox is checked, in case it is custom
                val tone = if (checkedList[0].tone == "custom"){
                    //if custom check box is checked, grabs the string from the custom checkbox edit text
                    customInput.text.toString()
                } else {
                    //grabs string of the given tone for the checkbox
                    checkList[0].tone
                }

                //an array string holding all relevant info
                val responseInput = arrayOf(prompt, tone, address)

                //passes info to draft fragment
                val action = PromptFragmentDirections.actionPromptFragmentToDraftFragment(responseInput)
                view.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPromptBinding.inflate(inflater, container, false)
        return binding.root
    }
}

//holds checkbox and the tone that it is as a string
class CheckBoxBundle(val checkBox: CheckBox, val tone: String);