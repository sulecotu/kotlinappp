package com.example.instakotlinapp.Home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.instakotlinapp.Login.LoginActivity
import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.BottomNavigationViewHelper
import com.example.instakotlinapp.utils.HomePagerAdapter
import com.example.instakotlinapp.utils.UniversalImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {


    private val ACTIVITY_NO = 0
    private val TAG = "HomeActivity"

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener:FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()


        initImageLoader()
        setupNavigationView()
        setupHomeViewPager()


    }


    fun setupNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomNavigationViewHelper.setupNavigation(this, bottomNavigationView)

        //activity'in seçili olduğunu belirtmek
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.isChecked = true
    }

    fun setupHomeViewPager() {

        var homePagerAdapter = HomePagerAdapter(supportFragmentManager)
        homePagerAdapter.addFragment(ShareFragment())  //id=0
        homePagerAdapter.addFragment(HomeFragment())    //id=1
        homePagerAdapter.addFragment(SearchFragment())    //id=2

        //activityMainde bulunan viewPager'a oluşturduğumuz adapter'i atadık
        homeViewPager.adapter = homePagerAdapter

        //fragmentler listeye eklediğimiz sırayla açılır ilk home fragmentinin açılmasını istiyoruz.
        homeViewPager.currentItem = 1   //viewPager'da ilk home fragmenti açılır

    }
    private  fun initImageLoader(){
        var universalImageLoader= UniversalImageLoader(this)
        ImageLoader.getInstance().init(universalImageLoader.config)
    }
    private fun setupAuthListener() {
        mAuthListener=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser
                if(user==null){
                    //kullanıcı doğrulandıysa homeaktivitye geç
                    var intent=Intent(this@HomeActivity,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                    startActivity(intent)
                    finish() //homeaktivityden geri tuşuna basınca logine geri döndürüyor

                }else{

                }

            }

        }
    }
    override fun onStart(){
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }
    override fun onStop(){
        super.onStop()
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }

}
