package com.example.instakotlinapp.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instakotlinapp.Login.LoginActivity
import com.example.instakotlinapp.Model.Posts
import com.example.instakotlinapp.Model.UserPosts
import com.example.instakotlinapp.Model.Users
import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.BottomNavigationViewHelper
import com.example.instakotlinapp.utils.HomeFragmentRecylerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {
    lateinit var fragmentView: View
    private val ACTIVITY_NO = 0

    lateinit var tumGonderiler:ArrayList<UserPosts>

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragmentView = inflater.inflate(R.layout.fragment_home, container, false)

       setupAuthListener()
        mAuth=FirebaseAuth.getInstance()
        mUser=mAuth.currentUser!!
        mRef= FirebaseDatabase.getInstance().reference
        tumGonderiler=ArrayList<UserPosts>()

        kullaniciPostlariniGetir(mUser.uid!!)


        return fragmentView
    }






    private fun kullaniciPostlariniGetir(kullaniciID:String) {

        mRef.child("kullanıcılar").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var userID=kullaniciID
                var kullaniciAdi=p0!!.getValue(Users::class.java)!!.kullanici_adi
               var kullaniciFotoURL=p0!!.getValue(Users::class.java)!!.kulaniciDetaylari!!.profilResmi

                mRef.run {
                    child("posts").child(kullaniciID).addListenerForSingleValueEvent(object :ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0!!.hasChildren()) {
                                Log.e("HATA", "ÇOCUK VAR")
                                for (ds in p0!!.children) {
                                    var eklenecekUserPosts=UserPosts()
                                    eklenecekUserPosts.userID=userID
                                    eklenecekUserPosts.userName=kullaniciAdi
                                    eklenecekUserPosts.userPhotoURL=kullaniciFotoURL
                                    eklenecekUserPosts.postID =
                                        ds.getValue(Posts::class.java)!!.post_id
                                    eklenecekUserPosts.postURL =
                                        ds.getValue(Posts::class.java)!!.photo_url
                                    eklenecekUserPosts.postAciklama =
                                        ds.getValue(Posts::class.java)!!.acıklama

                                    tumGonderiler.add(eklenecekUserPosts)

                                }
                            }

                            setupRecyclerView()


                        }
                    })
                }


            }

        })

    }

    private fun setupRecyclerView() {

        var recyclerView=fragmentView.recyclerView
        var recyclerAdapter=HomeFragmentRecylerAdapter(this.activity!!,tumGonderiler)
        recyclerView.adapter=recyclerAdapter
        recyclerView.layoutManager=LinearLayoutManager(this.activity!!,LinearLayoutManager.VERTICAL,true)

    }


    fun setupNavigationView() {
        var fragmentBottomNavView=fragmentView.bottomNavigationView
        BottomNavigationViewHelper.setupBottomNavigationView(fragmentBottomNavView)
        BottomNavigationViewHelper.setupNavigation(activity!!,fragmentBottomNavView)
        var menu=fragmentBottomNavView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)

    }

    private fun setupAuthListener() {

        mAuthListener=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser
                if(user==null){
                    //kullanıcı doğrulandıysa homeaktivitye geç
                    var intent=
                        Intent(activity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    activity!!.finish() //homeaktivityden geri tuşuna basınca logine geri döndürüyor

                }else{

                }

            }

        }
    }
    override fun onResume() {
        setupNavigationView()
        super.onResume()
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