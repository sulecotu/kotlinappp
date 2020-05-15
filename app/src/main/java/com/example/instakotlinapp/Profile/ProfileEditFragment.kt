package com.example.instakotlinapp.Profile

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.instakotlinapp.Model.Users

import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.EventbusDataEvents
import com.example.instakotlinapp.utils.UniversalImageLoader
import com.firebase.ui.auth.data.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.circleProfileImage
import kotlinx.android.synthetic.main.fragment_yukleniyor.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 */
class ProfileEditFragment : Fragment() {

  lateinit   var circleProfileImageFragment:CircleImageView
    lateinit var gelenKullaniciBilgileri:Users
    val RESIM_SEC=100
    lateinit var mDatabaseRef:DatabaseReference
    lateinit var mStorageRe: StorageReference

    var profilFotorafURI:Uri?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {



        val view =inflater!!.inflate(R.layout.fragment_profile_edit, container, false)
        mDatabaseRef=FirebaseDatabase.getInstance().reference
        mStorageRe=FirebaseStorage.getInstance().reference
        setupKullaniciBilgileri(view)

        view.imgClose.setOnClickListener {

            activity!!.onBackPressed()


        }
        /// profil resmini değiştire basıldığı zaman
        view.tvFotoğrafiDegistir.setOnClickListener {
        var intent =Intent()
            intent.setType("image/*")// kullanıcı galeriyi açtığında ona ne tür görseller sunulsuntype da ne tür olduğunu verir yani image her türünü gösterir
            intent.setAction(Intent.ACTION_PICK) ///bu butona tıklanıldığında ne yapmak istediğmizi belirityoruz.
            startActivityForResult(intent,RESIM_SEC)

        }
        // profil düzenle kısmında girilen herşeyin girildikten sonra veri tabanındakiverilerle karşılatırıp değiştime
            // BU KOD EĞİTMDEKİ DEĞİL.
        view.imgDegisiklikleriKaydet.setOnClickListener {
            if (profilFotorafURI != null) {// kullanıcı galeriye başarılı bir şekilde girmiştir ve başarılı bir şekilde fotoğraf seçmiştir.
                var dialogYukleniyor = YukleniyorFragment()
                dialogYukleniyor.show(activity!!.supportFragmentManager, "")

                dialogYukleniyor.isCancelable=false
                mStorageRe.child("kullanıcılar").child(gelenKullaniciBilgileri!!.kullanici_id!!)
                    .child(profilFotorafURI!!.lastPathSegment!!)
                    .putFile(profilFotorafURI!!)
                    .addOnSuccessListener { itUploadTask ->
                        itUploadTask?.storage?.downloadUrl?.addOnSuccessListener { itUri ->
                            val dowloadUrl: String = itUri.toString()
                            mDatabaseRef.child("kullanıcılar").child(gelenKullaniciBilgileri!!.kullanici_id!!).child("kulaniciDetaylari").child("profilResmi").setValue(dowloadUrl).addOnCompleteListener { itTask ->
                                if (itTask.isSuccessful) {
                                        dialogYukleniyor.dismiss()
                                        mStorageRe.downloadUrl
                                    kullaniciAdiniGuncelle(view,true) //true ise kullanıcı profil resmini güncelemiştir

                                } else {
                                        val message = itTask.exception?.message
                                        Toast.makeText(activity!!, "HATA" + message, Toast.LENGTH_SHORT)
                                            .show()
                                    kullaniciAdiniGuncelle(view,false) // değiştirmek istemiştir fakat hata almıştır.
                                    }

                                }}}}
            else{
                kullaniciAdiniGuncelle(view,null) //



            }

        }

        return view
    }
    /// profil resmi değişti
    //true ise başarılı bir şekilde resim storage yüklenmiş ve veri tabanına yazılmıştır.
    //false resim yüklenirken hata oluşmuştur.
    //null kullanıcı resmi değiştirmenk istememiştir.

