package id.co.belajar.kulinerappn

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.item_lokasi.*
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        //untuk judul Bar
        supportActionBar?.title = "List Menu Top"
        //untuk menampilkan icon back
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //meminta ijin lokasi
/*      ActivityCompat.requestPermissions(this@MainActivity,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), 1)*/

        //meminta ijin lokasi v.2
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), 1)
        }

        //pull down refresh
        swipeRefresh.setOnRefreshListener {
            GetData().execute()
        }

        //ambil data
        GetData().execute()

        //tombol menuju activity_entry
        faAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, EntryActivity::class.java)
            startActivity(intent)
        }
    }



    //aktivasi icon back
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //panggil data ke server
    private inner class GetData : AsyncTask<String, Void, String>() {
        private var datadata = ArrayList<LinkedHashMap<String, String>>()
        private var progressDialog = ProgressDialog(this@MainActivity)

        override fun onPreExecute() {
            super.onPreExecute()
            //progressDialog.setMessage("Mohon Menunggu")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg p0: String?): String {
            //koneksi ke API to fungsi NetworkTools.kt
            val response = getJSON("http://wp.garasitekno.com/service/lokasi.php")

            //convert respone ke string
            val jsonObject = JSONObject(response)
            val strStatus = jsonObject.getString("status")

            //jika total mengambil semua
            //val jsonArrayLokasi = JSONArray(response)

            //jika mengambil dadalam data
            val jsonArrayLokasi = jsonObject.getJSONArray("data")
            datadata = jsonArrayToList(jsonArrayLokasi)

            //njupuk id tok rek
            //val ambilah = jsonObject.getString("id")

            return strStatus
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(swipeRefresh.isRefreshing) swipeRefresh.isRefreshing = false
            if (progressDialog.isShowing) progressDialog.dismiss()
            if(result == "success"){
                //tampilkan list View
                    // Log.d("LIST_LOKASI", datadata.toString())
                //tampilkan list view
                val lokasiAdapter = SimpleAdapter(this@MainActivity, datadata, R.layout.item_lokasi,
                    arrayOf("nama", "keterangan", "kontributor", "lat", "lon"),
                    intArrayOf(R.id.textViewNama, R.id.textViewKeterangan, R.id.textViewKontributor, R.id.textViewLat, R.id.textViewLon  )
                )
                listViewLokasi.adapter = lokasiAdapter

            }
        }


    }
}
