package com.chi.attendance.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chi.attendance.R
import com.chi.attendance.models.Attendance
import com.chi.attendance.models.Employee

class AttendanceAdapter(
    private var employees: List<Employee>,
    private var attendanceMap: Map<Long, Attendance>,
    private val onAttendanceChanged: (Employee, String) -> Unit
) : RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    inner class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvUnionCouncil: TextView = itemView.findViewById(R.id.tvUnionCouncil)
        val radioGroup: RadioGroup = itemView.findViewById(R.id.radioGroup)
        val rbPresent: RadioButton = itemView.findViewById(R.id.rbPresent)
        val rbAbsent: RadioButton = itemView.findViewById(R.id.rbAbsent)
        val rbLeave: RadioButton = itemView.findViewById(R.id.rbLeave)
        val rbLate: RadioButton = itemView.findViewById(R.id.rbLate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val employee = employees[position]
        
        holder.tvName.text = employee.fullName
        holder.tvUnionCouncil.text = "UC: ${employee.unionCouncil}"
        
        // Set existing attendance status
        val attendance = attendanceMap[employee.id]
        when (attendance?.status) {
            Attendance.STATUS_PRESENT -> holder.rbPresent.isChecked = true
            Attendance.STATUS_ABSENT -> holder.rbAbsent.isChecked = true
            Attendance.STATUS_LEAVE -> holder.rbLeave.isChecked = true
            Attendance.STATUS_LATE -> holder.rbLate.isChecked = true
            else -> holder.radioGroup.clearCheck()
        }
        
        holder.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val status = when (checkedId) {
                R.id.rbPresent -> Attendance.STATUS_PRESENT
                R.id.rbAbsent -> Attendance.STATUS_ABSENT
                R.id.rbLeave -> Attendance.STATUS_LEAVE
                R.id.rbLate -> Attendance.STATUS_LATE
                else -> return@setOnCheckedChangeListener
            }
            onAttendanceChanged(employee, status)
        }
    }

    override fun getItemCount() = employees.size

    fun updateData(newEmployees: List<Employee>, newAttendanceMap: Map<Long, Attendance>) {
        employees = newEmployees
        attendanceMap = newAttendanceMap
        notifyDataSetChanged()
    }
}
