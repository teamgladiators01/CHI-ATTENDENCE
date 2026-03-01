package com.chi.attendance.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chi.attendance.R
import com.chi.attendance.adapters.AttendanceRecordAdapter
import com.chi.attendance.adapters.ReportSummaryAdapter
import com.chi.attendance.database.DatabaseHelper
import com.chi.attendance.models.Attendance
import com.chi.attendance.models.AttendanceSummary
import com.chi.attendance.utils.DateUtils
import com.chi.attendance.utils.PdfGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ViewReportActivity : AppCompatActivity() {

    private lateinit var tvReportTitle: TextView
    private lateinit var tvReportSubtitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tvSummary: TextView
    private lateinit var dbHelper: DatabaseHelper

    private var reportType = "daily"
    private var startDate = ""
    private var endDate = ""
    private var employeeId: Long = 0
    private var unionCouncil = "All"
    private var attendances = listOf<Attendance>()
    private var summaries = listOf<AttendanceSummary>()
    private var generatedPdfFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_report)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "View Report"

        dbHelper = DatabaseHelper(this)

        initViews()
        loadIntentData()
        generateReport()
    }

    private fun initViews() {
        tvReportTitle = findViewById(R.id.tvReportTitle)
        tvReportSubtitle = findViewById(R.id.tvReportSubtitle)
        recyclerView = findViewById(R.id.recyclerView)
        tvEmpty = findViewById(R.id.tvEmpty)
        tvSummary = findViewById(R.id.tvSummary)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadIntentData() {
        reportType = intent.getStringExtra("report_type") ?: "daily"
        startDate = intent.getStringExtra("start_date") ?: DateUtils.getCurrentDate()
        endDate = intent.getStringExtra("end_date") ?: DateUtils.getCurrentDate()
        employeeId = intent.getLongExtra("employee_id", 0)
        unionCouncil = intent.getStringExtra("union_council") ?: "All"
    }

    private fun generateReport() {
        when (reportType) {
            "daily" -> generateDailyReport()
            "weekly" -> generateWeeklyReport()
            "monthly" -> generateMonthlyReport()
        }
    }

    private fun generateDailyReport() {
        tvReportTitle.text = "Daily Attendance Report"
        tvReportSubtitle.text = "Date: ${DateUtils.formatDateForDisplayFull(startDate)}"
        if (unionCouncil != "All") {
            tvReportSubtitle.text = "${tvReportSubtitle.text}\nUnion Council: $unionCouncil"
        }

        attendances = if (unionCouncil != "All") {
            dbHelper.getAttendanceByUnionCouncilAndDate(unionCouncil, startDate)
        } else if (employeeId != 0L) {
            dbHelper.getAttendanceByEmployee(employeeId).filter { it.date == startDate }
        } else {
            dbHelper.getAttendanceByDate(startDate)
        }

        // Calculate summary
        val present = attendances.count { it.status == Attendance.STATUS_PRESENT }
        val absent = attendances.count { it.status == Attendance.STATUS_ABSENT }
        val leave = attendances.count { it.status == Attendance.STATUS_LEAVE }
        val late = attendances.count { it.status == Attendance.STATUS_LATE }

        tvSummary.text = "Present: $present | Absent: $absent | Leave: $leave | Late: $late"

        if (attendances.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            val adapter = AttendanceRecordAdapter(attendances)
            recyclerView.adapter = adapter
        }

        // Generate PDF
        CoroutineScope(Dispatchers.IO).launch {
            generatedPdfFile = PdfGenerator.generateDailyReport(this@ViewReportActivity, startDate, attendances, unionCouncil)
        }
    }

    private fun generateWeeklyReport() {
        tvReportTitle.text = "Weekly Attendance Report"
        tvReportSubtitle.text = "Period: ${DateUtils.formatDateForDisplay(startDate)} - ${DateUtils.formatDateForDisplay(endDate)}"
        if (unionCouncil != "All") {
            tvReportSubtitle.text = "${tvReportSubtitle.text}\nUnion Council: $unionCouncil"
        }

        val allAttendances = if (unionCouncil != "All") {
            dbHelper.getAttendanceByUnionCouncilAndDateRange(unionCouncil, startDate, endDate)
        } else if (employeeId != 0L) {
            dbHelper.getAttendanceByEmployee(employeeId).filter { it.date in startDate..endDate }
        } else {
            dbHelper.getAttendanceByDateRange(startDate, endDate)
        }

        // Generate summaries
        summaries = generateSummaries(allAttendances)

        if (summaries.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            tvSummary.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            tvSummary.visibility = View.VISIBLE
            val adapter = ReportSummaryAdapter(summaries)
            recyclerView.adapter = adapter

            // Overall summary
            val totalPresent = summaries.sumOf { it.presentDays }
            val totalAbsent = summaries.sumOf { it.absentDays }
            val totalLeave = summaries.sumOf { it.leaveDays }
            val totalLate = summaries.sumOf { it.lateDays }
            tvSummary.text = "Total - Present: $totalPresent | Absent: $totalAbsent | Leave: $totalLeave | Late: $totalLate"
        }

        // Generate PDF
        CoroutineScope(Dispatchers.IO).launch {
            generatedPdfFile = PdfGenerator.generateWeeklyReport(this@ViewReportActivity, startDate, endDate, summaries, unionCouncil)
        }
    }

    private fun generateMonthlyReport() {
        tvReportTitle.text = "Monthly Attendance Report"
        tvReportSubtitle.text = "Month: ${DateUtils.getMonthName(startDate)}"
        if (unionCouncil != "All") {
            tvReportSubtitle.text = "${tvReportSubtitle.text}\nUnion Council: $unionCouncil"
        }

        val allAttendances = if (unionCouncil != "All") {
            dbHelper.getAttendanceByUnionCouncilAndDateRange(unionCouncil, startDate, endDate)
        } else if (employeeId != 0L) {
            dbHelper.getAttendanceByEmployee(employeeId).filter { it.date in startDate..endDate }
        } else {
            dbHelper.getAttendanceByDateRange(startDate, endDate)
        }

        // Generate summaries
        summaries = generateSummaries(allAttendances)

        if (summaries.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            tvSummary.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            tvSummary.visibility = View.VISIBLE
            val adapter = ReportSummaryAdapter(summaries)
            recyclerView.adapter = adapter

            // Overall summary
            val totalPresent = summaries.sumOf { it.presentDays }
            val totalAbsent = summaries.sumOf { it.absentDays }
            val totalLeave = summaries.sumOf { it.leaveDays }
            val totalLate = summaries.sumOf { it.lateDays }
            tvSummary.text = "Total - Present: $totalPresent | Absent: $totalAbsent | Leave: $totalLeave | Late: $totalLate"
        }

        // Generate PDF
        CoroutineScope(Dispatchers.IO).launch {
            generatedPdfFile = PdfGenerator.generateMonthlyReport(this@ViewReportActivity, DateUtils.getMonthName(startDate), summaries, unionCouncil)
        }
    }

    private fun generateSummaries(attendances: List<Attendance>): List<AttendanceSummary> {
        val grouped = attendances.groupBy { it.employeeId }
        val summaries = mutableListOf<AttendanceSummary>()

        grouped.forEach { (empId, empAttendances) ->
            val employee = dbHelper.getEmployeeById(empId)
            if (employee != null) {
                val present = empAttendances.count { it.status == Attendance.STATUS_PRESENT }
                val absent = empAttendances.count { it.status == Attendance.STATUS_ABSENT }
                val leave = empAttendances.count { it.status == Attendance.STATUS_LEAVE }
                val late = empAttendances.count { it.status == Attendance.STATUS_LATE }
                val totalDays = empAttendances.size
                val percentage = if (totalDays > 0) ((present + late).toDouble() / totalDays * 100) else 0.0

                summaries.add(AttendanceSummary(
                    employeeId = empId,
                    employeeName = employee.fullName,
                    unionCouncil = employee.unionCouncil,
                    totalWorkingDays = totalDays,
                    presentDays = present,
                    absentDays = absent,
                    leaveDays = leave,
                    lateDays = late,
                    attendancePercentage = percentage
                ))
            }
        }

        return summaries.sortedBy { it.employeeName }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_report, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_share -> {
                shareReport()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareReport() {
        generatedPdfFile?.let { file ->
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )
            
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "CHI Attendance Report")
                putExtra(Intent.EXTRA_TEXT, "Please find the attached attendance report.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            startActivity(Intent.createChooser(shareIntent, "Share Report"))
        } ?: Toast.makeText(this, "PDF not generated yet", Toast.LENGTH_SHORT).show()
    }
}
