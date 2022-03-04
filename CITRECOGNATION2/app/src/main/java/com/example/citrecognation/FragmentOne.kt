package com.example.citrecognation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_one_deneme.view.*
import kotlinx.android.synthetic.main.fragment_zero.view.*
import kotlinx.android.synthetic.main.fragment_zero.view.button

class FragmentOne:Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_one_deneme,container,false)

        rootView.button3.setOnClickListener{

            val intent = Intent(getActivity(),ImageToText::class.java)
            startActivity(intent)
        }
        return rootView

    }
}