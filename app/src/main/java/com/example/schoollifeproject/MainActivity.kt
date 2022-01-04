package com.example.schoollifeproject
import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.schoollifeproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val TAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnLogin = binding.btnLogin
        val btnRegister = binding.btnRegister

        btnLogin.setOnClickListener {

            val id = binding.editId.text.toString()
            val pw = binding.editPw.text.toString()

            //쉐어드에 저장된 id, pw 가져오기(여기에 서버로부터 전송받는 코드 작성)
            val sharedPreference = getSharedPreferences("tempInfo", Context.MODE_PRIVATE)
            val savedId = sharedPreference.getString("id", "")
            val savedPw = sharedPreference.getString("pw", "")

            if(id == savedId && pw == savedPw){
//                로그인 성공 이벤트
                Toast.makeText(this, "성공", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MenuActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)

            }
            else{
                failDialog()
            }
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }


    }
    fun failDialog(){
        var dialog = AlertDialog.Builder(this)

         dialog.setTitle("로그인 실패")
         dialog.setMessage("아이디와 비밀번호를 확인해주세요")

         val dialog_listener = object: DialogInterface.OnClickListener{
           override fun onClick(dialog: DialogInterface?, which: Int) {
             when(which){
               DialogInterface.BUTTON_POSITIVE ->
               Log.d(TAG, "확인 버튼 클릭")
             }
           }
         }

         dialog.setPositiveButton("확인", dialog_listener)
         dialog.show()
    }
}