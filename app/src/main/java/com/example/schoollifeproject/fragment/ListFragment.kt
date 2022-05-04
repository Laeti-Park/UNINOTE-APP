package com.example.schoollifeproject.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.schoollifeproject.model.APIS_login
import com.example.schoollifeproject.model.Contacts
import com.example.schoollifeproject.model.PostModel
import com.example.schoollifeproject.WriteNoticeActivity
import com.example.schoollifeproject.adapter.ContactsListAdapter
import com.example.schoollifeproject.databinding.FragmentListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var contactsList: MutableList<Contacts> = mutableListOf()

    private val adapter = ContactsListAdapter(contactsList)

    private lateinit var getResult: ActivityResultLauncher<Intent>
    private lateinit var binding: FragmentListBinding

    private val api_notice = APIS_login.create()
    private var id: String? = null
    private var countKey: Int? = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false)

        binding.recyclerView.adapter = adapter

        id = arguments?.getString("ID")
        countKey = arguments?.getInt("countKey", 0)

        Log.d("arguments", id.toString() + ": " + countKey)

        for (i in 1..countKey!!) {
            Log.d("for", countKey.toString())
            api_notice.notice_load(
                i
            ).enqueue(object : Callback<PostModel> {
                override fun onResponse(
                    call: Call<PostModel>,
                    response: Response<PostModel>
                ) {

                    val contacts =
                        Contacts(
                            response.body()?.noticeKey,
                            response.body()?.noticeTitle.toString(),
                            response.body()?.noticeName.toString(),
                            response.body()?.noticeDate.toString()
                        )
                    contactsList.add(contacts)
                    Log.d("onResponse", "성공!=" + i)

                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(p0: Call<PostModel>, t: Throwable) {
                    Log.d("failedLoadNotice", t.message.toString())
                }
            })
        }

       getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                Log.d("리절트", "111")
                val title = it.data?.getStringExtra("Title")
                val notice = it.data?.getStringExtra("Notice")
                val date = it.data?.getStringExtra("Date")

                val contacts =
                    Contacts(
                        countKey!! + 1,
                        title,
                        notice,
                        date
                    )
                contactsList.add(contacts)


            }
            adapter.notifyDataSetChanged()
        }


        val addNote = binding.addNote

        addNote.setOnClickListener {

            val intent = Intent(context, WriteNoticeActivity::class.java)
            intent.putExtra("ID", id)
            getResult.launch(intent)
            Log.d("addNote", "11")
        }
        return binding.root
    }

}