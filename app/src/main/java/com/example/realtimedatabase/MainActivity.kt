package com.example.realtimedatabase

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.realtimedatabase.databinding.ActivityMainBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    var itemArray = arrayListOf<ItemData>()
    var recyclerAdapter = RecyclerAdapter(itemArray)
    lateinit var linearLayoutManager: LayoutManager
    var dbReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    private val TAG = "MainActivity"

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
        dbReference.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e(TAG, "snapshot $ {snapshot.value}")
                val itemData : ItemData? = snapshot.getValue(ItemData::class.java)
                itemData?.id = snapshot.key
                Log.e(TAG, "snapshot $ {snapshot.value}" )
                if (itemData != null){
                    itemArray.add(itemData)
                    recyclerAdapter.notifyDataSetChanged()

                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        itemArray.add(ItemData("Higher Studies","Maths",15))
        binding.rvDyn.adapter = recyclerAdapter
        linearLayoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvDyn.layoutManager = linearLayoutManager
    }
}