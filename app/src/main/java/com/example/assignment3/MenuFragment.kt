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
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.example.assignment3.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {

    private lateinit var binding: FragmentMenuBinding

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

        //menu button
        val menuButton = binding.menuStart

        //menu button navigates to next fragment
        menuButton.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_menuFragment_to_promptFragment)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }
}