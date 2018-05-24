package org.kinloch.colin.wear.bare.comms

import android.app.Service

import android.content.Intent
import android.content.Context
import android.os.IBinder
import android.os.ParcelUuid
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser

import java.util.UUID

import android.util.Log

val SERVICE_UUID = UUID.fromString("E87F8148-2DFF-4DF9-ADD9-8E6C458187AE")

class MessageService : Service() {
  val TAG = "bare_face"
  
  private lateinit var bluetoothAdapter: BluetoothAdapter
  
  private lateinit var bluetoothLeAdvertiser: BluetoothLeAdvertiser
  private lateinit var bluetoothGattServer: BluetoothGattServer
  
  override fun onStartCommand(intent: Intent, flags: Int, startId: Int) : Int {
    Log.v(TAG, "Message Service started")
    val bluetoothManager =
      getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    bluetoothAdapter = bluetoothManager.getAdapter()
    val devices = bluetoothAdapter.getBondedDevices()
    for (device in devices) {
      Log.v(TAG, "$device = ${device.getName()}")
    }
    bluetoothGattServer = bluetoothManager.openGattServer(this, gattServerCallback)
    
    val adSettings = AdvertiseSettings.Builder()
      .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
      .setConnectable(true)
      .setTimeout(0)
      .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
      .build()
    
    val adData = AdvertiseData.Builder()
      //.setIncludeDeviceName(true)
      .setIncludeTxPowerLevel(false)
      .addServiceUuid(ParcelUuid(SERVICE_UUID))
      .build()
    
    bluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
    bluetoothLeAdvertiser.startAdvertising(adSettings, adData, adCallback)
    bluetoothLeAdvertiser.stopAdvertising(adCallback)
    return super.onStartCommand(intent, flags, startId)
  }
  
  override fun onBind(intent: Intent): IBinder? {
    return null
  }
  
  private val adCallback = object : AdvertiseCallback() {
    override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
      Log.v(TAG, "Ad start")
    }
    override fun onStartFailure(errorCode: Int) {
      Log.v(TAG, "Ad fail $errorCode")
    }
  }
  
  private val gattServerCallback = object : BluetoothGattServerCallback() {
    override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
      when (newState) {
        BluetoothProfile.STATE_CONNECTED -> null
        BluetoothProfile.STATE_DISCONNECTED -> null
        else -> null
      }
      Log.v(TAG, "Connection state changed gattttt")
    }
    override fun onCharacteristicReadRequest(device: BluetoothDevice, requestId: Int, offset: Int,
      characteristic: BluetoothGattCharacteristic) {
    }
    override fun onDescriptorReadRequest(device: BluetoothDevice, requestId: Int, offset: Int,
      descriptor: BluetoothGattDescriptor) {
    }
    override fun onCharacteristicWriteRequest(device: BluetoothDevice, requestId: Int, characteristic: BluetoothGattCharacteristic,
      preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray) {
    }
  }
}
