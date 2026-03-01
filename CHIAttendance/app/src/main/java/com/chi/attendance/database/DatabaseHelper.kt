package com.chi.attendance.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.chi.attendance.models.*
import java.security.MessageDigest

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DatabaseContract.DATABASE_NAME,
    null,
    DatabaseContract.DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DatabaseContract.EmployeeEntry.CREATE_TABLE)
        db.execSQL(DatabaseContract.AttendanceEntry.CREATE_TABLE)
        db.execSQL(DatabaseContract.UnionCouncilEntry.CREATE_TABLE)
        db.execSQL(DatabaseContract.AdminEntry.CREATE_TABLE)
        
        // Insert default admin
        val values = ContentValues().apply {
            put(DatabaseContract.AdminEntry.COLUMN_USERNAME, "admin")
            put(DatabaseContract.AdminEntry.COLUMN_PASSWORD, hashPassword("admin123"))
        }
        db.insert(DatabaseContract.AdminEntry.TABLE_NAME, null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.AttendanceEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.EmployeeEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.UnionCouncilEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.AdminEntry.TABLE_NAME}")
        onCreate(db)
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // ==================== ADMIN OPERATIONS ====================

    fun validateAdmin(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.AdminEntry.TABLE_NAME,
            arrayOf(DatabaseContract.AdminEntry.COLUMN_ID),
            "${DatabaseContract.AdminEntry.COLUMN_USERNAME} = ? AND ${DatabaseContract.AdminEntry.COLUMN_PASSWORD} = ?",
            arrayOf(username, hashPassword(password)),
            null, null, null
        )
        val isValid = cursor.count > 0
        cursor.close()
        db.close()
        return isValid
    }

    fun changePassword(username: String, newPassword: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DatabaseContract.AdminEntry.COLUMN_PASSWORD, hashPassword(newPassword))
        }
        val result = db.update(
            DatabaseContract.AdminEntry.TABLE_NAME,
            values,
            "${DatabaseContract.AdminEntry.COLUMN_USERNAME} = ?",
            arrayOf(username)
        )
        db.close()
        return result > 0
    }

    // ==================== EMPLOYEE OPERATIONS ====================

    fun addEmployee(employee: Employee): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DatabaseContract.EmployeeEntry.COLUMN_FULL_NAME, employee.fullName)
            put(DatabaseContract.EmployeeEntry.COLUMN_CNIC, employee.cnic)
            put(DatabaseContract.EmployeeEntry.COLUMN_CONTACT_NUMBER, employee.contactNumber)
            put(DatabaseContract.EmployeeEntry.COLUMN_DESIGNATION, employee.designation)
            put(DatabaseContract.EmployeeEntry.COLUMN_UNION_COUNCIL, employee.unionCouncil)
            put(DatabaseContract.EmployeeEntry.COLUMN_DATE_OF_JOINING, employee.dateOfJoining)
            put(DatabaseContract.EmployeeEntry.COLUMN_STATUS, employee.status)
        }
        val id = db.insert(DatabaseContract.EmployeeEntry.TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun updateEmployee(employee: Employee): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DatabaseContract.EmployeeEntry.COLUMN_FULL_NAME, employee.fullName)
            put(DatabaseContract.EmployeeEntry.COLUMN_CNIC, employee.cnic)
            put(DatabaseContract.EmployeeEntry.COLUMN_CONTACT_NUMBER, employee.contactNumber)
            put(DatabaseContract.EmployeeEntry.COLUMN_DESIGNATION, employee.designation)
            put(DatabaseContract.EmployeeEntry.COLUMN_UNION_COUNCIL, employee.unionCouncil)
            put(DatabaseContract.EmployeeEntry.COLUMN_DATE_OF_JOINING, employee.dateOfJoining)
            put(DatabaseContract.EmployeeEntry.COLUMN_STATUS, employee.status)
        }
        val result = db.update(
            DatabaseContract.EmployeeEntry.TABLE_NAME,
            values,
            "${DatabaseContract.EmployeeEntry.COLUMN_ID} = ?",
            arrayOf(employee.id.toString())
        )
        db.close()
        return result > 0
    }

    fun deleteEmployee(id: Long): Boolean {
        val db = writableDatabase
        // Delete related attendance records first
        db.delete(
            DatabaseContract.AttendanceEntry.TABLE_NAME,
            "${DatabaseContract.AttendanceEntry.COLUMN_EMPLOYEE_ID} = ?",
            arrayOf(id.toString())
        )
        val result = db.delete(
            DatabaseContract.EmployeeEntry.TABLE_NAME,
            "${DatabaseContract.EmployeeEntry.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        db.close()
        return result > 0
    }

    fun getEmployeeById(id: Long): Employee? {
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.EmployeeEntry.TABLE_NAME,
            null,
            "${DatabaseContract.EmployeeEntry.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        var employee: Employee? = null
        if (cursor.moveToFirst()) {
            employee = cursorToEmployee(cursor)
        }
        cursor.close()
        db.close()
        return employee
    }

    fun getAllEmployees(): List<Employee> {
        val employees = mutableListOf<Employee>()
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.EmployeeEntry.TABLE_NAME,
            null,
            null, null, null, null,
            "${DatabaseContract.EmployeeEntry.COLUMN_FULL_NAME} ASC"
        )
        while (cursor.moveToNext()) {
            employees.add(cursorToEmployee(cursor))
        }
        cursor.close()
        db.close()
        return employees
    }

    fun getActiveEmployees(): List<Employee> {
        val employees = mutableListOf<Employee>()
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.EmployeeEntry.TABLE_NAME,
            null,
            "${DatabaseContract.EmployeeEntry.COLUMN_STATUS} = ?",
            arrayOf(Employee.STATUS_ACTIVE),
            null, null,
            "${DatabaseContract.EmployeeEntry.COLUMN_FULL_NAME} ASC"
        )
        while (cursor.moveToNext()) {
            employees.add(cursorToEmployee(cursor))
        }
        cursor.close()
        db.close()
        return employees
    }

    fun getEmployeesByUnionCouncil(unionCouncil: String): List<Employee> {
        val employees = mutableListOf<Employee>()
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.EmployeeEntry.TABLE_NAME,
            null,
            "${DatabaseContract.EmployeeEntry.COLUMN_UNION_COUNCIL} = ? AND ${DatabaseContract.EmployeeEntry.COLUMN_STATUS} = ?",
            arrayOf(unionCouncil, Employee.STATUS_ACTIVE),
            null, null,
            "${DatabaseContract.EmployeeEntry.COLUMN_FULL_NAME} ASC"
        )
        while (cursor.moveToNext()) {
            employees.add(cursorToEmployee(cursor))
        }
        cursor.close()
        db.close()
        return employees
    }

    fun searchEmployees(query: String): List<Employee> {
        val employees = mutableListOf<Employee>()
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.EmployeeEntry.TABLE_NAME,
            null,
            "${DatabaseContract.EmployeeEntry.COLUMN_FULL_NAME} LIKE ? OR ${DatabaseContract.EmployeeEntry.COLUMN_CNIC} LIKE ?",
            arrayOf("%$query%", "%$query%"),
            null, null,
            "${DatabaseContract.EmployeeEntry.COLUMN_FULL_NAME} ASC"
        )
        while (cursor.moveToNext()) {
            employees.add(cursorToEmployee(cursor))
        }
        cursor.close()
        db.close()
        return employees
    }

    private fun cursorToEmployee(cursor: android.database.Cursor): Employee {
        return Employee(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.EmployeeEntry.COLUMN_ID)),
            fullName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.EmployeeEntry.COLUMN_FULL_NAME)),
            cnic = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.EmployeeEntry.COLUMN_CNIC)) ?: "",
            contactNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.EmployeeEntry.COLUMN_CONTACT_NUMBER)) ?: "",
            designation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.EmployeeEntry.COLUMN_DESIGNATION)) ?: "Community Health Inspector",
            unionCouncil = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.EmployeeEntry.COLUMN_UNION_COUNCIL)),
            dateOfJoining = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.EmployeeEntry.COLUMN_DATE_OF_JOINING)) ?: "",
            status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.EmployeeEntry.COLUMN_STATUS)) ?: "Active"
        )
    }

    // ==================== ATTENDANCE OPERATIONS ====================

    fun addOrUpdateAttendance(attendance: Attendance): Long {
        val db = writableDatabase
        
        // Check if attendance already exists for this employee and date
        val cursor = db.query(
            DatabaseContract.AttendanceEntry.TABLE_NAME,
            arrayOf(DatabaseContract.AttendanceEntry.COLUMN_ID),
            "${DatabaseContract.AttendanceEntry.COLUMN_EMPLOYEE_ID} = ? AND ${DatabaseContract.AttendanceEntry.COLUMN_DATE} = ?",
            arrayOf(attendance.employeeId.toString(), attendance.date),
            null, null, null
        )
        
        val values = ContentValues().apply {
            put(DatabaseContract.AttendanceEntry.COLUMN_EMPLOYEE_ID, attendance.employeeId)
            put(DatabaseContract.AttendanceEntry.COLUMN_EMPLOYEE_NAME, attendance.employeeName)
            put(DatabaseContract.AttendanceEntry.COLUMN_UNION_COUNCIL, attendance.unionCouncil)
            put(DatabaseContract.AttendanceEntry.COLUMN_DATE, attendance.date)
            put(DatabaseContract.AttendanceEntry.COLUMN_STATUS, attendance.status)
            put(DatabaseContract.AttendanceEntry.COLUMN_REMARKS, attendance.remarks)
        }
        
        val id: Long = if (cursor.count > 0 && cursor.moveToFirst()) {
            val existingId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.AttendanceEntry.COLUMN_ID))
            db.update(
                DatabaseContract.AttendanceEntry.TABLE_NAME,
                values,
                "${DatabaseContract.AttendanceEntry.COLUMN_ID} = ?",
                arrayOf(existingId.toString())
            )
            existingId
        } else {
            db.insert(DatabaseContract.AttendanceEntry.TABLE_NAME, null, values)
        }
        
        cursor.close()
        db.close()
        return id
    }

    fun getAttendanceByDate(date: String): List<Attendance> {
        val attendanceList = mutableListOf<Attendance>()
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.AttendanceEntry.TABLE_NAME,
            null,
            "${DatabaseContract.AttendanceEntry.COLUMN_DATE} = ?",
            arrayOf(date),
            null, null,
            "${DatabaseContract.AttendanceEntry.COLUMN_EMPLOYEE_NAME} ASC"
        )
        while (cursor.moveToNext()) {
            attendanceList.add(cursorToAttendance(cursor))
        }
        cursor.close()
        db.close()
        return attendanceList
    }

    fun getAttendanceByEmployeeAndDate(employeeId: Long, date: String): Attendance? {
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.AttendanceEntry.TABLE_NAME,
            null,
            "${DatabaseContract.AttendanceEntry.COLUMN_EMPLOYEE_ID} = ? AND ${DatabaseContract.AttendanceEntry.COLUMN_DATE} = ?",
            arrayOf(employeeId.toString(), date),
            null, null, null
        )
        var attendance: Attendance? = null
        if (cursor.moveToFirst()) {
            attendance = cursorToAttendance(cursor)
        }
        cursor.close()
        db.close()
        return attendance
    }

    fun getAttendanceByEmployee(employeeId: Long): List<Attendance> {
        val attendanceList = mutableListOf<Attendance>()
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.AttendanceEntry.TABLE_NAME,
            null,
            "${DatabaseContract.AttendanceEntry.COLUMN_EMPLOYEE_ID} = ?",
            arrayOf(employeeId.toString()),
            null, null,
            "${DatabaseContract.AttendanceEntry.COLUMN_DATE} DESC"
        )
        while (cursor.moveToNext()) {
            attendanceList.add(cursorToAttendance(cursor))
        }
        cursor.close()
        db.close()
        return attendanceList
    }

    fun getAttendanceByDateRange(startDate: String, endDate: String): List<Attendance> {
        val attendanceList = mutableListOf<Attendance>()
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.AttendanceEntry.TABLE_NAME,
            null,
            "${DatabaseContract.AttendanceEntry.COLUMN_DATE} BETWEEN ? AND ?",
            arrayOf(startDate, endDate),
            null, null,
            "${DatabaseContract.AttendanceEntry.COLUMN_DATE} DESC, ${DatabaseContract.AttendanceEntry.COLUMN_EMPLOYEE_NAME} ASC"
        )
        while (cursor.moveToNext()) {
            attendanceList.add(cursorToAttendance(cursor))
        }
        cursor.close()
        db.close()
        return attendanceList
    }

    fun getAttendanceByUnionCouncilAndDate(unionCouncil: String, date: String): List<Attendance> {
        val attendanceList = mutableListOf<Attendance>()
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.AttendanceEntry.TABLE_NAME,
            null,
            "${DatabaseContract.AttendanceEntry.COLUMN_UNION_COUNCIL} = ? AND ${DatabaseContract.AttendanceEntry.COLUMN_DATE} = ?",
            arrayOf(unionCouncil, date),
            null, null,
            "${DatabaseContract.AttendanceEntry.COLUMN_EMPLOYEE_NAME} ASC"
        )
        while (cursor.moveToNext()) {
            attendanceList.add(cursorToAttendance(cursor))
        }
        cursor.close()
        db.close()
        return attendanceList
    }

    fun getAttendanceByUnionCouncilAndDateRange(unionCouncil: String, startDate: String, endDate: String): List<Attendance> {
        val attendanceList = mutableListOf<Attendance>()
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.AttendanceEntry.TABLE_NAME,
            null,
            "${DatabaseContract.AttendanceEntry.COLUMN_UNION_COUNCIL} = ? AND ${DatabaseContract.AttendanceEntry.COLUMN_DATE} BETWEEN ? AND ?",
            arrayOf(unionCouncil, startDate, endDate),
            null, null,
            "${DatabaseContract.AttendanceEntry.COLUMN_DATE} DESC, ${DatabaseContract.AttendanceEntry.COLUMN_EMPLOYEE_NAME} ASC"
        )
        while (cursor.moveToNext()) {
            attendanceList.add(cursorToAttendance(cursor))
        }
        cursor.close()
        db.close()
        return attendanceList
    }

    fun getAttendanceCountByDate(date: String, status: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${DatabaseContract.AttendanceEntry.TABLE_NAME} WHERE ${DatabaseContract.AttendanceEntry.COLUMN_DATE} = ? AND ${DatabaseContract.AttendanceEntry.COLUMN_STATUS} = ?",
            arrayOf(date, status)
        )
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    private fun cursorToAttendance(cursor: android.database.Cursor): Attendance {
        return Attendance(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.AttendanceEntry.COLUMN_ID)),
            employeeId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.AttendanceEntry.COLUMN_EMPLOYEE_ID)),
            employeeName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AttendanceEntry.COLUMN_EMPLOYEE_NAME)),
            unionCouncil = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AttendanceEntry.COLUMN_UNION_COUNCIL)),
            date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AttendanceEntry.COLUMN_DATE)),
            status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AttendanceEntry.COLUMN_STATUS)),
            remarks = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AttendanceEntry.COLUMN_REMARKS)) ?: ""
        )
    }

    // ==================== UNION COUNCIL OPERATIONS ====================

    fun addUnionCouncil(unionCouncil: UnionCouncil): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DatabaseContract.UnionCouncilEntry.COLUMN_NAME, unionCouncil.name)
            put(DatabaseContract.UnionCouncilEntry.COLUMN_CODE, unionCouncil.code)
        }
        val id = db.insert(DatabaseContract.UnionCouncilEntry.TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun updateUnionCouncil(unionCouncil: UnionCouncil): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DatabaseContract.UnionCouncilEntry.COLUMN_NAME, unionCouncil.name)
            put(DatabaseContract.UnionCouncilEntry.COLUMN_CODE, unionCouncil.code)
        }
        val result = db.update(
            DatabaseContract.UnionCouncilEntry.TABLE_NAME,
            values,
            "${DatabaseContract.UnionCouncilEntry.COLUMN_ID} = ?",
            arrayOf(unionCouncil.id.toString())
        )
        db.close()
        return result > 0
    }

    fun deleteUnionCouncil(id: Long): Boolean {
        val db = writableDatabase
        val result = db.delete(
            DatabaseContract.UnionCouncilEntry.TABLE_NAME,
            "${DatabaseContract.UnionCouncilEntry.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        db.close()
        return result > 0
    }

    fun getAllUnionCouncils(): List<UnionCouncil> {
        val unionCouncils = mutableListOf<UnionCouncil>()
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.UnionCouncilEntry.TABLE_NAME,
            null,
            null, null, null, null,
            "${DatabaseContract.UnionCouncilEntry.COLUMN_NAME} ASC"
        )
        while (cursor.moveToNext()) {
            unionCouncils.add(cursorToUnionCouncil(cursor))
        }
        cursor.close()
        db.close()
        return unionCouncils
    }

    fun getUnionCouncilByName(name: String): UnionCouncil? {
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.UnionCouncilEntry.TABLE_NAME,
            null,
            "${DatabaseContract.UnionCouncilEntry.COLUMN_NAME} = ?",
            arrayOf(name),
            null, null, null
        )
        var unionCouncil: UnionCouncil? = null
        if (cursor.moveToFirst()) {
            unionCouncil = cursorToUnionCouncil(cursor)
        }
        cursor.close()
        db.close()
        return unionCouncil
    }

    private fun cursorToUnionCouncil(cursor: android.database.Cursor): UnionCouncil {
        return UnionCouncil(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.UnionCouncilEntry.COLUMN_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UnionCouncilEntry.COLUMN_NAME)),
            code = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UnionCouncilEntry.COLUMN_CODE)) ?: ""
        )
    }

    // ==================== STATISTICS ====================

    fun getTodayStats(date: String): Map<String, Int> {
        val db = readableDatabase
        val stats = mutableMapOf<String, Int>()
        
        // Total employees
        val totalCursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${DatabaseContract.EmployeeEntry.TABLE_NAME} WHERE ${DatabaseContract.EmployeeEntry.COLUMN_STATUS} = ?",
            arrayOf(Employee.STATUS_ACTIVE)
        )
        if (totalCursor.moveToFirst()) {
            stats["total"] = totalCursor.getInt(0)
        }
        totalCursor.close()
        
        // Present count
        val presentCursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${DatabaseContract.AttendanceEntry.TABLE_NAME} WHERE ${DatabaseContract.AttendanceEntry.COLUMN_DATE} = ? AND ${DatabaseContract.AttendanceEntry.COLUMN_STATUS} = ?",
            arrayOf(date, Attendance.STATUS_PRESENT)
        )
        if (presentCursor.moveToFirst()) {
            stats["present"] = presentCursor.getInt(0)
        }
        presentCursor.close()
        
        // Absent count
        val absentCursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${DatabaseContract.AttendanceEntry.TABLE_NAME} WHERE ${DatabaseContract.AttendanceEntry.COLUMN_DATE} = ? AND ${DatabaseContract.AttendanceEntry.COLUMN_STATUS} = ?",
            arrayOf(date, Attendance.STATUS_ABSENT)
        )
        if (absentCursor.moveToFirst()) {
            stats["absent"] = absentCursor.getInt(0)
        }
        absentCursor.close()
        
        // Leave count
        val leaveCursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${DatabaseContract.AttendanceEntry.TABLE_NAME} WHERE ${DatabaseContract.AttendanceEntry.COLUMN_DATE} = ? AND ${DatabaseContract.AttendanceEntry.COLUMN_STATUS} = ?",
            arrayOf(date, Attendance.STATUS_LEAVE)
        )
        if (leaveCursor.moveToFirst()) {
            stats["leave"] = leaveCursor.getInt(0)
        }
        leaveCursor.close()
        
        // Late count
        val lateCursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${DatabaseContract.AttendanceEntry.TABLE_NAME} WHERE ${DatabaseContract.AttendanceEntry.COLUMN_DATE} = ? AND ${DatabaseContract.AttendanceEntry.COLUMN_STATUS} = ?",
            arrayOf(date, Attendance.STATUS_LATE)
        )
        if (lateCursor.moveToFirst()) {
            stats["late"] = lateCursor.getInt(0)
        }
        lateCursor.close()
        
        db.close()
        return stats
    }

    // ==================== RESET DATA ====================

    fun resetAllData(): Boolean {
        val db = writableDatabase
        db.delete(DatabaseContract.AttendanceEntry.TABLE_NAME, null, null)
        db.delete(DatabaseContract.EmployeeEntry.TABLE_NAME, null, null)
        db.delete(DatabaseContract.UnionCouncilEntry.TABLE_NAME, null, null)
        db.close()
        return true
    }

    fun getDatabasePath(context: Context): String {
        return context.getDatabasePath(DatabaseContract.DATABASE_NAME).absolutePath
    }
}
