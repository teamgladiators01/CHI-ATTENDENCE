#!/bin/bash

# CHI Attendance APK Build Script
# This script builds the CHI Attendance Android APK

echo "=========================================="
echo "CHI Attendance APK Builder"
echo "=========================================="
echo ""

# Check if Android SDK is available
if [ -z "$ANDROID_SDK_ROOT" ] && [ -z "$ANDROID_HOME" ]; then
    echo "Warning: ANDROID_SDK_ROOT or ANDROID_HOME not set"
    echo "Please set your Android SDK path:"
    echo "export ANDROID_SDK_ROOT=/path/to/android/sdk"
    echo ""
fi

# Make gradlew executable
chmod +x gradlew

# Clean previous builds
echo "Cleaning previous builds..."
./gradlew clean

# Build debug APK
echo ""
echo "Building Debug APK..."
./gradlew assembleDebug

# Check if build was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "Build Successful!"
    echo "=========================================="
    echo ""
    echo "APK Location:"
    find app/build/outputs/apk -name "*.apk" -type f
    echo ""
    echo "To install on device:"
    echo "adb install -r app/build/outputs/apk/debug/app-debug.apk"
    echo ""
else
    echo ""
    echo "=========================================="
    echo "Build Failed!"
    echo "=========================================="
    echo ""
    echo "Please check the error messages above."
    exit 1
fi
