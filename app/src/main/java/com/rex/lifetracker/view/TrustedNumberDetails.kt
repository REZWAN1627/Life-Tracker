package com.rex.lifetracker.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.rex.lifetracker.R
import com.rex.lifetracker.databinding.ActivityTrustedNumberDetailsBinding

class TrustedNumberDetails : AppCompatActivity() {
    private lateinit var binding:ActivityTrustedNumberDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrustedNumberDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

      //  setupActionBarWithNavController(findNavController(R.id.fragmentContainerView2)) as NavHostFragment


    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView2)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}