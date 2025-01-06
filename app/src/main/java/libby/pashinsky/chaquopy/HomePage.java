package libby.pashinsky.chaquopy;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import libby.pashinsky.chaquopy.databinding.HomePageBinding;
import java.util.Calendar;

public class HomePage extends AppCompatActivity {

    // Request code for notification permission
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using View Binding
        libby.pashinsky.chaquopy.databinding.HomePageBinding binding = HomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set click listener for the "Go to Login" button
        binding.LoginButton.setOnClickListener(view ->
                NavigationHelper.navigateToLoginFragment(getSupportFragmentManager()));

        // Check if the POST_NOTIFICATIONS permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, schedule the alarm
            scheduleAlarm();
        }
    }

    // Inflate the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Handle options menu item selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuHome) {
            // Stay on HomePage
            Toast.makeText(this, "You are already on HomePage", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menuLogin) {
            NavigationHelper.navigateToLoginFragment(getSupportFragmentManager());
            return true;
        } else if (id == R.id.menuRegister) {
            NavigationHelper.navigateToRegisterFragment(getSupportFragmentManager());
            return true;
        } else if (id == R.id.menuCloseApp) {
            finishAffinity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Schedule a repeating alarm to trigger a notification at a specific time
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
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Schedule the repeating alarm using AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    // Handle permission request result
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