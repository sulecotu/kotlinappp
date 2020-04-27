package com.example.instakotlinapp.Profile

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

import com.example.instakotlinapp.R
import com.google.firebase.auth.FirebaseAuth

/**
 * A simple [Fragment] subclass.
 */
class SignOutFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var alert=AlertDialog.Builder(this!!.activity!!)
            .setTitle("Çıkış Yap")

            .setPositiveButton("Evet",object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    FirebaseAuth.getInstance().signOut()
                    activity!!.finish()
                }


            })
            .setNegativeButton("Hayır",object:DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                   dismiss()

                }


            })
            .create()

        return alert
    }



}
