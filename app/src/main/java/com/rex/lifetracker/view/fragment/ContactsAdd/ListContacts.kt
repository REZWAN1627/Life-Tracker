package com.rex.lifetracker.view.fragment.ContactsAdd


import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.android.material.snackbar.Snackbar
import com.rex.lifetracker.R
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.DeleteContactsCacheModel
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SOSContacts_Entity
import com.rex.lifetracker.adapter.RecyclerAdapterTrustedContacts
import com.rex.lifetracker.databinding.FragmentListContactsBinding
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.utils.ImageConverter
import com.rex.lifetracker.utils.LoadingDialog
import com.rex.lifetracker.view.ManageTrustedContactsList
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.TrustedContactsViewModel
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class ListContacts : Fragment(R.layout.fragment_list_contacts) {

    private var contactExceed = false
    private var contactLessThan = false
    private var emptyList = false
    private lateinit var trustedContactsViewModel: TrustedContactsViewModel
    private lateinit var binding: FragmentListContactsBinding
    private lateinit var mAdapter: RecyclerAdapterTrustedContacts
    private lateinit var localDataBaseViewModel: LocalDataBaseViewModel

    private var internetDisposable: Disposable? = null
    private var isInternetConnected = false
    private var totalSizeOnline = 0
    private var totalSizeOfflineBefore = 0
    private lateinit var data: SOSContacts_Entity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentListContactsBinding.bind(view)



        initViewModel()
        offlineSetValue()





        binding.apply {
            saveContactList.setOnClickListener {

                when {
                    contactLessThan -> {
                        Toast.makeText(
                            requireContext(),
                            "You Have to add At-least 2 number",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    emptyList -> {
                        Toast.makeText(requireContext(), "Please Add Number", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        if (isInternetConnected) {
                            showDialog()
                        } else {
                            startActivity(
                                Intent(
                                    requireContext(),
                                    ManageTrustedContactsList::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                    }
                }

            }
            addNewContacts.setOnClickListener {
                when {
                    contactExceed -> {
                        Toast.makeText(
                            requireContext(),
                            "You Have Already Added 5 Contacts",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    else -> {

                        findNavController().navigate(R.id.action_listContacts_to_addContacts2)
                    }
                }

            }



            allAddedContacts.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = mAdapter
            }

            val itemTouchHelper = ItemTouchHelper(simpleCallback)
            itemTouchHelper.attachToRecyclerView(allAddedContacts)


        }


    }

    //-----------------------------------back ground thread--------------------//
    private fun uploadDataToFireBase(
        deletelist: List<DeleteContactsCacheModel>,
        NumberList: List<SOSContacts_Entity>
    ) {
        val mainJob = CoroutineScope(IO).launch {

            val job = launch {
                if (deletelist.isNotEmpty()) {
                    for (path in deletelist) {
                        trustedContactsViewModel.deleteContact(path.deleteNumber)
                        localDataBaseViewModel.deleteCache(
                            DeleteContactsCacheModel(
                                path.deleteNumber
                            )
                        )
                    }
                }
            }
            job.join()

            val job2 = launch {
                if (NumberList.isNotEmpty()) {
                    for (number in NumberList) {
                        trustedContactsViewModel.updateDataTOFireBase(
                            number, ImageConverter.getByte(number.Image!!)
                        )
                    }

                }

            }
            job2.join()

        }
        mainJob.invokeOnCompletion {
            Log.d(TAG, "uploadDataToFireBase: is complete")
            startActivity(
                Intent(
                    requireContext(),
                    ManageTrustedContactsList::class.java
                )
            )
            requireActivity().finish()

        }
        //-----------------------back ground thread------------------//

    }

    private fun offlineSetValue() {

        localDataBaseViewModel.readAllContacts.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                Log.d(TAG, "onViewCreated: ${it.size}")
                contactExceed = it.size >= 5
                contactLessThan = it.size <= 1
                totalSizeOfflineBefore = it.size
                emptyList = it.isEmpty()
                mAdapter.setValue(it)

            }

        })
    }


    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.askinguploadtodatabase_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        val cancel = dialog.findViewById<Button>(R.id.cancelupload)
        val ok = dialog.findViewById<Button>(R.id.okDownload)

        ok.setOnClickListener {
            Log.d(TAG, "showDialog: ok is clicked")

            localDataBaseViewModel.readAllContacts.observe(
                viewLifecycleOwner,
                { NumberList ->
                    localDataBaseViewModel.readAllCache.observe(
                        viewLifecycleOwner,
                        { deleteList ->
                            uploadDataToFireBase(deleteList, NumberList)


                        })
                })
            dialog.dismiss()
        }
        cancel.setOnClickListener {
            Log.d(TAG, "showDialog: cancel is called")
            startActivity(
                Intent(
                    requireContext(),
                    ManageTrustedContactsList::class.java
                )
            )
            requireActivity().finish()
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun initViewModel() {
        trustedContactsViewModel = ViewModelProvider(this).get(TrustedContactsViewModel::class.java)
        mAdapter = RecyclerAdapterTrustedContacts(requireContext())
        localDataBaseViewModel = ViewModelProvider(this).get(LocalDataBaseViewModel::class.java)


    }

    private var simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            when (direction) {
                ItemTouchHelper.LEFT -> {

                    data = mAdapter.deleteItem(viewHolder.adapterPosition)
                    localDataBaseViewModel.addCache(
                        DeleteContactsCacheModel(
                            data.Phone
                        )
                    )

                    localDataBaseViewModel.deleteContacts(data)


                    mAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                    LoadingDialog.loadingDialogStart(requireContext(), R.style.Custom)
                    localDataBaseViewModel.readAllContacts.observe(viewLifecycleOwner, {
                        Log.d(TAG, "onSwiped: current size after delete " + it.size)
                        contactExceed = it.size >= 5
                        contactLessThan = it.size <= 1
                        emptyList = it.isEmpty()

                        LoadingDialog.loadingDialogStop()
                        mAdapter.setValue(it)

                    })

                    Snackbar.make(
                        binding.allAddedContacts,
                        "This Contacts is deleted " + data.Phone,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Undo") {
                            localDataBaseViewModel.addContacts(data)
                            mAdapter.notifyItemInserted(viewHolder.adapterPosition)
                            localDataBaseViewModel.deleteCache(
                                DeleteContactsCacheModel(
                                    data.Phone
                                )
                            )
                            localDataBaseViewModel.readAllContacts.observe(
                                viewLifecycleOwner,
                                {
                                    Log.d(TAG, "onSwiped: current size after delete " + it.size)
                                    contactExceed = it.size >= 5
                                    contactLessThan = it.size <= 1
                                    emptyList = it.isEmpty()

                                    LoadingDialog.loadingDialogStop()
                                    mAdapter.setValue(it)

                                })
                        }.show()
                }


            }


        }

    }


    ////----------------------netWork-------------------------//
    override fun onResume() {
        Log.d(TAG, "onResume: is called")
        super.onResume()
        internetDisposable = ReactiveNetwork
            .observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isConnectedToInternet ->
                isInternetConnected = isConnectedToInternet
                if (isConnectedToInternet) {
                    localDataBaseViewModel.readAllContacts.observe(viewLifecycleOwner, {
                        if (it.isEmpty()) {
                            trustedContactsViewModel.getContactsLiveData.observe(
                                viewLifecycleOwner,
                                { onlineList ->
                                    Log.d(TAG, "onViewCreated online : ${onlineList.size}")
                                    // Log.d(TAG, "onResume: :${onlineList[0].Image}")
                                    if (onlineList.isNotEmpty()) {
                                        totalSizeOnline = onlineList.size

                                        lifecycleScope.launch {
                                            LoadingDialog.loadingDialogStart(requireContext(),R.style.Custom)
                                            for (model in onlineList) {
                                                val image = ImageConverter.getBitmap(model.Image,requireContext())
                                                localDataBaseViewModel.addContacts(
                                                    SOSContacts_Entity(
                                                        model.Phone,
                                                        model.Priority,
                                                        model.Name,
                                                        image
                                                    )
                                                )


                                            }
                                            LoadingDialog.loadingDialogStop()
                                        }
                                    }


                                })


                        }
                    })
                } else {
                    Log.d(TAG, "onResume: no internet")
                }
            }
    }

    override fun onPause() {
        super.onPause()
        safelyDispose(internetDisposable)
    }

    private fun safelyDispose(disposable: Disposable?) {
        if (disposable != null && !disposable.isDisposed) {
            disposable.dispose()
        }
    }






}