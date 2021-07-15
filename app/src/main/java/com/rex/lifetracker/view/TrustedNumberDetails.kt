package com.rex.lifetracker.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.rex.lifetracker.R
import com.rex.lifetracker.databinding.ActivityTrustedNumberDetailsBinding
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel

class TrustedNumberDetails : AppCompatActivity() {
    private lateinit var binding:ActivityTrustedNumberDetailsBinding
    private lateinit var localDataBaseViewModel: LocalDataBaseViewModel
    private var first = false
    private var second = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrustedNumberDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()

      //  setupActionBarWithNavController(findNavController(R.id.fragmentContainerView2)) as NavHostFragment


    }

    private fun initViewModel() {
        localDataBaseViewModel = ViewModelProvider(this).get(LocalDataBaseViewModel::class.java)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView2)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {

        localDataBaseViewModel.readAllContacts.observe(this,{list->
            if (list.isNotEmpty()){
                for(piroty in list){
                    if (piroty.Priority == "First"){
                        first = true
                    }
                    if (piroty.Priority == "Second"){
                        second = true
                    }
                }
                if (first && second){
                    Log.d(TAG, "onBackPressed: is called when back")
                    super.onBackPressed()
                }else{
                    Toast.makeText(this, "You haven't Select Priority of Number\nPlease Press Done Button!", Toast.LENGTH_SHORT).show()
                }

            }else{
                Log.d(TAG, "onBackPressed: is called")
                super.onBackPressed()
            }
        })

    }
}