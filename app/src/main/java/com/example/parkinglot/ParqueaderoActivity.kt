package com.example.parkinglot

import android.content.ContentValues
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_parqueadero.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    var cuantopaga = 0
    var costo = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parqueadero)

        val obj: ParqueaderoFirebase =
            intent.getSerializableExtra("extra_object") as ParqueaderoFirebase
        val hora: String = intent.getSerializableExtra("hora") as String


        costo = obj.parking_car_cost!!
        Log.d("boredyet", hora)

        val currentDateTime = LocalDateTime.now()
        val horaactual= currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

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
            if(hora!="na") {
                tvhorain.text = hora

                val t1 = horaactual.split(":")
                val t2 = hora.split(":")

                val h1 = t1[0].toInt()
                val m1 = t1[1].toInt()

                val h2 = t2[0].toInt()
                val m2 = t2[1].toInt()

                val diffho = h1-h2
                val diffmin = m1 - m2
                val tot = 60*diffho + diffmin


                cuantopaga = tot*costo
                tvapagar.text = cuantopaga.toString()

            }
            else{

                val sharedPref: SharedPreferences? = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
                if (sharedPref != null) {

                    val phora = sharedPref.getString("hora_parqueo", "DEFAULT")
                    tvhorain.text = phora

                    val t1 = horaactual.split(":")
                    val t2 = phora?.split(":")

                    val h1 = t1[0].toInt()
                    val m1 = t1[1].toInt()

                    val h2 = t2?.get(0)?.toInt()
                    val m2 = t2?.get(1)?.toInt()

                    val diffho = h1- h2!!
                    val diffmin = m1 - m2!!
                    val tot = 60*diffho + diffmin


                    cuantopaga = tot*costo
                    tvapagar.text = cuantopaga.toString()

                    }
            }

            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(date.toLong())



            tvcostomin.text = obj.parking_car_cost.toString()

        }

        btnVolver.setOnClickListener {
            onBackPressed()
        }

        btnOnline.setOnClickListener {
            //dbHandler.addName(user)
            var units = (java.util.Calendar.getInstance().timeInMillis.toDouble()-date.toDouble())/3600000

            val sharedPref: SharedPreferences? = getSharedPreferences("alreadylogged", PRIVATE_MODE)
            val guser = sharedPref?.getString("username", "DEFAULT")
            val nuser = guser.toString()

                            val docRef2 = database.collection("users").document(nuser)

                            docRef2.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                                if (task.isSuccessful) {
                                    val document2 = task.result?.toString()
                                    if (!document2?.contains("doc=null")!!) {
                                        val note: User? = task.result!!.toObject(User::class.java)
                                        var x = task.result?.data.toString()
                                        var indx2 = x.indexOf("saldo=") + 6
                                        x = x.substring(indx2)
                                        indx2 = x.indexOf(",");
                                        x = x.substring(0, indx2)

                                        var nSaldo = (x.toDouble() - units).roundToInt()
                                        val saldo = note?.saldo

                                        if (saldo !=null && saldo > cuantopaga) {
                                            val nuevosaldo = saldo - cuantopaga
                                            docRef2.update("saldo", nuevosaldo)

                                            Toast.makeText(
                                                this,
                                                "Listo! Pagaste $cuantopaga",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            lfile.createNewFile()

                                            val lfilewriter = FileWriter(lfile)
                                            val lout = BufferedWriter(lfilewriter)
                                            lout.write("EMPTY" + ";" + "EMPTY")
                                            lout.close()

                                            val sharedPref: SharedPreferences? =
                                                this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
                                            if (sharedPref != null) {
                                                Log.d(
                                                    "valorespref",
                                                    sharedPref.getString(PREF_NAME, "DEFAULT")
                                                )
                                            }
                                            val editor = sharedPref?.edit()
                                            if (editor != null) {
                                                editor.putString(PREF_NAME, "N")
                                                editor.apply()
                                            }

                                            onBackPressed()
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "No tienes suficiente saldo para este pago",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            })


        }

        btnIngresar.setOnClickListener {
            Toast.makeText(this,"Todavía no se puede pagar con un código :(", Toast.LENGTH_SHORT).show();
        }
    }
}
