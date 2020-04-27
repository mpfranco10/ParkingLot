package com.example.parkinglot

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import me.dm7.barcodescanner.zxing.ZXingScannerView
import com.google.zxing.Result

/**
 * Created by Parsania Hardik on 19-Mar-18.
 */
class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "esta_parqueado"

    companion object {
        private const val HUAWEI = "huawei"
        private const val MY_CAMERA_REQUEST_CODE = 6515
        fun getScanQrCodeActivity(callingClassContext: Context) =
            Intent(callingClassContext, ScanActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_scan)
        setScannerProperties()

    }

    /**
     * Set bar code scanner basic properties.
     */

    private fun setScannerProperties() {
        val qrCodeScanner = findViewById<ZXingScannerView>(R.id.qrCodeScanner)
        qrCodeScanner.setFormats(listOf(BarcodeFormat.QR_CODE))
        qrCodeScanner.setAutoFocus(true)
        qrCodeScanner.setLaserColor(R.color.colorAccent)
        qrCodeScanner.setMaskColor(R.color.colorAccent)
        if (Build.MANUFACTURER.equals(HUAWEI, ignoreCase = true))
            qrCodeScanner.setAspectTolerance(0.5f)
    }

    /**
     * resume the qr code camera when activity is in onResume state.
     */

    override fun onResume() {
        super.onResume()
        val qrCodeScanner = findViewById<ZXingScannerView>(R.id.qrCodeScanner)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA),
                    MY_CAMERA_REQUEST_CODE
                )
                return
            }
        }
        qrCodeScanner.startCamera()
        qrCodeScanner.setResultHandler(this)
    }

    /**
     * To check if user grant camera permission then called openCamera function.If not then show not granted
     * permission snack bar.
     *
     * @param requestCode  specify which request result came from operating system.
     * @param permissions  to specify which permission result is came.
     * @param grantResults to check if user granted the specific permission or not.
     */

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openCamera()
            else if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                Toast.makeText(
                    this,
                    "Se deben tener permisos de cámara para leer el código.",
                    Toast.LENGTH_LONG
                ).show();
        }
    }

    private fun openCamera() {
        val qrCodeScanner = findViewById<ZXingScannerView>(R.id.qrCodeScanner)
        qrCodeScanner.startCamera()
        qrCodeScanner.setResultHandler(this)
    }

    /**
     * stop the qr code camera scanner when activity is in onPause state.
     */

    override fun onPause() {
        val qrCodeScanner = findViewById<ZXingScannerView>(R.id.qrCodeScanner)
        super.onPause()
        qrCodeScanner.stopCamera()
    }

    override fun handleResult(p0: Result?) {
        if(p0!=null){
        val result = p0.text

        var database = FirebaseFirestore.getInstance()
            val docRef =
                database.collection("malls").document(result)

            docRef.get()
                .addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                    if (task.isSuccessful) {
                        val document = task.result?.toString()
                        val out = task.result

                        if (!document?.contains("doc=null")!!) {

                            //CREAR un objeto a partir de la bD de firebase
                            val note: ParqueaderoFirebase? = task.result!!.toObject(ParqueaderoFirebase::class.java)


                            val sharedPref: SharedPreferences? = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
                            if (sharedPref != null) {
                                Log.d("shared", sharedPref.getString(PREF_NAME, "DEFAULT"))
                            }
                            val editor = sharedPref?.edit()
                            if (editor != null) {
                                editor.putString(PREF_NAME, "P")
                                editor.apply()
                            }

                            //se lo mandamos a la otra actividad para que lo muestre
                            val intent = Intent(this@ScanActivity, ParqueaderoActivity::class.java)
                            intent.putExtra("extra_object", note)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@ScanActivity,
                                "No existe el parqueadero: " + result,
                                Toast.LENGTH_SHORT
                            ).show();
                            onBackPressed()
                        }
                    } else {
                        Log.d("Fallo QR", "get failed with ", task.exception)
                        onBackPressed()
                    }
                })



    }}
}