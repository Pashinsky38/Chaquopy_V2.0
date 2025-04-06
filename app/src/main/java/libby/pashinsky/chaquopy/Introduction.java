package libby.pashinsky.chaquopy;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import libby.pashinsky.chaquopy.databinding.ActivityIntroductionBinding;

/**
 * The introduction activity of the application.
 * Provides an introductory screen with a description and a button to proceed to the basics fragment.
 */
public class Introduction extends AppCompatActivity {

    private ActivityIntroductionBinding binding;

    /**
     * Called when the activity is starting.
     * Initializes the UI, sets up edge-to-edge display, and handles button clicks.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityIntroductionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set click listener for the TTS button
        binding.ttsButton.setOnClickListener(v -> {
            String textToSpeak = binding.textView2.getText().toString();
            TextToSpeechService.startService(this, textToSpeak);
        });

        // Set click listener for the "Next" button
        binding.nextButton.setOnClickListener(v -> {
            // Stop Text-to-Speech before navigating
            TextToSpeechService.stopSpeaking(this);
            navigateToBasicsFragment();
        });
    }

    /**
     * Navigates to the BasicsFragment.
     * Replaces the current fragment with the BasicsFragment and adds it to the back stack.
     * Hides the introduction elements (title, description, TTS button, and "Next" button).
     */
    private void navigateToBasicsFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_basics, new BasicsFragment());
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.IntroductionToPython) {
            Toast.makeText(this, "Introduction to Python", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.ConditionalStatements) {
            Toast.makeText(this, "Conditional Statements", Toast.LENGTH_SHORT).show();
            Intent intentConditionals = new Intent(this, ConditionalsStatements.class);
            startActivity(intentConditionals);
            return true;
        } else if (id == R.id.Loops) {
            Toast.makeText(this, "Loops", Toast.LENGTH_SHORT).show();
            Intent intentLoops = new Intent(this, Loops.class);
            startActivity(intentLoops);
            return true;
        } else if (id == R.id.Functions) {
            Toast.makeText(this, "Functions", Toast.LENGTH_SHORT).show();
            Intent intentFunctions = new Intent(this, Functions.class);
            startActivity(intentFunctions);
            return true;
        } else if (id == R.id.menuCloseApp) {
            finishAffinity();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}