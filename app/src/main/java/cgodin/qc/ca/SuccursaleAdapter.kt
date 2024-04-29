package cgodin.qc.ca

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cgodin.qc.ca.model.Succursale
import cgodin.qc.ca.viewmodel.SuccursaleViewModel

class SuccursaleAdapter(
    private var succursaleList: MutableList<Succursale>,
    private val listener: OnItemClickListener,
    private val viewModel: SuccursaleViewModel,
    private val context: Context,
    private val isVisible: Boolean
) : RecyclerView.Adapter<SuccursaleAdapter.SuccursaleViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(succursale: Succursale)
        fun onModifierClick(succursale: Succursale)
        fun onSupprimerClick(succursale: Succursale)
        fun onFavorisClick(succursale: Succursale)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuccursaleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.succursale_item, parent, false)
        return SuccursaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuccursaleViewHolder, position: Int) {
        val succursale = succursaleList[position]

        holder.villeTextView.text =
            holder.itemView.context.getString(R.string.format_ville, succursale.Ville)
        holder.budgetTextView.text =
            holder.itemView.context.getString(R.string.format_budget, succursale.Budget)

        // Gestion des clics sur les boutons dans chaque élément de la liste
        holder.itemView.setOnClickListener {
            holder.buttonContainer.visibility =
                if (holder.buttonContainer.visibility == View.GONE) View.VISIBLE else View.GONE
            listener.onItemClick(succursale)
        }

        // Définir la visibilité des boutons "Modifier" et "Supprimer"
        if (isVisible) {
            holder.modifierButton.visibility = View.VISIBLE
            holder.supprimerButton.visibility = View.VISIBLE
        } else {
            holder.modifierButton.visibility = View.GONE
            holder.supprimerButton.visibility = View.GONE
        }

        holder.modifierButton.setOnClickListener {
            listener.onModifierClick(succursale)
            holder.buttonContainer.visibility = View.GONE
        }

        holder.supprimerButton.setOnClickListener {
            listener.onSupprimerClick(succursale)
            holder.buttonContainer.visibility = View.GONE
        }

        holder.favorisButton.setOnClickListener {
            val updatedSuccursale = succursale.copy(isFavoris = !succursale.isFavoris)
            viewModel.updateFavoris(updatedSuccursale)
            succursaleList[position] = updatedSuccursale
            notifyItemChanged(position)
        }

        // Changez la couleur de fond si la succursale est un favori
        if (succursale.isFavoris) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.couleurFavori))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }

        override fun getItemCount(): Int {
        return succursaleList.size
    }

    fun updateData(newSuccursales: List<Succursale>) {
        succursaleList.filter { it !in newSuccursales }.forEach { succursale ->
            val index = succursaleList.indexOf(succursale)
            succursaleList.removeAt(index)
            notifyItemRemoved(index)
        }

        newSuccursales.forEachIndexed { index, succursale ->
            val oldIndex = succursaleList.indexOf(succursale)
            if (oldIndex >= 0 && oldIndex != index) {
                notifyItemMoved(oldIndex, index)
            } else if (oldIndex < 0) {
                succursaleList.add(index, succursale)
                notifyItemInserted(index)
            }
        }

        succursaleList.clear()
        succursaleList.addAll(newSuccursales)
        notifyDataSetChanged()
    }

    inner class SuccursaleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val villeTextView: TextView = itemView.findViewById(R.id.villeTextView)
        val budgetTextView: TextView = itemView.findViewById(R.id.budgetTextView)
        val buttonContainer: LinearLayout = itemView.findViewById(R.id.buttonContainer)
        val modifierButton: Button = itemView.findViewById(R.id.modifierButton)
        val supprimerButton: Button = itemView.findViewById(R.id.supprimerButton)
        val favorisButton: Button = itemView.findViewById(R.id.favorisButton)
    }
}
