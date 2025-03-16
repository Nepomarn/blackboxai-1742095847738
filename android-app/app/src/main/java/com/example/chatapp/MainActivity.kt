package com.example.chatapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var messageList: ListView
    private lateinit var database: DatabaseReference
    private lateinit var messages: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance().reference
        messages = ArrayList()

        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        messageList = findViewById(R.id.messageList)

        // Set up the adapter for the ListView
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messages)
        messageList.adapter = adapter

        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                // Send message to Firebase
                database.child("messages").push().setValue(message)
                messageInput.text.clear() // Clear the input field after sending
                Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
            }
        }

        // Retrieve messages from Firebase
        database.child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                for (data in snapshot.children) {
                    val message = data.getValue(String::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }
                // Update ListView with messages
                adapter.notifyDataSetChanged() // Notify the adapter to refresh the ListView
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load messages.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
