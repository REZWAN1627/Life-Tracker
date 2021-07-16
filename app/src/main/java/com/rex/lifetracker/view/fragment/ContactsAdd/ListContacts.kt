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
import androidx.lifecycle.Observer
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
import com.rex.lifetracker.R
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SOSContacts_Entity
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.deleteContactsCacheModel
import com.rex.lifetracker.adapter.RecyclerAdapterTrustedContacts
import com.rex.lifetracker.databinding.FragmentListContactsBinding
import com.rex.lifetracker.utils.Constant.TAG
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
    private var noNeedToUpload = false
    private var totalsizeOnline = 0
    private var totalsizeOfflinebefore = 0
    private var totalsizeOfflineAfter = 0
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
//                            uploadDataToFireBase()
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
        deletelist: List<deleteContactsCacheModel>,
        NumberList: List<SOSContacts_Entity>
    ) {
        val mainJob = CoroutineScope(IO).launch {

            val job = launch {
                if (deletelist.isNotEmpty()) {
                    for (path in deletelist) {
                        trustedContactsViewModel.deleteContact(path.deleteNumber)
                        localDataBaseViewModel.deleteCache(
                            deleteContactsCacheModel(
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
                        if (number.Image != null) {
                            trustedContactsViewModel.updateDataTOFireBase(
                                number, getByte(number.Image!!)
                            )
                        } else {
                            trustedContactsViewModel.updateDataTOFireBase(
                                number, null
                            )
                        }
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

        localDataBaseViewModel.readAllContacts?.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Log.d(TAG, "onViewCreated: ${it.size}")
                contactExceed = it.size >= 5
                contactLessThan = it.size <= 1
                totalsizeOfflinebefore = it.size
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
            localDataBaseViewModel.readAllContacts?.observe(
                viewLifecycleOwner,
                Observer { NumberList ->
                    localDataBaseViewModel.readAllCache?.observe(
                        viewLifecycleOwner,
                        Observer { deleteList ->
                            totalsizeOfflineAfter = NumberList.size
                            Log.d(
                                TAG,
                                "showDialog: total size --> $totalsizeOnline  delete cache -----> $deleteList  number lis --> ${NumberList.size}"
                            )

                            if (totalsizeOnline > 0 && deleteList.isEmpty() && totalsizeOnline == NumberList.size) {
                                Log.d(TAG, "showDialog: no change is made")
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        ManageTrustedContactsList::class.java
                                    )
                                )
                                requireActivity().finish()
                            }else{
                                uploadDataToFireBase(deleteList, NumberList)
                            }


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

                    val data = mAdapter.deleteItem(viewHolder.adapterPosition)
                    localDataBaseViewModel.addCache(
                        deleteContactsCacheModel(
                            data.Phone
                        )
                    )

                    localDataBaseViewModel.deleteContacts(
                        SOSContacts_Entity(
                            data.Phone,
                            data.Priority,
                            data.Name,
                            data.Image
                        )
                    )


                    mAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                    val dialogue =
                        SpotsDialog.Builder().setContext(requireContext()).setTheme(R.style.Custom)
                            .setCancelable(true).build()
                    dialogue?.show()
                    localDataBaseViewModel?.readAllContacts?.observe(viewLifecycleOwner, Observer {
                        Log.d(TAG, "onSwiped: current size after delete " + it.size)
                        contactExceed = it.size >= 5
                        contactLessThan = it.size <= 1
                        emptyList = it.isEmpty()

                        dialogue.dismiss()
                        mAdapter.setValue(it)

                    })
                }


            }


        }


    }

    private suspend fun getBitmap(imageUri: String): Bitmap {
        val loading = ImageLoader(requireContext())
        val request = ImageRequest.Builder(requireContext())
            .data(imageUri)
            .build()

        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
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
                    localDataBaseViewModel.readAllContacts?.observe(viewLifecycleOwner, {
                        if (it.isEmpty()) {
                            trustedContactsViewModel.getContactsLiveData?.observe(
                                viewLifecycleOwner,
                                { onlineList ->
                                    Log.d(TAG, "onViewCreated online : ${onlineList.size}")
                                   // Log.d(TAG, "onResume: :${onlineList[0].Image}")
                                    if (onlineList.isNotEmpty()) {
                                        totalsizeOnline = onlineList.size

                                        lifecycleScope.launch {
                                            val dialogue =
                                                SpotsDialog.Builder().setContext(requireContext())
                                                    .setTheme(R.style.Custom)
                                                    .setCancelable(true).build()
                                            dialogue?.show()
                                            for ((i, model) in onlineList.withIndex()) {
                                                val image = getBitmap(model.Image)
                                                localDataBaseViewModel.addContacts(
                                                    SOSContacts_Entity(
                                                        model.Phone,
                                                        model.Priority,
                                                        model.Name,
                                                        image
                                                    )
                                                )


                                            }
                                            dialogue?.dismiss()
                                        }
                                    }


                                })


                        } else {

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


    private fun getByte(image: Bitmap): ByteArray {
        Log.d(TAG, "getByte: is called")
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()

    }


}