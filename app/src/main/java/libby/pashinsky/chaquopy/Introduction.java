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

        // Initialize the binding and set the content view
        setupUI();

        // Set up button listeners
        setupButtonListeners();

        // Save the current activity as the last opened activity
        saveLastActivity(this.getClass().getName());
    }

    /**
     * Sets up the UI components for the activity including window insets
     */
    private void setupUI() {
        EdgeToEdge.enable(this);
        binding = ActivityIntroductionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup window insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Sets up all button click listeners
     */
    private void setupButtonListeners() {
        setupTextToSpeechButtonListener();
        setupNextButtonListener();
    }

    /**
     * Sets up the Text-to-Speech button click listener
     */
    private void setupTextToSpeechButtonListener() {
        binding.ttsButton.setOnClickListener(v -> {
            String textToSpeak = binding.textView2.getText().toString();
            TextToSpeechService.startService(this, textToSpeak);
        });
    }

    /**
     * Sets up the Next button click listener
     */
    private void setupNextButtonListener() {
        binding.nextButton.setOnClickListener(v -> {
            // Stop Text-to-Speech before navigating
            TextToSpeechService.stopSpeaking(this);
            navigateToBasicsFragment();
        });
    }

    /**
     * Saves the given activity class name as the last opened activity
     * @param activityClassName activity class name
     */
    private void saveLastActivity(String activityClassName) {
        HomePage.saveLastActivity(this, activityClassName);
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
            TextToSpeechService.stopSpeaking(this);
            Toast.makeText(this, "Introduction to Python", Toast.LENGTH_SHORT).show();
            Intent intentIntroduction = new Intent(this, Introduction.class);
            startActivity(intentIntroduction);
            return true;
        } else if (id == R.id.ConditionalStatements) {
            TextToSpeechService.stopSpeaking(this);
            Toast.makeText(this, "Conditional Statements", Toast.LENGTH_SHORT).show();
            Intent intentConditionals = new Intent(this, ConditionalsStatements.class);
            startActivity(intentConditionals);
            // Save the activity we're navigating to
            saveLastActivity(ConditionalsStatements.class.getName());
            return true;
        } else if (id == R.id.Loops) {
            TextToSpeechService.stopSpeaking(this);
            Toast.makeText(this, "Loops", Toast.LENGTH_SHORT).show();
            Intent intentLoops = new Intent(this, Loops.class);
            startActivity(intentLoops);
            // Save the activity we're navigating to
            saveLastActivity(Loops.class.getName());
            return true;
        } else if (id == R.id.Functions) {
            TextToSpeechService.stopSpeaking(this);
            Toast.makeText(this, "Functions", Toast.LENGTH_SHORT).show();
            Intent intentFunctions = new Intent(this, Functions.class);
            startActivity(intentFunctions);
            // Save the activity we're navigating to
            saveLastActivity(Functions.class.getName());
            return true;
        } else if (id == R.id.menuCloseApp) {
            finishAffinity();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update last activity to Introduction when it becomes visible
        HomePage.saveLastActivity(this, this.getClass().getName());
    }
}