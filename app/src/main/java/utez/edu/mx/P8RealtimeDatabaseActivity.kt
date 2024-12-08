package utez.edu.mx

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import utez.edu.mx.adapter.TaskAdapter
import utez.edu.mx.models.Task

class P8RealtimeDatabaseActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskList: MutableList<Task>
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_p8_realtime_database)

        database = FirebaseDatabase.getInstance().reference.child("tasks")

        taskList = mutableListOf()
        adapter = TaskAdapter(taskList)

        taskRecyclerView = findViewById(R.id.taskRecyclerView)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = adapter

        val addButton: Button = findViewById(R.id.addTaskButton)
        addButton.setOnClickListener { addTask() }

        loadTasks()

    }

    private fun loadTasks() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()

                for (taskSnapshot in snapshot.children) {
                    val taskId = taskSnapshot.child("taskId").getValue(String::class.java)
                    val title = taskSnapshot.child("title").getValue(String::class.java)
                    val description = taskSnapshot.child("description").getValue(String::class.java)

                    if (taskId != null && title != null && description != null) {
                        val task = Task(taskId, title, description)
                        taskList.add(task)
                    } else {
                        Log.w("P8RealtimeDatabase", "Skipping task with invalid structure")
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@P8RealtimeDatabaseActivity, "Error al cargar tareas", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun addTask() {
        val title = findViewById<EditText>(R.id.titleEditText).text.toString()
        val description = findViewById<EditText>(R.id.descriptionEditText).text.toString()

        val taskId = database.push().key ?: return

        val task = Task(taskId, title, description)

        database.child(taskId).setValue(task).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Tarea añadida", Toast.LENGTH_SHORT).show()
                findViewById<EditText>(R.id.titleEditText).text.clear()
                findViewById<EditText>(R.id.descriptionEditText).text.clear()
            } else {
                Toast.makeText(this, "Error al añadir la tarea", Toast.LENGTH_SHORT).show()
            }
        }
    }
}