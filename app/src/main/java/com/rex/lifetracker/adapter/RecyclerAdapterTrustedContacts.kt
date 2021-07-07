package com.rex.lifetracker.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rex.lifetracker.R
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SOSContacts_Entity
import com.rex.lifetracker.databinding.ReadContactsListBinding
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.view.fragment.ContactsAdd.ListContactsDirections


class RecyclerAdapterTrustedContacts(requireContext: Context) :
    RecyclerView.Adapter<RecyclerAdapterTrustedContacts.MyViewHolder>() {
    private val context = requireContext
    private var listInfoOffline =
        mutableListOf<SOSContacts_Entity>()

    class MyViewHolder(private val binding: ReadContactsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            soscontactsEntity: SOSContacts_Entity,
            context: Context
        ) {
            binding.apply {
                if (soscontactsEntity.Image!=null){
                    //trustPeopleImage.setImageBitmap(soscontactsEntity.Image)
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
                toUpdate.setOnClickListener {
                    val action =
                        ListContactsDirections.actionListContactsToUpdateContacts(soscontactsEntity)
                    itemView.findNavController().navigate(action)
                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(
            ReadContactsListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun deleteItem(position: Int): SOSContacts_Entity {
        Log.d(TAG, "deleteItem: is called")

        val tobeDelete = SOSContacts_Entity(
            listInfoOffline[position].Phone,
            listInfoOffline[position].Priority,
            listInfoOffline[position].Name,
            listInfoOffline[position].Image
        )

        listInfoOffline.removeAt(position)
        notifyDataSetChanged()
        return tobeDelete
    }


    fun setValue(list: List<SOSContacts_Entity>) {
        this.listInfoOffline = list as MutableList<SOSContacts_Entity>
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listInfoOffline[position], context)
    }

    override fun getItemCount(): Int {
        return listInfoOffline.size
    }


}