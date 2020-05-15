package com.example.instakotlinapp.Share

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.instakotlinapp.Login.LoginActivity
import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.SharePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 3
    private val TAG = "ShareActivity"

    lateinit var mAuthListener: FirebaseAuth.AuthStateListener



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        setupAuthListener()

        storageİzniİste()

        //


    }
        // kullanıcıdan izin isteme kısmı
    private fun storageİzniİste() {
        Dexter.withActivity(this)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

            .withListener(object : MultiplePermissionsListener{
                //kullanıcı bütünizinlere evet veya hayır tıkladıında çalışıyor
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                   //kullanıcı bütün izinleri verirse bu ksım çalışır
                    if(report!!.areAllPermissionsGranted()){
                        Log.e("hata","Tüm izinler verilmiştir")
                        setupShareViewPager()
                    }
                    //izin için kullanıcı bunu bir daha bana sorma dediği kısım
                    if(report.isAnyPermissionPermanentlyDenied){
                        Log.e("hata","İzinlerden birine bir daha sorma denildi")
                        var builder = AlertDialog.Builder(this@ShareActivity)
                        builder.setTitle("İzin Gerekli")
                        builder.setMessage("Ayarlar kısmından uygulamaya izin vermeniz gerekiyor.Onaylıyor musunuz?")
                        builder.setPositiveButton( "AYARLARA GİT",object :DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.cancel()
                                //ayarlara  gitmemizi sağlar
                                var intent =Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                var uri = Uri.fromParts("package",packageName,null)
                                intent.setData(uri)
                               startActivity(intent)

                            }

                        })
                        builder.setNegativeButton("İPTAL",object : DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.cancel()
                               finish()
                            }

                        })
                        builder.show()


                    }
                }

                //bu izne neden ihtiyac duyduğumuzu kullanıcıya açıklama
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                   var builder = AlertDialog.Builder(this@ShareActivity)
                    builder.setTitle("İzin Gerekli")
                    builder.setMessage("Uygulamaya izin vermeniz gerekiyor. Onaylıyor musunuz?")
                    builder.setPositiveButton( "ONAYLA",object :DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog!!.cancel()
                            token!!.continuePermissionRequest()


                        }

                    })
                    builder.setNegativeButton("İPTAL",object : DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog!!.cancel()
                          token!!.cancelPermissionRequest()
                            finish()
                        }


                    })
                    builder.show()




                }

            })
            ///dexter da hata olursa bu kısım çalışır
            .withErrorListener(object  :PermissionRequestErrorListener{
                override fun onError(error: DexterError?) {
                   Log.e( "hata",error!!.toString()  )
                }

            }) .check()






    }

    // activitye fragmentleri eklememizi sağlar
    private fun setupShareViewPager() {

        var tabAdı = ArrayList<String>()
        tabAdı.add("Galeri")
        var sharePagerAdapter = SharePagerAdapter(supportFragmentManager,tabAdı)
        sharePagerAdapter.addFragment(ShareGalleryFragment())

        shareViewPager.adapter=sharePagerAdapter
        shareTabLayout.setupWithViewPager(shareViewPager)
    }

    override fun onBackPressed() {
        anaLayout.visibility= View.VISIBLE
        fragmentContainerLayout.visibility=View.GONE
        super.onBackPressed()
    }
    private fun setupAuthListener() {

        mAuthListener=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser
                if(user==null){
                    //kullanıcı doğrulandıysa homeaktivitye geç
                    var intent=
                        Intent(this@ShareActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish() //homeaktivityden geri tuşuna basınca logine geri döndürüyor

                }else{

                }

            }

        }
    }

}


