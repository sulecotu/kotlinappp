package com.example.instakotlinapp.Login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.EventbusDataEvents
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import kotlinx.android.synthetic.main.fragment_telefon_kodu_gir.*
import kotlinx.android.synthetic.main.fragment_telefon_kodu_gir.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit
import android.widget.Toast.makeText as makeText1


class TelefonKoduGirFragment : Fragment() {

    var gelenTelNo =""
    lateinit var mCallbacks:OnVerificationStateChangedCallbacks
    var verificationID = ""
    var gelenKod=""
    lateinit var progressBar: ProgressBar




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_telefon_kodu_gir, container, false)
        view.kullaniciTelNo.setText(gelenTelNo)
        progressBar=view.pbTelNoOnayla

        setupCallback()
        view.btnTamam.setOnClickListener{
            if(gelenKod.equals(view.onayKodu.text.toString())) {
                //numaraya gelen onay kodu karşılaştırma
                // {
                //onay kodu doğruysa kayıt fragmentine gider44
                EventBus.getDefault().postSticky(EventbusDataEvents.KayitBilgileriniGonder(gelenTelNo,null,verificationID,gelenKod ,false))
                var transaction=activity!!.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.loginContainer,KayitFragment())
                transaction.addToBackStack("kayıtFragmentEklendi")
                transaction.commit()

            }
            else{
            Toast.makeText(activity,"Kod Hatalı",Toast.LENGTH_SHORT).show()

            }
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            gelenTelNo,        // Kodun Yollanacağı Numara
            60,                 // Yollanan Kodun Geçerlilik Süresi
            TimeUnit.SECONDS,   // Unit of timeout
            this!!.activity!!,               // Activity (for callback binding)
            mCallbacks);        // OnVerificationStateChangedCallbacks


        return view
    }

    //Firebase PhoneNumber Kodlarını çektik
    private fun setupCallback() {

        mCallbacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                if (!credential.smsCode.isNullOrEmpty()) {
                    gelenKod = credential.smsCode!!
                    progressBar.visibility=View.INVISIBLE
                    Log.e("HATA", "on verification completed sms gelmiş" + gelenKod)
                }else{
                    Log.e("HATA","on verification completed sms gelmeyecek")


                }
            }
            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("HATA","Hata çıktı" +e.message)
                progressBar.visibility=View.INVISIBLE
            }

            override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
                verificationID=verificationId!!
                progressBar.visibility=View.VISIBLE
                Log.e("HATA","oncodesent çalıştı")

            }
        }

    }

    @Subscribe (sticky = true)
    internal fun onTelefonNoEvent(kayitBilgileri :EventbusDataEvents.KayitBilgileriniGonder){
        gelenTelNo=kayitBilgileri.telNo!!
        Log.e("esma","gelen tel no"+gelenTelNo)
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

private fun PhoneAuthProvider.verifyPhoneNumber(gelenTelNo: String, i: Int, seconds: TimeUnit, aktivity: Any, mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks) {

}
