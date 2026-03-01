package com.chi.attendance.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chi.attendance.R
import com.chi.attendance.models.AttendanceSummary

class ReportSummaryAdapter(
    private var summaries: List<AttendanceSummary>
) : RecyclerView.Adapter<ReportSummaryAdapter.SummaryViewHolder>() {

    inner class SummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvUnionCouncil: TextView = itemView.findViewById(R.id.tvUnionCouncil)
        val tvPresent: TextView = itemView.findViewById(R.id.tvPresent)
        val tvAbsent: TextView = itemView.findViewById(R.id.tvAbsent)
        val tvLeave: TextView = itemView.findViewById(R.id.tvLeave)
        val tvLate: TextView = itemView.findViewById(R.id.tvLate)
        val tvPercentage: TextView = itemView.findViewById(R.id.tvPercentage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_summary, parent, false)
        return SummaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        val summary = summaries[position]
        
        holder.tvName.text = summary.employeeName
        holder.tvUnionCouncil.text = "UC: ${summary.unionCouncil}"
        holder.tvPresent.text = summary.presentDays.toString()
        holder.tvAbsent.text = summary.absentDays.toString()
        holder.tvLeave.text = summary.leaveDays.toString()
        holder.tvLate.text = summary.lateDays.toString()
        holder.tvPercentage.text = "${String.format("%.1f", summary.attendancePercentage)}%"
        
        // Color code percentage
        val context = holder.itemView.context
        when {
            summary.attendancePercentage >= 90 -> holder.tvPercentage.setTextColor(context.getColor(R.color.green))
            summary.attendancePercentage >= 75 -> holder.tvPercentage.setTextColor(context.getColor(R.color.orange))
            else -> holder.tvPercentage.setTextColor(context.getColor(R.color.red))
        }
    }

    override fun getItemCount() = summaries.size

    fun updateList(newList: List<AttendanceSummary>) {
        summaries = newList
        notifyDataSetChanged()
    }
}
