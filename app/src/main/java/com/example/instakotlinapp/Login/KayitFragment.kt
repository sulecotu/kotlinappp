package com.example.instakotlinapp.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.instakotlinapp.Model.UserDetails
import com.example.instakotlinapp.Model.Users
import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.EventbusDataEvents
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_kayit.*
import kotlinx.android.synthetic.main.fragment_kayit.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class KayitFragment : Fragment() {

    var telNo=""
    var verificationID=""
    var gelenKod=""
    var gelenEmail=""
    var emailİleKayitİşlemi=true
    lateinit var mAuth: FirebaseAuth  //authentication işlemleri bu nesne üzerinden
   lateinit var mRef: DatabaseReference //// veri tabanı işlemleri bu nesne üzerinden
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //içerdeki edittext ve butonlara erişmek için
        var view= inflater.inflate(R.layout.fragment_kayit, container, false)

        mAuth= FirebaseAuth.getInstance()

        // kayıt sayfasında bulanan giriş yap kısmına tıkladığımızda bizi login kısmına yönlendirmek için
        view.tvGirisYap.setOnClickListener {
            var intent= Intent(activity,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        mRef= FirebaseDatabase.getInstance().reference

        view.etAdSoyad.addTextChangedListener(watcher)
        view.etKullancıAdı.addTextChangedListener(watcher)
        view.etSifre.addTextChangedListener(watcher)

        view.btnGiris.setOnClickListener {
            ///kullanıcı adının kullanılıp kullanılmadğını kontrol etme

            var kullaniciAdiKullanimdaMi=false
            mRef.child("kullanıcılar").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
              }
                 override fun onDataChange(p0: DataSnapshot) {
                     if(p0!!.getValue()!=null){
                                for(kullanici in p0!!.children){
                                    var okunanKullanici=kullanici.getValue(Users::class.java)
                                    if(okunanKullanici!!.kullanici_adi!!.equals( view.etKullancıAdı.text.toString())){
                                        /// kullanıcı adı kullanılıyorsa ekrana mesaj yazar.
                                       Toast.makeText(activity,"Bu Kullanıcı Adı Kullanılıyor.",Toast.LENGTH_SHORT).show()
                                        kullaniciAdiKullanimdaMi=true
                                        break

                                    }
                                }
                         // eğer kullanıcı adı kullanılmıyorsa diğer kayıt işlemlerini sırayla yapmaya devam eder.
                               if(kullaniciAdiKullanimdaMi==false){
                                   //kullanıcı email ile kaydolmak istiyorsa
                                   if(emailİleKayitİşlemi){
                                       var sifre=view.etSifre.text.toString()// edit texte bulunan sifreyi alır.
                                       var adSoyad=view.etAdSoyad.text.toString()//edit texte bbulunan ad soyadı alır
                                       var kullaniciAdi=etKullancıAdı.text.toString()//edit textte olan kullanıcı adını alır




                                       mAuth.createUserWithEmailAndPassword(gelenEmail,sifre)
                                           .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                                               override fun onComplete(p0: Task<AuthResult>) {


                                                   if(p0!!.isSuccessful)///kullanıcı bilgilerle başarılı giriş yapmışsa
                                                   {
                                                       Toast.makeText(activity,"Oturum Açıldı",Toast.LENGTH_SHORT).show()
                                                       var kullaniciID=mAuth.currentUser!!.uid.toString()
                                                       // kullanıcı idsini alırız.

                                                       ///oturum açan kullanıcının verilerini database kaydetme
                                                       var kaydedilecekKullaniciDetayları=UserDetails("0","0","0","","")

                                                       var kaydedilecekKullanici=Users(gelenEmail,sifre,kullaniciAdi,adSoyad, " "," " ,kullaniciID,kaydedilecekKullaniciDetayları)
                                                       mRef.child("kullanıcılar").child(kullaniciID).setValue(kaydedilecekKullanici) // veri tabanına kaydetmek için kullanıcılar ve userıd düğümü oluşturulur.
                                                           .addOnCompleteListener(object :OnCompleteListener<Void>{
                                                               override fun onComplete(p0: Task<Void>) {
                                                                   if(p0!!.isSuccessful)// eğer kayıt başarılı şekilde gerçekleşmişse
                                                                   {
                                                                       Toast.makeText(activity,"Kullanıcı Kaydedildi",Toast.LENGTH_SHORT).show()
                                                                   } else{

                                                                       ///kayıt işlemi başarısız olursa o kullanıcıyı sil ve kuyllanıcı bilgilerini baştan girsin

                                                                       mAuth.currentUser!!.delete()
                                                                           .addOnCompleteListener(object : OnCompleteListener<Void> {
                                                                               override fun onComplete(p0: Task<Void>) {
                                                                                   if (p0!!.isSuccessful) {//kullanıcı verilerini silme işelmi başarılı olmuşsa
                                                                                       Toast.makeText(activity, "Kullanıcı Kaydedilemedi.Tekrar Deneyin", Toast.LENGTH_SHORT).show()
                                                                                   }

                                                                               }
                                                                           })
                                                                   }
                                                               }

                                                           })


                                                   }else ////kullanıcı başarılı giriş yapamamışsa

                                                   {
                                                       Toast.makeText(activity,"Oturum Açılamadı",Toast.LENGTH_SHORT).show()
                                                   }
                                               }


                                           })


                                   }

                                   ///kulanıcı telefon numarasıyla kayıt ol mak istiyor.
                                   else {


                                       var sifre=view.etSifre.text.toString()// edit texte bulunan sifreyi alır.
                                       var adSoyad=view.etAdSoyad.text.toString()//edit texte bbulunan ad soyadı alır
                                       var kullaniciAdi=view.etKullancıAdı.text.toString()  //edit textte olan kullanıcı adını alır


                                       var sahteEmail=telNo+"@gulben.com" //kullanıcı +90545686945@gulben.com şeklinde tutar
                                       //çünkü kullanıcı ilerde emailini değiştirmek isteyebilir o yüzden email kısmında böyle turulur

                                       mAuth.createUserWithEmailAndPassword(sahteEmail,sifre)
                                           .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                                               override fun onComplete(p0: Task<AuthResult>) {

                                                   if(p0!!.isSuccessful)///kullanıcı bilgilerle başarılı giriş yapmışsa
                                                   {
                                                       Toast.makeText(activity,"Oturum Açıldı",Toast.LENGTH_SHORT).show()
                                                       var kullaniciID=mAuth.currentUser!!.uid.toString()// kullanıcı idsini alırız.



                                                       ///oturum açan kullanıcının verilerini database kaydetme
                                                       var kaydedilecekKullaniciDetayları=UserDetails("0","0","0","","")

                                                       var kaydedilecekKullanici=Users( " ", sifre,kullaniciAdi,adSoyad,telNo,sahteEmail,kullaniciID,kaydedilecekKullaniciDetayları)
                                                       mRef.child("kullanıcılar").child(kullaniciID).setValue(kaydedilecekKullanici) // veri tabanına kaydetmek için kullanıcılar ve userıd düğümü oluşturulur.
                                                           .addOnCompleteListener(object :OnCompleteListener<Void>{
                                                               override fun onComplete(p0: Task<Void>) {
                                                                   if(p0!!.isSuccessful)// eğer kayıt başarılı şekilde gerçekleşmişse
                                                                   {
                                                                       Toast.makeText(activity,"Kullanıcı Kaydedildi",Toast.LENGTH_SHORT).show()
                                                                   } else{
                                                                       ///kayıt işlemi başarısız olursa o kullanıcıyı sil ve kuyllanıcı bilgilerini baştan girsin

                                                                       mAuth.currentUser!!.delete()
                                                                           .addOnCompleteListener(object : OnCompleteListener<Void>{
                                                                               override fun onComplete(p0: Task<Void>) {
                                                                                   if(p0!!.isSuccessful){//kullanıcı verilerini silme işelmi başarılı olmuşsa
                                                                                       Toast.makeText(activity,"Kullanıcı Kaydedilemedi.Tekrar Deneyin",Toast.LENGTH_SHORT).show()

                                                                                   }


                                                                               }


                                                                           })


                                                                   }
                                                               }

                                                           })



                                                   }else ////kullanıcı başarılı giriş yapamamışsa

                                                   {
                                                       Toast.makeText(activity,"Oturum Açılamadı",Toast.LENGTH_SHORT).show()
                                                   }
                                               }


                                           })


                                   }





                               }



                     }

                             }



                         })



            }












        return view
    }

    var watcher :TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            if(s!!.length >5 ){
                if(etAdSoyad.text.toString().length>5 && etKullancıAdı.text.toString().length>5 && etSifre.text.toString().length>5){
                    btnGiris.isEnabled=true
                    btnGiris.setTextColor(ContextCompat.getColor(activity!!,R.color.beyaz))
                    btnGiris.setBackgroundResource(R.drawable.register_button_aktif)

                    }else{

                    btnGiris.isEnabled=false
                    btnGiris.setTextColor(ContextCompat.getColor(activity!!,R.color.sonukmavi))
                    btnGiris.setBackgroundResource(R.drawable.register_button)
                }



            }else{
                //kullanıcı 5 karakter uzunluğundan kısa bir veri girerse buton false oluyor.
                btnGiris.isEnabled=false
                btnGiris.setTextColor(ContextCompat.getColor(activity!!,R.color.sonukmavi))
                btnGiris.setBackgroundResource(R.drawable.register_button)


            }

        }


    }




////////////////EVENTBUS///////
    @Subscribe(sticky = true)
    internal fun onKayitEvent(kayitBilgileri : EventbusDataEvents.KayitBilgileriniGonder){

       if(kayitBilgileri.emailkayit==true){////eğer emailkayıt doğruysa kullanıcı email ile kayıt olur
           emailİleKayitİşlemi=true
           gelenEmail=kayitBilgileri.email!!
           Log.e("esma","gelen email"+gelenEmail)
           Toast.makeText(activity,"Gelen Email:"+gelenEmail,Toast.LENGTH_SHORT).show()

       }else{///kullanıcı telefon ile kayıt olur.
            emailİleKayitİşlemi=false

           telNo=kayitBilgileri.telNo!!
           verificationID=kayitBilgileri.verificationID!!
           gelenKod=kayitBilgileri.code!!

           Toast.makeText(activity,"Gelen Kod:"+gelenKod+"VerificationID:"+verificationID,Toast.LENGTH_SHORT).show()


       }


    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)

    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }
    //////////////////EVENTBUS//////////////////////
}
