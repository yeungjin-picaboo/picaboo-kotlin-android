package com.example.picasso

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.picasso.databinding.ActivitySignupPageBinding

data class validationResult(val state: Boolean, val message: String)

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

        binding.editTextTextEmailAddress
        binding.editTextTextPassword


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
    private fun validation(): validationResult{
        val pwd = binding.editTextTextPassword
        val pwdConfirm = binding.editTextTextPasswordConfirm

        val email = binding.editTextTextEmailAddress
        if(pwd != pwdConfirm){
            return  validationResult(false, "비밀번호가 서로 다릅니다.")
        }
        // validation 조건 추가


        return validationResult(true, "성공")
    }

    //회원가입 함수
    private fun signup(): Boolean{
        //회원가입 로직
        return true
    }
}