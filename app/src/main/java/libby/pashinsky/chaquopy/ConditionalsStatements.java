package libby.pashinsky.chaquopy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import libby.pashinsky.chaquopy.databinding.ActivityConditionalsStatementsBinding;

public class ConditionalsStatements extends AppCompatActivity {

    private ActivityConditionalsStatementsBinding binding;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityConditionalsStatementsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Python
        Context context = getApplicationContext();
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.scrollView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set click listener for the Run button
        binding.runCodeButton.setOnClickListener(v -> runPythonCode());

        // Get the FragmentManager
        fragmentManager = getSupportFragmentManager();

        // Set click listener for the Go to Practice button
        Button goToPracticeButton = binding.goToConditionalsPracticeButton;
        goToPracticeButton.setOnClickListener(v -> {
            // Stop the TextToSpeechService before navigating
            TextToSpeechService.stopSpeaking(this);
            // Navigate to the ConditionalsStatementsPractice fragment
            navigateToConditionalsStatementsPractice(new ConditionalsStatementsPractice());
        });

        // Set click listener for the Text-to-Speech button
        binding.textToSpeechButton.setOnClickListener(v -> {
            // Start the TextToSpeechService to speak the explanation text
            String textToSpeak = binding.conditionalStatementsExplanation.getText().toString();
            TextToSpeechService.startService(this, textToSpeak);
        });
    }

    /**
     * Executes the Python code entered in the code editor.
     * Retrieves the code, executes it using Chaquopy, and displays the result in the output TextView.
     */
    private void runPythonCode() {
        // Get Python code from EditText using View Binding
        String pythonCode = binding.codeEditor.getText().toString();

        // Execute Python code using Chaquopy
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("pythonRunner"); // "pythonRunner" is the Python file name
        PyObject result = pyObject.callAttr("main", pythonCode); // Call a function named "main" in the Python file

        // Display the result in the TextView using View Binding
        binding.outputText.setText(result.toString());
    }

    // Method to navigate to the ConditionalsStatementsPractice fragment
    private void navigateToConditionalsStatementsPractice(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_conditionals_practice, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the TextToSpeechService when the activity is destroyed
        TextToSpeechService.stopSpeaking(this);
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
            return true;
        } else if (id == R.id.Loops) {
            TextToSpeechService.stopSpeaking(this);
            Toast.makeText(this, "Loops", Toast.LENGTH_SHORT).show();
            Intent intentLoops = new Intent(this, Loops.class);
            startActivity(intentLoops);
            return true;
        } else if (id == R.id.Functions) {
            TextToSpeechService.stopSpeaking(this);
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