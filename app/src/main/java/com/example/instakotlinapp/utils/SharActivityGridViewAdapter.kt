package com.example.instakotlinapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import com.example.instakotlinapp.R
import kotlinx.android.synthetic.main.tek_sutun_grid_resim.view.*

class SharActivityGridViewAdapter(context: Context, resource: Int, var klasordekiDosyalar: ArrayList< String>) : ArrayAdapter<String>(context, resource, klasordekiDosyalar) {

     var inflater:LayoutInflater
     var tekSutunResim:View?  = null
    lateinit var  viewHolder :ViewHolder

    init {
        inflater =LayoutInflater.from(context)
    }

    inner class ViewHolder(){
        lateinit var  imageView:GridImageView
        lateinit var  progressBar:ProgressBar


    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        tekSutunResim =convertView

        if(tekSutunResim== null){
            tekSutunResim=inflater.inflate(R.layout.tek_sutun_grid_resim,parent,false)
            viewHolder=ViewHolder()
            //tek bir view holder nesnesi olacağı için belleği etkili bir şekilde kullanırız
            viewHolder.imageView=tekSutunResim!!.imgTekSutunImage
            viewHolder.progressBar=tekSutunResim!!.progressBar

            //view holder nesenesini teksutunresime tuttururuz böylece else kısmında da view holder nesnesinden yararlanırız.
            tekSutunResim!!.setTag(viewHolder)
        }
        else{

            viewHolder= tekSutunResim!!.getTag() as ViewHolder




        }



UniversalImageLoader.setImage(klasordekiDosyalar.get(position),viewHolder.imageView,viewHolder.progressBar,"file:/")



        return  tekSutunResim!!




    }






}