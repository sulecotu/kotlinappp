package com.example.instakotlinapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.instakotlinapp.Model.UserPosts
import com.example.instakotlinapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.tek_post_recycler_item.view.*

class HomeFragmentRecylerAdapter(var context:Context,var tumGonderiler:ArrayList<UserPosts>):RecyclerView.Adapter<HomeFragmentRecylerAdapter.MyViewHolder>() {
    override fun getItemCount(): Int {
        return tumGonderiler.size

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
     var viewHolder=LayoutInflater.from(context).inflate(R.layout.tek_post_recycler_item,parent,false)
        return MyViewHolder(viewHolder, context)
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(position,tumGonderiler.get(position))

    }
    class MyViewHolder(itemView: View, myHomeActivity: Context) : RecyclerView.ViewHolder(itemView) {


        var tumLayout=itemView as ConstraintLayout
        var profilImage=tumLayout.imgUserProfile
        var userNameTitle=tumLayout.tvKullaniciAdiBaslik
        var gonderi=tumLayout.imgPostResim
        var gonderiAciklama=tumLayout.tvPostAciklama
        var  gonderiBegen =tumLayout.imgBegen
        var myHomeActivity = myHomeActivity
        var mInstaLikeView=tumLayout.insta_like_view
        var begeniSayisi=tumLayout.tvBegeniSayisi

        fun setData(position: Int,oankiGonderi:UserPosts) {
            userNameTitle.setText(oankiGonderi.userName)
            UniversalImageLoader.setImage(oankiGonderi.postURL!!,gonderi,null,"")
            gonderiAciklama.setText(oankiGonderi.postAciklama)
            UniversalImageLoader.setImage(oankiGonderi.userPhotoURL!!,profilImage,null,"")

            begeniKontrol(oankiGonderi)


            gonderiBegen.setOnClickListener {

                var mRef=FirebaseDatabase.getInstance().reference
                var userID =FirebaseAuth.getInstance().currentUser!!.uid
                mRef.child("beğeniler").child(oankiGonderi.postID!!).addListenerForSingleValueEvent( object :
                    ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                     if(p0!!.hasChild(userID)){

                         mRef.child("beğeniler").child(oankiGonderi.postID!!).child(userID).removeValue()
                         gonderiBegen.setImageResource(R.drawable.ic_begen)
                     }
                        else {
                         mRef.child("beğeniler").child(oankiGonderi.postID!!).child(userID)
                             .setValue(userID)

                         gonderiBegen.setImageResource(R.drawable.ic_like_kirmizi)
                         mInstaLikeView.start()
                         begeniSayisi.visibility=View.VISIBLE
                         begeniSayisi.setText( ""+ p0!!.childrenCount.toString()+ " beğenme")

                     }



                    }

                })

            }

            var ilkTiklama:Long=0
            var sonTiklama:Long=0
            gonderi.setOnClickListener {
               ilkTiklama=sonTiklama
                sonTiklama=System.currentTimeMillis()
                if(sonTiklama - ilkTiklama < 300){
                    mInstaLikeView.start()

                    FirebaseDatabase.getInstance().getReference().child("beğeniler").child(oankiGonderi.postID!!)
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(FirebaseAuth.getInstance().currentUser!!.uid)
                    sonTiklama=0
                }


            }


        }
        fun begeniKontrol(oankiGonderi: UserPosts) {
            var mRef= FirebaseDatabase.getInstance().reference
            var userID= FirebaseAuth.getInstance().currentUser!!.uid

            mRef.child("beğeniler").child(oankiGonderi.postID!!).addValueEventListener(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0!!.getValue()!=null){
                        begeniSayisi.visibility=View.VISIBLE
                        begeniSayisi.setText( ""+ p0!!.childrenCount.toString()+ " beğenme")
                    }
                    else{
                        begeniSayisi.visibility=View.GONE
                    }
                  if(p0!!.hasChild(userID)){
                      gonderiBegen.setImageResource(R.drawable.ic_like_kirmizi)
                  }
                    else{
                      gonderiBegen.setImageResource(R.drawable.ic_begen)

                  }
                }

            })
        }


    }
}