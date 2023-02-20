package com.example.picasso

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.picasso.databinding.ActivitySignupPageBinding

class SignupPage : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignupPageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent: Intent = Intent(this, LoginPage::class.java)

        binding.gotoLogin.setOnClickListener{
            startActivity(intent)
        }


        binding.signup.setOnClickListener{
            if(signup()){
                // 성공
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }else{
                // 실패
            }
        }
    }


    //회원가입 함수
    private fun signup(): Boolean{
        //회원가입 로직
        return true
    }
}