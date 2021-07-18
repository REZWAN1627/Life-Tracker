package com.rex.lifetracker.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rex.lifetracker.R
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SOSContacts_Entity
import com.rex.lifetracker.databinding.ItemManagelistcontactBinding
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel
import java.util.*


class TrustedContactsManageAdapter(context: Context) :
    RecyclerView.Adapter<TrustedContactsManageAdapter.MyViewHolder>() {

    private val context = context
    private var localDataBaseList = mutableListOf<SOSContacts_Entity>()
    private val selectCheck = ArrayList<Int>()
    private val selectCheck2 = ArrayList<Int>()

    //    private val positionIndex = ArrayList<Int>()
    private var flag1 = false
    private var flag2 = false

    class MyViewHolder(val binding: ItemManagelistcontactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(soscontactsEntity: SOSContacts_Entity, context: Context, position: Int) {
            binding.apply {

                if (soscontactsEntity.Image != null) {
                    Glide.with(context)
                        .asBitmap()
                        .load(soscontactsEntity.Image)
                        .placeholder(R.drawable.ic_man)
                        .into(trustPeopleImage)
                }else{
                    Glide.with(context)
                        .asBitmap()
                        .load(soscontactsEntity.Image)
                        .placeholder(R.drawable.ic_man)
                        .into(trustPeopleImage)
                }

                contactsName.text = soscontactsEntity.Name
                contactsNumber.text = soscontactsEntity.Phone


            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(
            ItemManagelistcontactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun setValue(list: List<SOSContacts_Entity>) {
        this.localDataBaseList = list as MutableList<SOSContacts_Entity>
        var i = 0
        var k = 0


        Log.d(TAG, "setValue: ${localDataBaseList.size}")
        while (k < localDataBaseList.size) {
            selectCheck.add(k, 0)
            selectCheck2.add(k, 0)
//            positionIndex.add(k,0)
            k++
        }
        Log.d(
            TAG,
            "setValue: initial arrayList1 -- > $selectCheck  initial arraylist2 --> $selectCheck2 "
        )
        while (i < localDataBaseList.size) {
            when (localDataBaseList[i].Priority) {
                "First" -> {
                    selectCheck[i] = 1

                }
                "Second" -> {
                    selectCheck2[i] = 1

                }
                else -> {
//                    selectCheck.add(k,0)
//                    selectCheck2.add(k,0)

                }
            }

            i++
        }
        Log.d(
            TAG,
            "setValue: initial arrayList1 -- > $selectCheck  initial arraylist2 --> $selectCheck2 "
        )

        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

//        if (listInfo[position].Priority == "First"){
//            holder.binding.checkbox.isChecked = true
//        }
//        if (listInfo[position].Priority == "Second"){
//            holder.binding.checkbox.isChecked = true
//        }
        holder.binding.checkbox.isChecked = selectCheck[position] == 1
        holder.binding.checkbox2.isChecked = selectCheck2[position] == 1

        if (selectCheck[position] == 1 && selectCheck2[position] == 1) {
            holder.binding.checkbox.isChecked = true
            selectCheck[position] = 1
            holder.binding.checkbox2.isChecked = false
            selectCheck2[position] = 0
            Toast.makeText(context, "Invalid Choice", Toast.LENGTH_SHORT).show()
        } else {
            holder.binding.checkbox.setOnClickListener {
                for (k in selectCheck.indices) {
                    if (k == position) {
                        selectCheck[k] = 1
//                        positionIndex[k] = position
                    } else {
                        selectCheck[k] = 0
//                        positionIndex[k] = 0
                    }
                }
                notifyDataSetChanged()
            }
            holder.binding.checkbox2.setOnClickListener {
                for (k in selectCheck2.indices) {
                    if (k == position) {
                        selectCheck2[k] = 1
//                        positionIndex[k] = position
                    } else {
                        selectCheck2[k] = 0
//                        positionIndex[k] = 0
                    }
                }
                notifyDataSetChanged()
            }
        }

        holder.bind(localDataBaseList[position], context, position)
    }

    fun getSelectedValue(): ArrayList<SOSContacts_Entity> {
        Log.d(TAG, "setSelectedValue: 1 $selectCheck")
        Log.d(TAG, "setSelectedValue: 2 $selectCheck2")
        val listContacts = ArrayList<SOSContacts_Entity>()
        var i = 0
        var flagFirst = true
        while (i < selectCheck.size) {
            if (selectCheck[i] == 1) {
                if (flagFirst){
                    listContacts.add(
                        0,
                        SOSContacts_Entity(
                            localDataBaseList[i].Phone,
                            "First",
                            localDataBaseList[i].Name,
                            localDataBaseList[i].Image
                        )
                    )

                    flagFirst = false
                }else{
                    listContacts.add(1,
                        SOSContacts_Entity(
                            localDataBaseList[i].Phone,
                            "First",
                            localDataBaseList[i].Name,
                            localDataBaseList[i].Image
                        )
                    )
                }


                flag1 = true
            }
            if (selectCheck2[i] == 1) {
                if (flagFirst){
                    listContacts.add(
                        0,
                        SOSContacts_Entity(
                            localDataBaseList[i].Phone,
                            "Second",
                            localDataBaseList[i].Name,
                            localDataBaseList[i].Image
                        )
                    )
                    flagFirst = false
                }else{
                    listContacts.add(
                        1,
                        SOSContacts_Entity(
                            localDataBaseList[i].Phone,
                            "Second",
                            localDataBaseList[i].Name,
                            localDataBaseList[i].Image
                        )
                    )
                }


                flag2 = true
            }
            i++
        }

        if (!flag1 && !flag2) {
            Toast.makeText(context, "Option not selected", Toast.LENGTH_SHORT).show()
            return listContacts

        } else if (!flag2 || !flag1) {
            Toast.makeText(context, "One of the option is not selected", Toast.LENGTH_SHORT).show()
            return listContacts
        } else {
            selectCheck.clear()
            selectCheck2.clear()
        }

        return listContacts

    }


    override fun getItemCount(): Int {
        return localDataBaseList.size
    }


}