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

import libby.pashinsky.chaquopy.databinding.ActivityFunctionsBinding;

public class Functions extends AppCompatActivity {

    private ActivityFunctionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the binding and set the content view
        setupUI();

        // Initialize Python
        initializePython();

        // Set up button listeners
        setupButtonListeners();
    }

    /**
     * Sets up the UI components for the activity including window insets
     */
    private void setupUI() {
        EdgeToEdge.enable(this);
        binding = ActivityFunctionsBinding.inflate(getLayoutInflater());
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
            String textToSpeak = binding.functionsExplanation.getText().toString();
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
        binding.goToFunctionsPracticeButton.setOnClickListener(v -> {
            // Stop the TextToSpeechService before navigating
            TextToSpeechService.stopSpeaking(this);
            // Navigate to the FunctionsPractice fragment
            navigateToFunctionsPractice(new FunctionsPractice());
        });
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
     * Method to navigate to the FunctionsPractice fragment
     * @param fragment fragment to navigate to FunctionsPractice
     */
    private void navigateToFunctionsPractice(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_functions_practice, fragment);
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
        } else if (id == R.id.SignOut) {
            TextToSpeechService.stopSpeaking(this);
            Toast.makeText(this, "Signing Out", Toast.LENGTH_SHORT).show();

            // Clear the saved last activity, so it doesn't mess with checkAndRedirectToLastActivity(); function in HomePage
            getSharedPreferences(HomePage.PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .remove(HomePage.LAST_ACTIVITY)
                    .apply();

            Intent intentHomePage = new Intent(this, HomePage.class);
            // Clear the task stack and start a new one
            intentHomePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentHomePage);
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
        // Update last activity when to Functions it becomes visible
        HomePage.saveLastActivity(this, this.getClass().getName());
    }
}