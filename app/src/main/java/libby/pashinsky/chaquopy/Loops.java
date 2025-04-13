package libby.pashinsky.chaquopy;

import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import libby.pashinsky.chaquopy.databinding.ActivityLoopsBinding;

public class Loops extends AppCompatActivity {

    private ActivityLoopsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the binding and set the content view
        setupUI();

        // Initialize Python
        initializePython();

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
        binding = ActivityLoopsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup window insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.scrollView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Initializes Python if it hasn't been started yet
     */
    private void initializePython() {
        Context context = getApplicationContext();
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }
    }

    /**
     * Sets up all button click listeners
     */
    private void setupButtonListeners() {
        setupTextToSpeechButtonListener();
        setupRunCodeButtonListener();
        setupGoToPracticeButtonListener();
    }

    /**
     * Sets up the Text-to-Speech button listener
     */
    private void setupTextToSpeechButtonListener() {
        binding.textToSpeechButton.setOnClickListener(v -> {
            String textToSpeak = binding.loopsExplanation.getText().toString();
            TextToSpeechService.startService(this, textToSpeak);
        });
    }

    /**
     * Sets up the Run Code button listener
     */
    private void setupRunCodeButtonListener() {
        binding.runCodeButton.setOnClickListener(v -> runPythonCode());
    }

    /**
     * Sets up the Go to Practice button listener
     */
    private void setupGoToPracticeButtonListener() {
        binding.goToLoopsPracticeButton.setOnClickListener(v -> {
            // Stop the TextToSpeechService before navigating
            TextToSpeechService.stopSpeaking(this);
            // Navigate to the LoopsPractice fragment
            navigateToLoopsPractice(new LoopsPractice());
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
     * Executes the Python code entered by the user in the code editor.
     * It retrieves the code, runs it using Chaquopy, and displays the result in the output TextView.
     */
    private void runPythonCode() {
        // Get Python code from EditText
        String pythonCode = binding.codeEditor.getText().toString();

        // Execute Python code using Chaquopy
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("pythonRunner"); // "pythonRunner" is the Python file name
        PyObject result = pyObject.callAttr("main", pythonCode); // Call a function named "main" in the Python file
        String output = result.toString();

        // Display the result in the output TextView
        binding.outputText.setText(output);
    }

    /**
     * Method to navigate to the LoopsPractice fragment
     * @param fragment fragment to navigate to LoopsPractice
     */
    private void navigateToLoopsPractice(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_loops_practice, fragment);
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
            // Save the activity we're navigating to
            saveLastActivity(Introduction.class.getName());
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
}