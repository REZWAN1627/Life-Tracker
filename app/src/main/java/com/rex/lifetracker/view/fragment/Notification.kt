package com.rex.lifetracker.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.rex.lifetracker.R
import com.rex.lifetracker.adapter.FragmentAdapter
import com.rex.lifetracker.databinding.FragmentNotificationBinding
import com.rex.lifetracker.utils.Constant.TAG

class Notification : Fragment(R.layout.fragment_notification) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentNotificationBinding.bind(view)

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

        binding.apply {
            viewPager.adapter = FragmentAdapter(childFragmentManager,lifecycle)
            tabLayout.addTab(tabLayout.newTab().setText("Information"))
            tabLayout.addTab(tabLayout.newTab().setText("Promotion"))
            tabLayout.addTab(tabLayout.newTab().setText("Your Plan"))

            tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })


            viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    tabLayout.selectTab(tabLayout.getTabAt(position))
                }
            })
        }

    }
    private val callback: OnBackPressedCallback =
        object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                try {
                    findNavController().navigate(R.id.mapsFragment)
                }catch (e:Exception){
                    Log.d(TAG, "handleOnBackPressed: $e")
                }

            }
        }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: is called")
        callback.isEnabled = false
        callback.remove()
        super.onDestroyView()
    }
}