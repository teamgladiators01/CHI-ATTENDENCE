package com.chi.attendance.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chi.attendance.R
import com.chi.attendance.models.UnionCouncil

class UnionCouncilAdapter(
    private var unionCouncils: List<UnionCouncil>,
    private val onEditClick: (UnionCouncil) -> Unit,
    private val onDeleteClick: (UnionCouncil) -> Unit
) : RecyclerView.Adapter<UnionCouncilAdapter.UnionCouncilViewHolder>() {

    inner class UnionCouncilViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvCode: TextView = itemView.findViewById(R.id.tvCode)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnionCouncilViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_union_council, parent, false)
        return UnionCouncilViewHolder(view)
    }

    override fun onBindViewHolder(holder: UnionCouncilViewHolder, position: Int) {
        val uc = unionCouncils[position]
        
        holder.tvName.text = uc.name
        holder.tvCode.text = if (uc.code.isNotEmpty()) "Code: ${uc.code}" else ""
        
        holder.btnEdit.setOnClickListener { onEditClick(uc) }
        holder.btnDelete.setOnClickListener { onDeleteClick(uc) }
    }

    override fun getItemCount() = unionCouncils.size

    fun updateList(newList: List<UnionCouncil>) {
        unionCouncils = newList
        notifyDataSetChanged()
    }
}
