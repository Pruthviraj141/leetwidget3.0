# Widget Debugging Guide

## Problem: "Can't load widget" on home screen

If your widget shows "can't load widget" or shows a blank/placeholder instead of your LeetCode calendar, follow these debugging steps:

## Quick Fix Steps

1. **Update Dependencies**
   ```bash
   flutter pub get
   ```

2. **Clean and Rebuild**
   ```bash
   flutter clean
   flutter pub get
   flutter run
   ```

3. **Test the Debug Feature**
   - Open the app
   - Fetch your LeetCode data
   - Look for the "Debug Widget" button (orange button below "Add to Home Screen")
   - Tap it and check the console output in Android Studio/VS Code

## Common Issues and Solutions

### Issue 1: Image Capture Fails
**Symptoms**: Widget shows "Error loading image" or blank
**Solution**: The Davinci library might have timing issues. Our updated code now:
- Increases capture timing from 300ms to 1000ms
- Uses alternative capture methods
- Saves to temporary directory for better accessibility

### Issue 2: File Permission Problems
**Symptoms**: Widget shows "Image not found"
**Solution**: The image is now saved to the temporary directory which has better cross-app accessibility

### Issue 3: Widget Not Updating
**Symptoms**: Widget shows old data or doesn't update
**Solution**: The debug button will show you exactly what's happening in the widget update process

## Debug Output Examples

### Success Case:
```
✓ HomeWidget initialized
Widget data saved: true
Saved filename: /data/user/0/com.atharva.leetcode_streak/cache/leetcode_calendar.png
File exists: true, canRead: true, size: 45678 bytes
Widget update completed
```

### Failure Cases:
```
Widget data saved: false
// No filename saved - image capture failed

File exists: false, canRead: false, size: 0 bytes
// File exists but can't be read by widget

Homewidgetconfig.update: capture returned no/invalid bytes
// Davinci capture failed
```

## Manual Testing Steps

1. **Check App Logs**:
   - Open Android Studio
   - Go to "Logcat" tab
   - Filter for "LeetCodeWidgetProvider" or "Homewidgetconfig"
   - Look for debug messages when updating the widget

2. **Test Widget Pinning**:
   - Long press home screen
   - Add widget manually
   - Check if it shows your data

3. **Clear App Data** (if all else fails):
   - Android Settings → Apps → LeetCode Streak → Storage → Clear Data
   - Reopen app and fetch data again

## What the Fixes Do

1. **Improved Package Versions**: Uses stable, tested versions of home_widget and davinci
2. **Better Error Handling**: Multiple fallback methods if image capture fails
3. **Increased Timing**: More time for complex widgets to render before capture
4. **Better File Location**: Uses temporary directory for better widget accessibility
5. **Enhanced Logging**: Detailed debug information to identify issues
6. **Alternative Capture**: Backup method if primary Davinci capture fails

## Next Steps

If the debug button shows issues:
1. Note the specific error message
2. Check if the file exists and has proper permissions
3. Try the manual widget update from the debug menu
4. If still failing, the issue might be device-specific or OS version related

The updated code should resolve most "can't load widget" issues by addressing the core problems in image capture and file accessibility between Flutter and Android widget.