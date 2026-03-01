package com.chi.attendance.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chi.attendance.R
import com.chi.attendance.models.Attendance
import com.chi.attendance.utils.DateUtils

class AttendanceRecordAdapter(
    private var attendances: List<Attendance>
) : RecyclerView.Adapter<AttendanceRecordAdapter.AttendanceRecordViewHolder>() {

    inner class AttendanceRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvUnionCouncil: TextView = itemView.findViewById(R.id.tvUnionCouncil)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvRemarks: TextView = itemView.findViewById(R.id.tvRemarks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceRecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance_record, parent, false)
        return AttendanceRecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceRecordViewHolder, position: Int) {
        val attendance = attendances[position]
        
        holder.tvDate.text = DateUtils.formatDateForDisplay(attendance.date)
        holder.tvName.text = attendance.employeeName
        holder.tvUnionCouncil.text = "UC: ${attendance.unionCouncil}"
        holder.tvStatus.text = attendance.status
        holder.tvRemarks.text = attendance.remarks.ifEmpty { "-" }
        
        // Status color
        val context = holder.itemView.context
        when (attendance.status) {
            Attendance.STATUS_PRESENT -> holder.tvStatus.setTextColor(context.getColor(R.color.green))
            Attendance.STATUS_ABSENT -> holder.tvStatus.setTextColor(context.getColor(R.color.red))
            Attendance.STATUS_LEAVE -> holder.tvStatus.setTextColor(context.getColor(R.color.orange))
            Attendance.STATUS_LATE -> holder.tvStatus.setTextColor(context.getColor(R.color.blue))
        }
    }

    override fun getItemCount() = attendances.size

    fun updateList(newList: List<Attendance>) {
        attendances = newList
        notifyDataSetChanged()
    }
}
