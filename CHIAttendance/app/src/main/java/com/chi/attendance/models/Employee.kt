package com.chi.attendance.models

data class Employee(
    val id: Long = 0,
    val fullName: String,
    val cnic: String = "",
    val contactNumber: String = "",
    val designation: String = "Community Health Inspector",
    val unionCouncil: String,
    val dateOfJoining: String,
    val status: String = "Active"
) {
    companion object {
        const val STATUS_ACTIVE = "Active"
        const val STATUS_INACTIVE = "Inactive"
    }
}
