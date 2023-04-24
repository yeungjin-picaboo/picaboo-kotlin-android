package com.example.picasso

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picasso.databinding.ActivityPicturesBinding

class PicturesActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityPicturesBinding.inflate(layoutInflater)
    }
    private val sharedPreference by lazy{
        getSharedPreferences("image", Context.MODE_PRIVATE)
    }

    private val adapter = ChatAdapter2()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        var currentSpan = sharedPreference.getInt("NumOfSpan", 2)

        setLayoutManager(currentSpan)
        binding.recycleView.adapter = adapter
//
//        adapter.setData(DataGenerator.get(1), this)



//        binding.button.setOnClickListener{
//            if(currentSpan < 4){
//                currentSpan += 1
//
//                setNumOfSpan(currentSpan)
//                setLayoutManager(currentSpan)
//            }
//        }
//
//        binding.button2.setOnClickListener{
//            if(currentSpan > 1){
//                currentSpan -= 1
//
//                setNumOfSpan(currentSpan)
//                setLayoutManager(currentSpan)
//            }
//        }

    }

    private fun setNumOfSpan(NumOfSpan: Int){
        with(sharedPreference.edit()){
            putInt("NumOfSpan", NumOfSpan)
            apply()
        }
    }
    private fun setLayoutManager(NumOfSpan: Int){
        binding.recycleView.layoutManager = GridLayoutManager(this, NumOfSpan)
    }

}