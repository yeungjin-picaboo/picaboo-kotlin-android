package com.example.picasso

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.picasso.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.internal.InternalTokenProvider
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject
import java.lang.NumberFormatException
import kotlin.math.log

class MainActivity : AppCompatActivity(), View.OnClickListener {
    var queue: RequestQueue? = null
    lateinit var dataText: TextView
    var googleSignInClient: GoogleSignInClient? = null

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
//    private val binding:ActivityMainBinding by lazy`` {
//        ActivityMainBinding.inflate(layoutInflater)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        //초기화
        if (queue == null) {
            queue = Volley.newRequestQueue(this)
        }
//        dataText = findViewById(R.id.dataText)
        dataText = binding.dataText
        val requestBtn: Button = findViewById(R.id.requestBtn)

        //버튼 클릭 이벤트
        requestBtn.setOnClickListener {
            //http 호출
            try {
                val intent = Intent(this, DiaryActivity::class.java)
                startActivity(intent)
            } catch (e: Throwable) {
                Log.d("test", "${e}")
            }
        }
        binding.DiaryBtn.setOnClickListener {

            try {
                val intent = Intent(this,DiaryActivity::class.java)
//                val intent = Intent(this, StatisticsActivity::class.java)
                startActivity(intent)
            } catch (e: Throwable) {
                Log.d("test", "${e}")
            }
        }
        binding.LoginBtn.setOnClickListener {
            val intenttest = Intent(this, LoginPage::class.java)
            startActivity(intenttest)
        }
        binding.Login2Button.setOnClickListener {
            try {
//
//                val intent = Intent(this, gallery::class.java)
//                startActivity(intent)
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
            } catch (e: NumberFormatException) {

            }
        }
        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            googleSignInClient?.signOut()

            var logoutIntent = Intent(this, MainActivity::class.java)
            logoutIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(logoutIntent)
        }
        binding.importUserInfo.setOnClickListener {
            var auth = FirebaseAuth.getInstance().currentUser
            var uid = auth?.uid
            var email = auth?.email
            var displayName = auth?.displayName
            Log.v("myDevice", "Build.VERSION.CODENAME = " + Build.VERSION.CODENAME);
            Log.v("myDevice", "Build.VERSION.INCREMENTAL = " + Build.VERSION.INCREMENTAL);
            Log.v("myDevice", "Build.VERSION.RELEASE = " + Build.VERSION.RELEASE);
            Log.v("myDevice", "Build.VERSION.SDK = " + Build.VERSION.SDK);
            Log.v("myDevice", "Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT);
            Log.d("UserInfo : ", "$auth")
            Log.d("UserInfo : ", "$uid")
            Log.d("UserInfo : ", "$email")
            Log.d("UserInfo : ", "$displayName")

            val jsonObject = JSONObject()
            jsonObject.put("email", "$email")
            jsonObject.put("nickName", "$displayName")
//            val body = JsonParser.parseString(jsonObject.toString()) as JsonObject
            postConnection(jsonObject)
//            Log.d("jsonObj : " , jsonObject)



        }
    }

    private fun httpConnection() {

        //데이터 요청할 url 주소
        val url: String = "http://10.0.2.2:3000"

        val stringRequest: StringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                //api 호출해서 받아온 값: response
                dataText.text = "Response: $response"
            },
            { error ->
                //에러 발생시 실행
                dataText.text = "Error: $error"
            })

        //url 호출 등록
        queue?.add(stringRequest)
    }

    private fun postConnection(whatIwant: JSONObject) {
        val url: String = "http://10.0.2.2:3000/google-user"

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            Response.Listener { response ->
                dataText.text = "Response: $response"
            }, Response.ErrorListener { error ->
                dataText.text = "Error: $error"
            }) {
//            // request 시 key, value 보낼 때
//            override fun getParams(): Map<String, String> {
//                val params = mutableMapOf<String, String>()
//                params["email"] = "$whatIwant"
//                Log.d("params : ", "$params")
//                return params
//            }
            override fun getParams(): MutableMap<String, String>? {
                val params = mutableMapOf<String,String>()
                params["email"] = "${whatIwant["email"]}"
                params["nick_name"] ="${whatIwant["nickName"]}"
                Log.d("params","$params")
                return params
            }
        }
        Log.d("request","$request")
        queue?.add(request)
    }

    override fun onClick(v: View?) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}