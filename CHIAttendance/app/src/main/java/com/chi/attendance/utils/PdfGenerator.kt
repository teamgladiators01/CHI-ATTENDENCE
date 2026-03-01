package com.chi.attendance.utils

import android.content.Context
import android.os.Environment
import com.chi.attendance.models.Attendance
import com.chi.attendance.models.AttendanceSummary
import com.chi.attendance.models.Employee
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {
    
    fun generateDailyReport(
        context: Context,
        date: String,
        attendances: List<Attendance>,
        unionCouncil: String = "All"
    ): File {
        val fileName = "Daily_Report_${date}_${System.currentTimeMillis()}.pdf"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        
        val pdfWriter = PdfWriter(file)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)
        
        // Title
        val title = Paragraph("Community Health Inspector Attendance Report")
            .setFontSize(18f)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(10f)
        document.add(title)
        
        // Subtitle
        val subtitle = Paragraph("Daily Attendance Report")
            .setFontSize(14f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(5f)
        document.add(subtitle)
        
        // Date
        val datePara = Paragraph("Date: ${DateUtils.formatDateForDisplayFull(date)}")
            .setFontSize(12f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(5f)
        document.add(datePara)
        
        // Union Council filter
        if (unionCouncil != "All") {
            val ucPara = Paragraph("Union Council: $unionCouncil")
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20f)
            document.add(ucPara)
        } else {
            document.add(Paragraph().setMarginBottom(20f))
        }
        
        // Summary
        val presentCount = attendances.count { it.status == Attendance.STATUS_PRESENT }
        val absentCount = attendances.count { it.status == Attendance.STATUS_ABSENT }
        val leaveCount = attendances.count { it.status == Attendance.STATUS_LEAVE }
        val lateCount = attendances.count { it.status == Attendance.STATUS_LATE }
        
        val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f, 1f, 1f)))
            .setWidth(UnitValue.createPercentValue(100f))
            .setMarginBottom(20f)
        
        summaryTable.addCell(createSummaryCell("Present", presentCount.toString(), ColorConstants.GREEN))
        summaryTable.addCell(createSummaryCell("Absent", absentCount.toString(), ColorConstants.RED))
        summaryTable.addCell(createSummaryCell("Leave", leaveCount.toString(), ColorConstants.ORANGE))
        summaryTable.addCell(createSummaryCell("Late", lateCount.toString(), ColorConstants.BLUE))
        
        document.add(summaryTable)
        
        // Attendance Table
        if (attendances.isNotEmpty()) {
            val table = Table(UnitValue.createPercentArray(floatArrayOf(0.5f, 2f, 1.5f, 1f, 1.5f)))
                .setWidth(UnitValue.createPercentValue(100f))
            
            // Header
            table.addHeaderCell(createHeaderCell("#"))
            table.addHeaderCell(createHeaderCell("Employee Name"))
            table.addHeaderCell(createHeaderCell("Union Council"))
            table.addHeaderCell(createHeaderCell("Status"))
            table.addHeaderCell(createHeaderCell("Remarks"))
            
            // Data
            attendances.forEachIndexed { index, attendance ->
                table.addCell(createDataCell((index + 1).toString()))
                table.addCell(createDataCell(attendance.employeeName))
                table.addCell(createDataCell(attendance.unionCouncil))
                table.addCell(createStatusCell(attendance.status))
                table.addCell(createDataCell(attendance.remarks))
            }
            
            document.add(table)
        } else {
            document.add(Paragraph("No attendance records found for this date.")
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20f))
        }
        
        // Footer
        document.add(Paragraph().setMarginTop(30f))
        val footer = Paragraph("Generated on: ${SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date())}")
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.GRAY)
        document.add(footer)
        
        document.close()
        return file
    }
    
    fun generateWeeklyReport(
        context: Context,
        startDate: String,
        endDate: String,
        summaries: List<AttendanceSummary>,
        unionCouncil: String = "All"
    ): File {
        val fileName = "Weekly_Report_${startDate}_to_${endDate}_${System.currentTimeMillis()}.pdf"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        
        val pdfWriter = PdfWriter(file)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)
        
        // Title
        val title = Paragraph("Community Health Inspector Attendance Report")
            .setFontSize(18f)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(10f)
        document.add(title)
        
        // Subtitle
        val subtitle = Paragraph("Weekly Attendance Report")
            .setFontSize(14f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(5f)
        document.add(subtitle)
        
        // Date Range
        val datePara = Paragraph("Period: ${DateUtils.formatDateForDisplay(startDate)} - ${DateUtils.formatDateForDisplay(endDate)}")
            .setFontSize(12f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(5f)
        document.add(datePara)
        
        // Union Council filter
        if (unionCouncil != "All") {
            val ucPara = Paragraph("Union Council: $unionCouncil")
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20f)
            document.add(ucPara)
        } else {
            document.add(Paragraph().setMarginBottom(20f))
        }
        
        // Table
        if (summaries.isNotEmpty()) {
            val table = Table(UnitValue.createPercentArray(floatArrayOf(0.3f, 1.5f, 1f, 0.8f, 0.8f, 0.8f, 0.8f, 1f)))
                .setWidth(UnitValue.createPercentValue(100f))
                .setFontSize(9f)
            
            // Header
            table.addHeaderCell(createHeaderCell("#"))
            table.addHeaderCell(createHeaderCell("Name"))
            table.addHeaderCell(createHeaderCell("UC"))
            table.addHeaderCell(createHeaderCell("Present"))
            table.addHeaderCell(createHeaderCell("Absent"))
            table.addHeaderCell(createHeaderCell("Leave"))
            table.addHeaderCell(createHeaderCell("Late"))
            table.addHeaderCell(createHeaderCell("%"))
            
            // Data
            summaries.forEachIndexed { index, summary ->
                table.addCell(createDataCell((index + 1).toString()))
                table.addCell(createDataCell(summary.employeeName))
                table.addCell(createDataCell(summary.unionCouncil))
                table.addCell(createDataCell(summary.presentDays.toString()))
                table.addCell(createDataCell(summary.absentDays.toString()))
                table.addCell(createDataCell(summary.leaveDays.toString()))
                table.addCell(createDataCell(summary.lateDays.toString()))
                table.addCell(createDataCell("${String.format("%.1f", summary.attendancePercentage)}%"))
            }
            
            document.add(table)
        } else {
            document.add(Paragraph("No attendance records found for this period.")
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20f))
        }
        
        // Footer
        document.add(Paragraph().setMarginTop(30f))
        val footer = Paragraph("Generated on: ${SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date())}")
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.GRAY)
        document.add(footer)
        
        document.close()
        return file
    }
    
    fun generateMonthlyReport(
        context: Context,
        monthName: String,
        summaries: List<AttendanceSummary>,
        unionCouncil: String = "All"
    ): File {
        val fileName = "Monthly_Report_${monthName.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        
        val pdfWriter = PdfWriter(file)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)
        
        // Title
        val title = Paragraph("Community Health Inspector Attendance Report")
            .setFontSize(18f)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(10f)
        document.add(title)
        
        // Subtitle
        val subtitle = Paragraph("Monthly Attendance Report")
            .setFontSize(14f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(5f)
        document.add(subtitle)
        
        // Month
        val monthPara = Paragraph("Month: $monthName")
            .setFontSize(12f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(5f)
        document.add(monthPara)
        
        // Union Council filter
        if (unionCouncil != "All") {
            val ucPara = Paragraph("Union Council: $unionCouncil")
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20f)
            document.add(ucPara)
        } else {
            document.add(Paragraph().setMarginBottom(20f))
        }
        
        // Table
        if (summaries.isNotEmpty()) {
            val table = Table(UnitValue.createPercentArray(floatArrayOf(0.3f, 1.5f, 1f, 0.8f, 0.8f, 0.8f, 0.8f, 1f)))
                .setWidth(UnitValue.createPercentValue(100f))
                .setFontSize(9f)
            
            // Header
            table.addHeaderCell(createHeaderCell("#"))
            table.addHeaderCell(createHeaderCell("Name"))
            table.addHeaderCell(createHeaderCell("UC"))
            table.addHeaderCell(createHeaderCell("Present"))
            table.addHeaderCell(createHeaderCell("Absent"))
            table.addHeaderCell(createHeaderCell("Leave"))
            table.addHeaderCell(createHeaderCell("Late"))
            table.addHeaderCell(createHeaderCell("%"))
            
            // Data
            summaries.forEachIndexed { index, summary ->
                table.addCell(createDataCell((index + 1).toString()))
                table.addCell(createDataCell(summary.employeeName))
                table.addCell(createDataCell(summary.unionCouncil))
                table.addCell(createDataCell(summary.presentDays.toString()))
                table.addCell(createDataCell(summary.absentDays.toString()))
                table.addCell(createDataCell(summary.leaveDays.toString()))
                table.addCell(createDataCell(summary.lateDays.toString()))
                table.addCell(createDataCell("${String.format("%.1f", summary.attendancePercentage)}%"))
            }
            
            document.add(table)
        } else {
            document.add(Paragraph("No attendance records found for this month.")
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20f))
        }
        
        // Footer
        document.add(Paragraph().setMarginTop(30f))
        val footer = Paragraph("Generated on: ${SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date())}")
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.GRAY)
        document.add(footer)
        
        document.close()
        return file
    }
    
    fun generateEmployeeHistoryReport(
        context: Context,
        employee: Employee,
        attendances: List<Attendance>
    ): File {
        val fileName = "Employee_History_${employee.fullName.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        
        val pdfWriter = PdfWriter(file)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)
        
        // Title
        val title = Paragraph("Community Health Inspector Attendance Report")
            .setFontSize(18f)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(10f)
        document.add(title)
        
        // Subtitle
        val subtitle = Paragraph("Employee Attendance History")
            .setFontSize(14f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20f)
        document.add(subtitle)
        
        // Employee Details
        val detailsTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 2f)))
            .setWidth(UnitValue.createPercentValue(100f))
            .setMarginBottom(20f)
        
        detailsTable.addCell(createDetailLabelCell("Name:"))
        detailsTable.addCell(createDetailValueCell(employee.fullName))
        detailsTable.addCell(createDetailLabelCell("CNIC:"))
        detailsTable.addCell(createDetailValueCell(employee.cnic.ifEmpty { "N/A" }))
        detailsTable.addCell(createDetailLabelCell("Contact:"))
        detailsTable.addCell(createDetailValueCell(employee.contactNumber.ifEmpty { "N/A" }))
        detailsTable.addCell(createDetailLabelCell("Designation:"))
        detailsTable.addCell(createDetailValueCell(employee.designation))
        detailsTable.addCell(createDetailLabelCell("Union Council:"))
        detailsTable.addCell(createDetailValueCell(employee.unionCouncil))
        detailsTable.addCell(createDetailLabelCell("Date of Joining:"))
        detailsTable.addCell(createDetailValueCell(DateUtils.formatDateForDisplay(employee.dateOfJoining)))
        
        document.add(detailsTable)
        
        // Attendance History
        if (attendances.isNotEmpty()) {
            val table = Table(UnitValue.createPercentArray(floatArrayOf(0.5f, 1.5f, 1f, 1.5f)))
                .setWidth(UnitValue.createPercentValue(100f))
            
            // Header
            table.addHeaderCell(createHeaderCell("#"))
            table.addHeaderCell(createHeaderCell("Date"))
            table.addHeaderCell(createHeaderCell("Status"))
            table.addHeaderCell(createHeaderCell("Remarks"))
            
            // Data
            attendances.forEachIndexed { index, attendance ->
                table.addCell(createDataCell((index + 1).toString()))
                table.addCell(createDataCell(DateUtils.formatDateForDisplay(attendance.date)))
                table.addCell(createStatusCell(attendance.status))
                table.addCell(createDataCell(attendance.remarks))
            }
            
            document.add(table)
        } else {
            document.add(Paragraph("No attendance records found for this employee.")
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20f))
        }
        
        // Footer
        document.add(Paragraph().setMarginTop(30f))
        val footer = Paragraph("Generated on: ${SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date())}")
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.GRAY)
        document.add(footer)
        
        document.close()
        return file
    }
    
    private fun createHeaderCell(text: String): Cell {
        return Cell()
            .add(Paragraph(text).setBold())
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setBorder(SolidBorder(ColorConstants.BLACK, 1f))
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(5f)
    }
    
    private fun createDataCell(text: String): Cell {
        return Cell()
            .add(Paragraph(text))
            .setBorder(SolidBorder(ColorConstants.BLACK, 0.5f))
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(5f)
    }
    
    private fun createStatusCell(status: String): Cell {
        val cell = Cell()
            .add(Paragraph(status))
            .setBorder(SolidBorder(ColorConstants.BLACK, 0.5f))
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(5f)
        
        when (status) {
            Attendance.STATUS_PRESENT -> cell.setBackgroundColor(com.itextpdf.kernel.colors.Color(200, 255, 200))
            Attendance.STATUS_ABSENT -> cell.setBackgroundColor(com.itextpdf.kernel.colors.Color(255, 200, 200))
            Attendance.STATUS_LEAVE -> cell.setBackgroundColor(com.itextpdf.kernel.colors.Color(255, 255, 200))
            Attendance.STATUS_LATE -> cell.setBackgroundColor(com.itextpdf.kernel.colors.Color(200, 200, 255))
        }
        
        return cell
    }
    
    private fun createSummaryCell(label: String, value: String, color: com.itextpdf.kernel.colors.Color): Cell {
        return Cell()
            .add(Paragraph(label).setBold().setFontSize(10f))
            .add(Paragraph(value).setBold().setFontSize(16f))
            .setBackgroundColor(color)
            .setFontColor(ColorConstants.WHITE)
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(10f)
    }
    
    private fun createDetailLabelCell(text: String): Cell {
        return Cell()
            .add(Paragraph(text).setBold())
            .setBorder(Border.NO_BORDER)
            .setPadding(5f)
    }
    
    private fun createDetailValueCell(text: String): Cell {
        return Cell()
            .add(Paragraph(text))
            .setBorder(Border.NO_BORDER)
            .setPadding(5f)
    }
}
