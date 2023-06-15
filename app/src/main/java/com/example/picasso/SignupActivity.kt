package com.example.picasso

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.picasso.api.ApiService
import com.example.picasso.databinding.ActivitySignupPageBinding
import com.example.picasso.dto.sign.SignUpDto
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    // bindingは遅延評価（lazy）で初期化します。
    // これはActivitySignupPageBindingのインスタンスを生成します。
    private val binding by lazy {
        ActivitySignupPageBinding.inflate(layoutInflater)
    }

    val api = ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // レイアウトを設定します。
        setContentView(binding.root)

        // SignInActivityへのIntentを作成します。
        val intent: Intent = Intent(this, SignInActivity::class.java)

        // ログイン画面へ移動するボタンのクリックイベントを設定します。
        binding.gotoLogin.setOnClickListener {
            startActivity(intent)
        }

        // サインアップボタンのクリックイベントを設定します。
        binding.signup.setOnClickListener {
            // 入力値を取得してSignUpDtoを作成します。
            val signUpDto = SignUpDto(
                binding.editTextEmailAddress.text.toString(),
                binding.editTextPassword.text.toString(),
                binding.editTextNickname.text.toString()
            )
            // 非同期処理を行います。
            lifecycleScope.launch {
                if (signup(signUpDto)) {
                    // サインアップ成功
                    // タスクを新規に作成し、以前のタスクを全てクリアしてからSignInActivityを開始します。
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                } else {
                    // サインアップ失敗
                }
            }
        }
    }

    // signupメソッドはサインアップ処理を行います。
    private suspend fun signup(signUpDto: SignUpDto): Boolean {
        // サインアップリクエストを送信します。
        val response = api.communicate().signup(signUpDto)
        // 成功したかどうかを判定します。
        return if (response.isSuccessful) {
            Log.d("body : ", response.body().toString())
            if (response.body()?.message == "Success Create user!") {
                // サインアップ成功
                Log.d("SignUp success", "success")
                true
            } else {
                // サインアップ失敗
                Log.d("SignUp false", "false")
                false
            }
        } else {
            // リクエスト自体が失敗した場合
            Log.d("失敗 : ", response.body().toString())
            false
        }
    }
}