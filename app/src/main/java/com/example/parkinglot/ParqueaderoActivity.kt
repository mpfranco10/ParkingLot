package com.example.parkinglot

import android.content.ContentValues
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_parqueadero.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class ParqueaderoActivity : AppCompatActivity() {

    val parqueader = ""
    val direccion = ""
    val abiertod = ""
    val abiertoh = ""
    val horai = ""
    val costopm = 0
    val apagar = 0

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "esta_parqueado"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parqueadero)

        val obj :ParqueaderoFirebase = intent.getSerializableExtra("extra_object") as ParqueaderoFirebase

        val tvpark = findViewById<View>(R.id.tvParqueadero) as TextView
        val tvabiertodesde = findViewById<View>(R.id.tvAbiertodesde) as TextView
        val tvabiertohasta = findViewById<View>(R.id.tvAbiertohasta) as TextView
        val tvhorain = findViewById<View>(R.id.tvHorain) as TextView
        val tvcostomin = findViewById<View>(R.id.tvCostoxmin) as TextView
        val tvapagar = findViewById<View>(R.id.tvApagar) as TextView
        var database = FirebaseFirestore.getInstance()

        val btnVolver = findViewById<View>(R.id.buttonBack) as Button
        val btnOnline = findViewById<View>(R.id.buttonPagoon) as Button
        val btnIngresar = findViewById<View>(R.id.buttonIngresarcod) as Button

        val lfile = File(getFilesDir(), "PARQUEADERO.txt")
        var date = ""
        var id = ""
        lfile.forEachLine {
            date = it.split(";")[1]
            id = it.split(";")[0]
        }

        var units = (java.util.Calendar.getInstance().timeInMillis.toDouble()-date.toDouble())/3600000
        var unitsResp = (units* obj.parking_car_cost!!).roundToInt()

        if (obj != null) {
            tvpark.text = obj.name
            tvabiertodesde.text  = obj.opening_hour
            tvabiertohasta.text = obj.closing_hour

            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(date.toLong())
            tvhorain.text = sdf.format(netDate)

            tvcostomin.text = obj.parking_car_cost.toString()
            tvapagar.text = unitsResp.toString()
        }

        btnVolver.setOnClickListener {
            onBackPressed()
        }

        btnOnline.setOnClickListener {
            //dbHandler.addName(user)
            var units = (java.util.Calendar.getInstance().timeInMillis.toDouble()-date.toDouble())/3600000

            val docRef = database.collection("malls").document(id.toString())
            docRef.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                if (task.isSuccessful)
                {
                    val document = task.result?.toString()

                    if (!document?.contains("doc=null")!!)
                    {
                        var str = task.result?.data.toString()
                        var indx = str.indexOf("parking_car_cost=")+17
                        str = str.substring(indx)
                        indx = str.indexOf(",");
                        str = str.substring(0,indx)

                        val lfile = File(getFilesDir(), "USERS.txt")
                        var username = "EMPTY"
                        lfile.forEachLine {
                            username = it.split(";")[0]
                        }

                        if(!username.equals("EMPTY"))
                        {
                            units = units*str.toDouble()
                            Toast.makeText(this,"Pagado: " + units.roundToInt().toString() + " !", Toast.LENGTH_SHORT).show();
                            val docRef2 = database.collection("users").document(username)

                            docRef2.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                                if (task.isSuccessful)
                                {
                                    val document2 = task.result?.toString()

                                    if (!document2?.contains("doc=null")!!)
                                    {
                                        var x = task.result?.data.toString()
                                        var indx2 = x.indexOf("saldo=")+6
                                        x = x.substring(indx2)
                                        indx2 = x.indexOf(",");
                                        x = x.substring(0,indx2)

                                        var nSaldo = (x.toDouble()-units).roundToInt()

                                        if(nSaldo>=0)
                                        {
                                            docRef2.update("saldo",nSaldo)

                                            lfile.createNewFile()

                                            val lfilewriter = FileWriter(lfile)
                                            val lout = BufferedWriter(lfilewriter)
                                            lout.write("EMPTY"+";"+"EMPTY")
                                            lout.close()

                                            val sharedPref: SharedPreferences? = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
                                            if (sharedPref != null) {
                                                Log.d("shared", sharedPref.getString(PREF_NAME, "DEFAULT"))
                                            }
                                            val editor = sharedPref?.edit()
                                            if (editor != null) {
                                                editor.putString(PREF_NAME, "N")
                                                editor.apply()
                                            }
                                        }
                                        else
                                        {
                                            Toast.makeText(this,"No suficiente Saldo", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            })
                        }
                        else
                        {
                            Toast.makeText(this,"NO EXISTES", Toast.LENGTH_SHORT).show();
                        }


                    }
                    else
                    {
                        Toast.makeText(this,"No existe el parqueadero: " + id, Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Log.d(ContentValues.TAG, "get failed with ", task.exception)
                }
            })
        }
    }
}
