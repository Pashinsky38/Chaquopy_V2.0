package libby.pashinsky.chaquopy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import libby.pashinsky.chaquopy.databinding.FragmentConditionalsStatementsPracticeBinding;

/**
 * A Fragment that allows users to practice conditional statements in Python using the Chaquopy library.
 * It provides an interface to input Python code for two different questions, execute them, and view the output.
 */
public class ConditionalsStatementsPractice extends Fragment {

    private FragmentConditionalsStatementsPracticeBinding binding;
    private boolean isQuestion1Correct = false;
    private boolean isQuestion2Correct = false;

    /**
     * Required empty public constructor for the ConditionalsStatementsPractice.
     */
    public ConditionalsStatementsPractice() {
        // Required empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentConditionalsStatementsPracticeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * Initializes the Python environment and sets up the click listeners for the buttons.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Python
        Context context = requireContext().getApplicationContext();
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }

        // Initially hide the "Go to Loops" button
        binding.goToLoopsButton.setVisibility(View.GONE);

        /*
          Sets a click listener on the runCodeButton1.
          When clicked, it calls the {@link #runPythonCode(int)} method to execute the Python code for question 1.
         */
        binding.runCodeButton1.setOnClickListener(v -> runPythonCode(1));

        /*
          Sets a click listener on the runCodeButton2.
          When clicked, it calls the {@link #runPythonCode(int)} method to execute the Python code for question 2.
         */
        binding.runCodeButton2.setOnClickListener(v -> runPythonCode(2));

        // Set a click listener for the "Go to Loops" button
        binding.goToLoopsButton.setOnClickListener(v -> goToLoops());
    }

    /**
     * Executes the Python code entered by the user in the code editor.
     * It retrieves the code, runs it using Chaquopy, and displays the result in the output TextView.
     *
     * @param questionNumber The number of the question (1 or 2) to determine which code editor to use.
     */
    private void runPythonCode(int questionNumber) {
        // Get Python code from EditText using View Binding
        String pythonCode = "";
        if (questionNumber == 1) {
            pythonCode = binding.codeEditor1.getText().toString();
        } else if (questionNumber == 2) {
            pythonCode = binding.codeEditor2.getText().toString();
        }

        // Check if the code uses if, elif, else for the relevant questions
        boolean usesConditionals = checkConditionalsUsage(questionNumber, pythonCode);

        // Execute Python code using Chaquopy
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("pythonRunner"); // "pythonRunner" is the Python file name
        PyObject result = pyObject.callAttr("main", pythonCode); // Call a function named "main" in the Python file
        String output = result.toString();

        // Check if the answer is correct
        boolean isCorrect = checkAnswer(questionNumber, output);

        // Update the correctness flags
        if (questionNumber == 1) {
            isQuestion1Correct = isCorrect;
        } else if (questionNumber == 2) {
            isQuestion2Correct = isCorrect;
        }

        // Display the result in the correct TextView using View Binding
        String feedback = "";
        if (!usesConditionals) {
            feedback = "You must use if, elif, and else statements for this question.\n";
        }
        if (questionNumber == 1) {
            binding.outputText1.setText(feedback + output + "\nCorrect: " + isCorrect);
        } else if (questionNumber == 2) {
            binding.outputText2.setText(feedback + output + "\nCorrect: " + isCorrect);
        }

        // Check if both questions are correct and show the button if so
        if (isQuestion1Correct && isQuestion2Correct) {
            binding.goToLoopsButton.setVisibility(View.VISIBLE);
        } else {
            binding.goToLoopsButton.setVisibility(View.GONE);
        }
    }

    /**
     * Checks if the user's Python code uses if, elif, and else statements for the given question.
     *
     * @param questionNumber The number of the question (1 or 2).
     * @param pythonCode     The user's Python code.
     * @return True if the code uses if, elif, and else, false otherwise.
     */
    private boolean checkConditionalsUsage(int questionNumber, String pythonCode) {
        if (questionNumber == 1 || questionNumber == 2) {
            return pythonCode.contains("if") && (pythonCode.contains("elif") || pythonCode.contains("else"));
        }
        return true; // No need to check for other questions
    }

    /**
     * Checks if the user's Python code produced the correct output for the given question.
     *
     * @param questionNumber The number of the question (1 or 2).
     * @param output         The output produced by the user's Python code.
     * @return True if the output is correct, false otherwise.
     */
    private boolean checkAnswer(int questionNumber, String output) {
        switch (questionNumber) {
            case 1:
                // Question 1: Check if a number is even or odd
                // We'll test with a few different inputs to make sure the logic is correct
                return output.contains("Even") || output.contains("Odd");
            case 2:
                // Question 2: Compare two numbers
                // We'll test with a few different inputs to make sure the logic is correct
                return output.contains("The first number is greater") ||
                        output.contains("The second number is greater") ||
                        output.contains("Both numbers are equal");
            default:
                return false;
        }
    }

    /**
     * Navigates to the Loops activity.
     */
    private void goToLoops() {
        Intent intent = new Intent(requireActivity(), Loops.class);
        startActivity(intent);
    }
}