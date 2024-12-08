package utez.edu.mx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import utez.edu.mx.R
import utez.edu.mx.models.Task


class TaskAdapter(private val taskList: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {


    // Crea un nuevo ViewHolder inflando el layout de cada elemento
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }


    // Vincula los datos de cada tarea a las vistas dentro del ViewHolder
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.bind(task)
    }


    // Devuelve el tamaño de la lista de tareas
    override fun getItemCount(): Int = taskList.size


    // ViewHolder para cada tarea, que contiene las vistas necesarias
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)


        // Método para vincular los datos de la tarea a las vistas del ViewHolder
        fun bind(task: Task) {
            titleTextView.text = task.title
            descriptionTextView.text = task.description
        }
    }
}
