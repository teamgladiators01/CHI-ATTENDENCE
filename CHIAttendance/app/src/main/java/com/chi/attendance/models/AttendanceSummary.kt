package com.chi.attendance.models

data class AttendanceSummary(
    val employeeId: Long,
    val employeeName: String,
    val unionCouncil: String,
    val totalWorkingDays: Int,
    val presentDays: Int,
    val absentDays: Int,
    val leaveDays: Int,
    val lateDays: Int,
    val attendancePercentage: Double
)
