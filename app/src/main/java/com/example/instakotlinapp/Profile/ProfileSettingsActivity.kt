package com.example.instakotlinapp.Profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.instakotlinapp.Login.LoginActivity
import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.BottomNavigationViewHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettingsActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 2
    private val TAG = "ProfileActivity"
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        setupAuthListener()
        mRef= FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        mUser=mAuth.currentUser!!
        setupNavigationView()
        setupToolbar()
        fragmentNavigations()

        mRef.child("kullanıcılar").child(mUser.uid).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                switchGizli.isChecked=p0!!.child("gizli_profil").getValue().toString().toBoolean()
            }

        })

        containerGizli.setOnClickListener {
            if(switchGizli.isChecked){
                switchGizli.isChecked=false
                mRef.child("kullanıcılar").child(mUser.uid).child("gizli_profil").setValue(false)
            }
            else{
                switchGizli.isChecked=true
                mRef.child("kullanıcılar").child(mUser.uid).child("gizli_profil").setValue(true)

            }
        }

    }
    private fun fragmentNavigations() {
        profilDüzenle.setOnClickListener {
            profileSettingsRoot.visibility= View.GONE
            var transaction=supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profileSettingsContainer, ProfileEditFragment())
            transaction.addToBackStack("editFragment eklendi")
            transaction.commit()


        }
        cıkısYap.setOnClickListener {
          var dialog=SignOutFragment()
            dialog.show(supportFragmentManager,"cıkısYapDialoGöster")


        }



    }

    private fun setupToolbar(){
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        profileSettingsRoot.visibility=View.VISIBLE
        super.onBackPressed()
    }

    fun setupNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomNavigationViewHelper.setupNavigation(this, bottomNavigationView)

        //activity'in seçili olduğunu belirtmek
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.isChecked = true
    }
    private fun setupAuthListener() {

        mAuthListener=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser
                if(user==null){
                    //kullanıcı doğrulandıysa homeaktivitye geç
                    var intent=
                        Intent(this@ProfileSettingsActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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
