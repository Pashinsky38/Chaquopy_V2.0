package libby.pashinsky.chaquopy;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

    /**
     * Called when the activity is starting.
     * Initializes the UI, sets up navigation, and schedules a daily notification.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using View Binding
        libby.pashinsky.chaquopy.databinding.HomePageBinding binding = HomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set click listener for the "Go to Login" button
        binding.LoginButton.setOnClickListener(view -> navigateToLoginFragment());

        // Check if the POST_NOTIFICATIONS permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, schedule the alarm
            scheduleAlarm();
        }
    }

    /**
     * Navigates to the LoginFragment.
     * Replaces the current fragment in the fragment_login container with the LoginFragment.
     */
    private void navigateToLoginFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_login, new LoginFragment());
        transaction.addToBackStack(null); // Add to back stack for navigation history
        transaction.commit();
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

        // Schedule the repeating alarm using AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
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
                scheduleAlarm();
            } else {
                // Permission denied, display a message to the user
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}