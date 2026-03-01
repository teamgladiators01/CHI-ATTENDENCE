package com.chi.attendance.utils

import android.content.Context
import android.os.Environment
import com.chi.attendance.database.DatabaseHelper
import java.io.*

object BackupManager {
    
    fun exportDatabase(context: Context): File? {
        return try {
            val dbHelper = DatabaseHelper(context)
            val dbPath = dbHelper.getDatabasePath(context)
            val dbFile = File(dbPath)
            
            if (!dbFile.exists()) {
                return null
            }
            
            val backupDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Backups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }
            
            val backupFileName = "CHI_Attendance_Backup_${System.currentTimeMillis()}.db"
            val backupFile = File(backupDir, backupFileName)
            
            copyFile(dbFile, backupFile)
            backupFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun importDatabase(context: Context, backupFile: File): Boolean {
        return try {
            val dbHelper = DatabaseHelper(context)
            val dbPath = dbHelper.getDatabasePath(context)
            val dbFile = File(dbPath)
            
            // Close the database before replacing
            dbHelper.close()
            
            copyFile(backupFile, dbFile)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun getBackupFiles(context: Context): List<File> {
        val backupDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Backups")
        if (!backupDir.exists() || !backupDir.isDirectory) {
            return emptyList()
        }
        
        return backupDir.listFiles { file ->
            file.isFile && file.name.endsWith(".db")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }
    
    private fun copyFile(source: File, destination: File) {
        FileInputStream(source).use { input ->
            FileOutputStream(destination).use { output ->
                input.copyTo(output)
            }
        }
    }
}
