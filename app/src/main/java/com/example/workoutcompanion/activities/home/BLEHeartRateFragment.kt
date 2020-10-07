package com.example.workoutcompanion.activities.home

import android.Manifest
import android.bluetooth.BluetoothAdapter
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
import com.example.workoutcompanion.R
import com.example.workoutcompanion.adapters.BleListAdapter
import com.example.workoutcompanion.interfaces.OnLoadFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*


class BLEHeartRateFragment : Fragment() {
    private lateinit var bleAdapter: BluetoothAdapter
    private var bleScanning = false
    private lateinit var bLeScanner: BluetoothLeScanner
    private lateinit var bleHandler: Handler
    private var bleScanResult : Array<String> = arrayOf()
    lateinit var listAdapter: BleListAdapter
    private lateinit var activityListener: OnLoadFragment

    companion object {
        const val SCAN_PERIOD: Long = 3000
        const val REQUEST_CODE_ENABLE = 0
        const val REQUEST_CODE_FINE_LOCATION = 1
        const val checkedItem = 1
        fun newInstance() = BLEHeartRateFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLoadFragment) {
            activityListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bltManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
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
            MaterialAlertDialogBuilder(it)
                .setTitle(resources.getString(R.string.sanned))
                .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                    activityListener.onLoadFragment()
                }
                .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                    toast("you are connected")
                }
                // Single-choice items (initialized with checked item)
                .setSingleChoiceItems(listAdapter, checkedItem) { dialog, which ->
                    // Respond to item chosen
                }
                .show()
        }

        return fragLayout
    }

    private fun checkPermissionAndBleStatus(): Boolean {
        if ( activity?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            Log.d("DBG", "No fine location access")
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
        Log.d("DBG", "Scan start")
        bLeScanner = bleAdapter.bluetoothLeScanner
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()
        val hrtUUID = convertToUUID(0x180D)
        val hrtUUIDs = arrayOf<UUID>(hrtUUID)

        var filters: MutableList<ScanFilter>? = null

        filters = ArrayList()
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

    inner class BleScanCallback  : ScanCallback() {
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
            Log.d("result", deviceName)
        }
    }

    private fun convertToUUID(i: Int): UUID {
        val MSB = 0x0000000000001000L
        val LSB = -0x7fffff7fa064cb05L  // 8000 0080 5F9B 34FB
        val value = (i and -0x1).toLong()
        return UUID(MSB or (value shl 32), LSB)
    }

    private fun toast(text: String) {
        Toast.makeText(
            activity, text, Toast.LENGTH_SHORT).show()
    }
}