package com.example.schoollifeproject.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.schoollifeproject.databinding.FragmentSettingsBinding
import com.example.schoollifeproject.model.APIS
import com.example.schoollifeproject.model.PostModel
import retrofit2.Call
import retrofit2.Response

/**
 * 설정 Fragment
 * 작성자 : 박동훈
 */
class SettingsFragment : Fragment() {

    private lateinit var userID: String
    private lateinit var binding: FragmentSettingsBinding
    private val api = APIS.create()

    fun newInstance(userID: String): SettingsFragment {
        val args = Bundle()
        args.putString("userID", userID)

        val settingsFragment = SettingsFragment()
        settingsFragment.arguments = args

        return settingsFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        userID = arguments?.getString("userID").toString()
        binding.loginInfo.text = userID

        val btnLogout = binding.btnLogout
        val btnDelete = binding.btnDeleteInfo


        if (userID == "Admin" || userID == "비회원") {
            btnDelete.visibility = View.INVISIBLE
        }

        if (userID == "비회원") {
            btnLogout.text = "로그인"
        } else {
            btnLogout.text = "로그아웃"
        }
        btnLogout.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.remove(this)
                ?.commit()
            activity?.finish()
        }
        btnDelete.setOnClickListener {
            dialog(userID)
        }

        return binding.root
    }

    fun dialog(id: String) {
        var dialog = AlertDialog.Builder(this.requireActivity())

        dialog.setTitle("회원탈퇴")
        dialog.setMessage("정말 탈퇴하시겠습니까?")

        val dialog_listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    deleteInfo(id)
                    Toast.makeText(this.context, "회원이 탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
                    activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.remove(this)
                        ?.commit()
                    activity?.finish()
                }
                DialogInterface.BUTTON_NEGATIVE ->
                    Toast.makeText(this.context, "취소되었습니다", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setPositiveButton("예", dialog_listener)
        dialog.setNegativeButton("아니요", dialog_listener)
        dialog.show()
    }

    fun deleteInfo(id: String) {
        api.delete_info(id).enqueue(object : retrofit2.Callback<PostModel> {
            override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
            }

            override fun onFailure(call: Call<PostModel>, t: Throwable) {
            }
        })
    }
}