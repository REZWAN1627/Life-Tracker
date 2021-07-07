package com.rex.lifetracker.view.fragment.notificationTabItem

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rex.lifetracker.R
import com.rex.lifetracker.databinding.FragmentInformationBinding
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.viewModel.firebaseViewModel.AppsInformationViewModel

//
//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// * Use the [Information.newInstance] factory method to
// * create an instance of this fragment.
// */
class Information : Fragment(R.layout.fragment_information) {
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

    private lateinit var appsInformationViewModel: AppsInformationViewModel
    private lateinit var binding: FragmentInformationBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInformationBinding.bind(view)

        initViewModel()
        appsInformationViewModel.getAppsInformationLiveData?.observe(viewLifecycleOwner, Observer {



            binding.apply {

                if (it != null) {
                    info1.text = it.info
                }
                if (it != null) {
                    info2.text = it.info
                }
                if (it != null) {
                    info3.text = it.info
                }

            }

        })


    }


    private fun initViewModel() {

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
//         * @return A new instance of fragment Information.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            Information().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}