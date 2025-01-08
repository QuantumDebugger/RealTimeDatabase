package com.example.realtimedatabase

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.realtimedatabase.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity(), RecyclerInterface {
    private lateinit var binding: ActivityMainBinding
    var itemArray = arrayListOf<ItemData>()
    var recyclerAdapter = RecyclerAdapter(itemArray, this)
    lateinit var linearLayoutManager: LayoutManager
    private var dbReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val TAG = "MainActivity"
    // firebase
    val db = Firebase.firestore
    var collName = "stdCollection"



    // Fire Store Database work

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Listerner of Firestore
        db.collection(collName).addSnapshotListener { snapshot, e ->
            if (e !== null){
                return@addSnapshotListener
            }
            for (snapshot in snapshot !!.documentChanges){
                val userModel = convertObject(snapshot.document)

                when(snapshot.type){

                    DocumentChange.Type.ADDED->{
                        userModel.let {
                            if (it != null) {
                                itemArray.add(it)
                            }
                        }
                        Log.e(TAG, "onCreate: ${itemArray.size}" )
                        Log.e(TAG, "onCreate: $itemArray" )

                    }
                    DocumentChange.Type.MODIFIED-> {
                        userModel.let {
                            val index = userModel?.let { it1 -> getIndex(it1) }
                            if (index != null) {
                                if (index > -1)
                                    it?.let { it1 ->
                                        if (index != null) {
                                            itemArray.set(index, it1)
                                        }
                                    }
                            }
                        }
                    }

                    DocumentChange.Type.REMOVED-> {
                        userModel.let {
                            val index = userModel?.let { it1 -> getIndex(it1) }
                            if (index != null) {
                                if(index > -1) {
                                    itemArray.removeAt(index)
                                }
                            }
                        }
                    }
                }
            }
            recyclerAdapter.notifyDataSetChanged()
        }







        binding.rvDyn.adapter = recyclerAdapter
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvDyn.layoutManager = linearLayoutManager


        binding.fab.setOnClickListener {
            Dialog(this).apply {
                setContentView(R.layout.custom_dialog_box)
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val classNameText = findViewById<EditText>(R.id.classeEt)
                val rollNoText = findViewById<EditText>(R.id.rollNumberEt)
                val subject = findViewById<EditText>(R.id.subjectEt)
                val addBtn = findViewById<Button>(R.id.addBtn)

                addBtn.setOnClickListener {
                    if (classNameText.text.trim().isNullOrEmpty()) {
                        classNameText.error = "pleae enter the class name"
                    } else if (rollNoText.text.trim().isNullOrEmpty()) {
                        rollNoText.error = "Enter the roll number"
                    } else if (subject.text.trim().isNullOrEmpty()) {
                        subject.error = "enter subject name"
                    } else {
                        var categoriesModel = ItemData(
                            className = classNameText.text.toString(),
                            rollNumber = rollNoText.text.toString().toInt(),
                            subject = subject.text.toString()

                        )
                        db.collection(collName).add(categoriesModel).addOnCompleteListener {
                            if (it.isSuccessful)
                                println("Data Saved, ${it.result}")
                            dismiss()
                        }
//                        itemArray.add(
//                            ItemData(
//                                className = classNameText.text.toString(),
//                                subject = subject.text.toString(),
//                                rollNumber = rollNoText.text.toString().toInt()
//                            )
//                        )
                        recyclerAdapter.notifyDataSetChanged()

                    }
                }


            }.show()
        }


    }

    //firestore conversation data object

    fun convertObject(snapshot : QueryDocumentSnapshot): ItemData{
        val itemModel : ItemData=
            snapshot.toObject(ItemData ::class.java)
        if (itemModel != null) {
            itemModel.id = snapshot.id?: ""
        }
        return itemModel

    }
    fun getIndex(itemModel : ItemData) : Int{
        var index = -1
        index = itemArray.indexOfFirst { element ->
            element.id?.equals(itemModel.id) == true
        }
        return index
    }

    override fun updateBtn(position: Int) {
        Toast.makeText(this, "$position", Toast.LENGTH_SHORT).show()
        Dialog(this).apply {
            setContentView(R.layout.custom_dialog_box)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val classEditText = findViewById<EditText>(R.id.classeEt)
            val rollNoEditText = findViewById<EditText>(R.id.rollNumberEt)
            val subjectEditText = findViewById<EditText>(R.id.subjectEt)
            val addbtn = findViewById<Button>(R.id.addBtn)


            val classData = itemArray[position].className
            val subjectData = itemArray[position].subject
            val rollNoData = itemArray[position].rollNumber


            classEditText.setText(classData)
            subjectEditText.setText(subjectData)
            rollNoEditText.setText(rollNoData.toString())


            addbtn.setOnClickListener {

                if (classEditText.text.trim().isNullOrEmpty()) {
                    classEditText.error = "pleae enter the class name"
                } else if (rollNoEditText.text.trim().isNullOrEmpty()) {
                    rollNoEditText.error = "Enter the roll number"
                } else if (subjectEditText.text.trim().isNullOrEmpty()) {
                    subjectEditText.error = "enter subject name"
                } else{
                    var updatedData = ItemData(id = itemArray[position].id, className = classEditText.text.toString(), subject = subjectEditText.text.toString(), rollNumber = rollNoEditText.text.toString().toInt() )
                    db.collection(collName).document(itemArray[position].id?:"").set(updatedData).addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this@MainActivity, "Update Successfully", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@MainActivity, "Update Failed", Toast.LENGTH_SHORT).show()
                        }
                        dismiss()
                    }




                }
            }


        }.show()
    }


    override fun deleteBtn(position: Int) {
        db.collection(collName).document(itemArray[position].id?:"").delete()


    }
}