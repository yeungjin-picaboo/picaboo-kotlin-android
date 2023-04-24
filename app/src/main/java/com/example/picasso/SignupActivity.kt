package com.example.picasso

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.picasso.api.WeatherService
import com.example.picasso.databinding.ActivitySignupPageBinding
import com.example.picasso.dto.SignUpDto
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignupPageBinding.inflate(layoutInflater)
    }

    val api = WeatherService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent: Intent = Intent(this, SignInActivity::class.java)

        binding.gotoLogin.setOnClickListener {
            startActivity(intent)
        }



        binding.signup.setOnClickListener {
            val signUpDto = SignUpDto(
                binding.editTextEmailAddress.text.toString(),
                binding.editTextPassword.text.toString(),
                binding.editTextNickname.text.toString()
            )
            lifecycleScope.launch {
                if (signup(signUpDto)) {
                    // 성공
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                } else {
                    // 실패
                }
            }

        }
    }
//    private fun validation(): validationResult{
//        val pwd = binding.editTextTextPassword
//        val pwdConfirm = binding.editTextTextPasswordConfirm
//
//        val email = binding.editTextTextEmailAddress
//        if(pwd != pwdConfirm){
//            return  validationResult(false, "비밀번호가 서로 다릅니다.")
//        }
//        // validation 조건 추가
//
//
//        return validationResult(true, "성공")
//    }

    //회원가입 함수
    private suspend fun signup(signUpDto: SignUpDto): Boolean {
        //회원가입 로직
        val response = api.communicate().signup(signUpDto)
        //무결성 검사를 해야함.
        return if (response.isSuccessful) {
            Log.d("body : ", response.body().toString())
            if (response.body()?.message == "Success Create user!") {
                Log.d("SignUp success", "success")
                true
            } else {
                Log.d("SignUp false", "false")
                false
            }
        } else {
            Log.d("실패함 : ", response.body().toString())
            false
        }

    }
}