package com.rex.lifetracker.view.fragment.notificationTabItem

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rex.lifetracker.R
import com.rex.lifetracker.databinding.FragmentPromotionBinding
import com.rex.lifetracker.model.FireBaseModel.AppsAdminDataModelPackage.One_Month_Pack_Model
import com.rex.lifetracker.model.FireBaseModel.AppsAdminDataModelPackage.Six_Month_Pack_Model
import com.rex.lifetracker.model.FireBaseModel.AppsAdminDataModelPackage.Twelve_Month_Pack_Model
import com.rex.lifetracker.utils.LoadingDialog
import com.rex.lifetracker.view.Purchase.Purchase_Package
import com.rex.lifetracker.viewModel.firebaseViewModel.AppsInformationViewModel


class Promotion : Fragment(R.layout.fragment_promotion) {

    private lateinit var appsInformationViewModel: AppsInformationViewModel
    private lateinit var binding: FragmentPromotionBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPromotionBinding.bind(view)
        initViewModel()
        setValue()


        binding.apply {
            firstPlan.setOnClickListener {
                requireContext().startActivity(
                    Intent(
                        requireContext(),
                        Purchase_Package::class.java
                    ).putExtra("Package", "OneMonth")
                )
            }

            secondPlan.setOnClickListener {
                requireContext().startActivity(
                    Intent(
                        requireContext(),
                        Purchase_Package::class.java
                    ).putExtra("Package", "SixMonth")
                )
            }
            thirdPlan.setOnClickListener {
                requireContext().startActivity(
                    Intent(
                        requireContext(),
                        Purchase_Package::class.java
                    ).putExtra("Package", "TwelveMonth")
                )
            }
        }

    }

    private fun setValue() {
        LoadingDialog.loadingDialogStart(requireContext(),R.style.LoadingList)
        appsInformationViewModel.getAppsInformationLiveData.observe(viewLifecycleOwner, { apps ->

            if (apps != null) {
                LoadingDialog.loadingDialogStop()
                oneMonthPlane(apps.oneMonthPackModel)
                sixMonthPlane(apps.sixMonthPackModel)
                twelveMonthPlan(apps.twelveMonthPackModel)
            }

        })
    }

    private fun twelveMonthPlan(twelveMonthPackModel: Twelve_Month_Pack_Model?) {
        binding.apply {
            twelveMonthPlan.text = twelveMonthPackModel?.days + " Days Plan"
            twelveMonthPlanCost.text = "BDT " + twelveMonthPackModel?.cost
        }
    }

    private fun sixMonthPlane(sixMonthPackModel: Six_Month_Pack_Model?) {
        binding.apply {
            sixMonthDays.text = sixMonthPackModel?.days + " Days Plan"
            sixMonthPlanCost.text = "BDT " + sixMonthPackModel?.cost
        }
    }

    private fun oneMonthPlane(oneMonthPackModel: One_Month_Pack_Model?) {
        binding.apply {
            oneMonth.text = oneMonthPackModel?.days + " Days Plan"
            oneMonthCost.text = "BDT " + oneMonthPackModel?.cost

        }
    }

    private fun initViewModel() {

        appsInformationViewModel = ViewModelProvider(this)
            .get(AppsInformationViewModel::class.java)
    }


}