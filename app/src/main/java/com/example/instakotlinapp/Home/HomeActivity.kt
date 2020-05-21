package com.example.instakotlinapp.Home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.instakotlinapp.Login.LoginActivity
import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.BottomNavigationViewHelper
import com.example.instakotlinapp.utils.HomePagerAdapter
import com.example.instakotlinapp.utils.UniversalImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.activity_home.homeViewPager as homeViewPager1

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

        setupHomeViewPager()


    }

    override fun onResume() {
        super.onResume()
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


        homeViewPager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            @SuppressLint("MissingSuperCall")
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if(position==0) {
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

                    kameraIzniIste()
                }
                if(position==1){
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                }
                if(position==2){
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                }

            }

        })

    }

    private fun kameraIzniIste() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener{
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {

                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                    //kullanıcı kamera isteğini kabul etmezse tekrar gönder
                    token!!.continuePermissionRequest()

                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                }

            })
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
