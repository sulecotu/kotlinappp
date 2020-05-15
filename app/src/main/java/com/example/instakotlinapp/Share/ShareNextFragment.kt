package com.example.instakotlinapp.Share

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.instakotlinapp.Model.Posts
import com.example.instakotlinapp.Profile.YukleniyorFragment

import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.Dosyaİslemleri
import com.example.instakotlinapp.utils.EventbusDataEvents
import com.example.instakotlinapp.utils.UniversalImageLoader
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_share_next.*
import kotlinx.android.synthetic.main.fragment_share_next.view.*
import kotlinx.android.synthetic.main.fragment_yukleniyor.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * A simple [Fragment] subclass.
 */
class ShareNextFragment : Fragment() {
    var secilenDosyaYolu:String?=null
    var dosyaTuruResimMi:Boolean?=null
    lateinit var photoURI:Uri
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    lateinit var mStorageReference:StorageReference



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_share_next, container, false)
        UniversalImageLoader.setImage(secilenDosyaYolu!!,view!!.imgSecilenResim,null,"file://")

       // photoURI=Uri.parse("file://"+secilenResimYolu)
        mAuth=FirebaseAuth.getInstance()
        mUser=mAuth.currentUser!!
        mRef=FirebaseDatabase.getInstance().reference
        mStorageReference=FirebaseStorage.getInstance().reference

        view.tvIleriButton.setOnClickListener {



            //resim dosyasını sıkıştır
            if(dosyaTuruResimMi==true){
               Dosyaİslemleri.compressResimDosya(this,secilenDosyaYolu)



            }
            ///video dosyasını sıkıştır
            else if(dosyaTuruResimMi==false){
                Dosyaİslemleri.compressVideoDosya(this,secilenDosyaYolu!!)



            }







   }
        return view
    }

    private fun veritabaninaBilgileriYaz(yuklenenFotUrl: String) {
        var postID =mRef.child("posts").child(mUser.uid).push().key
       var yuklenenPosts = Posts(mUser!!.uid!! , postID!!, " " , etPostAciklama.text.toString(),yuklenenFotUrl)



        mRef.child("posts").child(mUser.uid).child(postID!!).setValue(yuklenenPosts)
        mRef.child("posts").child(postID).child("yuklenme_tarihi").setValue(ServerValue.TIMESTAMP)





    }

    ///////////////////////EVENTBUS////////////////////
    @Subscribe(sticky = true)
    internal fun onSecilenDosyaEvent(secilenResim : EventbusDataEvents.PaylasilacakResmiGonder) {
        secilenDosyaYolu = secilenResim!!.dosyaYolu!!
        dosyaTuruResimMi=secilenResim!!.dosyaTuruResimMi!!
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)

    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    fun uploadStorage(filePath: String?) {
        var fileUri =Uri.parse("file://"+ filePath)
        var dialogYukleniyor = CompressLoadingFragment()
        dialogYukleniyor.show(activity!!.supportFragmentManager, "")
        dialogYukleniyor.isCancelable=false


        var ref=mStorageReference.child("kullanıcılar").child(mUser.uid).child(fileUri!!.lastPathSegment!!)
        var uploadTask =ref.putFile(fileUri!!)
        var urlTask=uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot ,Task<Uri > >{  task->
            if(!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl

        }).addOnCompleteListener { task->
            if(task.isSuccessful){
                val dowloandUri =task.result
                dialogYukleniyor.dismiss()
                veritabaninaBilgileriYaz(dowloandUri.toString())
            }
            else{
                dialogYukleniyor.dismiss()
                Toast.makeText(activity,"hata oluştu"+task.exception!!.message,Toast.LENGTH_SHORT).show()
            }

        }
        uploadTask .addOnProgressListener(object:OnProgressListener<UploadTask.TaskSnapshot>{
            override fun onProgress(p0: UploadTask.TaskSnapshot) {
                //yüklemek istediğimiz resmin ne kadar yüklendiğini
                var progress=100.0 * p0!!.bytesTransferred/p0!!.totalByteCount
                Log.e("hata","ilerleme"+progress)
                dialogYukleniyor.tvBilgi.text= "%" + progress.toInt().toString()+ "yüklendi..."

            }

        })
    }

}
