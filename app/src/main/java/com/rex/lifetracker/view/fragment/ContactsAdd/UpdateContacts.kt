package com.rex.lifetracker.view.fragment.ContactsAdd

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.rex.lifetracker.R
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.DeleteContactsCacheModel
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SOSContacts_Entity
import com.rex.lifetracker.databinding.FragmentUpdateContactsBinding
import com.rex.lifetracker.utils.Constant
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.utils.ImageConverter
import com.rex.lifetracker.utils.LoadingDialog
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.launch


class UpdateContacts : Fragment(R.layout.fragment_update_contacts),
    EasyPermissions.PermissionCallbacks {
    private var imageUri: Uri? = null
    private var selectNewImage = false
    private lateinit var localDataBaseViewModel: LocalDataBaseViewModel
    private val args by navArgs<UpdateContactsArgs>()
    private lateinit var binding: FragmentUpdateContactsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUpdateContactsBinding.bind(view)

        initViewModel()
        setValue()

        binding.apply {

            updatecontactNumber.doAfterTextChanged {
                if (it != null) {
                    if (it.length == 11) {
                        closeKeyboard()
                        updatecontactNumber.clearFocus()

                    } else {
                        updatecontactNumber.error = "Invalid Number"
                    }
                }
            }

            updateImage.setOnClickListener {
                checkPhotoPermission()
            }
            updateAddContacts.setOnClickListener {
                if (TextUtils.isEmpty(updatecontactName.text.toString()) || TextUtils.isEmpty(
                        updatecontactNumber.text.toString()
                    )
                ) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                } else if (updatecontactNumber.text.length < 11) {
                    updatecontactNumber.error = "Invalid Number"
                    Toast.makeText(requireContext(), "Invalid Number", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener

                } else {
                    if (selectNewImage) {
                        if (updatecontactNumber.text.toString() == args.localData.Phone) {
                            uploadInDataBase(args.localData.Priority)
                        } else {
                            localDataBaseViewModel.deleteContacts(
                                SOSContacts_Entity(
                                    args.localData.Phone,
                                    args.localData.Priority,
                                    args.localData.Name,
                                    args.localData.Image
                                )
                            )
                            localDataBaseViewModel.addCache(
                                DeleteContactsCacheModel(
                                    args.localData.Phone
                                )
                            )
                            uploadInDataBase(args.localData.Priority)
                        }

                    } else {
                        if (updatecontactNumber.text.toString() == args.localData.Phone) {


                            localDataBaseViewModel.addContacts(
                                SOSContacts_Entity(
                                    updatecontactNumber.text.toString(),
                                    args.localData.Priority,
                                    updatecontactName.text.toString(),
                                    AppCompatResources.getDrawable(
                                        requireContext(),
                                        R.drawable.defaultimage
                                    )!!.toBitmap()
                                )
                            )

                            findNavController().navigate(R.id.action_updateContacts_to_listContacts)
                        } else {
                            localDataBaseViewModel.addCache(
                                DeleteContactsCacheModel(
                                    args.localData.Phone
                                )
                            )
                            localDataBaseViewModel.deleteContacts(
                                SOSContacts_Entity(
                                    args.localData.Phone,
                                    args.localData.Priority,
                                    args.localData.Name,
                                    args.localData.Image
                                )
                            )

                            localDataBaseViewModel.addContacts(
                                SOSContacts_Entity(
                                    updatecontactNumber.text.toString(),
                                    "null",
                                    updatecontactName.text.toString(),
                                    AppCompatResources.getDrawable(
                                        requireContext(),
                                        R.drawable.defaultimage
                                    )!!.toBitmap()
                                )
                            )

                            findNavController().navigate(R.id.action_updateContacts_to_listContacts)
                        }

                    }
                }
            }
        }

    }


    private fun initViewModel() {

        localDataBaseViewModel = ViewModelProvider(this).get(LocalDataBaseViewModel::class.java)
    }

    private fun setValue() {


        LoadingDialog.loadingDialogStart(requireContext(), R.style.LoadingUserInfo)

        binding.apply {

            updatecontactName.setText(args.localData.Name)
            updatecontactNumber.setText(args.localData.Phone)
            if (args.localData.Image != null) {
                // updateContactImage.setImageBitmap(args.localData.Image)
                Glide.with(requireContext())
                    .asBitmap()
                    .load(args.localData.Image)
                    .placeholder(R.drawable.ic_man)
                    .into(updateContactImage)
                upAddIcon.visibility = View.GONE
            } else {
                Glide.with(requireContext())
                    .asBitmap()
                    .load(R.drawable.ic_team)
                    .placeholder(R.drawable.ic_man)
                    .into(updateContactImage)
            }



            LoadingDialog.loadingDialogStop()

        }

    }

    private fun uploadInDataBase(priority: String) {


        LoadingDialog.loadingDialogStart(requireContext(), R.style.Custom)

        lifecycleScope.launch {
            binding.apply {
                localDataBaseViewModel.addContacts(
                    SOSContacts_Entity(
                        updatecontactNumber.text.toString(),
                        priority,
                        updatecontactName.text.toString(),
                        ImageConverter.getBitmap(imageUri.toString(), requireContext())
                    )
                )

            }
        }
        findNavController().navigate(R.id.action_updateContacts_to_listContacts)
        LoadingDialog.loadingDialogStop()


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
        Log.d(TAG, "onActivityResult: is called")
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.d(TAG, "onActivityResult: is called")
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                binding.apply {
                    updateContactImage.setImageURI(result.uri)
                    upAddIcon.visibility = View.GONE
                    imageUri = result.uri
                    selectNewImage = true

                }
            }
        }
    }

    private fun closeKeyboard() {
        val view: View = requireView()
        if (view != null) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            // PhoneNumber.clearFocus()
        }
    }


}