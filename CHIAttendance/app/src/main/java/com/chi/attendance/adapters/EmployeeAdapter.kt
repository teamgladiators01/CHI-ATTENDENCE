package com.chi.attendance.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chi.attendance.R
import com.chi.attendance.models.Employee

class EmployeeAdapter(
    private var employees: List<Employee>,
    private val onItemClick: (Employee) -> Unit,
    private val onEditClick: (Employee) -> Unit,
    private val onDeleteClick: (Employee) -> Unit
) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDesignation: TextView = itemView.findViewById(R.id.tvDesignation)
        val tvUnionCouncil: TextView = itemView.findViewById(R.id.tvUnionCouncil)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_employee, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employees[position]
        
        holder.tvName.text = employee.fullName
        holder.tvDesignation.text = employee.designation
        holder.tvUnionCouncil.text = "UC: ${employee.unionCouncil}"
        holder.tvStatus.text = employee.status
        
        // Status color
        if (employee.status == Employee.STATUS_ACTIVE) {
            holder.tvStatus.setTextColor(holder.itemView.context.getColor(R.color.green))
        } else {
            holder.tvStatus.setTextColor(holder.itemView.context.getColor(R.color.red))
        }
        
        holder.itemView.setOnClickListener { onItemClick(employee) }
        holder.btnEdit.setOnClickListener { onEditClick(employee) }
        holder.btnDelete.setOnClickListener { onDeleteClick(employee) }
    }

    override fun getItemCount() = employees.size

    fun updateList(newList: List<Employee>) {
        employees = newList
        notifyDataSetChanged()
    }
}
