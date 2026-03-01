package com.chi.attendance.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chi.attendance.R
import com.chi.attendance.database.DatabaseHelper
import com.chi.attendance.models.Employee
import java.util.Calendar

class AddEditEmployeeActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etCNIC: EditText
    private lateinit var etContactNumber: EditText
    private lateinit var etDesignation: EditText
    private lateinit var spinnerUnionCouncil: Spinner
    private lateinit var etDateOfJoining: EditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var btnSave: Button
    private lateinit var dbHelper: DatabaseHelper

    private var employeeId: Long = 0
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_employee)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)

        initViews()
        setupSpinners()
        setupDatePicker()

        employeeId = intent.getLongExtra("employee_id", 0)
        if (employeeId != 0L) {
            isEditMode = true
            supportActionBar?.title = "Edit Employee"
            loadEmployeeData()
        } else {
            supportActionBar?.title = "Add Employee"
        }
    }

    private fun initViews() {
        etFullName = findViewById(R.id.etFullName)
        etCNIC = findViewById(R.id.etCNIC)
        etContactNumber = findViewById(R.id.etContactNumber)
        etDesignation = findViewById(R.id.etDesignation)
        spinnerUnionCouncil = findViewById(R.id.spinnerUnionCouncil)
        etDateOfJoining = findViewById(R.id.etDateOfJoining)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        btnSave = findViewById(R.id.btnSave)

        etDesignation.setText("Community Health Inspector")

        btnSave.setOnClickListener {
            saveEmployee()
        }
    }

    private fun setupSpinners() {
        // Union Council Spinner
        val unionCouncils = dbHelper.getAllUnionCouncils()
        val ucNames = unionCouncils.map { it.name }.toMutableList()
        if (ucNames.isEmpty()) {
            ucNames.add("No Union Councils")
        }
        val ucAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ucNames)
        ucAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnionCouncil.adapter = ucAdapter

        // Status Spinner
        val statuses = arrayOf(Employee.STATUS_ACTIVE, Employee.STATUS_INACTIVE)
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = statusAdapter
    }

    private fun setupDatePicker() {
        etDateOfJoining.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                etDateOfJoining.setText(date)
            }, year, month, day).show()
        }
    }

    private fun loadEmployeeData() {
        val employee = dbHelper.getEmployeeById(employeeId)
        if (employee != null) {
            etFullName.setText(employee.fullName)
            etCNIC.setText(employee.cnic)
            etContactNumber.setText(employee.contactNumber)
            etDesignation.setText(employee.designation)
            etDateOfJoining.setText(employee.dateOfJoining)

            // Set Union Council
            val ucAdapter = spinnerUnionCouncil.adapter as ArrayAdapter<String>
            val ucPosition = ucAdapter.getPosition(employee.unionCouncil)
            if (ucPosition >= 0) {
                spinnerUnionCouncil.setSelection(ucPosition)
            }

            // Set Status
            val statusAdapter = spinnerStatus.adapter as ArrayAdapter<String>
            val statusPosition = statusAdapter.getPosition(employee.status)
            if (statusPosition >= 0) {
                spinnerStatus.setSelection(statusPosition)
            }
        }
    }

    private fun saveEmployee() {
        val fullName = etFullName.text.toString().trim()
        val cnic = etCNIC.text.toString().trim()
        val contactNumber = etContactNumber.text.toString().trim()
        val designation = etDesignation.text.toString().trim()
        val unionCouncil = spinnerUnionCouncil.selectedItem?.toString() ?: ""
        val dateOfJoining = etDateOfJoining.text.toString().trim()
        val status = spinnerStatus.selectedItem?.toString() ?: Employee.STATUS_ACTIVE

        if (fullName.isEmpty()) {
            etFullName.error = "Please enter full name"
            return
        }

        if (unionCouncil.isEmpty() || unionCouncil == "No Union Councils") {
            Toast.makeText(this, "Please add a Union Council first", Toast.LENGTH_SHORT).show()
            return
        }

        if (dateOfJoining.isEmpty()) {
            etDateOfJoining.error = "Please select date of joining"
            return
        }

        val employee = Employee(
            id = employeeId,
            fullName = fullName,
            cnic = cnic,
            contactNumber = contactNumber,
            designation = designation,
            unionCouncil = unionCouncil,
            dateOfJoining = dateOfJoining,
            status = status
        )

        val result = if (isEditMode) {
            dbHelper.updateEmployee(employee)
        } else {
            dbHelper.addEmployee(employee) != -1L
        }

        if (result) {
            Toast.makeText(this, if (isEditMode) "Employee updated" else "Employee added", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to save employee", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
