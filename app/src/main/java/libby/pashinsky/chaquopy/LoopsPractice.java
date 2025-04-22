package libby.pashinsky.chaquopy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
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

import libby.pashinsky.chaquopy.databinding.FragmentLoopsPracticeBinding;

/**
 * A Fragment that allows users to practice loops in Python using the Chaquopy library.
 * It provides an interface to input Python code for three different questions about loops,
 * execute them, and view the output.
 */
public class LoopsPractice extends Fragment {

    private FragmentLoopsPracticeBinding binding;
    private boolean isQuestion1Correct = false;
    private boolean isQuestion2Correct = false;
    private boolean isQuestion3Correct = false;
    private CountDownTimer timer;
    private int incorrectAttempts = 0;
    private static final int MAX_INCORRECT_ATTEMPTS = 3;
    private static final long TIMER_DURATION = 3 * 60 * 1000; // 3 minutes in milliseconds
    private static final int QUESTIONS_IN_QUIZ = 3; // Total number of questions in this quiz

    private HelperDB dbHelper;
    private String currentUserEmail;
    private boolean quizCompleted = false; // Flag to track if quiz was already completed

    /**
     * Required empty public constructor for the LoopsPractice.
     */
    public LoopsPractice() {
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
        binding = FragmentLoopsPracticeBinding.inflate(inflater, container, false);
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

        initializePython();
        setupDatabase();
        hideInitialElements();
        startTimer();
        setupButtonListeners();
    }

