package com.example.ble_connect

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private var deviceData: ArrayList<BluetoothDevice>) : RecyclerView.Adapter<RecyclerViewHolder>(){
    private val data = (1..100).toList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerViewHolder {
                return RecyclerViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(
                            R.layout.item_recyclerviewholder,
                            parent,
                            false
                        )
                )
    }

    override fun getItemCount() = deviceData.size

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        // 因為 ViewHolder 會重複使用，
        // 我們要在這個 function 依據 position
        // 把正確的資料跟 ViewHolder 綁定在一起。
        holder.bind(deviceData?.get(position))
    }


}
