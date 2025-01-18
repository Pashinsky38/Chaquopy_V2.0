package libby.pashinsky.chaquopy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        TextView descriptionTextView = binding.textView2;
        Button ttsButton = binding.ttsButton;
        Button nextButton = binding.introductionToBasics;

        // Set click listener for the TTS button
        ttsButton.setOnClickListener(v -> {
            String textToSpeak = descriptionTextView.getText().toString();
            TextToSpeechService.startService(this, textToSpeak);
        });

        // Set click listener for the "Next" button using NavigationHelper
        nextButton.setOnClickListener(v -> {
            // Stop Text-to-Speech before navigating
            TextToSpeechService.stopSpeaking(this);
            navigateToBasicsFragment();
        });
    }

    /**
     * Perform any final cleanup before an activity is destroyed.
     * This can happen either because the activity is finishing (someone called {@link #finish} on it, or because the system is temporarily destroying this instance of the activity to save space.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
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

        binding.textView.setVisibility(View.GONE);
        binding.textView2.setVisibility(View.GONE);
        binding.ttsButton.setVisibility(View.GONE);
        binding.introductionToBasics.setVisibility(View.GONE);
    }
}