    private fun kullaniciAdiniGuncelle(view: View, profilResmiDegisti: Boolean?) {
        if (!gelenKullaniciBilgileri!!.kullanici_adi!!.equals(view.etUserName.text.toString())) {
            mDatabaseRef.child("kullanıcılar").orderByChild("kullanici_adi")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var kullaniciAdiKullanimdaMi = false

                        for (ds in p0!!.children) {
                            var okunanKullaniciAdi =
                                ds!!.getValue(Users::class.java)!!.kullanici_adi


                            if (okunanKullaniciAdi!!.equals(view.etUserName.text.toString())) {
                                /// kullanıcı adı kullanılıyorsa ekrana mesaj yazar.
                                kullaniciAdiKullanimdaMi = true
                                profilBilgileriniGüncelle(view,profilResmiDegisti,false)
                                break


                            }

                        }
                        if (kullaniciAdiKullanimdaMi == false) {
                            mDatabaseRef.child("kullanıcılar")
                                .child(gelenKullaniciBilgileri!!.kullanici_id!!)
                                .child("kullanici_adi")
                                .setValue(view.etUserName.text.toString())
                            profilBilgileriniGüncelle(view,profilResmiDegisti,true)

                        }

                    }

                })

        }else{
            profilBilgileriniGüncelle(view,profilResmiDegisti,null)


        }

    }

    private fun profilBilgileriniGüncelle(view: View, profilResmiDegisti: Boolean?, kullaniciAdiDegisti: Boolean?) {

        var profilGüncellendiMi:Boolean? = null

        if (!gelenKullaniciBilgileri!!.ad_soyad!!.equals(view.etProfileName.text.toString())) {// ilk gelen değer ile kullanıcının kaydetmek istediği değer farklıysa
            mDatabaseRef.child("kullanıcılar").child(gelenKullaniciBilgileri!!.kullanici_id!!)
                .child("ad_soyad").setValue(view.etProfileName.text.toString())
            profilGüncellendiMi = true
        }
        if (!gelenKullaniciBilgileri!!.kulaniciDetaylari!!.biyografi!!.equals(view.etUserBio.text.toString())) {
            mDatabaseRef.child("kullanıcılar").child(gelenKullaniciBilgileri!!.kullanici_id!!)
                .child("kulaniciDetaylari").child("biyografi")
                .setValue(view.etUserBio.text.toString())
            profilGüncellendiMi = true
        }
        if(profilResmiDegisti == null && kullaniciAdiDegisti == null &&profilGüncellendiMi ==null){

        }
        else if(kullaniciAdiDegisti == false && (profilGüncellendiMi == true||profilResmiDegisti ==true )){
            Toast.makeText(activity,"Kullanıcı Adı Kullanılıyor.",Toast.LENGTH_SHORT).show()

        }

        else{

            Toast.makeText(activity,"Kullanıcı Güncellendi.",Toast.LENGTH_SHORT).show()
            activity?.onBackPressed()


        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if(requestCode==RESIM_SEC && resultCode== AppCompatActivity.RESULT_OK && data!!.data!=null){
                profilFotorafURI=data!!.data!!    // URI android içinden birşey eriştiğmiz zaman onun adresini temsi eden yapı

                circleProfileImage.setImageURI(profilFotorafURI)
            }

    }





    private fun setupKullaniciBilgileri(view: View?) {
        view!!.etProfileName.setText(gelenKullaniciBilgileri!!.ad_soyad)
        view!!.etUserName.setText(gelenKullaniciBilgileri!!.kullanici_adi)
        //biyografinin boş olma ihtimali olduğu için if döndürdük
        if(!gelenKullaniciBilgileri!!.kulaniciDetaylari!!.biyografi!!.isNullOrEmpty()){
            view!!.etUserBio.setText(gelenKullaniciBilgileri!!.kulaniciDetaylari!!.biyografi)
        }
      var imgUrl=gelenKullaniciBilgileri!!.kulaniciDetaylari!!.profilResmi
        UniversalImageLoader.setImage(imgUrl!!,view!!.circleProfileImage,view.progressBar,"")

    }

    ////////////////EVENTBUS///////
    @Subscribe(sticky = true)
    internal fun onKullaniciBilgileriEvent(kullaniciBilgileri : EventbusDataEvents.KullaniciBilgileriniGonder){
     gelenKullaniciBilgileri=kullaniciBilgileri!!.kullanici!!





    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)

    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

}
