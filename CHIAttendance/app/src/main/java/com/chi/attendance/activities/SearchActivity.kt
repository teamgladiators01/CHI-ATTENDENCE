package com.chi.attendance.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chi.attendance.R
import com.chi.attendance.adapters.AttendanceRecordAdapter
import com.chi.attendance.adapters.EmployeeAdapter
import com.chi.attendance.database.DatabaseHelper
import com.chi.attendance.models.Attendance
import com.chi.attendance.models.Employee
import com.chi.attendance.utils.DateUtils
import com.chi.attendance.utils.PdfGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class SearchActivity : AppCompatActivity() {

    private lateinit var radioGroupSearchType: RadioGroup
    private lateinit var etSearch: EditText
    private lateinit var spinnerUnionCouncil: Spinner
    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText
    private lateinit var btnSearch: Button
    private lateinit var btnViewHistory: Button
    private lateinit var btnExportPDF: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper

    private var searchType = "employee"
    private var selectedUnionCouncil = "All"
    private var startDate = ""
    private var endDate = ""
    private var selectedEmployee: Employee? = null
    private var searchResults = listOf<Attendance>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Advanced Search"

        dbHelper = DatabaseHelper(this)

        initViews()
        setupSpinners()
        setupListeners()
    }

    private fun initViews() {
        radioGroupSearchType = findViewById(R.id.radioGroupSearchType)
        etSearch = findViewById(R.id.etSearch)
        spinnerUnionCouncil = findViewById(R.id.spinnerUnionCouncil)
        etStartDate = findViewById(R.id.etStartDate)
        etEndDate = findViewById(R.id.etEndDate)
        btnSearch = findViewById(R.id.btnSearch)
        btnViewHistory = findViewById(R.id.btnViewHistory)
        btnExportPDF = findViewById(R.id.btnExportPDF)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        
        btnViewHistory.visibility = View.GONE
        btnExportPDF.visibility = View.GONE
    }

    private fun setupSpinners() {
        // Union Council Spinner
        val unionCouncils = mutableListOf("All Union Councils")
        unionCouncils.addAll(dbHelper.getAllUnionCouncils().map { it.name })
        
        val ucAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, unionCouncils)
        ucAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnionCouncil.adapter = ucAdapter

        spinnerUnionCouncil.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedUnionCouncil = if (position == 0) "All" else unionCouncils[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupListeners() {
        radioGroupSearchType.setOnCheckedChangeListener { _, checkedId ->
            searchType = when (checkedId) {
                R.id.rbSearchEmployee -> "employee"
                R.id.rbSearchUnionCouncil -> "union_council"
                else -> "employee"
            }
            updateUIForSearchType()
        }

        etStartDate.setOnClickListener {
            showDatePicker(true)
        }

        etEndDate.setOnClickListener {
            showDatePicker(false)
        }

        btnSearch.setOnClickListener {
            performSearch()
        }

        btnViewHistory.setOnClickListener {
            selectedEmployee?.let { employee ->
                viewEmployeeHistory(employee)
            }
        }

        btnExportPDF.setOnClickListener {
            exportToPDF()
        }
    }

    private fun updateUIForSearchType() {
        when (searchType) {
            "employee" -> {
                etSearch.visibility = View.VISIBLE
                etSearch.hint = "Search by Name or CNIC"
                spinnerUnionCouncil.visibility = View.GONE
            }
            "union_council" -> {
                etSearch.visibility = View.GONE
                spinnerUnionCouncil.visibility = View.VISIBLE
            }
        }
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val dateToUse = if (isStartDate) startDate else endDate
        
        if (dateToUse.isNotEmpty()) {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val currentDate = sdf.parse(dateToUse)
            if (currentDate != null) {
                calendar.time = currentDate
            }
        }
        
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            if (isStartDate) {
                startDate = date
                etStartDate.setText(DateUtils.formatDateForDisplay(startDate))
            } else {
                endDate = date
                etEndDate.setText(DateUtils.formatDateForDisplay(endDate))
            }
        }, year, month, day).show()
    }

    private fun performSearch() {
        when (searchType) {
            "employee" -> searchByEmployee()
            "union_council" -> searchByUnionCouncil()
        }
    }

    private fun searchByEmployee() {
        val query = etSearch.text.toString().trim()
        
        if (query.isEmpty()) {
            etSearch.error = "Please enter search query"
            return
        }

        val employees = dbHelper.searchEmployees(query)
        
        if (employees.isEmpty()) {
            Toast.makeText(this, "No employees found", Toast.LENGTH_SHORT).show()
            recyclerView.adapter = null
            btnViewHistory.visibility = View.GONE
            btnExportPDF.visibility = View.GONE
            return
        }

        if (employees.size == 1) {
            // Single employee found - show their attendance
            selectedEmployee = employees[0]
            loadEmployeeAttendance(employees[0])
            btnViewHistory.visibility = View.VISIBLE
        } else {
            // Multiple employees - show list
            val adapter = EmployeeAdapter(
                employees,
                onItemClick = { employee ->
                    selectedEmployee = employee
                    loadEmployeeAttendance(employee)
                    btnViewHistory.visibility = View.VISIBLE
                },
                onEditClick = { employee ->
                    val intent = Intent(this, AddEditEmployeeActivity::class.java)
                    intent.putExtra("employee_id", employee.id)
                    startActivity(intent)
                },
                onDeleteClick = { employee ->
                    // Not applicable in search
                }
            )
            recyclerView.adapter = adapter
            btnViewHistory.visibility = View.GONE
            btnExportPDF.visibility = View.GONE
        }
    }

    private fun searchByUnionCouncil() {
        if (selectedUnionCouncil == "All") {
            Toast.makeText(this, "Please select a Union Council", Toast.LENGTH_SHORT).show()
            return
        }

        val employees = dbHelper.getEmployeesByUnionCouncil(selectedUnionCouncil)
        
        if (employees.isEmpty()) {
            Toast.makeText(this, "No employees found in this Union Council", Toast.LENGTH_SHORT).show()
            recyclerView.adapter = null
            btnExportPDF.visibility = View.GONE
            return
        }

        // Load attendance for all employees in this UC
        val allAttendances = mutableListOf<Attendance>()
        
        val dateRange = if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            startDate..endDate
        } else null

        employees.forEach { employee ->
            val attendances = if (dateRange != null) {
                dbHelper.getAttendanceByEmployee(employee.id).filter { it.date in dateRange }
            } else {
                dbHelper.getAttendanceByEmployee(employee.id)
            }
            allAttendances.addAll(attendances)
        }

        searchResults = allAttendances.sortedByDescending { it.date }
        
        if (searchResults.isEmpty()) {
            Toast.makeText(this, "No attendance records found", Toast.LENGTH_SHORT).show()
            recyclerView.adapter = null
            btnExportPDF.visibility = View.GONE
        } else {
            val adapter = AttendanceRecordAdapter(searchResults)
            recyclerView.adapter = adapter
            btnExportPDF.visibility = View.VISIBLE
            Toast.makeText(this, "${searchResults.size} records found", Toast.LENGTH_SHORT).show()
        }
        
        btnViewHistory.visibility = View.GONE
    }

    private fun loadEmployeeAttendance(employee: Employee) {
        val dateRange = if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            startDate..endDate
        } else null

        val attendances = if (dateRange != null) {
            dbHelper.getAttendanceByEmployee(employee.id).filter { it.date in dateRange }
        } else {
            dbHelper.getAttendanceByEmployee(employee.id)
        }

        searchResults = attendances
        
        if (attendances.isEmpty()) {
            Toast.makeText(this, "No attendance records for this employee", Toast.LENGTH_SHORT).show()
            recyclerView.adapter = null
            btnExportPDF.visibility = View.GONE
        } else {
            val adapter = AttendanceRecordAdapter(attendances)
            recyclerView.adapter = adapter
            btnExportPDF.visibility = View.VISIBLE
            Toast.makeText(this, "${attendances.size} records found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun viewEmployeeHistory(employee: Employee) {
        val intent = Intent(this, ViewReportActivity::class.java)
        intent.putExtra("report_type", "employee_history")
        intent.putExtra("employee_id", employee.id)
        intent.putExtra("start_date", "")
        intent.putExtra("end_date", "")
        startActivity(intent)
    }

    private fun exportToPDF() {
        selectedEmployee?.let { employee ->
            CoroutineScope(Dispatchers.IO).launch {
                PdfGenerator.generateEmployeeHistoryReport(this@SearchActivity, employee, searchResults)
                runOnUiThread {
                    Toast.makeText(this@SearchActivity, "PDF exported successfully", Toast.LENGTH_SHORT).show()
                }
            }
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
