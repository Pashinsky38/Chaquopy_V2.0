package libby.pashinsky.chaquopy;

import android.content.Context;
import android.content.Intent;
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

import libby.pashinsky.chaquopy.databinding.FragmentConditionalsStatementsPracticeBinding;

/**
 * A Fragment that allows users to practice conditional statements in Python using the Chaquopy library.
 * It provides an interface to input Python code for two different questions, execute them, and view the output.
 */
public class ConditionalsStatementsPractice extends Fragment {

    private FragmentConditionalsStatementsPracticeBinding binding;
    private boolean isQuestion1Correct = false;
    private boolean isQuestion2Correct = false;
    private CountDownTimer timer;
    private int incorrectAttempts = 0;
    private static final int MAX_INCORRECT_ATTEMPTS = 3;
    private static final long TIMER_DURATION = 3 * 60 * 1000; // 3 minutes in milliseconds

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

        // Initially hide the "Go to Loops" and "Show Solution" buttons
        binding.goToLoopsButton.setVisibility(View.GONE);
        binding.showSolutionButton.setVisibility(View.GONE);

        // Initially hide the solution TextViews
        binding.solutionText1.setVisibility(View.GONE);
        binding.solutionText2.setVisibility(View.GONE);

        // Set up the timer
        startTimer();

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

        // Set a click listener for the "Show Solution" button
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

        // If the answer is incorrect, increment the incorrect attempts counter
        if (!isCorrect) {
            incorrectAttempts++;
            // If the user has reached the maximum incorrect attempts, show the solution button
            if (incorrectAttempts >= MAX_INCORRECT_ATTEMPTS) {
                binding.showSolutionButton.setVisibility(View.VISIBLE);
            }
        }

        // Check if both questions are correct and show the button if so
        if (isQuestion1Correct && isQuestion2Correct) {
            binding.goToLoopsButton.setVisibility(View.VISIBLE);
            // Cancel the timer since the user has completed both questions correctly
            cancelTimer();
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
     * Shows the solution for both questions in dedicated TextViews.
     */
    private void showSolutions() {
        // Solution text for Question 1
        String solution1 = "# Solution for Question 1\n" +
                "num = 5  # You can change this value to test different numbers\n" +
                "if num % 2 == 0:\n" +
                "    print(\"Even\")\n" +
                "else:\n" +
                "    print(\"Odd\")";

        // Solution text for Question 2
        String solution2 = "# Solution for Question 2\n" +
                "num1 = 10  # First number for comparison\n" +
                "num2 = 5   # Second number for comparison\n" +
                "if num1 > num2:\n" +
                "    print(\"The first number is greater\")\n" +
                "elif num2 > num1:\n" +
                "    print(\"The second number is greater\")\n" +
                "else:\n" +
                "    print(\"Both numbers are equal\")";

        // Display solutions in dedicated TextViews
        binding.solutionText1.setText("Solution:\n" + solution1);
        binding.solutionText1.setVisibility(View.VISIBLE);

        binding.solutionText2.setText("Solution:\n" + solution2);
        binding.solutionText2.setVisibility(View.VISIBLE);

        // Show a toast message indicating solutions are shown
        Toast.makeText(requireContext(), "Solutions have been displayed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigates to the Loops activity.
     */
    private void goToLoops() {
        Intent intent = new Intent(requireActivity(), Loops.class);
        startActivity(intent);
    }

    /**
     * Called when the fragment is no longer in use.
     * Cancels the timer to prevent memory leaks.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }
}