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
import com.chi.attendance.R
import com.chi.attendance.database.DatabaseHelper
import com.chi.attendance.models.Attendance
import com.chi.attendance.models.AttendanceSummary
import com.chi.attendance.models.Employee
import com.chi.attendance.utils.DateUtils
import java.util.Calendar

class ReportsActivity : AppCompatActivity() {

    private lateinit var radioGroupReportType: RadioGroup
    private lateinit var spinnerEmployee: Spinner
    private lateinit var spinnerUnionCouncil: Spinner
    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText
    private lateinit var btnGenerateReport: Button
    private lateinit var dbHelper: DatabaseHelper

    private var reportType = "daily"
    private var selectedEmployee: Employee? = null
    private var selectedUnionCouncil = "All"
    private var startDate = DateUtils.getCurrentDate()
    private var endDate = DateUtils.getCurrentDate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Reports"

        dbHelper = DatabaseHelper(this)

        initViews()
        setupSpinners()
        setupListeners()
        updateDateFields()
    }

    private fun initViews() {
        radioGroupReportType = findViewById(R.id.radioGroupReportType)
        spinnerEmployee = findViewById(R.id.spinnerEmployee)
        spinnerUnionCouncil = findViewById(R.id.spinnerUnionCouncil)
        etStartDate = findViewById(R.id.etStartDate)
        etEndDate = findViewById(R.id.etEndDate)
        btnGenerateReport = findViewById(R.id.btnGenerateReport)
    }

    private fun setupSpinners() {
        // Employee Spinner
        val employees = dbHelper.getAllEmployees()
        val employeeNames = mutableListOf("All Employees")
        employeeNames.addAll(employees.map { it.fullName })
        
        val employeeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, employeeNames)
        employeeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEmployee.adapter = employeeAdapter

        spinnerEmployee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedEmployee = if (position == 0) null else employees.getOrNull(position - 1)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

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
        radioGroupReportType.setOnCheckedChangeListener { _, checkedId ->
            reportType = when (checkedId) {
                R.id.rbDaily -> "daily"
                R.id.rbWeekly -> "weekly"
                R.id.rbMonthly -> "monthly"
                else -> "daily"
            }
            updateDateFields()
        }

        etStartDate.setOnClickListener {
            showDatePicker(true)
        }

        etEndDate.setOnClickListener {
            showDatePicker(false)
        }

        btnGenerateReport.setOnClickListener {
            generateReport()
        }
    }

    private fun updateDateFields() {
        when (reportType) {
            "daily" -> {
                etStartDate.setText(DateUtils.formatDateForDisplay(startDate))
                etEndDate.visibility = View.GONE
            }
            "weekly" -> {
                val weekStart = DateUtils.getWeekStartDate(startDate)
                val weekEnd = DateUtils.getWeekEndDate(startDate)
                etStartDate.setText(DateUtils.formatDateForDisplay(weekStart))
                etEndDate.visibility = View.VISIBLE
                etEndDate.setText(DateUtils.formatDateForDisplay(weekEnd))
                etEndDate.isEnabled = false
            }
            "monthly" -> {
                val monthStart = DateUtils.getMonthStartDate(startDate)
                val monthEnd = DateUtils.getMonthEndDate(startDate)
                etStartDate.setText(DateUtils.formatDateForDisplay(monthStart))
                etEndDate.visibility = View.VISIBLE
                etEndDate.setText(DateUtils.formatDateForDisplay(monthEnd))
                etEndDate.isEnabled = false
            }
        }
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val dateToUse = if (isStartDate) startDate else endDate
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val currentDate = sdf.parse(dateToUse)
        if (currentDate != null) {
            calendar.time = currentDate
        }
        
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            if (isStartDate) {
                startDate = date
                updateDateFields()
            } else {
                endDate = date
                etEndDate.setText(DateUtils.formatDateForDisplay(endDate))
            }
        }, year, month, day).show()
    }

    private fun generateReport() {
        val intent = Intent(this, ViewReportActivity::class.java)
        intent.putExtra("report_type", reportType)
        intent.putExtra("start_date", startDate)
        intent.putExtra("end_date", endDate)
        intent.putExtra("employee_id", selectedEmployee?.id ?: 0L)
        intent.putExtra("union_council", selectedUnionCouncil)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
