package com.chi.attendance.database

object DatabaseContract {
    const val DATABASE_NAME = "chi_attendance.db"
    const val DATABASE_VERSION = 1

    object EmployeeEntry {
        const val TABLE_NAME = "employees"
        const val COLUMN_ID = "id"
        const val COLUMN_FULL_NAME = "full_name"
        const val COLUMN_CNIC = "cnic"
        const val COLUMN_CONTACT_NUMBER = "contact_number"
        const val COLUMN_DESIGNATION = "designation"
        const val COLUMN_UNION_COUNCIL = "union_council"
        const val COLUMN_DATE_OF_JOINING = "date_of_joining"
        const val COLUMN_STATUS = "status"

        const val CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FULL_NAME TEXT NOT NULL,
                $COLUMN_CNIC TEXT,
                $COLUMN_CONTACT_NUMBER TEXT,
                $COLUMN_DESIGNATION TEXT DEFAULT 'Community Health Inspector',
                $COLUMN_UNION_COUNCIL TEXT NOT NULL,
                $COLUMN_DATE_OF_JOINING TEXT,
                $COLUMN_STATUS TEXT DEFAULT 'Active'
            )
        """
    }

    object AttendanceEntry {
        const val TABLE_NAME = "attendance"
        const val COLUMN_ID = "id"
        const val COLUMN_EMPLOYEE_ID = "employee_id"
        const val COLUMN_EMPLOYEE_NAME = "employee_name"
        const val COLUMN_UNION_COUNCIL = "union_council"
        const val COLUMN_DATE = "date"
        const val COLUMN_STATUS = "status"
        const val COLUMN_REMARKS = "remarks"

        const val CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EMPLOYEE_ID INTEGER NOT NULL,
                $COLUMN_EMPLOYEE_NAME TEXT NOT NULL,
                $COLUMN_UNION_COUNCIL TEXT NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_STATUS TEXT NOT NULL,
                $COLUMN_REMARKS TEXT,
                FOREIGN KEY ($COLUMN_EMPLOYEE_ID) REFERENCES ${EmployeeEntry.TABLE_NAME}(${EmployeeEntry.COLUMN_ID})
            )
        """
    }

    object UnionCouncilEntry {
        const val TABLE_NAME = "union_councils"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_CODE = "code"

        const val CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL UNIQUE,
                $COLUMN_CODE TEXT
            )
        """
    }

    object AdminEntry {
        const val TABLE_NAME = "admin"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"

        const val CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """
    }
}