    /**
     * Initializes the Python environment.
     */
    private void initializePython() {
        // Initialize Python
        Context context = requireContext().getApplicationContext();
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }
    }

    /**
     * Sets up the database helper and retrieves current user email.
     */
    private void setupDatabase() {
        dbHelper = new HelperDB(getContext());

        // Simply get the first user from the database
        currentUserEmail = dbHelper.getFirstUserEmail();

        // Save the current user email in shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("current_user_email", currentUserEmail);
        editor.apply();
    }

    /**
     * Hides elements that should not be initially visible.
     */
    private void hideInitialElements() {
        // Initially hide the "Go to Functions" and "Show Solution" buttons
        binding.goToFunctionsButton.setVisibility(View.GONE);
        binding.showSolutionButton.setVisibility(View.GONE);

        // Initially hide the solution TextViews
        binding.solutionText1.setVisibility(View.GONE);
        binding.solutionText2.setVisibility(View.GONE);
        binding.solutionText3.setVisibility(View.GONE);
    }

    /**
     * Sets up all button click listeners.
     */
    private void setupButtonListeners() {
        // Set up run code buttons
        binding.runCodeButton1.setOnClickListener(v -> runPythonCode(1));
        binding.runCodeButton2.setOnClickListener(v -> runPythonCode(2));
        binding.runCodeButton3.setOnClickListener(v -> runPythonCode(3));

        // Set up "Go to Functions" button
        binding.goToFunctionsButton.setOnClickListener(v -> goToFunctions());

        // Set up "Show Solution" button
        binding.showSolutionButton.setOnClickListener(v -> showSolutions());
    }

    /**
     * Starts a 3-minute countdown timer. When the timer finishes, the "Show Solution" button becomes visible.
     */
    private void startTimer() {
        timer = new CountDownTimer(TIMER_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Does nothing on each tick
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
     * @param questionNumber The number of the question (1, 2, or 3) to determine which code editor to use.
     */
    private void runPythonCode(int questionNumber) {
        // Increment total tries every time a Run Code button is clicked
        if (!quizCompleted) {
            dbHelper.updateTotalTries(currentUserEmail, 1);
        }

        // Get Python code from EditText using View Binding
        String pythonCode = "";
        switch (questionNumber) {
            case 1:
                pythonCode = binding.codeEditor1.getText().toString();
                break;
            case 2:
                pythonCode = binding.codeEditor2.getText().toString();
                break;
            case 3:
                pythonCode = binding.codeEditor3.getText().toString();
                break;
        }

        // Check if the code uses loops for the relevant questions
        boolean usesLoops = checkLoopsUsage(questionNumber, pythonCode);

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
            case 3:
                isQuestion3Correct = isCorrect;
                break;
        }

        // Display the result in the correct TextView using View Binding
        String feedback = "";
        if (!usesLoops) {
            if (questionNumber == 1) {
                feedback = "You must use a for loop for this question.\n";
            } else if (questionNumber == 3) {
                feedback = "You must use a while loop for this question.\n";
            } else if (questionNumber == 2) {
                feedback = "You must use a for loop for this question.\n";
            }
        }

        switch (questionNumber) {
            case 1:
                binding.outputText1.setText(feedback + output + "\nCorrect: " + isCorrect);
                break;
            case 2:
                binding.outputText2.setText(feedback + output + "\nCorrect: " + isCorrect);
                break;
            case 3:
                binding.outputText3.setText(feedback + output + "\nCorrect: " + isCorrect);
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
        if (isQuestion1Correct && isQuestion2Correct && isQuestion3Correct) {
            handleAllQuestionsCorrect();
        }
    }

    /**
     * Handles the case when all questions are answered correctly.
     */
    private void handleAllQuestionsCorrect() {
        binding.goToFunctionsButton.setVisibility(View.VISIBLE);
        // Cancel the timer since the user has completed all questions correctly
        cancelTimer();

        // Update database with correct answers if not already completed
        if (!quizCompleted) {
            // Mark quiz as completed to prevent multiple updates
            quizCompleted = true;

            // Update correct answers (tries are already updated in runPythonCode method)
            dbHelper.updateCorrectAnswers(currentUserEmail, QUESTIONS_IN_QUIZ);

            // Get updated statistics
            int totalCorrectAnswers = dbHelper.getCorrectAnswers(currentUserEmail);
            int totalTries = dbHelper.getTotalTries(currentUserEmail);

            // Show a brief toast first
            Toast.makeText(getContext(), "Marvellous!", Toast.LENGTH_SHORT).show();

            // Show statistics in an AlertDialog after a short delay
            new android.os.Handler().postDelayed(() -> {
                showStatisticsDialog(totalCorrectAnswers, totalTries);
            }, 1000);
        } else {
            // Just show the basic toast if already completed
            Toast.makeText(requireContext(), "Marvellous!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows an AlertDialog with the user's statistics.
     *
     * @param totalCorrectAnswers Total number of correct answers across all quizzes
     * @param totalTries Total number of quiz attempts
     */
    private void showStatisticsDialog(int totalCorrectAnswers, int totalTries) {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Your Statistics");
        builder.setMessage("Total Correct Answers: " + totalCorrectAnswers + "\n\n" +
                "Total Tries: " + totalTries);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.setCancelable(true);

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Checks if the user's Python code uses loops for the given question.
     *
     * @param questionNumber The number of the question (1, 2, or 3).
     * @param pythonCode     The user's Python code.
     * @return True if the code uses loops appropriately, false otherwise.
     */
    private boolean checkLoopsUsage(int questionNumber, String pythonCode) {
        switch (questionNumber) {
            case 1:
                return pythonCode.contains("for") && pythonCode.contains("range");
            case 2:
                return pythonCode.contains("for") && pythonCode.contains("in");
            case 3:
                return pythonCode.contains("while");
            default:
                return true; // No need to check for other questions
        }
    }

    /**
     * Checks if the user's Python code produced the correct output for the given question.
     *
     * @param questionNumber The number of the question (1, 2, or 3).
     * @param output         The output produced by the user's Python code.
     * @return True if the output is correct, false otherwise.
     */
    private boolean checkAnswer(int questionNumber, String output) {
        output = output.toLowerCase(); // Convert to lowercase for case-insensitive comparison

        switch (questionNumber) {
            case 1:
                // Question 1: Print even numbers from 1 to 20 and check if greater than or less than 10
                int evenNumbersFound = 0;

                // Check for even numbers between 2 and 20
                for (int i = 2; i <= 20; i += 2) {
                    if (output.contains(String.valueOf(i))) {
                        evenNumbersFound++;
                    }
                }

                // We should find at least some even numbers (not necessarily all of them)
                boolean hasAllEvenNumbers = evenNumbersFound >= 5; // At least 5 even numbers should be found

                // Check for comparison text (allow various phrasings)
                boolean hasGreaterThanText = output.contains("greater than 10") ||
                        output.contains("> 10") ||
                        output.contains("above 10");

                boolean hasLessThanText = output.contains("less than 10") ||
                        output.contains("< 10") ||
                        output.contains("below 10") ||
                        output.contains("equal to 10");

                return hasAllEvenNumbers && hasGreaterThanText && hasLessThanText;

            case 2:
                // Question 2: Create a new list with numbers > 10 from [12, 5, 8, 21, 13, 9, 31, 2]
                // Check if output contains both original and filtered list
                boolean hasOriginalList = output.contains("[12, 5, 8, 21, 13, 9, 31, 2]") ||
                        output.contains("[12,5,8,21,13,9,31,2]");

                boolean hasFilteredList = output.contains("[12, 21, 13, 31]") ||
                        output.contains("[12,21,13,31]") ||
                        output.contains("[12, 13, 21, 31]") ||
                        output.contains("[12,13,21,31]") ||
                        // Handle other possible formats
                        output.contains("12") &&
                                output.contains("21") &&
                                output.contains("13") &&
                                output.contains("31") &&
                                output.contains("greater than 10");

                return hasOriginalList && hasFilteredList;

            case 3:
                // Question 3: FizzBuzz from 1 to 15
                return output.contains("fizz") && output.contains("buzz") && output.contains("fizzbuzz");

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
                "for i in range(1, 21):\n" +
                "    if i % 2 == 0:  # Check if even\n" +
                "        if i > 10:\n" +
                "            print(str(i) + \" is greater than 10\")\n" +
                "        else:\n" +
                "            print(str(i) + \" is less than or equal to 10\")";

        // Solution text for Question 2
        String solution2 = "# Solution for Question 2\n" +
                "numbers = [12, 5, 8, 21, 13, 9, 31, 2]\n" +
                "greater_than_10 = []\n" +
                "\n" +
                "for num in numbers:\n" +
                "    if num > 10:\n" +
                "        greater_than_10.append(num)\n" +
                "\n" +
                "print(\"Original list:\", numbers)\n" +
                "print(\"Numbers greater than 10:\", greater_than_10)";

        // Solution text for Question 3
        String solution3 = "# Solution for Question 3\n" +
                "num = 1\n" +
                "while num <= 15:\n" +
                "    if num % 3 == 0 and num % 5 == 0:\n" +
                "        print(\"FizzBuzz\")\n" +
                "    elif num % 3 == 0:\n" +
                "        print(\"Fizz\")\n" +
                "    elif num % 5 == 0:\n" +
                "        print(\"Buzz\")\n" +
                "    else:\n" +
                "        print(num)\n" +
                "    num += 1";

        // Display solutions in dedicated TextViews
        binding.solutionText1.setText("Solution:\n" + solution1);
        binding.solutionText1.setVisibility(View.VISIBLE);

        binding.solutionText2.setText("Solution:\n" + solution2);
        binding.solutionText2.setVisibility(View.VISIBLE);

        binding.solutionText3.setText("Solution:\n" + solution3);
        binding.solutionText3.setVisibility(View.VISIBLE);

        // Show a toast message indicating solutions are shown
        Toast.makeText(requireContext(), "Solutions have been displayed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigates to the Functions activity.
     */
    private void goToFunctions() {
        Intent intent = new Intent(requireActivity(), Functions.class);
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
        binding = null;
    }
}