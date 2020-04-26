package com.example.instakotlinapp.Model

class Users {
    ////////////////////kullanıcıları veritabanına kaydetmek için buna ihtiyacımız var. verileri toplu olarak eklemede bu sınıf işlerimizi kolaylaştırır.
    var email: String? = null
    var sifre: String? = null
    var kullanici_adi: String? = null
    var ad_soyad: String? = null
    var telefon_numarasi: String? = null
    var email_telefon_numarasi: String? =null
    var kullanici_id: String? = null

    constructor() {}

    //ister telefon ile kayıt olsun isterse de emaille farketmez
    constructor(email: String?, sifre: String?, kullanici_adi: String?, ad_soyad: String?, telefon_numarasi: String?, email_telefon_numarasi: String?, kullanici_id: String?) {
        this.email = email
        this.sifre = sifre
        this.kullanici_adi = kullanici_adi
        this.ad_soyad = ad_soyad
        this.telefon_numarasi = telefon_numarasi
        this.email_telefon_numarasi = email_telefon_numarasi
         this.kullanici_id= kullanici_id
    }

}