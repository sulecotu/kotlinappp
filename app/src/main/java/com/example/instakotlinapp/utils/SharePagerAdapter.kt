package com.example.instakotlinapp.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SharePagerAdapter(
    fm: FragmentManager,
    tabAdı: ArrayList<String>
) :FragmentPagerAdapter( fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    //fragmentleri tutan bir arraylist yapısı oluşturuyoruz
    private var mFragmentList : ArrayList<Fragment> = ArrayList()
    private var mtabAdı:ArrayList<String> = tabAdı

    ///bu metot çalışınca bize fragmentlist teki ilgili positiondaki elemanı dönücek
    override fun getItem(position: Int): Fragment {
        return mFragmentList.get(position)


    }
 // bu metot çalısınca fragmentin size dönücek
    override fun getCount(): Int {

        return mFragmentList.size

    }
    fun  addFragment(fragment : Fragment){
        mFragmentList.add(fragment)

    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mtabAdı.get(position)
    }

}