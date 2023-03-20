package com.example.picasso

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.picasso.databinding.ActivityLoginPageBinding

class LoginPage : AppCompatActivity() {
    private val binding by lazy{
        ActivityLoginPageBinding.inflate(layoutInflater)
    }

    var queue: RequestQueue? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent: Intent = Intent(this, SignupPage::class.java)

        binding.gotoRegister.setOnClickListener{
            startActivity(intent)
        }
    
        binding.login.setOnClickListener{
            if(queue == null){
                queue = Volley.newRequestQueue(this)
            }

            if(login()){
                postConnection("test", "password")
                //성공시
            }else{
                //실패시
            }
        }

    }
    
    // 로그인 함수
    private fun login(): Boolean{
        return true
    }

    private fun postConnection(email: String, password: String){
        val url: String = "http://10.0.2.2:8080/post"

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            Response.Listener {
                    response ->
                //dataText.text = "Response: $response"
                              Log.d("test", "success")
            }, Response.ErrorListener {
                   error ->
                Log.d("test", "${error}")
            }) {
            // request 시 key, value 보낼 때
            override fun getParams(): Map<String, String> {
                val params = mutableMapOf<String,String>()
                params["email"] = email
                params["password"] = password
                return params
            }

        }

        queue?.add(request)
    }
}