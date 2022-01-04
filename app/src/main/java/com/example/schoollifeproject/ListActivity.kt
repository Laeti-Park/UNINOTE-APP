package com.example.schoollifeproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.schoollifeproject.databinding.ActivityListBinding

class ListActivity : AppCompatActivity() {

    //    샘플
    val contactsList: List<Contacts> = listOf(
        Contacts("top", "khan", "20220104"),
        Contacts("jg", "canyon", "20220104"),
        Contacts("mid", "faker", "20220104"),
        Contacts("ad", "viper", "20220104"),
        Contacts("sup", "keria", "20220104")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ContactsListAdapter(contactsList)
        binding.recyclerView.adapter = adapter

    }
}