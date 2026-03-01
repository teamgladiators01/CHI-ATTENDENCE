package com.chi.attendance.models

data class Attendance(
    val id: Long = 0,
    val employeeId: Long,
    val employeeName: String,
    val unionCouncil: String,
    val date: String,
    val status: String,
    val remarks: String = ""
) {
    companion object {
        const val STATUS_PRESENT = "Present"
        const val STATUS_ABSENT = "Absent"
        const val STATUS_LEAVE = "Leave"
        const val STATUS_LATE = "Late"
    }
}
