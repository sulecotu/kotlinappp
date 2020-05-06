package com.example.instakotlinapp.utils

import java.io.File


class Dosyaİslemleri  {
     // compain javadaki static kavramına denk geliyor.
    companion object {
        fun klasordekiDosyalariGetir(klasorAdi:String):ArrayList<String>{

            var tumDosyalar =ArrayList<String>()
            var file=File(klasorAdi)

            //parametre olarak gönderdiğimiz kaalsordeki tüm dosyalar alınır
            var klasordekiTumDosyalar=file.listFiles()

            //parametre olarak göndediğmiz klasör yolunda eleman olup olmadığı kontrol ediliyor
            if(klasordekiTumDosyalar!= null){

                for(i in 0..klasordekiTumDosyalar.size){
                    //sadece dosyalara bakılır
                    if(klasordekiTumDosyalar[i].isFile){
                        //okuduğumuz dosyanın telefondaki yeri ve adını içerir
                        // okunan dosya files://root/logo
                        var okunanDosyaYolu=klasordekiTumDosyalar[i].absolutePath
                        //substring noktayı gördükten sonra okur yani .png kısmını okur
                        var dosyaTuru=okunanDosyaYolu.substring(okunanDosyaYolu.lastIndexOf("."))

                        if(dosyaTuru.equals(".jpg") || dosyaTuru.equals(".jpeg") || dosyaTuru.equals(".png") || dosyaTuru.equals(".mp4") ){

                            tumDosyalar.add(okunanDosyaYolu)


                        }



                    }

                }


            }
         return tumDosyalar








        }
    }

}