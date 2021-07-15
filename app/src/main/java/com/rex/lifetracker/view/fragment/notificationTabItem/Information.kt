package com.rex.lifetracker.view.fragment.notificationTabItem

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
            }
        })
    }


    private fun initViewModel() {
       appsInformationViewModel = ViewModelProvider(this).get(AppsInformationViewModel::class.java)
    }

}