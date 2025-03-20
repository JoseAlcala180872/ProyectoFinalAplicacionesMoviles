package alcala.jose.personalhabits

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HabitAdapter(
    private val context: Context,
    private val habitList: MutableList<Habito>,  // Lista de hábitos
    private val onEditClick: (Habito) -> Unit,   // Función para editar
    private val onDeleteClick: (Habito) -> Unit,  // Función para eliminar
    private val onCompleteClick: (Habito) -> Unit  // Función para manejar completar
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {


    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.habitTitle)
        val category: TextView = itemView.findViewById(R.id.habitCategory)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val completeButton: Button = itemView.findViewById(R.id.completeButton)  // Completar hábito
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.habit_item, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habitList[position]

        holder.title.text = habit.nombre
        holder.category.text = habit.categoria

        // Si el hábito está completado, cambia el texto y color
        if (habit.completado) {
            holder.title.text = String.format("%s (Completado)", habit.nombre)
            holder.title.setTextColor(Color.GRAY)  // O el color que desees
        } else {
            holder.title.setTextColor(Color.BLACK)
        }

        // Botón Editar
        holder.editButton.setOnClickListener {
            onEditClick(habit)
        }

        // Botón Eliminar
        holder.deleteButton.setOnClickListener {
            onDeleteClick(habit)
        }

        // Botón Completar: marca el hábito como completado
        holder.completeButton.setOnClickListener {
            habit.completado = true
            onCompleteClick(habit)
        }
    }


    override fun getItemCount(): Int {
        return habitList.size
    }
}
