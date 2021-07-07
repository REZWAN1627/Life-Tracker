package com.rex.lifetracker.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rex.lifetracker.view.fragment.notificationTabItem.Information
import com.rex.lifetracker.view.fragment.notificationTabItem.Promotion
import com.rex.lifetracker.view.fragment.notificationTabItem.YourPlan


class FragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager,lifecycle) {


    override fun createFragment(position: Int): Fragment {
        when (position) {
            1 -> return Promotion()
            2 -> return YourPlan()
        }
        return Information()
    }

    override fun getItemCount(): Int {
        return 3
    }
}