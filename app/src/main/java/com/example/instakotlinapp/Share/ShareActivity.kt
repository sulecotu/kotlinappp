package com.example.instakotlinapp.Share

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.SharePagerAdapter
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 3
    private val TAG = "ShareActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)


        setupShareViewPager()


    }

    // activitye fragmentleri eklememizi sağlar
    private fun setupShareViewPager() {

        var tabAdı = ArrayList<String>()
        tabAdı.add("Galeri")
        var sharePagerAdapter = SharePagerAdapter(supportFragmentManager,tabAdı)
        sharePagerAdapter.addFragment(ShareGalleryFragment())

        shareViewPager.adapter=sharePagerAdapter
        shareTabLayout.setupWithViewPager(shareViewPager)
    }

}


