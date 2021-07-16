package com.rex.lifetracker.view.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.bumptech.glide.Glide
import com.rex.lifetracker.R
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.PersonalInfo_Entity
import com.rex.lifetracker.databinding.FragmentProfileBinding
import com.rex.lifetracker.utils.Constant
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.launch


class Profile : Fragment(R.layout.fragment_profile), EasyPermissions.PermissionCallbacks {


    private lateinit var binding: FragmentProfileBinding
    private lateinit var localDataBaseViewModel: LocalDataBaseViewModel
    private var imageUri: Uri? = null
    private var selectImage = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        initViewModel()
        setValue()

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.mapsFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)


    }

    private fun setValue() {
        binding.apply {
            localDataBaseViewModel.realAllUserInfo.observe(viewLifecycleOwner, { userInfo ->

                Glide.with(requireContext())
                    .asBitmap()
                    .load(userInfo.Image)
                    .placeholder(R.drawable.ic_man)
                    .into(editProfileImage)
                editProfileImage.visibility = View.VISIBLE
                addEditPhotoIcon.visibility = View.GONE
                editprofileFirstName.setText(userInfo.First_Name)
                editprofileLastName.setText(userInfo.Last_Name)
                edituserEmail.text = userInfo.User_Email

                editTakeProfileImage.setOnClickListener {
                    checkPhotoPermission()
                }

                editProfileSavebtn.setOnClickListener {
                    if (TextUtils.isEmpty(editprofileFirstName.text.toString()) || TextUtils.isEmpty(
                            editprofileLastName.text.toString()
                        )
                    ) {
                        editprofileLastName.error = "Field is empty"
                        editprofileFirstName.error = "Field is empty"
                        return@setOnClickListener
                    } else {

                        if (selectImage) {
                            uploadToDatabase(
                                editprofileFirstName.text.toString(),
                                editprofileLastName.text.toString(),
                                userInfo.Deactivate_Time,
                                userInfo.Active_Time,
                                userInfo.Subscription_Pack,
                                userInfo.brought_pack_time,
                                userInfo.status,
                                userInfo.User_Email,
                                imageUri
                            )
                            Toast.makeText(
                                requireContext(),
                                "Information Updated",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {

                            localDataBaseViewModel.addUserInfo(
                                PersonalInfo_Entity(
                                    0,editprofileFirstName.text.toString(),editprofileLastName.text.toString(),
                                    userInfo.Deactivate_Time,userInfo.Active_Time,userInfo.Subscription_Pack,userInfo.brought_pack_time
                                ,userInfo.status,userInfo.User_Email,userInfo.Image
                                )
                            )
                            Toast.makeText(
                                requireContext(),
                                "Information Updated",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }

            })
        }
    }

    private fun uploadToDatabase(
        Firstname: String,
        LastName: String,
        deactivateTime: String,
        activeTime: String,
        subscriptionPack: String,
        broughtPackTime: String,
        status: String,
        userEmail: String,
        imageUri: Uri?
    ) {
        val dialogue =
            SpotsDialog.Builder().setContext(requireContext())
                .setTheme(R.style.Custom)
                .setCancelable(true).build()
        dialogue?.show()
        lifecycleScope.launch {
            binding.apply {
                localDataBaseViewModel.addUserInfo(
                    PersonalInfo_Entity(
                        0,
                        Firstname,
                        LastName,
                        deactivateTime,
                        activeTime,
                        subscriptionPack,
                        broughtPackTime,
                        status,
                        userEmail,
                        getBitmap(imageUri.toString())

                    )
                )

            }
        }
        dialogue.dismiss()


    }

    private fun initViewModel() {
        localDataBaseViewModel = ViewModelProvider(this).get(LocalDataBaseViewModel::class.java)
    }


    private fun hasPermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    private fun checkPhotoPermission() {
        EasyPermissions.requestPermissions(
            this,
            "This application cannot work without  Permission.",
            Constant.REQUEST_STORAGE_READ_WRITE_CODE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireContext()).build().show()
        } else {
            checkPhotoPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

        if (!hasPermission()) {
            // requestLocationPermission()
        } else {
            selectImage()
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun selectImage() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                binding.apply {
                    Glide.with(requireContext())
                        .asBitmap()
                        .load(result.uri)
                        .placeholder(R.drawable.ic_man)
                        .into(editProfileImage)
                    addEditPhotoIcon.visibility = View.GONE
                    imageUri = result.uri
                    selectImage = true

                }
            }
        }
    }


    private suspend fun getBitmap(imageUri: String): Bitmap {
        Log.d(Constant.TAG, "getBitmap: $imageUri")
        val loading = ImageLoader(requireContext())
        val request = ImageRequest.Builder(requireContext())
            .data(imageUri)
            .build()

        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }


}