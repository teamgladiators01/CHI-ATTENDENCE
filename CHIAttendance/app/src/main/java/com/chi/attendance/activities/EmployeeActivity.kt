package com.chi.attendance.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chi.attendance.R
import com.chi.attendance.adapters.EmployeeAdapter
import com.chi.attendance.database.DatabaseHelper
import com.chi.attendance.models.Employee
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EmployeeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var adapter: EmployeeAdapter
    private lateinit var dbHelper: DatabaseHelper
    private var employeeList = listOf<Employee>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Employees"

        dbHelper = DatabaseHelper(this)

        initViews()
        setupRecyclerView()
        loadEmployees()
    }

    override fun onResume() {
        super.onResume()
        loadEmployees()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        fabAdd = findViewById(R.id.fabAdd)

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddEditEmployeeActivity::class.java))
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchEmployees(newText)
                }
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = EmployeeAdapter(
            emptyList(),
            onItemClick = { employee ->
                // View employee details
            },
            onEditClick = { employee ->
                val intent = Intent(this, AddEditEmployeeActivity::class.java)
                intent.putExtra("employee_id", employee.id)
                startActivity(intent)
            },
            onDeleteClick = { employee ->
                showDeleteDialog(employee)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadEmployees() {
        employeeList = dbHelper.getAllEmployees()
        adapter.updateList(employeeList)
    }

    private fun searchEmployees(query: String) {
        val filtered = employeeList.filter {
            it.fullName.contains(query, ignoreCase = true) ||
            it.cnic.contains(query, ignoreCase = true)
        }
        adapter.updateList(filtered)
    }

    private fun showDeleteDialog(employee: Employee) {
        AlertDialog.Builder(this)
            .setTitle("Delete Employee")
            .setMessage("Are you sure you want to delete ${employee.fullName}? This will also delete all attendance records.")
            .setPositiveButton("Delete") { _, _ ->
                if (dbHelper.deleteEmployee(employee.id)) {
                    Toast.makeText(this, "Employee deleted", Toast.LENGTH_SHORT).show()
                    loadEmployees()
                } else {
                    Toast.makeText(this, "Failed to delete employee", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
