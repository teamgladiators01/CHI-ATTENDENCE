package com.chi.attendance.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chi.attendance.R
import com.chi.attendance.adapters.UnionCouncilAdapter
import com.chi.attendance.database.DatabaseHelper
import com.chi.attendance.models.UnionCouncil
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UnionCouncilActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var adapter: UnionCouncilAdapter
    private lateinit var dbHelper: DatabaseHelper
    private var ucList = listOf<UnionCouncil>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_union_council)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Union Councils"

        dbHelper = DatabaseHelper(this)

        initViews()
        setupRecyclerView()
        loadUnionCouncils()
    }

    override fun onResume() {
        super.onResume()
        loadUnionCouncils()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        fabAdd = findViewById(R.id.fabAdd)

        fabAdd.setOnClickListener {
            showAddDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = UnionCouncilAdapter(
            emptyList(),
            onEditClick = { uc ->
                showEditDialog(uc)
            },
            onDeleteClick = { uc ->
                showDeleteDialog(uc)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadUnionCouncils() {
        ucList = dbHelper.getAllUnionCouncils()
        adapter.updateList(ucList)
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_union_council, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etCode = dialogView.findViewById<EditText>(R.id.etCode)

        AlertDialog.Builder(this)
            .setTitle("Add Union Council")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                val code = etCode.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val uc = UnionCouncil(name = name, code = code)
                val id = dbHelper.addUnionCouncil(uc)
                if (id != -1L) {
                    Toast.makeText(this, "Union Council added", Toast.LENGTH_SHORT).show()
                    loadUnionCouncils()
                } else {
                    Toast.makeText(this, "Failed to add Union Council", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditDialog(uc: UnionCouncil) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_union_council, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etCode = dialogView.findViewById<EditText>(R.id.etCode)

        etName.setText(uc.name)
        etCode.setText(uc.code)

        AlertDialog.Builder(this)
            .setTitle("Edit Union Council")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val name = etName.text.toString().trim()
                val code = etCode.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val updatedUc = UnionCouncil(id = uc.id, name = name, code = code)
                if (dbHelper.updateUnionCouncil(updatedUc)) {
                    Toast.makeText(this, "Union Council updated", Toast.LENGTH_SHORT).show()
                    loadUnionCouncils()
                } else {
                    Toast.makeText(this, "Failed to update Union Council", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteDialog(uc: UnionCouncil) {
        AlertDialog.Builder(this)
            .setTitle("Delete Union Council")
            .setMessage("Are you sure you want to delete ${uc.name}?")
            .setPositiveButton("Delete") { _, _ ->
                if (dbHelper.deleteUnionCouncil(uc.id)) {
                    Toast.makeText(this, "Union Council deleted", Toast.LENGTH_SHORT).show()
                    loadUnionCouncils()
                } else {
                    Toast.makeText(this, "Failed to delete Union Council", Toast.LENGTH_SHORT).show()
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
