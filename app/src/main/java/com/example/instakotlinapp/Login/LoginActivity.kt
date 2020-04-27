package com.example.instakotlinapp.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.instakotlinapp.Home.HomeActivity
import com.example.instakotlinapp.Model.Users
import com.example.instakotlinapp.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_kayit.*

class LoginActivity : AppCompatActivity() {

    lateinit var mRef: DatabaseReference

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener:FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupAuthListener()

        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference

        init()
    }



    fun init() {
        etEmailTelorKullaniciAdi.addTextChangedListener(watcher)
        etSifree.addTextChangedListener(watcher)

        /// giriş yap butonuna tıklayınca kullanıcıyı denetler.
        btnGirisYap.setOnClickListener {
            oturumAcacakKullaniciDenetle(
                etEmailTelorKullaniciAdi.text.toString(),
                etSifree.text.toString()
            )


        }




    }


    private fun oturumAcacakKullaniciDenetle(
        emailTelefonNumarasıKullaniciAdi: String,
        sifre: String
    ) {
        var kullaniciBulundu=false


        /// veritabanındaki kullanıcılara eriştik ve orderbychilde ile verileri emaile göre sıralanmasını sağladık.
        mRef.child("kullanıcılar").orderByChild("email")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (ds in p0!!.children) {
                        var okunanKullanici = ds.getValue(Users::class.java)

                        // bu if ve elseiflerde yapmaya çalıştığımız activity loginde kullanıcının hani verilerle giriş yapmaya çalıştığı
                        // yani kullanıcı telefonnumarası-şifre,email-sifre,yada kullanıcıadı-sifre ile giriş yapmaya çalışıyor olabilir.
                        //verileri aldıktan sonra oturum aç fonksiyonuna geçiş yapılıyor.

                        if (okunanKullanici!!.email!!.toString().equals(
                                emailTelefonNumarasıKullaniciAdi
                            )
                        ) {
                            oturumAc(okunanKullanici, sifre, false)
                             kullaniciBulundu=true
                            break

                        } else if (okunanKullanici!!.kullanici_adi!!.toString().equals(
                                emailTelefonNumarasıKullaniciAdi
                            )
                        ) {
                            oturumAc(okunanKullanici, sifre, false)
                             kullaniciBulundu=true
                            break


                        } else if (okunanKullanici!!.telefon_numarasi!!.toString().equals(
                                emailTelefonNumarasıKullaniciAdi
                            )
                        ) {
                            oturumAc(okunanKullanici, sifre, true)
                             kullaniciBulundu=true
                            break


                        }



                    }
                    if(kullaniciBulundu==false){
                        Toast.makeText(this@LoginActivity,"Kullanıcı Bulunamadı",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            )


    }


    private fun oturumAc(okunanKullanici: Users, sifre: String, telefonileGiris: Boolean) {
        var girisYapacakEmail = ""
        if (telefonileGiris == true) {

            girisYapacakEmail = okunanKullanici.email_telefon_numarasi.toString()
        } else {
            girisYapacakEmail = okunanKullanici.email.toString()

        }

        mAuth.signInWithEmailAndPassword(girisYapacakEmail, sifre)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {
                    if (p0!!.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "Oturum Açıldı", Toast.LENGTH_SHORT)
                            .show()


                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Kullanıcı Adı/ Şifre Hatalı",
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                }

            })


    }

    var watcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (etEmailTelorKullaniciAdi.text.toString().length >= 6 && etSifree.text.toString().length >= 6) {
                btnGirisYap.isEnabled = true
                btnGirisYap.setTextColor(
                    ContextCompat.getColor(
                        this@LoginActivity!!,
                        R.color.beyaz
                    )
                )
                btnGirisYap.setBackgroundResource(R.drawable.register_button_aktif)


            } else {

                btnGirisYap.isEnabled = false
                btnGirisYap.setTextColor(
                    ContextCompat.getColor(
                        this@LoginActivity!!,
                        R.color.sonukmavi
                    )
                )
                btnGirisYap.setBackgroundResource(R.drawable.register_button)


            }
        }

    }
    private fun setupAuthListener() {
        mAuthListener=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser
                if(user!=null){
                    //kullanıcı doğrulandıysa homeaktivitye geç
                    var intent=Intent(this@LoginActivity,HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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

