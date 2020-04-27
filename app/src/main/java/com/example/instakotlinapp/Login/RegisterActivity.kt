package com.example.instakotlinapp.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.example.instakotlinapp.Home.HomeActivity
import com.example.instakotlinapp.Model.Users
import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.EventbusDataEvents
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus

class RegisterActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {
    lateinit var manager: FragmentManager
    lateinit var  mRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener:FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupAuthListener()

        // bu tanımlamayı yapınca doğrudan database de instkotlinApp erişiriz.
        mAuth= FirebaseAuth.getInstance()
        mRef=FirebaseDatabase.getInstance().reference
        manager = supportFragmentManager
        manager.addOnBackStackChangedListener(this)

        init()
    }

    private fun init() {

        tvGirisYap.setOnClickListener {
            var intent= Intent(this@RegisterActivity,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        tvEposta.setOnClickListener {
            viewTelefon.visibility = View.INVISIBLE
            viewEposta.visibility = View.VISIBLE
            etGirisYontemi.setText("")
            etGirisYontemi.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            etGirisYontemi.setHint("E-Posta")
            btnIleri.isEnabled = false
        }
        tvTelefon.setOnClickListener {
            viewTelefon.visibility = View.VISIBLE
            viewEposta.visibility = View.INVISIBLE
            etGirisYontemi.setText("")
            etGirisYontemi.inputType = InputType.TYPE_CLASS_NUMBER
            etGirisYontemi.setHint("Telefon")
            btnIleri.isEnabled = false
        }

        etGirisYontemi.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length >= 10) {
                    btnIleri.isEnabled = true
                    btnIleri.setTextColor(
                        ContextCompat.getColor(
                            this@RegisterActivity,
                            R.color.beyaz
                        )
                    )
                    btnIleri.setBackgroundResource(R.drawable.register_button_aktif)
                } else {

                    btnIleri.isEnabled = false
                    btnIleri.setTextColor(
                        ContextCompat.getColor(
                            this@RegisterActivity,
                            R.color.sonukmavi
                        )
                    )
                    btnIleri.setBackgroundResource(R.drawable.register_button)


                }
            }


        })

        btnIleri.setOnClickListener {
            //girilen telefon numarası uygunssa
            if (etGirisYontemi.hint.toString().equals("Telefon")) {
                var telefonKullanimdaMi=false
                if(isValidTelefon(etGirisYontemi.text.toString())){

                   mRef.child("kullanıcılar").addListenerForSingleValueEvent(object : ValueEventListener{
                       override fun onCancelled(p0: DatabaseError) {


                       }

                       override fun onDataChange(p0: DataSnapshot) {
                         if(p0!!.getValue()!= null){

                             for( kullanici in p0!!.children){
                                 var okunanKullanici=kullanici.getValue(Users::class.java)
                                 if(okunanKullanici!!.telefon_numarasi!!.equals(etGirisYontemi.text.toString())){
                                     Toast.makeText(this@RegisterActivity,"Bu Telefon Numarası Kullanılıyor.",Toast.LENGTH_SHORT).show()
                                     telefonKullanimdaMi=true
                                     break

                                 }


                             }
                             if(telefonKullanimdaMi==false){
                                 loginRoot.visibility = View.GONE
                                 loginContainer.visibility = View.VISIBLE
                                 var transaction = supportFragmentManager.beginTransaction()
                                 transaction.replace(R.id.loginContainer, TelefonKoduGirFragment())
                                 transaction.addToBackStack("telefonKoduGirFragmentEklendi")
                                 transaction.commit()
                                 EventBus.getDefault().postSticky(EventbusDataEvents.KayitBilgileriniGonder(etGirisYontemi.text.toString(), null, null, null, false))


                             }


                         }

                       }


                   })



                }else{// eğer telefn numarası uygun değilse ekrana mesaj yazar
                    Toast.makeText(this,"Lütfen Geçerli Bir Telefon Numarası Giriniz",Toast.LENGTH_SHORT).show()


                }

            } else { /// girilen email uygunsa
                if(isValidEmail(etGirisYontemi.text.toString())){

                    mRef.child("kullanıcılar").addListenerForSingleValueEvent(object :ValueEventListener{
                        var emailKullanimdaMi=false

                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if(p0!!.getValue()!=null){
                                for(kullanici in p0!!.children){

                                var okunanKullanici=kullanici.getValue(Users::class.java)

                                    if(okunanKullanici!!.email!!.equals(etGirisYontemi.text.toString())){
                                        Toast.makeText(this@RegisterActivity,"Bu Email Adresi Kullanılıyor",Toast.LENGTH_SHORT).show()
                                        emailKullanimdaMi=true
                                        break


                                    }


                                }
                                if(emailKullanimdaMi==false){
                                    loginRoot.visibility = View.GONE
                                    loginContainer.visibility = View.VISIBLE
                                    var transaction = supportFragmentManager.beginTransaction()
                                    transaction.replace(R.id.loginContainer, KayitFragment())
                                    transaction.addToBackStack("EmailGirisYontemiFragmentEklendi")
                                    transaction.commit()
                                    EventBus.getDefault().postSticky(
                                        EventbusDataEvents.KayitBilgileriniGonder(null, etGirisYontemi.text.toString(), null, null,
                                            true
                                        )
                                    )

                                }


                            }
                        }

                    })







                }else {// email uygun değilse ekrana mesaj yazar

                    Toast.makeText(this, "Lütfen Geçerli Bir Email Adresi Giriniz", Toast.LENGTH_SHORT).show()
                }


                }
        }
    }


    override fun onBackStackChanged() {
        //geri tuşuna basınca bir önceki fragmente gider.
        val elemanSayisi = manager.backStackEntryCount

        if (elemanSayisi == 0) {
            loginRoot.visibility = View.VISIBLE

        }
    }
    ///kullanıcının girdiği emailin kontrolünü yapan fonksiyon

    fun isValidEmail(kontrolEdilecekEmail: String): Boolean {
        if (kontrolEdilecekEmail == null) {

            return false
        }

        return android.util.Patterns.EMAIL_ADDRESS.matcher(kontrolEdilecekEmail).matches()
    }
        //kullanıcının girdiği telefon numarasını kontrol eden fonksiyon
    fun isValidTelefon(kontrolEdilecekTelefon: String): Boolean {
        if (kontrolEdilecekTelefon == null || kontrolEdilecekTelefon.length > 14) {
            return false
        }
        return android.util.Patterns.PHONE.matcher(kontrolEdilecekTelefon).matches()

    }
    private fun setupAuthListener() {
        //oturum açmış kullanıcı var mı varsa home aktivity açılsın
        mAuthListener=object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user= FirebaseAuth.getInstance().currentUser
                if(user!=null){
                    //kullanıcı doğrulandıysa homeaktivitye geç
                    var intent=Intent(this@RegisterActivity, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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