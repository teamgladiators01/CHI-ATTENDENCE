package com.chi.attendance.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val DATE_FORMAT = "yyyy-MM-dd"
    private const val DISPLAY_DATE_FORMAT = "dd MMM yyyy"
    private const val DISPLAY_DATE_FORMAT_FULL = "EEEE, dd MMMM yyyy"
    
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return sdf.format(Date())
    }
    
    fun getCurrentDateDisplay(): String {
        val sdf = SimpleDateFormat(DISPLAY_DATE_FORMAT_FULL, Locale.getDefault())
        return sdf.format(Date())
    }
    
    fun formatDateForDisplay(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }
    
    fun formatDateForDisplayFull(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat(DISPLAY_DATE_FORMAT_FULL, Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }
    
    fun getWeekStartDate(dateString: String): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val date = sdf.parse(dateString) ?: Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        
        // Set to Monday of current week
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, -1)
        }
        
        return sdf.format(calendar.time)
    }
    
    fun getWeekEndDate(dateString: String): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val date = sdf.parse(dateString) ?: Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        
        // Set to Sunday of current week
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, 1)
        }
        
        return sdf.format(calendar.time)
    }
    
    fun getMonthStartDate(dateString: String): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val date = sdf.parse(dateString) ?: Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return sdf.format(calendar.time)
    }
    
    fun getMonthEndDate(dateString: String): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val date = sdf.parse(dateString) ?: Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return sdf.format(calendar.time)
    }
    
    fun getMonthName(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }
    
    fun getDaysBetween(startDate: String, endDate: String): List<String> {
        val days = mutableListOf<String>()
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        
        val start = sdf.parse(startDate) ?: return days
        val end = sdf.parse(endDate) ?: return days
        
        val calendar = Calendar.getInstance()
        calendar.time = start
        
        while (!calendar.time.after(end)) {
            days.add(sdf.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        
        return days
    }
    
    fun addDays(dateString: String, days: Int): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val date = sdf.parse(dateString) ?: Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, days)
        return sdf.format(calendar.time)
    }
    
    fun getWorkingDaysInMonth(dateString: String): Int {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val date = sdf.parse(dateString) ?: Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        
        calendar.set(year, month, 1)
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        var workingDays = 0
        for (day in 1..maxDay) {
            calendar.set(year, month, day)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                workingDays++
            }
        }
        
        return workingDays
    }
}
