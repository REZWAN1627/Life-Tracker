package com.rex.lifetracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rex.lifetracker.R
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SOSContacts_Entity
import com.rex.lifetracker.databinding.TrustedPropleItemBinding


class Contacts_RecyclerView(context: Context) :
    RecyclerView.Adapter<Contacts_RecyclerView.MyViewHolder>() {

    private var OfflinecontactsList = emptyList<SOSContacts_Entity>()
    private val context = context

    class MyViewHolder(private val binding: TrustedPropleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            soscontactsEntity: SOSContacts_Entity,
            context: Context
        ) {
            binding.apply {

                if (soscontactsEntity.Image != null){
                    Glide.with(context)
                        .asBitmap()
                        .load(soscontactsEntity.Image)
                        .placeholder(R.drawable.ic_man)
                        .into(listContact)
                }else{
                    Glide.with(context)
                        .asBitmap()
                        .load(soscontactsEntity.Image)
                        .placeholder(R.drawable.ic_man)
                        .into(listContact)
                }


                trustedName.text = soscontactsEntity.Name

                // trusted_Name.text = trustedContactsModel.Name

            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(
            TrustedPropleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(OfflinecontactsList[position], context)
    }

    fun setData(soscontactsEntity:List<SOSContacts_Entity>) {
        this.OfflinecontactsList = soscontactsEntity
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return OfflinecontactsList.size
    }
}