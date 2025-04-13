package libby.pashinsky.chaquopy;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import libby.pashinsky.chaquopy.databinding.FragmentFunctionsPracticeBinding;

/**
 * A Fragment that allows users to practice functions in Python using the Chaquopy library.
 * It provides an interface to input Python code for two different questions about functions,
 * execute them, and view the output.
 */
public class FunctionsPractice extends Fragment {

    private FragmentFunctionsPracticeBinding binding;
    private boolean isQuestion1Correct = false;
    private boolean isQuestion2Correct = false;
    private CountDownTimer timer;
    private int incorrectAttempts = 0;
    private static final int MAX_INCORRECT_ATTEMPTS = 3;
    private static final long TIMER_DURATION = 3 * 60 * 1000; // 3 minutes in milliseconds

    /**
     * Required empty public constructor for the FunctionsPractice.
     */
    public FunctionsPractice() {
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
        binding = FragmentFunctionsPracticeBinding.inflate(inflater, container, false);
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
        initializePython();

        // Setup UI components
        setupUI();

        // Set up the timer
        startTimer();

        // Set up button listeners
        setupButtonListeners();
    }

    /**
     * Initializes Python if it hasn't been started yet
     */
    private void initializePython() {
        Context context = requireContext().getApplicationContext();
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }
    }

    /**
     * Sets up the UI components for the fragment
     */
    private void setupUI() {
        // Initially hide the "Show Solution" button
        binding.showSolutionButton.setVisibility(View.GONE);

        // Initially hide the solution TextViews
        binding.solutionText1.setVisibility(View.GONE);
        binding.solutionText2.setVisibility(View.GONE);
    }

    /**
     * Sets up all button click listeners
     */
    private void setupButtonListeners() {
        setupRunCodeButton1Listener();
        setupRunCodeButton2Listener();
        setupShowSolutionButtonListener();
    }

    /**
     * Sets up the Run Code Button 1 listener
     */
    private void setupRunCodeButton1Listener() {
        binding.runCodeButton1.setOnClickListener(v -> runPythonCode(1));
    }

    /**
     * Sets up the Run Code Button 2 listener
     */
    private void setupRunCodeButton2Listener() {
        binding.runCodeButton2.setOnClickListener(v -> runPythonCode(2));
    }

    /**
     * Sets up the Show Solution Button listener
     */
    private void setupShowSolutionButtonListener() {
        binding.showSolutionButton.setOnClickListener(v -> showSolutions());
    }

    /**
     * Starts a 3-minute countdown timer. When the timer finishes, the "Show Solution" button becomes visible.
     */
    private void startTimer() {
        timer = new CountDownTimer(TIMER_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // We don't update UI since we want the timer to be hidden
            }

            @Override
            public void onFinish() {
                // Show the "Show Solution" button when the timer finishes
                if (binding != null && isAdded()) {  // Check if fragment is still attached
                    binding.showSolutionButton.setVisibility(View.VISIBLE);
                }
            }
        }.start();
    }

    /**
     * Cancels the timer if it's running.
     */
    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
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
        switch (questionNumber) {
            case 1:
                pythonCode = binding.codeEditor1.getText().toString();
                break;
            case 2:
                pythonCode = binding.codeEditor2.getText().toString();
                break;
        }

        // Check if the code uses functions for the relevant questions
        boolean usesFunctions = checkFunctionsUsage(questionNumber, pythonCode);

        // Execute Python code using Chaquopy
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("pythonRunner"); // "pythonRunner" is the Python file name
        PyObject result = pyObject.callAttr("main", pythonCode); // Call a function named "main" in the Python file
        String output = result.toString();

        // Check if the answer is correct
        boolean isCorrect = checkAnswer(questionNumber, output);

        // Update the correctness flags
        switch (questionNumber) {
            case 1:
                isQuestion1Correct = isCorrect;
                break;
            case 2:
                isQuestion2Correct = isCorrect;
                break;
        }

        // Display the result in the correct TextView using View Binding
        String feedback = "";
        if (!usesFunctions) {
            feedback = "You must define a function for this question.\n";
        }

        switch (questionNumber) {
            case 1:
                binding.outputText1.setText(feedback + output + "\nCorrect: " + isCorrect);
                break;
            case 2:
                binding.outputText2.setText(feedback + output + "\nCorrect: " + isCorrect);
                break;
        }

        // If the answer is incorrect, increment the incorrect attempts counter
        if (!isCorrect) {
            incorrectAttempts++;
            // If the user has reached the maximum incorrect attempts, show the solution button
            if (incorrectAttempts >= MAX_INCORRECT_ATTEMPTS) {
                binding.showSolutionButton.setVisibility(View.VISIBLE);
            }
        }

        // Check if all questions are correct and show the button if so
        if (isQuestion1Correct && isQuestion2Correct) {
            // Cancel the timer since the user has completed all questions correctly
            cancelTimer();
        }
    }

    /**
     * Checks if the user's Python code uses functions for the given question.
     *
     * @param questionNumber The number of the question (1 or 2).
     * @param pythonCode     The user's Python code.
     * @return True if the code uses functions appropriately, false otherwise.
     */
    private boolean checkFunctionsUsage(int questionNumber, String pythonCode) {
        switch (questionNumber) {
            case 1:
                return pythonCode.contains("def print_pattern") && pythonCode.contains("(");
            case 2:
                return pythonCode.contains("def calculate_grades") && pythonCode.contains("return");
            default:
                return true; // No need to check for other questions
        }
    }

    /**
     * Checks if the user's Python code produced the correct output for the given question.
     *
     * @param questionNumber The number of the question (1 or 2).
     * @param output         The output produced by the user's Python code.
     * @return True if the output is correct, false otherwise.
     */
    private boolean checkAnswer(int questionNumber, String output) {
        // Check for errors first
        if (output.startsWith("Error:")) {
            return false;
        }

        switch (questionNumber) {
            case 1:
                // For the pattern question, we need to check:
                // 1. If there's a function definition for print_pattern
                // 2. If the function is called with parameter 5
                // 3. If the output shows the correct pattern

                // Check for single asterisks in sequence (ignore exact formatting)
                boolean hasAsteriskPattern = true;
                String[] patterns = {"*", "**", "***", "****", "*****"};
                for (String pattern : patterns) {
                    if (!output.contains(pattern)) {
                        hasAsteriskPattern = false;
                        break;
                    }
                }

                // Return true if all checks pass
                return hasAsteriskPattern;

            case 2:
                // For the grades question, we need to check:
                // 1. If the output contains the input scores
                // 2. If the output contains the correct grades
                // 3. If the function calculate_grades is used

                // Check for grades (A, B, C, D, F)
                // We're being flexible with the exact format
                boolean hasAllGrades = output.contains("A") &&
                        output.contains("B") &&
                        output.contains("C") &&
                        output.contains("D") &&
                        output.contains("F");

                // Check for input scores somewhere in the output
                boolean hasScores = output.contains("95") &&
                        output.contains("82") &&
                        output.contains("74") &&
                        output.contains("65") &&
                        output.contains("48");

                // Return true if all checks pass
                return hasAllGrades && hasScores;

            default:
                return false;
        }
    }

    /**
     * Shows the solution for all questions in dedicated TextViews.
     */
    private void showSolutions() {
        // Solution text for Question 1
        String solution1 = "# Solution for Question 1\n" +
                "def print_pattern(n):\n" +
                "    for i in range(1, n + 1):\n" +
                "        print(\"*\" * i)\n" +
                "\n" +
                "# Test the function\n" +
                "print_pattern(5)";

        // Solution text for Question 2
        String solution2 = "# Solution for Question 2\n" +
                "def calculate_grades(scores):\n" +
                "    results = []\n" +
                "    \n" +
                "    for score in scores:\n" +
                "        if score >= 90:\n" +
                "            results.append(\"A\")\n" +
                "        elif score >= 80:\n" +
                "            results.append(\"B\")\n" +
                "        elif score >= 70:\n" +
                "            results.append(\"C\")\n" +
                "        elif score >= 60:\n" +
                "            results.append(\"D\")\n" +
                "        else:\n" +
                "            results.append(\"F\")\n" +
                "    \n" +
                "    return results\n" +
                "\n" +
                "# Test the function\n" +
                "test_scores = [95, 82, 74, 65, 48]\n" +
                "print(\"Scores:\", test_scores)\n" +
                "print(\"Grades:\", calculate_grades(test_scores))";

        // Display solutions in dedicated TextViews
        binding.solutionText1.setText("Solution:\n" + solution1);
        binding.solutionText1.setVisibility(View.VISIBLE);

        binding.solutionText2.setText("Solution:\n" + solution2);
        binding.solutionText2.setVisibility(View.VISIBLE);

        // Show a toast message indicating solutions are shown
        Toast.makeText(requireContext(), "Solutions have been displayed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the fragment is no longer in use.
     * Cancels the timer to prevent memory leaks.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTimer();
        binding = null;
    }
}