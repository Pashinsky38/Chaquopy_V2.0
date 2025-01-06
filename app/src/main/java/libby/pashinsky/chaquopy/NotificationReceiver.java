package libby.pashinsky.chaquopy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

/**
 * A BroadcastReceiver that handles the daily notification for Python lessons.
 * Creates and displays a notification when triggered by the AlarmManager.
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
    }

    /**
     * Creates the notification channel for Android Oreo and above.
     * This is required for displaying notifications on devices running Android 8.0 or higher.
     *
     * @param context The Context in which the receiver is running.
     */
    private void createNotificationChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}