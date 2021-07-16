package com.rex.lifetracker.view.fragment.notificationTabItem

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rex.lifetracker.R
import com.rex.lifetracker.databinding.FragmentYourPlanBinding
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel
import java.text.SimpleDateFormat
import java.util.*

class YourPlan : Fragment(R.layout.fragment_your_plan) {
    private val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("EEE, d MMM yy")
    private lateinit var localDataBaseViewModel: LocalDataBaseViewModel
    private val calendar = Calendar.getInstance()
    private lateinit var binding: FragmentYourPlanBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentYourPlanBinding.bind(view)
        binding.CurrentDate.text = simpleDateFormat.format(calendar.time)


        initViewModel()
        setValue()


    }

    private fun setValue() {
        binding.apply {
            localDataBaseViewModel.realAllUserInfo.observe(viewLifecycleOwner, { userInfo ->
                boughtPack.text = userInfo.brought_pack_time
                subscriptionPack.text = userInfo.Subscription_Pack
                packageName.text = userInfo.Subscription_Pack
                status.text = "status: " + userInfo.status
                packageDes.text = "You are using "+userInfo.Subscription_Pack
                userPlaneActiveDate.text = userInfo.Active_Time
                userPlaneDeactivatedDate.text = userInfo.Deactivate_Time


            })
        }
    }

    private fun initViewModel() {
        localDataBaseViewModel = ViewModelProvider(this).get(LocalDataBaseViewModel::class.java)
    }



}