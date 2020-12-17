package id.co.belajar.kulinerappn

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.CalendarView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.activity_entry.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.item_lokasi.*
import org.json.JSONObject
import java.util.*

class EntryActivity : AppCompatActivity() {
    //variable untuk google location
    lateinit var lokasiTerkahir : Location
    lateinit var latitude : String
    lateinit var longtitude : String
    lateinit var fusedLocationProviderClient : FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        //untuk judul Bar
        supportActionBar?.title = "Entry Kuliner Top"
        //untuk menampilkan icon back
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //inisiasi location service
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        btsimpan.setOnClickListener {

            val kalender = Calendar.getInstance().time
            val strNama = btnamaku.text.toString().replace(" ", "%20")
            val strKontributor = etkontributor.text.toString().replace(" ", "%20")
            val strKeterangan = kalender.time.toString().replace(" ", "%20")
            val strLat = btlatitud.text.toString()
            val strLon = btlongtitud.text.toString()

            EntryData().execute(strNama, strKeterangan, strKontributor, strLat, strLon)
            }
    }
    //ambil lokasi
    override fun onStart() {
        super.onStart()
        getLastLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnCompleteListener(this,
                OnCompleteListener<Location> { task ->
                    if (task.isSuccessful && task.result != null) {
                        lokasiTerkahir = task.result
                        latitude = lokasiTerkahir.latitude.toString()
                        longtitude = lokasiTerkahir.longitude.toString()
                        Toast.makeText(
                            this@EntryActivity,
                            "lat: $latitude, lng: $longtitude",
                            Toast.LENGTH_SHORT
                        ).show()
                        //tampilan latitude & longtitude
                            btlatitud.setText(latitude)
                            btlongtitud.setText(longtitude)
                    }
                })
    }

    //aktivasi icon back
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private inner class EntryData : AsyncTask<String, Void, String>() {
        private var progressDialog = ProgressDialog(this@EntryActivity)

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.setMessage("Mohon Menunggu")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg p0: String?): String {
            //ambil parameter fungsi array pada p0
            val nama = p0[0]
            val keterangan = p0[1]
            val kontributor = p0 [2]
            val lat = p0 [3]
            val lon =  p0[4]


            //koneksi ke API to fungsi NetworkTools.kt
            val response = getJSON("http://wp.garasitekno.com/service/lokasi.php?" +
                    "aksi=simpan&" +
                    "nama=$nama&" +
                    "keterangan=$keterangan&" +
                    "kontributor=$kontributor&" +
                    "lat=$lat&" +
                    "lon=$lon")

            //convert respone ke string
            val jsonObject = JSONObject(response)
            val strStatus = jsonObject.getString("status")


            return strStatus
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (progressDialog.isShowing) progressDialog.dismiss()
            if(result == "success"){
                Toast.makeText(this@EntryActivity, "Entry sukses", Toast.LENGTH_SHORT).show()
                onBackPressed()

            }else{
                Toast.makeText(this@EntryActivity, "Entry tidak sukses", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
