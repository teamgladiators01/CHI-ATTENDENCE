package com.chi.attendance.activities

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.chi.attendance.R
import com.chi.attendance.database.DatabaseHelper
import com.chi.attendance.utils.BackupManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SettingsActivity : AppCompatActivity() {

    private lateinit var layoutChangePassword: LinearLayout
    private lateinit var layoutBackupData: LinearLayout
    private lateinit var layoutRestoreData: LinearLayout
    private lateinit var layoutResetData: LinearLayout
    private lateinit var layoutAbout: LinearLayout
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        dbHelper = DatabaseHelper(this)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        layoutChangePassword = findViewById(R.id.layoutChangePassword)
        layoutBackupData = findViewById(R.id.layoutBackupData)
        layoutRestoreData = findViewById(R.id.layoutRestoreData)
        layoutResetData = findViewById(R.id.layoutResetData)
        layoutAbout = findViewById(R.id.layoutAbout)
    }

    private fun setupListeners() {
        layoutChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        layoutBackupData.setOnClickListener {
            backupData()
        }

        layoutRestoreData.setOnClickListener {
            showRestoreDialog()
        }

        layoutResetData.setOnClickListener {
            showResetDialog()
        }

        layoutAbout.setOnClickListener {
            showAboutDialog()
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null)
        val etCurrentPassword = dialogView.findViewById<EditText>(R.id.etCurrentPassword)
        val etNewPassword = dialogView.findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = dialogView.findViewById<EditText>(R.id.etConfirmPassword)

        AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Change") { _, _ ->
                val currentPassword = etCurrentPassword.text.toString()
                val newPassword = etNewPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()

                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword != confirmPassword) {
                    Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (!dbHelper.validateAdmin("admin", currentPassword)) {
                    Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (dbHelper.changePassword("admin", newPassword)) {
                    Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun backupData() {
        CoroutineScope(Dispatchers.IO).launch {
            val backupFile = BackupManager.exportDatabase(this@SettingsActivity)
            withContext(Dispatchers.Main) {
                if (backupFile != null) {
                    Toast.makeText(this@SettingsActivity, "Backup created: ${backupFile.name}", Toast.LENGTH_LONG).show()
                    shareBackupFile(backupFile)
                } else {
                    Toast.makeText(this@SettingsActivity, "Failed to create backup", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun shareBackupFile(file: File) {
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "CHI Attendance Backup")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        startActivity(Intent.createChooser(shareIntent, "Share Backup"))
    }

    private fun showRestoreDialog() {
        val backupFiles = BackupManager.getBackupFiles(this)
        
        if (backupFiles.isEmpty()) {
            Toast.makeText(this, "No backup files found", Toast.LENGTH_SHORT).show()
            return
        }

        val fileNames = backupFiles.map { it.name }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select Backup to Restore")
            .setItems(fileNames) { _, which ->
                val selectedFile = backupFiles[which]
                showRestoreConfirmation(selectedFile)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRestoreConfirmation(backupFile: File) {
        AlertDialog.Builder(this)
            .setTitle("Restore Backup")
            .setMessage("This will replace all current data with the backup. Are you sure?")
            .setPositiveButton("Restore") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    val success = BackupManager.importDatabase(this@SettingsActivity, backupFile)
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(this@SettingsActivity, "Backup restored successfully. Please restart the app.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@SettingsActivity, "Failed to restore backup", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showResetDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reset_data, null)
        val etConfirm = dialogView.findViewById<EditText>(R.id.etConfirm)

        AlertDialog.Builder(this)
            .setTitle("Reset All Data")
            .setMessage("WARNING: This will delete all employees and attendance records. This action cannot be undone.")
            .setView(dialogView)
            .setPositiveButton("Reset") { _, _ ->
                if (etConfirm.text.toString() == "RESET") {
                    if (dbHelper.resetAllData()) {
                        Toast.makeText(this, "All data has been reset", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to reset data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Confirmation text did not match", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("About CHI Attendance")
            .setMessage("""
                CHI Attendance
                Version 1.0.0
                
                A comprehensive offline attendance management system for Community Health Inspectors.
                
                Features:
                - Employee Management
                - Daily Attendance Tracking
                - Daily, Weekly, and Monthly Reports
                - PDF Export
                - Data Backup & Restore
                
                Developed for Government Health Departments
            """.trimIndent())
            .setPositiveButton("OK", null)
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
