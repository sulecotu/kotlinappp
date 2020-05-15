package com.example.instakotlinapp.utils

import com.example.instakotlinapp.Model.Users

class EventbusDataEvents {

    internal class KayitBilgileriniGonder(var telNo:String? , var email:String?, var verificationID :String?, var code:String?,var emailkayit:Boolean)
    internal class KullaniciBilgileriniGonder(var kullanici:Users?)
    internal class PaylasilacakResmiGonder(var dosyaYolu:String?, var dosyaTuruResimMi :Boolean?)



}