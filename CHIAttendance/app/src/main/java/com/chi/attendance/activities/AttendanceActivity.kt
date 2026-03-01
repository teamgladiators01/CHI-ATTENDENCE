package com.chi.attendance.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chi.attendance.R
import com.chi.attendance.adapters.AttendanceAdapter
import com.chi.attendance.database.DatabaseHelper
import com.chi.attendance.models.Attendance
import com.chi.attendance.models.Employee
import com.chi.attendance.utils.DateUtils
import java.util.Calendar

class AttendanceActivity : AppCompatActivity() {

    private lateinit var tvSelectedDate: TextView
    private lateinit var etDate: EditText
    private lateinit var spinnerFilter: Spinner
    private lateinit var spinnerUnionCouncil: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnSaveAttendance: Button
    private lateinit var adapter: AttendanceAdapter
    private lateinit var dbHelper: DatabaseHelper

    private var selectedDate = DateUtils.getCurrentDate()
    private var employees = listOf<Employee>()
    private var attendanceMap = mutableMapOf<Long, Attendance>()
    private var selectedFilter = "All Employees"
    private var selectedUnionCouncil = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mark Attendance"

        dbHelper = DatabaseHelper(this)

        initViews()
        setupSpinners()
        setupRecyclerView()
        loadData()
    }

    private fun initViews() {
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        etDate = findViewById(R.id.etDate)
        spinnerFilter = findViewById(R.id.spinnerFilter)
        spinnerUnionCouncil = findViewById(R.id.spinnerUnionCouncil)
        recyclerView = findViewById(R.id.recyclerView)
        btnSaveAttendance = findViewById(R.id.btnSaveAttendance)

        etDate.setText(DateUtils.formatDateForDisplay(selectedDate))
        tvSelectedDate.text = "Attendance for: ${DateUtils.formatDateForDisplayFull(selectedDate)}"

        etDate.setOnClickListener {
            showDatePicker()
        }

        btnSaveAttendance.setOnClickListener {
            saveAttendance()
        }
    }

    private fun setupSpinners() {
        // Filter Spinner
        val filters = arrayOf("All Employees", "By Union Council")
        val filterAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filters)
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = filterAdapter

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedFilter = filters[position]
                spinnerUnionCouncil.visibility = if (selectedFilter == "By Union Council") View.VISIBLE else View.GONE
                loadEmployees()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Union Council Spinner
        val unionCouncils = mutableListOf("All")
        unionCouncils.addAll(dbHelper.getAllUnionCouncils().map { it.name })
        
        val ucAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, unionCouncils)
        ucAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnionCouncil.adapter = ucAdapter
        spinnerUnionCouncil.visibility = View.GONE

        spinnerUnionCouncil.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedUnionCouncil = unionCouncils[position]
                loadEmployees()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupRecyclerView() {
        adapter = AttendanceAdapter(emptyList(), emptyMap()) { employee, status ->
            val attendance = Attendance(
                employeeId = employee.id,
                employeeName = employee.fullName,
                unionCouncil = employee.unionCouncil,
                date = selectedDate,
                status = status
            )
            attendanceMap[employee.id] = attendance
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val currentDate = sdf.parse(selectedDate)
        if (currentDate != null) {
            calendar.time = currentDate
        }
        
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            etDate.setText(DateUtils.formatDateForDisplay(selectedDate))
            tvSelectedDate.text = "Attendance for: ${DateUtils.formatDateForDisplayFull(selectedDate)}"
            loadData()
        }, year, month, day).show()
    }

    private fun loadData() {
        loadEmployees()
        loadExistingAttendance()
    }

    private fun loadEmployees() {
        employees = when {
            selectedFilter == "By Union Council" && selectedUnionCouncil != "All" -> {
                dbHelper.getEmployeesByUnionCouncil(selectedUnionCouncil)
            }
            else -> dbHelper.getActiveEmployees()
        }
        adapter.updateData(employees, attendanceMap)
    }

    private fun loadExistingAttendance() {
        attendanceMap.clear()
        val existingAttendance = dbHelper.getAttendanceByDate(selectedDate)
        existingAttendance.forEach { attendance ->
            attendanceMap[attendance.employeeId] = attendance
        }
        adapter.updateData(employees, attendanceMap)
    }

    private fun saveAttendance() {
        if (attendanceMap.isEmpty()) {
            Toast.makeText(this, "No attendance to save", Toast.LENGTH_SHORT).show()
            return
        }

        var savedCount = 0
        attendanceMap.values.forEach { attendance ->
            val id = dbHelper.addOrUpdateAttendance(attendance)
            if (id != -1L) {
                savedCount++
            }
        }

        Toast.makeText(this, "$savedCount attendance records saved", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
