package com.example.instakotlinapp.Profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.instakotlinapp.Home.HomeActivity
import com.example.instakotlinapp.Login.LoginActivity
import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.BottomNavigationViewHelper
import com.example.instakotlinapp.utils.UniversalImageLoader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 2
    private val TAG = "ProfileActivity"

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener:FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()


        setupNavigationView()
        setupToolBar()
        setupProfilePhoto()

    }

    private fun setupProfilePhoto() {
        val imgUrl="i.pinimg.com/236x/48/44/60/4844604305738af2fecf9c42308e2826--heart-care-original-paintings.jpg"
        UniversalImageLoader.setImage(imgUrl,circleProfileImage,progressBar,"https://")
    }

    //ayarlar tuşunu aktifleştirdik
    private fun setupToolBar() {
     imgProfileSettings.setOnClickListener {
     var intent=Intent(this,ProfileSettingsActivity::class.java)
         startActivity(intent)

     }
         profillDüzenleButon.setOnClickListener {

             profilRoot.visibility= View.GONE
             var transaction=supportFragmentManager.beginTransaction()
             transaction.replace(R.id.profileContainer, ProfileEditFragment())
             transaction.addToBackStack("editFragment eklendi")
             transaction.commit()


         }

    }

    fun setupNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomNavigationViewHelper.setupNavigation(this, bottomNavigationView)

        //activity'in seçili olduğunu belirtmek
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.isChecked = true
    }

    override fun onBackPressed() {
        profilRoot.visibility= View.VISIBLE
        super.onBackPressed()
    }
    private fun setupAuthListener() {
        mAuthListener=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser
                if(user==null){
                    //kullanıcı doğrulandıysa homeaktivitye geç
                    var intent=Intent(this@ProfileActivity,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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
