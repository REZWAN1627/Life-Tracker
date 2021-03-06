package com.rex.lifetracker.view.fragment.notificationTabItem

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.rex.lifetracker.R
import com.rex.lifetracker.databinding.FragmentInformationBinding
import com.rex.lifetracker.viewModel.firebaseViewModel.AppsInformationViewModel

class Information : Fragment(R.layout.fragment_information) {


    private lateinit var binding: FragmentInformationBinding
    private lateinit var appsInformationViewModel: AppsInformationViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInformationBinding.bind(view)

        initViewModel()

        appsInformationViewModel.getAppsInformationLiveData.observe(viewLifecycleOwner,{
            binding.apply {
                about.text = it?.informationModel?.about
                helpCall.setOnClickListener {
                    callNumber("01886271808")
                }

            }
        })
    }

    private fun callNumber(number: String) {
        val intent =
            Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }


    private fun initViewModel() {
       appsInformationViewModel = ViewModelProvider(this).get(AppsInformationViewModel::class.java)
    }



}