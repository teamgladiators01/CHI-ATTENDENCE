# CHI Attendance - Android Application

A comprehensive offline attendance management system for Community Health Inspectors.

## Features

### Core Functionality
- **Employee Management**: Add, edit, delete, and view employee profiles
- **Daily Attendance**: Mark attendance with Present, Absent, Leave, and Late options
- **Reports**: Generate Daily, Weekly, and Monthly attendance reports
- **Union Council Management**: Organize employees by Union Council
- **Advanced Search**: Search by name, CNIC, or Union Council with date range filtering

### Admin Features
- Secure login system (default: admin / admin123)
- Change password functionality
- Data backup and restore
- Reset all data with confirmation

### Export & Sharing
- Export reports as PDF
- Share reports via WhatsApp and other apps
- Backup database locally

## Technical Specifications

- **Platform**: Android 8.0 (API 26) and above
- **Database**: SQLite (Room Database compatible)
- **Language**: Kotlin
- **Architecture**: Single-activity with multiple screens
- **Offline**: Works completely without internet

## Project Structure

```
CHIAttendance/
├── app/
│   ├── src/main/java/com/chi/attendance/
│   │   ├── activities/       # All Activity classes
│   │   ├── adapters/         # RecyclerView Adapters
│   │   ├── database/         # Database Helper and Contract
│   │   ├── models/           # Data models
│   │   └── utils/            # Utility classes
│   └── src/main/res/         # Layouts, drawables, values
├── build.gradle              # App-level build config
└── settings.gradle           # Project settings
```

## Building the APK

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 11 or later
- Android SDK with API 34

### Build Steps

1. Open the project in Android Studio
2. Sync project with Gradle files
3. Build > Build Bundle(s) / APK(s) > Build APK(s)
4. The APK will be generated in `app/build/outputs/apk/debug/`

### Command Line Build
```bash
./gradlew assembleDebug
```

## Default Login Credentials
- **Username**: admin
- **Password**: admin123

## Employee Data Fields
- Full Name (Required)
- CNIC (Optional)
- Contact Number (Optional)
- Designation (Default: Community Health Inspector)
- Allotted Union Council (Required)
- Date of Joining
- Status (Active/Inactive)

## Attendance Options
- Present
- Absent
- Leave
- Late

## Report Types

### Daily Report
- Shows attendance for selected date
- Filter by: All Employees, Specific Employee, Specific Union Council

### Weekly Report
- Auto-calculates Monday-Sunday attendance
- Shows: Total Present, Absent, Leave, Late days
- Filter by: All Employees, Single Employee, Single Union Council

### Monthly Report
- Full month summary
- Per employee statistics:
  - Total Working Days
  - Present Days
  - Absent Days
  - Leave Days
  - Late Days
  - Attendance Percentage

## PDF Report Headers
All PDF reports include:
```
Community Health Inspector Attendance Report
[Report Type]
Date/Period: [Date Range]
Union Council: [If filtered]
```

## UI Theme
- **Primary Color**: Green (#2E7D32)
- **Background**: Light Green (#E8F5E9)
- **Style**: Professional government-style interface

## Permissions Required
- WRITE_EXTERNAL_STORAGE (for PDF export and backup)
- READ_EXTERNAL_STORAGE (for restore)

## Data Storage
All data is stored locally in the app's private directory:
- Database: `/data/data/com.chi.attendance/databases/chi_attendance.db`
- PDF Exports: `/Android/data/com.chi.attendance/files/Documents/`
- Backups: `/Android/data/com.chi.attendance/files/Documents/Backups/`

## Version History
- **v1.0.0** - Initial release with all core features

## Support
For issues or feature requests, please contact the development team.

## License
This application is developed for government health departments.
