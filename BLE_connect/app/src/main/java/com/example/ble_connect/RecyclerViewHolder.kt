package com.example.ble_connect

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ble_connect.BLEmodule.ConnectActivity
import android.content.Context
import android.widget.Toast
import kotlin.coroutines.coroutineContext

class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val deviceName: TextView = itemView.findViewById(R.id.textView_DeviceName)
    private val deviceAddress: TextView = itemView.findViewById(R.id.textView_Address)
    @SuppressLint("MissingPermission")
    fun bind(currentDevice: BluetoothDevice?) {
        deviceName.text = currentDevice?.name
        deviceAddress.text = currentDevice?.address


    }
}