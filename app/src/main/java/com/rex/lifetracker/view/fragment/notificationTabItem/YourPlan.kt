package com.rex.lifetracker.view.fragment.notificationTabItem

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rex.lifetracker.R
import com.rex.lifetracker.databinding.FragmentYourPlanBinding
import com.rex.lifetracker.viewModel.firebaseViewModel.AppsInformationViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.UserInfoViewModel

//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// * Use the [YourPlan.newInstance] factory method to
// * create an instance of this fragment.
// */
class YourPlan : Fragment(R.layout.fragment_your_plan) {
    //    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
    private lateinit var binding: FragmentYourPlanBinding
    private lateinit var userInfoViewModel: UserInfoViewModel
    private lateinit var appsInformationViewModel: AppsInformationViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentYourPlanBinding.bind(view)
        initViewModel()

        binding.apply {

            userInfoViewModel.getUserInfoLiveData?.observe(viewLifecycleOwner, Observer { user ->
                appsInformationViewModel.getAppsInformationLiveData?.observe(
                    viewLifecycleOwner,
                    Observer { apps ->

                        binding.apply {
                            if (apps != null) {
                                monthSubPack.text = apps.monthly_sub_pack
                                boughtPack.text = "bought: " + apps.bought_date_time
                            }

                            if (user != null) {
                                userPlaneActiveDate.text = user.active_Time
                                userPlaneDeactivatedDate.text = user.deactivate_Time
                            }


                        }

                    })


            })
        }

    }

    private fun initViewModel() {
        userInfoViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(
            UserInfoViewModel::class.java
        )
        appsInformationViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )
            .get(AppsInformationViewModel::class.java)
        appsInformationViewModel.getAppsInformation()

    }

    //    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment YourPlan.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            YourPlan().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}