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


//    fun test(user: FirebaseUser?) {
//        if (user != null) {
//            val userDto = GoogleLoginDto(user.email.toString(), user.uid)
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val response = api.communicate().loginGoogleUser(userDto)
//
//                    if (response.isSuccessful && !response.body().isNullOrEmpty()) {
//                        val sharedPreferences = applicationContext.getSharedPreferences(
//                            "JWT", Context.MODE_PRIVATE
//                        )
//                        val editor = sharedPreferences.edit().also {
//                            it.putString("JWT", response.body().toString())
//                            it.apply() // JWT를 저장
//                        }
//                        startActivity(Intent(this@LoginPage, MainActivity::class.java))
//                        //현재 코드는 MainActivity로 가는거임 나중에 LoginActivity로 가면 됨.
//                        finish()
//                    } else {
//                        Log.d("Error", "Error")
//                    }
//                } catch (err: Error) {
//                    Log.d("Error in toMainActivity", err.toString())
//                }
//            }
//        } else {
//            Toast.makeText(this, "구글 회원가입이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }
//    public override fun onStart() {
//        super.onStart()
//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        if (account !== null) { // 이미 로그인 되어있을시 바로 메인 액티비티로 이동
//            toMainActivity(firebaseAuth.currentUser)
//        }
//    } //onStart End
//    fun toMainActivity(user: FirebaseUser?) {
//        if (user != null) { // MainActivity 로 이동
//
//            // 로그인과 동시에 서버로 요청 보내면 됨.
//            val userDto =
//                GoogleRegisterDto(user.email.toString(), user.uid, user.displayName.toString())
//
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val response = api.communicate().registerGoogleUser(userDto)
//
//                    if (response.isSuccessful && response.body() == true) {
//                        startActivity(Intent(this@LoginPage, MainActivity::class.java))
//                        //현재 코드는 MainActivity로 가는거임 나중에 LoginActivity로 가면 됨.
//                        finish()
//                    } else {
//                        Log.d("Error", "Error")
//                    }
//                } catch (err: Error) {
//                    Log.d("Error in toMainActivity", err.toString())
//                }
//            }
//
//
//        }
//    } // toMainActivity End
//
//    // onActivityResult
//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)
//                firebaseAuthWithGoogle(account!!)
//
//            } catch (e: ApiException) {
//                // Google Sign In failed, update UI appropriately
//                Log.w("LoginActivity", "Google sign in failed", e)
//            }
//        }
//    } // onActivityResult End

    // firebaseAuthWithGoogle
//    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
//        Log.d("LoginActivity", "firebaseAuthWithGoogle:" + acct.id!!)
//
//        //Google SignInAccount 객체에서 ID 토큰을 가져와서 Firebase Auth로 교환하고 Firebase에 인증
//        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
//        firebaseAuth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    Log.w("LoginActivity", "firebaseAuthWithGoogle 성공", task.exception)
//                    toMainActivity(firebaseAuth?.currentUser)
//                } else {
//                    Log.w("LoginActivity", "firebaseAuthWithGoogle 실패", task.exception)
////                    Snackbar.make(login_layout, "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT).show()
//                }
//            }
//    }// firebaseAuthWithGoogle END


    //    private fun signIn() {
//        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
//    }
    // 로그인 함수
    private suspend fun login(email: String, password: String): Boolean {
        return try {
            val response = api.communicate().signIn(SignInDto(email, password))
            Log.d("Jwt", response.body()?.accessToken.toString())
            Log.d("email", SignInDto(email, password).email)
            Log.d("response", response.body().toString())

            if (response.body()?.accessToken.toString().isEmpty()) {
                //실패했을 경우
                return false
            } else {
                // 성공했을 경우
                val sharedPreferences =
                    applicationContext.getSharedPreferences("JWT", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("JWT", response.body()?.accessToken).apply()
                return true
            }

        } catch (err: Error) {
            Log.d("error", "Error")
            return false
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