package com.example.schoollifeproject

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.schoollifeproject.databinding.ActivityRegisterBinding
import com.example.schoollifeproject.model.APIS
import com.example.schoollifeproject.model.PostModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 회원가입 실행 Activity
 * 작성자 : 이준영
 */

class RegisterActivity : AppCompatActivity() {
    var isExistBlank = false
    var isPWSame = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val api = APIS.create()
        val btnRegister = binding.btnRegister

        /**
         * 회원가입 버튼 클릭리스너
         * */
        btnRegister.setOnClickListener {
            val id = binding.editId.text.toString()
            val pw = binding.editPw.text.toString()
            val rePw = binding.editPwRe.text.toString()
            val name = binding.editName.text.toString()
            val email = binding.editEmail.text.toString()
            //텍스트를 채우지 않았을 때
            if (id.isBlank() || pw.isBlank() || rePw.isBlank() || name.isBlank() || email.isBlank()) {
                isExistBlank = true
            } else {
                if (pw == rePw) isPWSame = true
            }

            if (!isExistBlank && isPWSame) {

                api.register_users(
                    id, pw, name, email
                ).enqueue(object : Callback<PostModel> {
                    override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                        Log.d("onReaspon", "리스폰 성공")
                        if (response.body()?.error.toString() == "ok")
                            dialog("success")
                        else
                            dialog("id same")
                    }

                    override fun onFailure(call: Call<PostModel>, t: Throwable) {}
                })
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

        if (type == "blank") {
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("입력란을 모두 작성해주세요.(공백을 제외해주세요)")
        } else if (type == "not same") {
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("비밀번호를 정확히 입력해주세요.")
        } else if (type == "id same") {
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("중복된 아이디가 존재합니다.")
        } else if (type == "success") {
            dialog.setTitle("회원가입 성공")
            dialog.setMessage("환영합니다.")
        }

        val dialog_litener = DialogInterface.OnClickListener { _, _ ->
            if (type == "success") {
                finish()
            }
        }

        dialog.setPositiveButton("확인", dialog_litener)
        dialog.show()
    }

}