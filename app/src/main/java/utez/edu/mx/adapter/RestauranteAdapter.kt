package utez.edu.mx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import utez.edu.mx.R
import utez.edu.mx.models.Restaurante

class RestauranteAdapter(private val listaRestaurantes: List<Restaurante>) :
    RecyclerView.Adapter<RestauranteAdapter.RestauranteViewHolder>() {

    class RestauranteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewRestaurante)
        val titulo: TextView = view.findViewById(R.id.textViewTitulo)
        val descripcion: TextView = view.findViewById(R.id.textViewDescripcion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestauranteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurante, parent, false)
        return RestauranteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestauranteViewHolder, position: Int) {
        val restaurante = listaRestaurantes[position]
        holder.titulo.text = restaurante.titulo
        holder.descripcion.text = restaurante.descripcion

        // Cargar imagen usando Glide
        Glide.with(holder.itemView.context)
            .load(restaurante.imagenUrl)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = listaRestaurantes.size
}
