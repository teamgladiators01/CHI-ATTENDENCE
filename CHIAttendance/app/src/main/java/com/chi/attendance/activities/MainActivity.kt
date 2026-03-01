package com.chi.attendance.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.chi.attendance.R
import com.chi.attendance.database.DatabaseHelper
import com.chi.attendance.utils.DateUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var tvDate: TextView
    private lateinit var tvTotalEmployees: TextView
    private lateinit var tvTodayPresent: TextView
    private lateinit var tvTodayAbsent: TextView
    private lateinit var cardAttendance: CardView
    private lateinit var cardReports: CardView
    private lateinit var cardEmployees: CardView
    private lateinit var cardSearch: CardView
    private lateinit var cardUnionCouncil: CardView
    private lateinit var cardSettings: CardView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        initViews()
        setupListeners()
        updateDashboard()
    }

    override fun onResume() {
        super.onResume()
        updateDashboard()
    }

    private fun initViews() {
        tvDate = findViewById(R.id.tvDate)
        tvTotalEmployees = findViewById(R.id.tvTotalEmployees)
        tvTodayPresent = findViewById(R.id.tvTodayPresent)
        tvTodayAbsent = findViewById(R.id.tvTodayAbsent)
        cardAttendance = findViewById(R.id.cardAttendance)
        cardReports = findViewById(R.id.cardReports)
        cardEmployees = findViewById(R.id.cardEmployees)
        cardSearch = findViewById(R.id.cardSearch)
        cardUnionCouncil = findViewById(R.id.cardUnionCouncil)
        cardSettings = findViewById(R.id.cardSettings)
        bottomNavigation = findViewById(R.id.bottomNavigation)
    }

    private fun setupListeners() {
        tvDate.text = DateUtils.getCurrentDateDisplay()

        cardAttendance.setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }

        cardReports.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }

        cardEmployees.setOnClickListener {
            startActivity(Intent(this, EmployeeActivity::class.java))
        }

        cardSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        cardUnionCouncil.setOnClickListener {
            startActivity(Intent(this, UnionCouncilActivity::class.java))
        }

        cardSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true
                R.id.nav_attendance -> {
                    startActivity(Intent(this, AttendanceActivity::class.java))
                    true
                }
                R.id.nav_reports -> {
                    startActivity(Intent(this, ReportsActivity::class.java))
                    true
                }
                R.id.nav_employees -> {
                    startActivity(Intent(this, EmployeeActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun updateDashboard() {
        val currentDate = DateUtils.getCurrentDate()
        val stats = dbHelper.getTodayStats(currentDate)
        
        tvTotalEmployees.text = stats["total"]?.toString() ?: "0"
        tvTodayPresent.text = stats["present"]?.toString() ?: "0"
        tvTodayAbsent.text = (stats["absent"] ?: 0).toString()
    }
}
