package com.example.instakotlinapp.Model

class UserDetails {

    var takipciSayisi:String?= null
    var takipSayisi:String?=null
    var gönderiSayisi:String?=null
    var profilResmi:String?=null
    var biyografi:String?=null


    //verileri çekerken kullanılan constructor
    constructor()

    constructor(
        takipciSayisi: String?,
        takipSayisi: String?,
        gönderiSayisi: String?,
        profilResmi: String?,
        biyografi: String?
    ) {
        this.takipciSayisi = takipciSayisi
        this.takipSayisi = takipSayisi
        this.gönderiSayisi = gönderiSayisi
        this.profilResmi = profilResmi
        this.biyografi = biyografi
    }

    // veri girişi yapılırken kullanılan constructor


    override fun toString(): String {
        return "UserDetails(takipciSayisi=$takipciSayisi, takipSayisi=$takipSayisi, gönderiSayisi=$gönderiSayisi, profilResmi=$profilResmi, biyografi=$biyografi)"
    }


}