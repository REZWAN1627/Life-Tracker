package com.rex.lifetracker.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.rex.lifetracker.R
import com.rex.lifetracker.databinding.FragmentProfileBinding
import com.rex.lifetracker.viewModel.firebaseViewModel.SignInViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.UserInfoViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment(R.layout.fragment_profile) {
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
//


    //    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false)
//    }
//
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var userInfoViewModel: UserInfoViewModel
    private lateinit var binding: FragmentProfileBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        initViewModel()
        setValue()


    }

    private fun setValue() {
        binding.apply {
            signInViewModel.collectUserInfoLiveData?.observe(viewLifecycleOwner, Observer { userEmailData ->
                userInfoViewModel.getUserInfoLiveData?.observe(viewLifecycleOwner, Observer { userData ->

                    Glide.with(this@Profile).load(userEmailData.imageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.ic_team)
                        .into(userProfileImage)
                    if (userData != null) {
                        profileUserName.text = userData.first_Name+ " "+userData.last_Name
                    }
                    profileUserEmail.text = userEmailData.email


                })
            })
        }
    }

    private fun initViewModel() {
        signInViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(
            SignInViewModel::class.java
        )
        userInfoViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(
            UserInfoViewModel::class.java
        )

    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment Profile.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            Profile().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}