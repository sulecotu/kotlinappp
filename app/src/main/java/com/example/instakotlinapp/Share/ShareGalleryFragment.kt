package com.example.instakotlinapp.Share

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.Dosyaİslemleri
import com.example.instakotlinapp.utils.SharActivityGridViewAdapter
import com.example.instakotlinapp.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.fragment_share_gallery.*
import kotlinx.android.synthetic.main.fragment_share_gallery.view.*

/**
 * A simple [Fragment] subclass.
 */
class ShareGalleryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_share_gallery, container, false)

        //gridviewda göstermek istediğimiz farklı klasördeki resimleri göstermek için
        var klasorlerYollari = ArrayList<String>()
        var klasorAdlari = ArrayList<String>()

        var root = Environment.getExternalStorageDirectory().path
        Log.e("hata", root)
        var cameraResimler = root + "/DCIM/Camera"
        var indirilenResimler = root + "/Download"
        var whatsappResimleri = root + "/WhatsApp/Media/WhatsApp Images"

        klasorlerYollari.add(cameraResimler)
        klasorlerYollari.add(indirilenResimler)
        klasorlerYollari.add(whatsappResimleri)

        klasorAdlari.add("Kamera")
        klasorAdlari.add("İndirilenler")
        klasorAdlari.add("WhatsApp")

        //spinnera atama işlemi
        var spinnerArrayAdapter =
            ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, klasorAdlari)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.spnKlasorAdları.adapter = spinnerArrayAdapter


        view.spnKlasorAdları.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            // whatsappa veya indirilenlere tıklayınca tetiklenir.
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                setupGridView(Dosyaİslemleri.klasordekiDosyalariGetir(klasorlerYollari.get(position)))
          }

        }






        return view
    }
    fun setupGridView(secilenKlasordekiDosyalar :ArrayList<String>){
        var gridAdapter = SharActivityGridViewAdapter(activity!!, R.layout.tek_sutun_grid_resim, secilenKlasordekiDosyalar)
        gridviewResimler.adapter = gridAdapter


        gridviewResimler.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            //seçilen resmi göster-me
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                UniversalImageLoader.setImage(secilenKlasordekiDosyalar.get(position),imgBuyukResim,null,"file:/")
            }


        })

    }

}
