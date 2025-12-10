import 'dart:io';

import 'package:davinci/core/davinci_capture.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:home_widget/home_widget.dart';
import 'package:leetcode_streak/Screens/ContributionCalendar.dart';
import 'package:leetcode_streak/constants/string.dart';
import 'package:path_provider/path_provider.dart';

class Homewidgetconfig {
  static Future<void> initialize() async {
    try {
      await HomeWidget.setAppGroupId(groupId);
      debugPrint('HomeWidget initialized with groupId: $groupId');
    } catch (e) {
      debugPrint('HomeWidget initialization failed: $e');
      rethrow;
    }
  }

  static Future<void> update(BuildContext context, ContributionCalendar calendar) async {
    try {
      debugPrint('Starting widget update process...');
      
      // Increase wait time for better rendering
      final result = await DavinciCapture.offStage(
        calendar,
        context: context,
        returnImageUint8List: true,
        openFilePreview: false,
        wait: const Duration(milliseconds: 1000), // Increased from 300ms to 1000ms
      );

      debugPrint('Davinci capture result type: ${result.runtimeType}');
      
      // Ensure we have bytes and the type is what we expect
      if (result is! Uint8List || result.isEmpty) {
        debugPrint('Homewidgetconfig.update: capture returned no/invalid bytes (type: ${result.runtimeType}, isEmpty: ${result is Uint8List ? (result as Uint8List).isEmpty : "N/A"})');
        
        // Try alternative approach with render widget
        debugPrint('Trying alternative capture method...');
        return await _updateWithAlternativeMethod(context, calendar);
      }

      // Upcast to List<int> for File.writeAsBytes (Uint8List implements List<int>)
      final List<int> bytes = result;
      debugPrint('Captured image bytes: ${bytes.length}');

      // Use temporary directory for better accessibility
      final directory = await getTemporaryDirectory();
      final file = File('${directory.path}/leetcode_calendar.png');

      await file.writeAsBytes(bytes, flush: true);
      debugPrint('Image saved to: ${file.path}');

      // Verify file was created
      if (!await file.exists()) {
        throw Exception('Failed to create image file');
      }

      // Save file path to HomeWidget
      await HomeWidget.saveWidgetData('filename', file.path);
      debugPrint('File path saved to HomeWidget: ${file.path}');

      // Update the widget
      await HomeWidget.updateWidget(
        iOSName: iosWidget,
        androidName: androidWidget,
      );
      
      debugPrint('Widget updated successfully');
    } catch (e, st) {
      debugPrint('Homewidgetconfig.update error: $e\n$st');
      
      // Try alternative method on error
      try {
        debugPrint('Trying alternative update method due to error...');
        await _updateWithAlternativeMethod(context, calendar);
      } catch (altError) {
        debugPrint('Alternative method also failed: $altError');
        rethrow;
      }
    }
  }

  // Alternative method - simply retry with increased timing
  static Future<void> _updateWithAlternativeMethod(BuildContext context, ContributionCalendar calendar) async {
    try {
      debugPrint('Using alternative capture method with extended timing...');
      
      // Force a longer delay to ensure widget is fully rendered
      await Future.delayed(const Duration(milliseconds: 2000));
      
      // Try capture again with longer wait time
      final result = await DavinciCapture.offStage(
        calendar,
        context: context,
        returnImageUint8List: true,
        openFilePreview: false,
        wait: const Duration(milliseconds: 2000), // Even longer wait time
      );
      
      debugPrint('Alternative capture result type: ${result.runtimeType}');
      
      if (result is! Uint8List || result.isEmpty) {
        throw Exception('Alternative capture also failed');
      }

      final List<int> bytes = result;
      debugPrint('Alternative capture successful, bytes: ${bytes.length}');

      // Save to temporary directory
      final directory = await getTemporaryDirectory();
      final file = File('${directory.path}/leetcode_calendar_alt.png');
      
      await file.writeAsBytes(bytes, flush: true);
      debugPrint('Alternative image saved to: ${file.path}');

      // Update HomeWidget
      await HomeWidget.saveWidgetData('filename', file.path);
      await HomeWidget.updateWidget(
        iOSName: iosWidget,
        androidName: androidWidget,
      );
      
      debugPrint('Alternative widget update successful');
    } catch (e) {
      debugPrint('Alternative method error: $e');
      rethrow;
    }
  }

  // Method to check if widget data is properly saved
  static Future<bool> isWidgetDataSaved() async {
    try {
      final filename = await HomeWidget.getWidgetData<String>('filename');
      if (filename != null && filename.isNotEmpty) {
        debugPrint('Widget data exists: $filename');
        final file = File(filename);
        final exists = await file.exists();
        debugPrint('Widget image file exists: $exists');
        return exists;
      }
    } catch (e) {
      debugPrint('Error checking widget data: $e');
    }
    return false;
  }
}