package com.example.picasso

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.picasso.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var queue: RequestQueue? = null
    lateinit var dataText: TextView

    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //초기화
        if(queue == null){
            queue = Volley.newRequestQueue(this)
        }

//        dataText = findViewById(R.id.dataText)
        dataText = binding.dataText
        val requestBtn: Button = findViewById(R.id.requestBtn)

        //버튼 클릭 이벤트
        requestBtn.setOnClickListener {
            //http 호출
            httpConnection()
        }
    }

//    val stringRequest = object : StringRequest(
//        Request.Method.GET,
//        "https://localhost:5000",
//        Response.Listener<String> { // 서버로부터 결과를 받을 때 호출할콜백
//            Log.d("kkang", "server data : $it")
//        },
//        Response.ErrorListener { error -> // 서버 연동 실패할 시
//            Log.d("kkang", "error......$error")
//        }
//    )
//
    private fun httpConnection(){

        //데이터 요청할 url 주소
        val url: String =  "http://10.0.2.2:3000"

        val stringRequest: StringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                //api 호출해서 받아온 값: response
                dataText.text = "Response: $response"
            },
            { error ->
                //에러 발생시 실행
                dataText.text = "Error: $error"
            })

        //url 호출 등록
        queue?.add(stringRequest)
    }
}