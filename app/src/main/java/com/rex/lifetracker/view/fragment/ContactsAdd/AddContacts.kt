package com.rex.lifetracker.view.fragment.ContactsAdd

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.rex.lifetracker.R
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SOSContacts_Entity
import com.rex.lifetracker.databinding.FragmentAddContactsBinding
import com.rex.lifetracker.utils.Constant
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.utils.ImageConverter
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.launch


class AddContacts : Fragment(R.layout.fragment_add_contacts), EasyPermissions.PermissionCallbacks {
    private var imageUri: Uri? = null
    private lateinit var binding: FragmentAddContactsBinding
    private var selectImage = false
    private lateinit var localDataBaseViewModel: LocalDataBaseViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddContactsBinding.bind(view)

        initViewModel()

        binding.apply {

            contactNumber.doAfterTextChanged {
                if (it != null) {
                    if (it.length == 11) {
                        closeKeyboard()
                        contactNumber.clearFocus()

                    } else {
                        contactNumber.error = "Invalid Number"
                    }
                }
            }

            InsertImage.setOnClickListener {
                checkPhotoPermission()

            }

            addContacts.setOnClickListener {
                if (TextUtils.isEmpty(contactName.text.toString()) || TextUtils.isEmpty(
                        contactNumber.text.toString()
                    )
                ) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if (contactNumber.text.length < 11) {

                    Toast.makeText(requireContext(), "Invalid Number", Toast.LENGTH_SHORT).show()
                    contactNumber.error = "Invalid number"
                    return@setOnClickListener

                } else {
                    if (selectImage) {

                        uploadInDataBase()


                    } else {
                        Log.d(TAG, "onViewCreated: is called for no image ${R.drawable.defaultimage}")
                        val imageUri = Uri.parse(
                            ContentResolver.SCHEME_ANDROID_RESOURCE +
                                    "://" + resources.getResourcePackageName(R.drawable.defaultimage)
                                    + '/' + resources.getResourceTypeName(R.drawable.defaultimage) + '/' + resources.getResourceEntryName(
                                R.drawable.defaultimage
                            )
                        )

                       lifecycleScope.launch {
                           localDataBaseViewModel.addContacts(
                               SOSContacts_Entity(
                                   contactNumber.text.toString(),
                                   "null",
                                   contactName.text.toString(),
                                   ImageConverter.getBitmap(imageUri.toString(),requireContext())


                               )
                           )
                       }

                        findNavController().navigate(R.id.action_addContacts2_to_listContacts)
                    }
                }


            }
        }

    }

    private fun uploadInDataBase() {
        val dialogue =
            SpotsDialog.Builder().setContext(requireContext())
                .setTheme(R.style.Custom)
                .setCancelable(true).build()
        dialogue?.show()

        lifecycleScope.launch {
            binding.apply {
                localDataBaseViewModel.addContacts(
                    SOSContacts_Entity(
                        contactNumber.text.toString(),
                        "null",
                        contactName.text.toString(),
                        ImageConverter.getBitmap(imageUri.toString(),requireContext())
                    )
                )

            }
        }
        findNavController().navigate(R.id.action_addContacts2_to_listContacts)
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
                    contactImage.setImageURI(result.uri)
                    addIcon.visibility = View.GONE
                    imageUri = result.uri
                    selectImage = true

                }
            }
        }
    }



    private fun closeKeyboard() {
        val view: View? = requireView()
        if (view != null) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            // PhoneNumber.clearFocus()
        }
    }




}