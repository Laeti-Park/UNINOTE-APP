package com.example.schoollifeproject

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.schoollifeproject.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    val TAG: String = "Register"
    var isExistBlank = false
    var isPWSame = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnRegister = binding.btnRegister

        btnRegister.setOnClickListener {
            Log.d(TAG, "회원가입 버튼 클릭")

            val id = binding.editId.text.toString()
            val pw = binding.editPw.text.toString()
            val pw_re = binding.editPwRe.text.toString()

            //텍스트를 채우지 않았을 때
            if (id.isEmpty() || pw.isEmpty() || pw_re.isEmpty()) {
                isExistBlank = true
            } else {
                if (pw == pw_re) isPWSame = true
            }

            if (!isExistBlank && isPWSame) {
                Toast.makeText(this, "가입성공", Toast.LENGTH_SHORT).show()

                //쉐어드에 저장(이 부분에 회원가입 정보 서버전송 코드 작성)
                val sharedPreference = getSharedPreferences("tempInfo", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putString("id", id)
                editor.putString("pw", pw)
                editor.apply()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                if (isExistBlank) {
                    dialog("blank")
                } else if (!isPWSame) {
                    dialog("not same")
                }
            }
        }
    }


    //회원가입 오류시 띄우는 다이얼로그
    fun dialog(type: String) {
        val dialog = AlertDialog.Builder(this)

        if(type.equals("blank")){
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("입력란을 모두 작성해주세요")
        }
        else if(type.equals("not same")){
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("비밀번호를 정확히 입력해주세요")
        }

        val dialog_litener = object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when(p1){
                    DialogInterface.BUTTON_POSITIVE ->
                        Log.d(TAG, "다이얼로그")
                }
            }
        }

        dialog.setPositiveButton("확인", dialog_litener)
        dialog.show()
    }
}