# CHI Attendance - User Guide

## Table of Contents
1. [Getting Started](#getting-started)
2. [Login](#login)
3. [Dashboard](#dashboard)
4. [Employee Management](#employee-management)
5. [Union Council Management](#union-council-management)
6. [Marking Attendance](#marking-attendance)
7. [Generating Reports](#generating-reports)
8. [Search Functionality](#search-functionality)
9. [Settings](#settings)
10. [Backup and Restore](#backup-and-restore)

---

## Getting Started

### Installation
1. Download the CHI Attendance APK file
2. Enable "Install from Unknown Sources" in your Android settings
3. Tap the APK file to install
4. Open the app after installation

### Default Login
- **Username**: admin
- **Password**: admin123

**Important**: Change the default password after first login for security.

---

## Login

1. Enter your username (default: admin)
2. Enter your password (default: admin123)
3. Tap the "Login" button
4. If credentials are correct, you will be taken to the Dashboard

---

## Dashboard

The Dashboard provides a quick overview of:
- **Total Employees**: Total number of registered employees
- **Today Present**: Number of employees marked present today
- **Today Absent**: Number of employees marked absent today
- **Current Date**: Today's date displayed

### Quick Actions
Tap on any card to quickly navigate to:
- Mark Attendance
- View Reports
- Manage Employees
- Search
- Union Councils
- Settings

---

## Employee Management

### Adding a New Employee
1. Go to Dashboard → Employees (or tap Employees tab)
2. Tap the "+" button (bottom right)
3. Fill in the employee details:
   - **Full Name** (Required)
   - **CNIC** (Optional but recommended)
   - **Contact Number** (Optional)
   - **Designation** (Default: Community Health Inspector)
   - **Union Council** (Required - select from dropdown)
   - **Date of Joining** (Required)
   - **Status** (Active/Inactive)
4. Tap "Save"

### Editing an Employee
1. In the Employees list, tap the edit icon (pencil) next to the employee
2. Modify the required fields
3. Tap "Save"

### Deleting an Employee
1. In the Employees list, tap the delete icon (trash) next to the employee
2. Confirm the deletion
3. **Note**: This will also delete all attendance records for this employee

### Searching Employees
- Use the search bar at the top to search by name or CNIC
- Results will filter automatically as you type

---

## Union Council Management

### Adding a Union Council
1. Go to Dashboard → Union Council (or tap Union Council in menu)
2. Tap the "+" button
3. Enter:
   - **Union Council Name** (Required)
   - **Code** (Optional)
4. Tap "Add"

### Editing a Union Council
1. Tap the edit icon next to the Union Council
2. Modify the details
3. Tap "Update"

### Deleting a Union Council
1. Tap the delete icon next to the Union Council
2. Confirm the deletion

**Note**: You cannot delete a Union Council that has employees assigned to it.

---

## Marking Attendance

### Daily Attendance
1. Go to Dashboard → Mark Attendance
2. Select the date (defaults to today)
3. Choose filter:
   - **All Employees**: Show all active employees
   - **By Union Council**: Show employees from specific UC
4. For each employee, select:
   - **Present**: Employee is present
   - **Absent**: Employee is absent
   - **Leave**: Employee is on leave
   - **Late**: Employee arrived late
5. Tap "Save Attendance"

### Viewing/Editing Past Attendance
- Select any past date from the date picker
- Existing attendance will be displayed
- Make changes if needed
- Tap "Save Attendance"

---

## Generating Reports

### Daily Report
1. Go to Dashboard → Reports
2. Select "Daily Report"
3. Choose date
4. Optional filters:
   - Select specific employee (or All)
   - Select specific Union Council (or All)
5. Tap "Generate Report"
6. View the report on screen
7. Tap share icon to share as PDF

### Weekly Report
1. Select "Weekly Report"
2. Choose any date within the desired week
3. The app automatically calculates Monday-Sunday
4. Apply optional filters
5. Tap "Generate Report"

### Monthly Report
1. Select "Monthly Report"
2. Choose any date within the desired month
3. The app automatically calculates the full month
4. Apply optional filters
5. Tap "Generate Report"

### Report Statistics
All reports show:
- Total Present
- Total Absent
- Total Leave
- Total Late
- Attendance Percentage (Monthly/Weekly)

### Exporting PDF
- After generating a report, tap the share icon
- Choose "Share via WhatsApp" or any other app
- PDF will include the official header:
  ```
  Community Health Inspector Attendance Report
  ```

---

## Search Functionality

### Search by Employee
1. Go to Dashboard → Search
2. Select "By Employee"
3. Enter name or CNIC in search box
4. Optional: Set date range
5. Tap "Search"

### Search by Union Council
1. Select "By Union Council"
2. Choose Union Council from dropdown
3. Optional: Set date range
4. Tap "Search"

### View Employee History
1. Search for an employee
2. Tap "View History" button
3. Complete attendance history will be displayed
4. Can export as PDF

---

## Settings

### Change Password
1. Go to Dashboard → Settings
2. Tap "Change Password"
3. Enter:
   - Current password
   - New password
   - Confirm new password
4. Tap "Change"

### Backup Data
1. Tap "Backup Data"
2. Database will be exported to:
   `/Android/data/com.chi.attendance/files/Documents/Backups/`
3. Share the backup file via WhatsApp, Email, etc.

### Restore Data
1. Tap "Restore Data"
2. Select a backup file from the list
3. Confirm the restore
4. **Warning**: Current data will be replaced

### Reset All Data
1. Tap "Reset All Data"
2. Type "RESET" in the confirmation field
3. Tap confirm
4. **Warning**: This will delete ALL data permanently

### About
- View app version and information

---

## Backup and Restore

### Creating a Backup
1. Go to Settings → Backup Data
2. The app creates a `.db` file with timestamp
3. Share or save the file securely

### Restoring from Backup
1. Go to Settings → Restore Data
2. Select the backup file
3. Confirm restoration
4. App will restart with restored data

### Best Practices
- Create backups regularly (weekly recommended)
- Store backups in multiple locations
- Name backups with dates for easy identification
- Test restore process periodically

---

## Tips and Best Practices

### Daily Operations
1. Mark attendance at the same time each day
2. Review reports weekly
3. Keep employee information up to date
4. Create regular backups

### Data Management
1. Add all Union Councils before adding employees
2. Use consistent naming conventions
3. Enter CNIC numbers for better tracking
4. Mark inactive employees instead of deleting

### Report Generation
1. Generate daily reports at day end
2. Generate weekly reports on Sundays
3. Generate monthly reports at month end
4. Export and share reports immediately

---

## Troubleshooting

### App Won't Open
- Check Android version (requires 8.0 or higher)
- Reinstall the app
- Clear app data from Settings

### Cannot Login
- Verify username and password
- Default: admin / admin123
- Check Caps Lock

### Attendance Not Saving
- Check if employee is active
- Verify date selection
- Ensure all required fields are filled

### PDF Not Generating
- Check storage permissions
- Ensure enough free space
- Try sharing again

### Data Loss
- Restore from backup if available
- Contact technical support

---

## Contact and Support

For technical support or feature requests:
- Email: [Your Support Email]
- Phone: [Your Support Phone]

---

## Version Information

**Current Version**: 1.0.0
**Last Updated**: March 2026
**Compatible with**: Android 8.0 and above

---

*This application is designed for government health departments to manage Community Health Inspector attendance efficiently.*
