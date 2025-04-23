package libby.pashinsky.chaquopy;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import libby.pashinsky.chaquopy.databinding.HomePageBinding;

import java.util.Calendar;

/**
 * The main activity of the application, representing the home page.
 * Handles navigation to other fragments and schedules a daily notification.
 */
public class HomePage extends AppCompatActivity {

    // Request code for notification permission
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 123;
    // Request code for exact alarm permission
    private static final int EXACT_ALARM_PERMISSION_REQUEST_CODE = 124;
    // Shared preferences constants
    public static final String PREFS_NAME = "savedLastActivityPrefs";
    public static final String LAST_ACTIVITY = "lastActivity";

    private HomePageBinding binding;

    /**
     * Called when the activity is starting.
     * Initializes the UI, sets up navigation, and schedules a daily notification.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if we should redirect to the last opened activity
        checkAndRedirectToLastActivity();

        // Setup UI components
        setupUI();

        // Set up register button listener
        binding.RegisterButton.setOnClickListener(view -> navigateToRegisterFragment());

        // Check and request notification permissions
        checkNotificationPermissions();

        // Save the last opened activity
        saveLastActivity(this.getClass().getName());
    }

    /**
     * Sets up the UI components for the activity
     */
    private void setupUI() {
        // Inflate the layout using View Binding
        binding = HomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    /**
     * Checks and requests notification permissions if needed
     */
    private void checkNotificationPermissions() {
        // Check if the POST_NOTIFICATIONS permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it from the user
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            } else {
                // Permission already granted, schedule the alarm
                checkAndScheduleAlarm();
            }
        } else {
            // For versions prior to Android 13, no runtime permission is needed
            checkAndScheduleAlarm();
        }
    }

    /**
     * Checks if there's a saved last activity and redirects to it if needed
     */
    private void checkAndRedirectToLastActivity() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastActivity = settings.getString(LAST_ACTIVITY, "");

        // If we have a saved activity and it's not HomePage
        if (!lastActivity.isEmpty() && !lastActivity.equals(this.getClass().getName())) {
            if (lastActivity.equals(Introduction.class.getName())) {
                Intent intent = new Intent(this, Introduction.class);
                startActivity(intent);
                finish();
            } else if (lastActivity.equals(ConditionalsStatements.class.getName())) {
                Intent intent = new Intent(this, ConditionalsStatements.class);
                startActivity(intent);
                finish();
            } else if (lastActivity.equals(Loops.class.getName())) {
                Intent intent = new Intent(this, Loops.class);
                startActivity(intent);
                finish();
            } else if (lastActivity.equals(Functions.class.getName())) {
                Intent intent = new Intent(this, Functions.class);
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * Saves the given activity class name as the last opened activity
     * @param context The context to use for accessing SharedPreferences
     * @param activityClassName The fully qualified class name of the activity
     */
    public static void saveLastActivity(Context context, String activityClassName) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LAST_ACTIVITY, activityClassName);
        editor.apply();
    }

    /**
     * Saves the current activity class name as the last opened activity
     */
    private void saveLastActivity(String activityClassName) {
        saveLastActivity(this, activityClassName);
    }

    /**
     * Navigates to the RegisterFragment.
     * Replaces the current fragment in the fragment_register container with the RegisterFragment.
     */
    private void navigateToRegisterFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_register, new RegisterFragment());
        transaction.addToBackStack(null); // Add to back stack for navigation history
        transaction.commit();
    }

    /**
     * Checks for exact alarm permission on Android S+ and schedules the alarm
     */
    private void checkAndScheduleAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android 12 (S) and above, check for exact alarm permission
            if (!alarmManager.canScheduleExactAlarms()) {
                // Request permission for exact alarms
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivityForResult(intent, EXACT_ALARM_PERMISSION_REQUEST_CODE);
                Toast.makeText(this, "Please grant permission for exact alarms", Toast.LENGTH_LONG).show();
                return;
            }
        }

        scheduleAlarm();
    }

    /**
     * Schedules a repeating alarm to trigger a notification at a specific time.
     * The notification is scheduled to be delivered daily at noon.
     */
    private void scheduleAlarm() {
        // Get the AlarmManager system service
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Create an intent to trigger the NotificationReceiver
        Intent intent = new Intent(this, NotificationReceiver.class);
        // Create a PendingIntent for the BroadcastReceiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Get the current time and set the desired notification time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12); // Set the hour to 12 (noon)
        calendar.set(Calendar.MINUTE, 0); // Set the minute to 0
        calendar.set(Calendar.SECOND, 0); // Set the second to 0

        // If the scheduled time has already passed for today, schedule it for tomorrow
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Cancel any existing alarms first
        alarmManager.cancel(pendingIntent);

        // Schedule the repeating alarm using AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            // Use setExactAndAllowWhileIdle for SDK >= 31 if we have permission
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            // Set a daily repeating alarm using a broadcast receiver that will reschedule the alarm
            Intent rescheduleIntent = new Intent(this, NotificationReceiver.class);
            rescheduleIntent.setAction("RESCHEDULE_ALARM");
            PendingIntent reschedulePendingIntent = PendingIntent.getBroadcast(this, 2, rescheduleIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar rescheduleCalendar = Calendar.getInstance();
            rescheduleCalendar.setTimeInMillis(calendar.getTimeInMillis());
            rescheduleCalendar.add(Calendar.HOUR_OF_DAY, 1); // Schedule rescheduling 1 hour after notification

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, rescheduleCalendar.getTimeInMillis(), reschedulePendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For Android M through Android R
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            // We'll need to reschedule this alarm daily
            Intent rescheduleIntent = new Intent(this, NotificationReceiver.class);
            rescheduleIntent.setAction("RESCHEDULE_ALARM");
            PendingIntent reschedulePendingIntent = PendingIntent.getBroadcast(this, 2, rescheduleIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar rescheduleCalendar = Calendar.getInstance();
            rescheduleCalendar.setTimeInMillis(calendar.getTimeInMillis());
            rescheduleCalendar.add(Calendar.HOUR_OF_DAY, 1); // Schedule rescheduling 1 hour after notification

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, rescheduleCalendar.getTimeInMillis(), reschedulePendingIntent);
        } else {
            // For older Android versions, we can still use setRepeating
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }

        // Save the next alarm time in shared preferences
        getSharedPreferences("alarm_prefs", MODE_PRIVATE).edit()
                .putLong("next_alarm_time", calendar.getTimeInMillis())
                .apply();
    }

    /**
     * Callback for the result of the permission request.
     * If the permission is granted, schedules the alarm.
     * If the permission is denied, displays a message to the user.
     *
     * @param requestCode  The request code passed in {@link #requestPermissions}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either {@link PackageManager#PERMISSION_GRANTED} or {@link PackageManager#PERMISSION_DENIED}. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Call the super method to handle other permission requests
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check if the request code matches the notification permission request code
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            // Check if the permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, schedule the alarm
                checkAndScheduleAlarm();
            } else {
                // Permission denied, display a message to the user
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EXACT_ALARM_PERMISSION_REQUEST_CODE) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    scheduleAlarm();
                } else {
                    Toast.makeText(this, "Exact alarm permission denied. Notifications may not be reliable.", Toast.LENGTH_LONG).show();
                    // Try scheduling without exact timing
                    scheduleAlarm();
                }
            }
        }
    }
}