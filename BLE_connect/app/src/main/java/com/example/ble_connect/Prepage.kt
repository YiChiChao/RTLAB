package com.example.ble_connect

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ble_connect.BLEmodule.ConnectActivity


class Prepage : AppCompatActivity(){

    val MY_PERMISSIONS_REQUEST_BLE: Int = 1
    private val BLE_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val ANDROID_12_BLE_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_first)
        val switchBtn = findViewById<Button>(R.id.button_first)
        switchBtn.setOnClickListener {
            needPermission(MY_PERMISSIONS_REQUEST_BLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(
                    this,
                    ANDROID_12_BLE_PERMISSIONS,
                    MY_PERMISSIONS_REQUEST_BLE
                )
                if (requestBLUETOOTH_SCANPermissions(1) && requestBLUETOOTH_CONNECTPermissions(1) &&
                    requestACCESS_FINE_LOCATIONPermissions(1)
                ) {
                    val intent = Intent(this, ConnectActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                ActivityCompat.requestPermissions(
                    this,
                    BLE_PERMISSIONS,
                    MY_PERMISSIONS_REQUEST_BLE
                )
                if ( requestACCESS_COARSE_LOCATIONPermissions(1) &&
                    requestACCESS_FINE_LOCATIONPermissions(1)
                ) {
                    val intent = Intent(this, ConnectActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    fun needPermission(requestCode: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
           return
        }
        requestAllPermissions(requestCode)
    }


    fun requestAllPermissions(requestCode: Int) {
        ActivityCompat.requestPermissions(
            this,
            ANDROID_12_BLE_PERMISSIONS,
            MY_PERMISSIONS_REQUEST_BLE
        )
    }


    fun requestBLUETOOTH_SCANPermissions(requestCode: Int): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            )
            != PackageManager.PERMISSION_GRANTED
        ) { //沒有許可權
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                MY_PERMISSIONS_REQUEST_BLE
            )
            false
        } else {
            true
        }
    }


    fun requestBLUETOOTH_CONNECTPermissions(requestCode: Int): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            )
            != PackageManager.PERMISSION_GRANTED
        ) { //沒有許可權
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                MY_PERMISSIONS_REQUEST_BLE
            )
            false
        } else {
            true
        }
    }

    fun requestACCESS_FINE_LOCATIONPermissions(requestCode: Int): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) { //沒有許可權
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_BLE
            )
            false
        } else {
            true
        }
    }

    fun requestACCESS_COARSE_LOCATIONPermissions(requestCode: Int): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) { //沒有許可權
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                MY_PERMISSIONS_REQUEST_BLE
            )
            false
        } else {
            true
        }
    }
}

