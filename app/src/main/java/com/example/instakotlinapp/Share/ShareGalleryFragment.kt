package com.example.instakotlinapp.Share

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.GridLayoutManager

import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.*
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_gallery.*
import kotlinx.android.synthetic.main.fragment_share_gallery.view.*
import org.greenrobot.eventbus.EventBus

/**
 * A simple [Fragment] subclass.
 */
class ShareGalleryFragment : Fragment() {
    var secilenResimYolu:String?=null
    var dosyaTuruResimMi:Boolean?=null

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

        //en son açık kalan dosya gösterilir
        view.spnKlasorAdları.setSelection(0)



        view.spnKlasorAdları.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            // whatsappa veya indirilenlere tıklayınca tetiklenir.
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //setupRecycView(Dosyaİslemleri.klasordekiDosyalariGetir(klasorlerYollari.get(position)))
                setupGridView(Dosyaİslemleri.klasordekiDosyalariGetir(klasorlerYollari.get(position)))
          }

        }
        view.tvIleriButton.setOnClickListener {
            activity!!.anaLayout.visibility= View.GONE
            activity!!.fragmentContainerLayout.visibility=View.VISIBLE
            var transaction=activity!!.supportFragmentManager.beginTransaction()
            EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakResmiGonder(secilenResimYolu,dosyaTuruResimMi))
                videoView.stopPlayback()
            transaction.replace(R.id.fragmentContainerLayout, ShareNextFragment())
            transaction.addToBackStack("shareNextFragmentEklendi")
            transaction.commit()
        }

        view.imageView2.setOnClickListener {

            activity!!.onBackPressed()

        }




        return view
    }

   /* private fun setupRecycView(klasordekiDosyalar: ArrayList<String>) {

        var recyclViewAdapter=ShareGalleryRecyclerAdapter(klasordekiDosyalar,this.activity!!)
        recycleViewDosyalar.adapter=recyclViewAdapter


        var layoutManager =GridLayoutManager(this.activity,4)
        recycleViewDosyalar
    }*/


    fun setupGridView(secilenKlasordekiDosyalar :ArrayList<String>){
        var gridAdapter = SharActivityGridViewAdapter(activity!!, R.layout.tek_sutun_grid_resim, secilenKlasordekiDosyalar)
        recycleViewDosyalar.adapter = gridAdapter

        secilenResimYolu=secilenKlasordekiDosyalar.get(0)
        resimVeyaVideoGoster(secilenKlasordekiDosyalar.get(0))


        recycleViewDosyalar.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            //seçilen resmi göster-me
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                secilenResimYolu=secilenKlasordekiDosyalar.get(position)
                resimVeyaVideoGoster(secilenKlasordekiDosyalar.get(position))

            }







            })

        }

    private fun resimVeyaVideoGoster(dosyaYolu: String) {
        var dosyaTuru=dosyaYolu.substring(dosyaYolu.lastIndexOf("."))
        if(dosyaTuru != null){
            if( dosyaTuru.equals(".mp4")){
                videoView.visibility=View.VISIBLE
                imgCropView.visibility=View.GONE
                dosyaTuruResimMi=false
                videoView.setVideoURI(Uri.parse("file://"+dosyaYolu))
                Log.e("HATA","Video : "  +"file://"+dosyaYolu)
                videoView.start()
            }
            else{
                videoView.visibility=View.GONE
                imgCropView.visibility=View.VISIBLE
                dosyaTuruResimMi=true
                UniversalImageLoader.setImage(dosyaYolu,imgCropView,null,"file://")
            }


        }



    }
}





