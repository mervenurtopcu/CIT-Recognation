package com.example.citrecognation

import android.content.Intent
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController


import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_zero.*


class HomeActivity : AppCompatActivity() {

    private lateinit var tempFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        toolbar.title="Character Recognation"
        setSupportActionBar(toolbar)


        supportFragmentManager.beginTransaction()
        .add(R.id.fragment_tutucu,FragmentZero()).commit()


    bottom_nav.setOnNavigationItemSelectedListener { menuItem->
        if(menuItem.itemId == R.id.action_zero){
            Toast.makeText(applicationContext,"Character Recognation",Toast.LENGTH_SHORT).show()
            tempFragment=FragmentZero()
            toolbar.title="Character Recognation"
        }
        if(menuItem.itemId == R.id.action_one){
            Toast.makeText(applicationContext,"Text Recognation From The Image",Toast.LENGTH_SHORT).show()
            tempFragment=FragmentOne()
            toolbar.title="Text Recognation From The Image"
        }

        if(menuItem.itemId == R.id.action_two){
            Toast.makeText(applicationContext,"Info",Toast.LENGTH_SHORT).show()
            tempFragment=FragmentTwo()
            toolbar.title="Info"
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_tutucu,tempFragment).commit()
        true
    }
    }
   

}