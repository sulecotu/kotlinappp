package com.example.instakotlinapp.utils

import android.util.Log
import java.io.File
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


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

                /// galeride getirilen resimlerin tarihe göre sondan başa listelenmesi
                //son eklenen en başa gelir
                // eğer ilk önce return 1 deseydik ilk eklenen en başa gelirdi

                if(klasordekiTumDosyalar.size>1){
                    Arrays.sort(klasordekiTumDosyalar,object  :Comparator<File>{
                        override fun compare(o1: File?, o2: File?): Int {
                            if(o1!!.lastModified() > o2!!.lastModified()){
                                return  -1
                            }
                            else return 1



                        }

                    })



                }




                for(i in 0..klasordekiTumDosyalar.size-1){
                    //sadece dosyalara bakılır
                    if(klasordekiTumDosyalar[i].isFile){
                        Log.e("hata","okunan veri bir dosya")

                        //okuduğumuz dosyanın telefondaki yeri ve adını içerir
                        // okunan dosya files://root/logo
                        var okunanDosyaYolu=klasordekiTumDosyalar[i].absolutePath
                        Log.e("hata","okunan veri bir dosya yolu "+okunanDosyaYolu)

                        //substring noktayı gördükten sonra okur yani .png kısmını okur
                        var dosyaTuru=okunanDosyaYolu.substring(okunanDosyaYolu.lastIndexOf("."))
                        Log.e("hata","okunan veri bir dosya türü "+dosyaTuru)

                        if(dosyaTuru.equals(".jpg") || dosyaTuru.equals(".jpeg") || dosyaTuru.equals(".png") || dosyaTuru.equals(".mp4") ){

                            tumDosyalar.add(okunanDosyaYolu)
                            Log.e("hata","arrayliste eklenen dosya yolu "+okunanDosyaYolu)

                        }



                    }

                }


            }
         return tumDosyalar








        }
    }

}