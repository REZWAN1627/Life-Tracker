package com.rex.lifetracker.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rex.lifetracker.databinding.LocalEmergencyItemRecyclerBinding
import com.rex.lifetracker.model.FireBaseModel.EmergencyModel.LocalEmergencyModel

class LocalEmergency_RecyclerView(context: Context) : RecyclerView.Adapter<LocalEmergency_RecyclerView.MyViewHolder>() {
    private var localItem = ArrayList<LocalEmergencyModel>()
    private val context = context
    class MyViewHolder(val binding: LocalEmergencyItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, localEmergencyModel: LocalEmergencyModel) {
            binding.apply {
                thanaName.text = localEmergencyModel.thana
                hospitalName.text = localEmergencyModel.hospital
                hospitalNumber.text = localEmergencyModel.hospitalNumber
                fireServiceName.text = localEmergencyModel.fireService
                fireServiceNumber.text = localEmergencyModel.fireServiceNumber
                numberOS.text = localEmergencyModel.osNumber
                hospitalCall.setOnClickListener {
                    callNumber(localEmergencyModel.hospitalNumber,context)
                }
                fireServiceCall.setOnClickListener {
                    callNumber(localEmergencyModel.fireServiceNumber,context)
                }
            }
        }

        private fun callNumber(number: String, context: Context) {
            val intent =
                Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("tel:$number")

            context.startActivity(intent)
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(
            LocalEmergencyItemRecyclerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun callNumber(number:String){

    }

    fun setData(localItem:ArrayList<LocalEmergencyModel>){
        this.localItem = localItem
        notifyDataSetChanged()
    }
    fun clear(){
        localItem.clear()
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(context,localItem[position])
    }

    override fun getItemCount(): Int {
        return localItem.size
    }
}