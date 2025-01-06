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

public class Introduction extends AppCompatActivity {

    private ActivityIntroductionBinding binding;
    private TextToSpeechHelper textToSpeechHelper;

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

        // Initialize TextToSpeechHelper
        textToSpeechHelper = new TextToSpeechHelper(this);

        // Set click listener for the TTS button
        ttsButton.setOnClickListener(v -> {
            String textToSpeak = descriptionTextView.getText().toString();
            textToSpeechHelper.speak(textToSpeak);
        });

        // Set click listener for the "Next" button using NavigationHelper
        nextButton.setOnClickListener(v -> navigateToBasicsFragment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown TextToSpeech when the activity is destroyed
        if (textToSpeechHelper != null) {
            textToSpeechHelper.shutdown();
        }
    }

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