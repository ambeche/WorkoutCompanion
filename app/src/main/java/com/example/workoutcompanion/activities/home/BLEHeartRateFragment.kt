package com.example.workoutcompanion.activities.home
/*
* BLE Scanner is set to filter just BLE HEART_RATE_SERVICE
* Scanner runs in android Bluetooth thread and scan results are received through the ScanCallback
* that is implemented
* The gatt connector and characteristics reader are implemented in BleWrapper class that runs in its
* own thread separate from the Bluetooth Thread (BleWrapper is Authored by Jarkko Vuori)
* The heart rate in beats per minute is saved in room,  queried as liveData and visualized with the
* use of MPAndroidChart
*/

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.handler.BleWrapper
import com.example.workoutcompanion.R
import com.example.workoutcompanion.adapters.BleListAdapter
import com.example.workoutcompanion.interfaces.OnLoadFragment
import com.example.workoutcompanion.model.WorkoutCompanionViewModel
import com.example.workoutcompanion.model.roomdb.HeartRate
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_b_l_e_heart_rate.*
import java.util.*

class BLEHeartRateFragment : Fragment(), BleWrapper.BleCallback {
    private lateinit var bleAdapter: BluetoothAdapter
    private var bleScanning = false
    private lateinit var bLeScanner: BluetoothLeScanner
    private lateinit var bleHandler: Handler
    private lateinit var bleWrapper: BleWrapper
    lateinit var listAdapter: BleListAdapter
    var scanResult = ArrayList<ScanResult>()
    private val hrtData = ArrayList<Int>()
    private lateinit var activityListener: OnLoadFragment
    private lateinit var appViewModel: WorkoutCompanionViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLoadFragment) {
            activityListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bltManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        appViewModel = ViewModelProvider(this).get(WorkoutCompanionViewModel::class.java)
        bleAdapter = bltManager.adapter
        listAdapter = BleListAdapter(context)
        checkPermissionAndBleStatus()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragLayout =  inflater.inflate(
            R.layout.fragment_b_l_e_heart_rate,
            container, false
        )
        startBleScan()
        context?.let {
            // populates pop window with a list of scanned BLE results
            MaterialAlertDialogBuilder(it)
                .setTitle(resources.getString(R.string.sanned))
                .setAdapter(listAdapter) { dialog, which ->
                    connectToBleHrt(which)
                }
                .show()
        }

        appViewModel.heartRate.observe(viewLifecycleOwner, {
             // UI set to observe live data; heart rate from room
             val lineEntries = ArrayList<Entry>()
             it.forEach {hrt ->
                 hrt.bpm?.let { it1 -> Entry((hrt.hrt).toFloat(), it1) }?.let { it2 ->
                     lineEntries.add(
                         it2
                     )
                 }
             }
            if (lineEntries.isNotEmpty()) {
                val lineDataSet = LineDataSet(lineEntries, getString(R.string.hrt_bpm))
                val iLineDataSet = ArrayList<ILineDataSet>()
                iLineDataSet.add(lineDataSet)

                vLineChart.apply {
                    description.apply {
                        setPosition(700f, 900f)
                        textSize = 16f
                        text = getString(R.string.label_hrt)
                    }

                    xAxis.isEnabled = false

                    legend.isEnabled = false
                    data = LineData(iLineDataSet)
                    notifyDataSetChanged()
                    invalidate()
                }

            }
        })

        return fragLayout
    }

    override fun onStop() {
        super.onStop()
        // cleans the heart rate table
        appViewModel.deleteHrt()
    }

    private fun checkPermissionAndBleStatus(): Boolean {
        if ( activity?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            Log.d("DBG", getString(R.string.no_locaton))
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_FINE_LOCATION
            )
            return true
        } else if (!bleAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE)
        }
        return true
    }

    private fun startBleScan() {
        // scans for BLE devices
        Log.d("DBG", getString(R.string.tag_scan))
        bLeScanner = bleAdapter.bluetoothLeScanner
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()
        val hrtUUID = converttoUUID(0x180D)
        val hrtUUIDs = arrayOf<UUID>(hrtUUID)

        val filters: MutableList<ScanFilter>?

        filters = ArrayList()
        // filter is defined to only scan for BLE HEART RATE SERVICE by UUID; 0x1800
        for (bleHrt in hrtUUIDs) {
            val filter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(bleHrt))
                .build()
            filters.add(filter)
        }
        val bleScanCallback = BleScanCallback()
        if (!bleScanning) {
            bleHandler = Handler(Looper.getMainLooper())
            bleHandler.postDelayed({
                bleScanning = false
                bLeScanner.stopScan(bleScanCallback)
            }, SCAN_PERIOD)
            bleScanning = true
            bLeScanner.startScan(filters, settings, bleScanCallback)
        }else {
            bleScanning = false
            bLeScanner.stopScan(bleScanCallback)
        }
    }

    private fun connectToBleHrt( pos: Int) {
        // connects to a chosen BLe heart rate device on the scanned result list
        bleWrapper = context?.let { BleWrapper(it, scanResult[pos].device.address) }!!
        bleWrapper.also {
            it.addListener(this@BLEHeartRateFragment)
            it.connect(false)
        }
        toast(getString(R.string.connecting))
    }

    inner class BleScanCallback  : ScanCallback() {
        // callback methods for receiving scan results from the bluetooth service
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            setBleResults(result)
            Log.d("result1", result.device.address)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            if (results != null) {
                for (result in results) { setBleResults(result)}
            }
        }
        override fun onScanFailed(errorCode: Int) {
            Log.d("Scan", errorCode.toString())
        }

        private fun setBleResults(result: ScanResult) {
            val deviceName = result.device.name
            listAdapter.setAdapter(result)
            scanResult.add(result)
            Log.d("result", deviceName)
        }
    }

    override fun onDeviceReady(gatt: BluetoothGatt) {
        // called when connection has been established between BLE devices
        val hrtUUID = bleWrapper.HEART_RATE_SERVICE_UUID
        for (service in gatt.services) {
            Log.d("services", "${service.uuid}")
            if (service.uuid == hrtUUID) {
                Log.d("HRT_UUID", getString(R.string.is_matched))
                for (characteristic in service.characteristics) {
                    Log.d("xtics", "${characteristic.uuid}")
                    bleWrapper.getNotifications(gatt, service.uuid, characteristic.uuid)
                }
            }
        }
        toast(getString(R.string.connected, gatt.device.name))
    }

    override fun onDeviceDisconnected() {
        toast(getString(R.string.ble_disconnected))
    }

    override fun onNotify(characteristic: BluetoothGattCharacteristic) {
        // called to update the ui of data read from the external BLE device
        val format = BluetoothGattCharacteristic.FORMAT_UINT16
        val bpm = characteristic.getIntValue(format, 1)
        val hrt = "$bpm bpm"
        tvHrt?.text = hrt
        // persisting heart rate to room
        appViewModel.addHearRate(HeartRate(0, bpm.toFloat()))
        hrtData.add(bpm)
        Log.d("value", hrt)
    }

    private fun toast(text: String) {
        Toast.makeText(
            activity, text, Toast.LENGTH_SHORT).show()
    }
    fun converttoUUID(i: Int): UUID {
        val MSB = 0x0000000000001000L
        val LSB = -0x7fffff7fa064cb05L
        val value = (i and -0x1).toLong()
        return UUID(MSB or (value shl 32), LSB)
    }

    companion object {
        const val SCAN_PERIOD: Long = 3000
        const val REQUEST_CODE_ENABLE = 0
        const val REQUEST_CODE_FINE_LOCATION = 1

        fun newInstance() = BLEHeartRateFragment()
    }
}