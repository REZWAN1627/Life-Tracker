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
import com.rex.lifetracker.viewModel.firebaseViewModel.AppsInformationViewModel

//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// * Use the [Promotion.newInstance] factory method to
// * create an instance of this fragment.
// */
class Promotion : Fragment(R.layout.fragment_promotion) {
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
    private lateinit var binding:FragmentPromotionBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPromotionBinding.bind(view)
        initViewModel()
        setValue()
    }

    private fun setValue() {
        appsInformationViewModel.getAppsInformationLiveData?.observe(viewLifecycleOwner, Observer {apps ->

            binding.apply {

                if (apps!=null){
                    pla1.text = apps.plan
                    pla2.text = apps.plan
                    pla3.text = apps.plan
                    pla4.text = apps.plan

                    planCost1.text = apps.cost
                    planCost2.text = apps.cost
                    planCost3.text = apps.cost
                    planCost4.text = apps.cost
                }


            }

        })
    }

    private fun initViewModel() {

        appsInformationViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
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
//         * @return A new instance of fragment Promotion.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            Promotion().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}