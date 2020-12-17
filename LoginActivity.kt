package id.co.belajar.kulinerappn

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.text.trimmedLength
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.etemail
import kotlinx.android.synthetic.main.activity_login.etpassword
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //hidden action bar
        supportActionBar?.hide()
        //hidden status bar
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        tvregister.paint?.isUnderlineText = true

        btlogin.setOnClickListener {
            val strEmail = etemail.text.toString()
            val strPassword = etpassword.text.toString()

            if( strEmail != "" && strPassword !=""){
                CheckUser().execute(strEmail,strPassword)
                //var new = Intent(this@LoginActivity, MainActivity::class.java)
                //startActivity(new)
            }else{Toast.makeText(
                this@LoginActivity, "Email/Password belum terisi",
                Toast.LENGTH_SHORT).show()}
                }

        tvregister.setOnClickListener {
            var intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    //async task login
    private inner class CheckUser : AsyncTask<String, Void, String>() {
        private var progressDialog = ProgressDialog(this@LoginActivity)

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.setMessage("Mohon Menunggu")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg p0: String?): String {
            //ambil parameter fungsi array pada p0
            val email = p0 [0]
            val password = p0 [1]

            //koneksi ke API to fungsi NetworkTools.kt
            val response = getJSON("http://192.3.168.178/flutter/login.php?" +
                    "email=$email&" +
                    "password=$password")

            //convert respone ke string
            val jsonObject = JSONObject(response)
            val strStatus = jsonObject.getString("status")

            return strStatus
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (progressDialog.isShowing) progressDialog.dismiss()

            if(result == "success"){
                Toast.makeText(this@LoginActivity, "Login sukses", Toast.LENGTH_SHORT).show()
                var new = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(new)
            }else{
                Toast.makeText(this@LoginActivity, "Email/Password salah", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
