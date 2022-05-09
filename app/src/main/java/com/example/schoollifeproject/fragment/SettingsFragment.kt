package com.example.schoollifeproject.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.schoollifeproject.R
import com.example.schoollifeproject.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var userID: String
    private lateinit var binding: FragmentSettingsBinding

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

        return binding.root
    }

}