package com.example.picasso

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.picasso.databinding.ActivityLoginPageBinding

class LoginPage : AppCompatActivity() {
    private val binding by lazy{
        ActivityLoginPageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent: Intent = Intent(this, SignupPage::class.java)

        binding.gotoRegister.setOnClickListener{
            startActivity(intent)
        }
    
        binding.login.setOnClickListener{
            if(login()){
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
}