package com.example.picasso

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.picasso.api.ApiService
import com.example.picasso.databinding.ActivityLoginPageBinding
import com.example.picasso.dto.sign.SignInDto
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginPageBinding.inflate(layoutInflater)
    }
    private val api = ApiService

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

                    // ログイン成功時
                    Toast.makeText(applicationContext, "ログインに成功しました！", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SignInActivity, gallery::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    // ログイン失敗時
                    Toast.makeText(applicationContext, "ログインに失敗しました！", Toast.LENGTH_SHORT).show()
                }
            }

        }

        binding.gotoRegister1.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
//        binding.login2.setOnClickListener { test(firebaseAuth.currentUser) }
    }

    /**
     * ログイン関数
     */
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
}
