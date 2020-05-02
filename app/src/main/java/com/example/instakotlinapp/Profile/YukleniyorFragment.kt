package com.example.instakotlinapp.Profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.example.instakotlinapp.R

/**
 * A simple [Fragment] subclass.
 */
class YukleniyorFragment :DialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_yukleniyor, container, false)
    }

}
