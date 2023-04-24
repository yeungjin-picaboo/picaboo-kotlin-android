package com.example.picasso

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.picasso.api.WeatherService
import com.example.picasso.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity(), View.OnClickListener {
    var queue: RequestQueue? = null
    lateinit var dataText: TextView

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val api = WeatherService

        lifecycleScope.launch {
            val a = api.communicateJwt(this@MainActivity).validateLogin()
            if (a.isSuccessful) {
                //로그인 성공시 ( JWT 토큰이 유효할 때 )
                Log.d("JWTLoginResultSuccess", a.toString())
                val intent = Intent(this@MainActivity, gallery::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.d("JWTLoginResultSuccess", a.toString())
            }
        }

        binding.ButtonRegister.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }


        //초기화
        if (queue == null) {
            queue = Volley.newRequestQueue(this)
        }
//        dataText = findViewById(R.id.dataText)
//        dataText = binding.dataText
//        val requestBtn: Button = findViewById(R.id.requestBtn)
//
//        //버튼 클릭 이벤트
//        requestBtn.setOnClickListener {
//            //http 호출
//            try {
//                val intent = Intent(this, DiaryActivity::class.java)
//                startActivity(intent)
//            } catch (e: Throwable) {
//                Log.d("test", "${e}")
//            }
//        }
//        binding.DiaryBtn.setOnClickListener {
//
//            try {
//                val intent = Intent(this,DiaryActivity::class.java)
////                val intent = Intent(this, StatisticsActivity::class.java)
//                startActivity(intent)
//            } catch (e: Throwable) {
//                Log.d("test", "${e}")
//            }
//        }
//        binding.LoginBtn.setOnClickListener {
//            val intenttest = Intent(this, LoginPage::class.java)
//            startActivity(intenttest)
//        }
//        binding.Login2Button.setOnClickListener {
//            try {
////
////                val intent = Intent(this, gallery::class.java)
////                startActivity(intent)
//                val intent = Intent(this,LoginActivity::class.java)
//                startActivity(intent)
//            } catch (e: NumberFormatException) {
//
//            }
//        }
//        binding.logoutButton.setOnClickListener {
////            FirebaseAuth.getInstance().signOut()
////            googleSignInClient?.signOut()
////
////            var logoutIntent = Intent(this, MainActivity::class.java)
////            logoutIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
////            startActivity(logoutIntent)
//            var intent = Intent(this,gallery::class.java)
//            startActivity(intent)
//
//        }
//        binding.importUserInfo.setOnClickListener {
////            var auth = FirebaseAuth.getInstance().currentUser
////            var uid = auth?.uid
////            var email = auth?.email
////            var displayName = auth?.displayName
////            Log.v("myDevice", "Build.VERSION.CODENAME = " + Build.VERSION.CODENAME);
////            Log.v("myDevice", "Build.VERSION.INCREMENTAL = " + Build.VERSION.INCREMENTAL);
////            Log.v("myDevice", "Build.VERSION.RELEASE = " + Build.VERSION.RELEASE);
////            Log.v("myDevice", "Build.VERSION.SDK = " + Build.VERSION.SDK);
////            Log.v("myDevice", "Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT);
////            Log.d("UserInfo : ", "$auth")
////            Log.d("UserInfo : ", "$uid")
////            Log.d("UserInfo : ", "$email")
////            Log.d("UserInfo : ", "$displayName")
////
////            val jsonObject = JSONObject()
////            jsonObject.put("email", "$email")
////            jsonObject.put("nickName", "$displayName")
//////            val body = JsonParser.parseString(jsonObject.toString()) as JsonObject
////            postConnection(jsonObject)
//////            Log.d("jsonObj : " , jsonObject)
//
//            val intent = Intent(this, DetailDiaryActivity::class.java)
//            intent.putExtra("title","title")
//            intent.putExtra("content","content")
//            intent.putExtra("date","January 2022 12")
//            intent.putExtra("mood","bad")
//            intent.putExtra("weather","Clear")
//            intent.putExtra("diaryId","12")
//
//            startActivity(intent)
//
//
//        }
    }

    override fun onClick(v: View?) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}