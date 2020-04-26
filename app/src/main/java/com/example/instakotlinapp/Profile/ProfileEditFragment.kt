package com.example.instakotlinapp.Profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.UniversalImageLoader
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.circleProfileImage

/**
 * A simple [Fragment] subclass.
 */
class ProfileEditFragment : Fragment() {

  lateinit   var circleProfileImageFragment:CircleImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view =inflater.inflate(R.layout.fragment_profile_edit, container, false)

        circleProfileImageFragment=view.findViewById(R.id.circleProfileImage)

        setupProfilePicture()

        view.imgClose.setOnClickListener {

            activity?.onBackPressed()


        }


        return view


    }


    private fun setupProfilePicture() {
        var imgURL ="w7.pngwing.com/pngs/118/687/png-transparent-smiley-emoticon-emoji-happiness-emoticon-smiley-emoji-love-computer-icons-text-messaging-thumbnail.png"
        UniversalImageLoader.setImage(imgURL,circleProfileImageFragment,null,ilkKİsim = "https://")
    }

}
