package com.example.ble_connect.BLEmodule

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ble_connect.R
import com.example.ble_connect.RecyclerViewAdapter


class ConnectActivity : AppCompatActivity() {
    private var scanning: Boolean = false
    var devicesArray = ArrayList<BluetoothDevice>()
    private var bLEScanner: BluetoothLeScanner? = null
    private var bLEManager: BluetoothManager? = null
    private var bLEAdapter: BluetoothAdapter? = null
    private var bleGatt: BluetoothGatt? = null
    var mAdapter: RecyclerViewAdapter? = null
    var statusText:TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.connect_ble)
        statusText = findViewById(R.id.statusText)
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show()
            finish()
        }
        val scanBtn = findViewById<Button>(R.id.searchBtn)
        val recyclerView: RecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = RecyclerViewAdapter(devicesArray)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = recyclerView.adapter as RecyclerViewAdapter
        bLEManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bLEAdapter = bLEManager!!.adapter
        bLEScanner = bLEManager!!.adapter.bluetoothLeScanner

        // Checks if Bluetooth is supported on the device.
        if (bLEAdapter == null) {
            Toast.makeText(this, "error_bluetooth_not_supported", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        scanBtn.setOnClickListener {
            scanLeDevice(scanning)
        }

    }

    //Android 5.0以上
    private fun scanFilters(): List<ScanFilter> {
        //https://btprodspecificationrefs.blob.core.windows.net/assigned-numbers/Assigned%20Number%20Types/Health%20Device%20Profile.pdf
        val filter = ScanFilter.Builder().setDeviceName("MI").build()
        val list = ArrayList<ScanFilter>()
        list.add(filter)
        return list
    }
    //set the BLE scan mode
    private fun scanSettings(): ScanSettings {
        return ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()
    }

    private val scanCallback: ScanCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result != null) {
                Log.i(TAG, "Remote device name: " + result.device.address)
                addScanResult(result)
                mAdapter!!.notifyDataSetChanged()
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {

        }

        override fun onScanFailed(_error: Int) {
            Log.e(TAG, "BLE scan failed with code $_error")
        }


        @SuppressLint("MissingPermission", "NotifyDataSetChanged")
        private fun addScanResult(result: ScanResult) {
            // get scanned device
            val device = result.device
            // get scanned device MAC address
            val deviceAddress = device.address
            val deviceName = device.name

            for (dev in devicesArray) {
                if (dev.address == deviceAddress) return
            }
            // add arrayList
            devicesArray.add(result.device)
        }
    }



    @SuppressLint("MissingPermission")
    private fun scanLeDevice(enable: Boolean) {
        //Toast.makeText(baseContext, "Into scan function", Toast.LENGTH_SHORT).show()
        when (enable) {
            false -> {
                statusText?.text = getString(R.string.scanning)
                Handler(Looper.getMainLooper()).postDelayed({
                    scanning = false
                    statusText?.text = getString(R.string.disconnect)
                    bLEScanner?.stopScan(scanCallback)
                }, 10000)
                scanning = true
                bLEScanner!!.startScan(scanFilters(), scanSettings(), scanCallback)
                Toast.makeText(baseContext, "Start Scan", Toast.LENGTH_SHORT).show()

            }
            true -> {
                statusText?.text = getString(R.string.disconnect)
                scanning = false
                bLEScanner!!.stopScan(scanCallback)
                devicesArray.clear()
                mAdapter!!.notifyDataSetChanged()
                Toast.makeText(baseContext, "Stop", Toast.LENGTH_SHORT).show()
            }
        }

    }
    @SuppressLint("MissingPermission")
    fun connectDevice(device: BluetoothDevice?){
        bleGatt = device?.connectGatt(this, false, gattClientCallback)
    }

    private val gattClientCallback: BluetoothGattCallback = object: BluetoothGattCallback(){

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if(status == BluetoothGatt.GATT_FAILURE ) {
                disconnectGattServer()
                return
            } else if( status != BluetoothGatt.GATT_SUCCESS ) {
                disconnectGattServer()
                return
            }

            if(newState == BluetoothProfile.STATE_CONNECTED){
                statusText?.setOnClickListener {
                    statusText?.text = getString(R.string.connect)
                }
                Log.d(TAG, "Connected to the GATT server")
                gatt!!.discoverServices()
            } else if ( newState == BluetoothProfile.STATE_DISCONNECTED ) {
                disconnectGattServer()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            //check if the discovery failed
            if(status != BluetoothGatt.GATT_SUCCESS){
                Log.e(TAG, "Device service discovery failed, status: $status")
                return
            }

            //log for successful discovery
            Log.d(TAG, "Services discovery is successful")
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            //readCharacteristic(characteristic)
        }
        @SuppressLint("MissingPermission")
        fun disconnectGattServer(){
            Log.d(TAG, "Closing Gatt connection")
            // disconnect and close the gatt
            if (bleGatt != null) {
                bleGatt!!.disconnect()
                bleGatt!!.close()
                statusText?.setOnClickListener {
                    statusText?.text = getString(R.string.disconnect)
                }
            }
        }

    }




}

