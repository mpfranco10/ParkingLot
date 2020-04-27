package com.example.parkinglot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_parqueadero.*

class ParqueaderoActivity : AppCompatActivity() {

    val parqueader = ""
    val direccion = ""
    val abiertod = ""
    val abiertoh = ""
    val horai = ""
    val costopm = 0
    val apagar = 0

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

        val btnVolver = findViewById<View>(R.id.buttonBack) as Button
        val btnOnline = findViewById<View>(R.id.buttonPagoon) as Button
        val btnIngresar = findViewById<View>(R.id.buttonIngresarcod) as Button

        if (obj != null) {
            tvpark.text = obj.name
            tvabiertodesde.text  = obj.opening_hour
            tvabiertohasta.text = obj.closing_hour
            tvhorain.text = "ya miramos"
            tvcostomin.text = obj.parking_car_cost.toString()
            tvapagar.text = "5000"
        }

        btnVolver.setOnClickListener {
            onBackPressed()
        }
    }
}
