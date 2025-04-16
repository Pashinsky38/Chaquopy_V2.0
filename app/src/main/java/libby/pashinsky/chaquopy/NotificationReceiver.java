package libby.pashinsky.chaquopy;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

/**
 * A BroadcastReceiver that handles the daily notification for Python lessons.
 * Creates and displays a notification when triggered by the AlarmManager.
 * Also handles rescheduling the alarm for the next day.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "daily_lesson_channel";
    private static final String CHANNEL_NAME = "Daily Lesson Reminders";
    private static final int NOTIFICATION_ID = 123;

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
     * Creates and displays a notification reminding the user to learn Python.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("RESCHEDULE_ALARM")) {
            // This is a request to reschedule the alarm for the next day
            scheduleAlarmForNextDay(context);
            return;
        }

        // Create the notification channel (for Android Oreo and above)
        createNotificationChannel(context);

        // Create an intent to open the HomePage activity when the notification is clicked
        Intent notificationIntent = new Intent(context, HomePage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Python Daily Lesson")
                .setContentText("Time to learn something new in Python!")
                .setSmallIcon(R.drawable.python_programming_language_icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

        // On newer Android versions, we need to reschedule the alarm for the next day after it fires
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scheduleAlarmForNextDay(context);
        }
    }

    /**
     * Reschedules the alarm for the next day at noon
     * This is necessary on Android 6.0+ as exact alarms don't repeat reliably
     *
     * @param context The Context in which the receiver is running.
     */
    private void scheduleAlarmForNextDay(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Get the stored alarm time
        SharedPreferences prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE);
        long lastAlarmTime = prefs.getLong("next_alarm_time", 0);

        // Set up the calendar for tomorrow's alarm at noon
        Calendar calendar = Calendar.getInstance();

        if (lastAlarmTime > 0) {
            // Use the last alarm time as base and add one day
            calendar.setTimeInMillis(lastAlarmTime);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        } else {
            // Set up a new alarm time for noon tomorrow
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            // If noon has already passed today, schedule for tomorrow
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        // Save the next alarm time
        prefs.edit().putLong("next_alarm_time", calendar.getTimeInMillis()).apply();

        // Cancel any existing alarms
        alarmManager.cancel(pendingIntent);

        // Schedule the new alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    /**
     * Creates the notification channel for Android Oreo and above.
     * This is required for displaying notifications on devices running Android 8.0 or higher.
     *
     * @param context The Context in which the receiver is running.
     */
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}