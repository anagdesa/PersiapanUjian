package id.co.belajar.kulinerappn

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.etemail
import kotlinx.android.synthetic.main.activity_register.etpassword
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        var strRole = ""
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //untuk mengubah judul di slide bar
        supportActionBar?.title = "Silahkan Registrasi"
        //untuk memberi icon back to home
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /*//button listener punya bapake
        val listRole = arrayOf("Jadi", "Pergi", "Pulang")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listRole
        )

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        srole.adapter = adapter
        */

        //perintah dropdown
        val strspinner = arrayOf("Guru", "Dosen", "Mahasiswa")
        srole.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,strspinner)

        srole.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                strRole = p0?.getItemAtPosition(p2).toString()
            }

        }

        //default jenis kelamin
        rgJenisK.check(rlaki2.id)

        //button
        btregister.setOnClickListener {
            val strNama = etnamal.text.toString().replace(" ", "%20")
            val strUmur = etumur.text.toString()
            val strEmail = etemail.text.toString()
            val strPsswrd = etpassword.text.toString()

            //variable untuk radio group button
            val intRadioGroup: Int = rgJenisK!!.checkedRadioButtonId
            val rbJenisK: RadioButton = findViewById(intRadioGroup)
            val strJenisK = rbJenisK.text.toString()

            //RegisterUser().execute(strNama, strUmur, strEmail, strPsswrd, strJenisK, strRole)

            //menggabungkan variable string
            //val strTampil = "$strNama, $strUmur $strEmail, $strPsswrd, $strJenisK, $strRole"

            //condition tampilan menggunakan when

            when {
                strNama != "" && strUmur != "" && strEmail != "" && strPsswrd !=""
                -> RegisterUser().execute(strNama, strUmur, strEmail, strPsswrd, strJenisK, strRole)
            else ->
                Toast.makeText(this@RegisterActivity, "Harus diisi Semua", Toast.LENGTH_SHORT).show()
            }

            /* //if condition
            if (etnamal.length()==0){
                etnamal.setError("masukkan nama")
                }
            else if ( etemail.length()==0){
                etemail.setError("masukkan email")
                }
            else if (etpassword.length()==0){
                etpassword.setError("masukkan password")}
            else{
            //menampilkan
                Toast.makeText(this@RegisterActivity, strTampil, Toast.LENGTH_SHORT).show()}*/
        }

        //spinner
        //srole.adapter = ArrayAdapter<String>(this,android)
    }
    //untuk aktivasi tombok back to home
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    //async task login
    private inner class RegisterUser : AsyncTask<String, Void, String>() {
        private var progressDialog = ProgressDialog(this@RegisterActivity)

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.setMessage("Mohon Menunggu")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg p0: String?): String {
            //ambil parameter fungsi array pada p0
            val nama = p0[0]
            val umur = p0[1]
            val email = p0 [2]
            val password = p0 [3]
            val jenisK =  p0[4]
            val role = p0[5]

            //koneksi ke API to fungsi NetworkTools.kt
            val response = getJSON("http://192.3.168.178/flutter/insert_user.php?" +
                    "nama=$nama&" +
                    "umur=$umur&" +
                    "email=$email&" +
                    "password=$password&" +
                    "sex=$jenisK&" +
                    "role=$role")

            //convert respone ke string
            val jsonObject = JSONObject(response)
            val strStatus = jsonObject.getString("status")

            return strStatus
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (progressDialog.isShowing) progressDialog.dismiss()

            if(result == "success"){
                //Log.
                Toast.makeText(this@RegisterActivity, "Registrasi sukses", Toast.LENGTH_SHORT).show()
                finish()
            //}else{
                //Toast.makeText(this@RegisterActivity, "Isi seluruh kolom", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
