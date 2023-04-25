package com.example.picasso

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.os.IResultReceiver._Parcel
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.picasso.api.WeatherService
import com.example.picasso.databinding.ActivityLoginPageBinding
import com.example.picasso.dto.SignInDto
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginPageBinding.inflate(layoutInflater)
    }
    private val api = WeatherService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val email = binding.editTextEmailAddress.text
        val password = binding.editTextPassword.text

        binding.login.setOnClickListener {

            lifecycleScope.launch {
                Log.d("Login = ", login(email.toString(), password.toString()).toString())
                if (login(email.toString(), password.toString())) {
                    val myValue = getSharedPreferences("JWT", 0)
                    Log.d("myValue : ", myValue.getString("JWT", "false")!!)

                    // 로그인에 성공했을 때
                    Toast.makeText(applicationContext, "Login Success!!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SignInActivity, gallery::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    // 로그인에 실패했을 때
                    Toast.makeText(applicationContext, "Login Failed!!", Toast.LENGTH_SHORT).show()
                }
            }

        }

        binding.gotoRegister1.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
//        binding.login2.setOnClickListener { test(firebaseAuth.currentUser) }
    }


    // 로그인 함수
    private suspend fun login(email: String, password: String): Boolean {
        return try {
            val response = api.communicate().signIn(SignInDto(email, password))
            Log.d("Jwt", response.body().toString())
            Log.d("email", SignInDto(email, password).email)
            Log.d("response", response.body().toString())

            if (response.body()?.accessToken.isNullOrEmpty()) {
                false
            } else {
                val sharedPreferences =
                    applicationContext.getSharedPreferences("JWT", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("JWT", response.body()?.accessToken).apply()
                true
            }

        } catch (err: Error) {
            Log.d("error", "Error")
            false
        }
    }

//    private fun postConnection(email: String, password: String) {
//        val url: String = "http://10.0.2.2:8080/post"
//
//        val request = object : StringRequest(
//            Method.POST,
//            url,
//            Response.Listener { response ->
//                //dataText.text = "Response: $response"
//                Log.d("test", "success")
//            }, Response.ErrorListener { error ->
//                Log.d("test", "${error}")
//            }) {
//            // request 시 key, value 보낼 때
//            override fun getParams(): Map<String, String> {
//                val params = mutableMapOf<String,String>()
//                params["email"] = email
//                params["password"] = password
//                return params
//            }
//
//        }
//
//        queue?.add(request)
//    }
}