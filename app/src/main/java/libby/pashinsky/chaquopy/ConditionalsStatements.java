package libby.pashinsky.chaquopy;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

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
            // Navigate to the ConditionalsStatementsPractice fragment
            navigateToConditionalsStatementsPractice(new ConditionalsStatementsPractice());
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

    private void navigateToConditionalsStatementsPractice(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_conditionals_practice, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}