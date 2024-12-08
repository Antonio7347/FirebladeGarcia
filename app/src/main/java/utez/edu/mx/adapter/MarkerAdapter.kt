import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import utez.edu.mx.MarkerItem
import utez.edu.mx.R

class MarkerAdapter(private val markerList: List<MarkerItem>) :
    RecyclerView.Adapter<MarkerAdapter.MarkerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_marker, parent, false)
        return MarkerViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarkerViewHolder, position: Int) {
        val marker = markerList[position]
        holder.tvTitle.text = marker.title
        holder.tvCoordinates.text = marker.coordinates
    }

    override fun getItemCount(): Int = markerList.size

    class MarkerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvMarkerTitle)
        val tvCoordinates: TextView = itemView.findViewById(R.id.tvMarkerCoordinates)
    }
}
