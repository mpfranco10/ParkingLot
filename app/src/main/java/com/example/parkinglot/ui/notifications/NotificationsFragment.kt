package com.example.parkinglot.ui.notifications

<<<<<<< Updated upstream
=======
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
>>>>>>> Stashed changes
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
<<<<<<< Updated upstream
import com.example.parkinglot.R
=======
import com.example.parkinglot.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.Instant
import java.time.format.DateTimeFormatter

>>>>>>> Stashed changes

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        // val textView: TextView = root.findViewById(R.id.text_notifications)
        // notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        // })
<<<<<<< Updated upstream
=======

        var database = FirebaseFirestore.getInstance()

        val t:TextView= root.findViewById(R.id.text_notifications2)
        val tum:TextView= root.findViewById(R.id.text_edad2)
        val ted:TextView= root.findViewById(R.id.text_edad)

        val tp:TextView= root.findViewById(R.id.textPark)

        val btnPrueba: Button = root.findViewById(R.id.boton_park)
        val db = FirebaseFirestore.getInstance()
        // [END get_firestore_instance]

        var perfil = ""

        // [START set_firestore_settings]
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
        // [END set_firestore_settings]

        db.collection("users")
            .whereEqualTo("firstname", "Sergio")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    perfil = "${document.id} => ${document.data}"
                    val note: User = document.toObject(User::class.java)
                    t.text = note.firstname + " " + note.lastname
                    tum.text = note.phone_number.toString()
                    ted.text = note.birthday.toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        //Cambiar esto a si no hay conexión y si todavia no se ha guardado la info ->
        val context = context as MainActivity
        btnPrueba.setOnClickListener{
            btnPrueba.isEnabled = false
            btnPrueba.isClickable = false
            var datos = ""

            //////////////////////
            db.collection("malls")
                .whereEqualTo("name", "Unicentro")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        datos = "${document.data}"
                        tp.text = datos
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }

            /////////////////////////

            if(isNetwork()) {
                val dbHandler = ParqueaderoOpenHelper(context, null)
                val nombre = "Parqueadero"
                val imagen = "-"
                val precio = "Precio"
                val horario = "Horario"
                val user = Parqueadero(nombre, precio, horario, imagen)
                val valores =
                    "Nombre:" + nombre + ",imagen:" + imagen + ",precio:" + precio + ",horario:" + horario

                val lfile = File(context.getFilesDir(), "PARQUEADERO.txt")
                var resp = ""
                var id = ""
                lfile.forEachLine {
                    resp = it.split(";")[1]
                    id = it.split(";")[0]
                }
                //dbHandler.addName(user)
                var units = (java.util.Calendar.getInstance().timeInMillis.toDouble()-resp.toDouble())/3600000
                val docRef = database.collection("malls").document(id.toString())
                docRef.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                    if (task.isSuccessful)
                    {
                        val document = task.result?.toString()

                        if (document?.contains("doc=null")!!)
                        {
                        }
                        else
                        {
                            Toast.makeText(context,"No existe el parqueadero: " + id, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Log.d(TAG, "get failed with ", task.exception)
                    }
                })

                units = units*1
                Toast.makeText(getActivity(),"Debes: " + units.toString() + "!", Toast.LENGTH_SHORT).show();
                println(java.util.Calendar.getInstance().timeInMillis.toString())
                println(units)
                println(valores)


            }
        }


>>>>>>> Stashed changes

        return root
    }
}
