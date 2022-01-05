package com.example.schoollifeproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.schoollifeproject.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnMap = binding.btnMap
        val btnNotice = binding.btnNotice
        val tvNick = binding.tvNick

        tvNick.setText(intent.getStringExtra("id")+"님 환영합니다")

        btnNotice.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        }

        btnMap.setOnClickListener {
            val intent = Intent(this, MindMapActivity::class.java)
            startActivity(intent)
        }

    }
}