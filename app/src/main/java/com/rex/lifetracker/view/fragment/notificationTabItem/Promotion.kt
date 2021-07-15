package com.rex.lifetracker.view.fragment.notificationTabItem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rex.lifetracker.R
import com.rex.lifetracker.databinding.FragmentPromotionBinding
import com.rex.lifetracker.model.FireBaseModel.AppsAdminDataModelPackage.One_Month_Pack_Model
import com.rex.lifetracker.model.FireBaseModel.AppsAdminDataModelPackage.Six_Month_Pack_Model
import com.rex.lifetracker.model.FireBaseModel.AppsAdminDataModelPackage.Twelve_Month_Pack_Model
import com.rex.lifetracker.viewModel.firebaseViewModel.AppsInformationViewModel


class Promotion : Fragment(R.layout.fragment_promotion) {

    private lateinit var appsInformationViewModel: AppsInformationViewModel
    private lateinit var binding:FragmentPromotionBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPromotionBinding.bind(view)
        initViewModel()
        setValue()
    }

    private fun setValue() {
        appsInformationViewModel.getAppsInformationLiveData?.observe(viewLifecycleOwner, {apps ->

                if (apps!=null){
                    oneMonthPlane(apps.oneMonthPackModel)
                    sixMonthPlane(apps.sixMonthPackModel)
                    twelveMonthPlan(apps.twelveMonthPackModel)
                }

        })
    }

    private fun twelveMonthPlan(twelveMonthPackModel: Twelve_Month_Pack_Model?) {
        binding.apply {
            twelveMonthPlan.text = twelveMonthPackModel?.days + " Days Plan"
            twelveMonthPlanCost.text ="$"+twelveMonthPackModel?.cost
        }
    }

    private fun sixMonthPlane(sixMonthPackModel: Six_Month_Pack_Model?) {
        binding.apply {
            sixMonthDays.text = sixMonthPackModel?.days + " Days Plan"
            sixMonthPlanCost.text = "$"+sixMonthPackModel?.cost
        }
    }

    private fun oneMonthPlane(oneMonthPackModel: One_Month_Pack_Model?) {
        binding.apply {
            oneMonth.text = oneMonthPackModel?.days + " Days Plan"
            oneMonthCost.text = "$"+oneMonthPackModel?.cost

        }
    }

    private fun initViewModel() {

        appsInformationViewModel = ViewModelProvider(this)
            .get(AppsInformationViewModel::class.java)
    }


}