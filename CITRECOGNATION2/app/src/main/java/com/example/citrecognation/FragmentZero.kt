package com.example.citrecognation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import androidx.fragment.app.Fragment
import com.divyanshu.draw.widget.DrawView
import kotlinx.android.synthetic.main.fragment_zero.view.*


class FragmentZero: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

       val rootView = inflater.inflate(R.layout.fragment_zero_deneme,container,false)

       rootView.button.setOnClickListener{

           val intent = Intent(getActivity(),CharacterRecognationPart::class.java)
           startActivity(intent)
       }
        return rootView
    }


}