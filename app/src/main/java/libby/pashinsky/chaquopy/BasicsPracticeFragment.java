package libby.pashinsky.chaquopy;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import libby.pashinsky.chaquopy.databinding.FragmentBasicsPracticeBinding;

/**
 * A Fragment that provides a basic practice quiz with three questions.
 * Users can input their answers, check them, and view the solutions.
 * A countdown timer is implemented to show the solution button after a set time.
 */
public class BasicsPracticeFragment extends Fragment {

    private static final long TIMER_DURATION = 30 * 1000; // 30 seconds in milliseconds

    private FragmentBasicsPracticeBinding binding;
    private CountDownTimer countDownTimer;
    private Button showSolutionButton;
    private boolean allAnswersCorrect = false;
    private TextView resultsText;
    private boolean solutionsShown = false;
    private EditText question1Answer;
    private EditText question2Answer;
    private EditText question3Answer;
    private int incorrectAttempts = 0; // Counter for incorrect attempts
    private Button goToConditionalStatementsButton;

    /**
     * Required empty public constructor for the BasicsPracticeFragment.
     */
    public BasicsPracticeFragment() {
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
        binding = FragmentBasicsPracticeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews();
        setupButtonListeners();
        startCountdownTimer();
    }

    /**
     * Initializes all the UI views and sets their initial visibility.
     */
    private void initializeViews() {
        // Initialize views using binding
        resultsText = binding.resultsText;
        showSolutionButton = binding.showSolutionButton;
        question1Answer = binding.question1Answer;
        question2Answer = binding.question2Answer;
        question3Answer = binding.question3Answer;
        goToConditionalStatementsButton = binding.goToConditionalStatementsButton;

        // Initially hide the "Show Solution" button and the "Go to Conditional Statements" button
        showSolutionButton.setVisibility(View.GONE);
        goToConditionalStatementsButton.setVisibility(View.GONE);
    }

    /**
     * Sets up all the button click listeners.
     */
    private void setupButtonListeners() {
        // Set click listener for the check answers button
        binding.checkAnswersButton.setOnClickListener(v -> checkAnswers());

        // Set click listener for the show solution button
        showSolutionButton.setOnClickListener(v -> showSolutions());

        // Set click listener for the "Go to Conditional Statements" button
        goToConditionalStatementsButton.setOnClickListener(v -> navigateToConditionalStatements());
    }

    /**
     * Checks the user's answers against the correct answers and updates the UI accordingly.
     */
    private void checkAnswers() {
        // Get user's answers
        String answer1 = question1Answer.getText().toString().trim();
        String answer2 = question2Answer.getText().toString().trim();
        String answer3 = question3Answer.getText().toString().trim();

        // Check answers
        boolean isCorrect1 = answer1.equals("Michal");
        boolean isCorrect2 = answer2.equals("My age is 25");
        boolean isCorrect3 = answer3.equals("15");

        // Display results
        if (isCorrect1 && isCorrect2 && isCorrect3) {
            handleCorrectAnswers();
        } else {
            handleIncorrectAnswers();
        }
    }

    /**
     * Handles the case when all answers are correct.
     */
    private void handleCorrectAnswers() {
        resultsText.setText(getString(R.string.all_answers_correct));
        allAnswersCorrect = true;

        // Display a toast message when all answers are correct
        Toast.makeText(getContext(), "Great job!", Toast.LENGTH_SHORT).show();

        // Stop the timer if all answers are correct
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Hide the "Show Solution" button if all answers are correct
        showSolutionButton.setVisibility(View.GONE);

        // Disable the EditTexts
        disableEditTexts();

        // Reset incorrect attempts
        incorrectAttempts = 0;

        // Show the "Go to Conditional Statements" button
        goToConditionalStatementsButton.setVisibility(View.VISIBLE);
    }

    /**
     * Handles the case when some answers are incorrect.
     */
    private void handleIncorrectAnswers() {
        resultsText.setText(getString(R.string.some_answers_incorrect));
        incorrectAttempts++; // Increment incorrect attempts

        // Show the "Show Solution" button if there are 3 incorrect attempts and solutions haven't been shown
        if (incorrectAttempts >= 3 && !solutionsShown) {
            showSolutionButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Shows the solutions to all questions.
     */
    private void showSolutions() {
        // Display the solutions
        String solution = "Solutions:\n" +
                "Question 1: Michal\n" +
                "Question 2: My age is 25\n" +
                "Question 3: 15";
        resultsText.setText(solution);

        // Set solutionsShown to true
        solutionsShown = true;

        // Keep the "Show Solution" button visible
        showSolutionButton.setVisibility(View.VISIBLE);
    }

    /**
     * Navigates to the ConditionalsStatements activity.
     */
    private void navigateToConditionalStatements() {
        Intent intent = new Intent(getActivity(), ConditionalsStatements.class);
        startActivity(intent);
    }

    /**
     * Starts a countdown timer that, upon finishing, shows the "Show Solution" button
     * if all answers are not correct and solutions haven't been shown.
     */
    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(TIMER_DURATION, 1000) {
            /**
             * Callback fired on regular interval.
             * @param millisUntilFinished The amount of time until finished.
             */
            public void onTick(long millisUntilFinished) {
                // We don't need to do anything on each tick
            }

            /**
             * Callback fired when the time is up.
             */
            public void onFinish() {
                // Show the "Show Solution" button if all answers are not correct and solutions haven't been shown
                if (!allAnswersCorrect && !solutionsShown) {
                    showSolutionButton.setVisibility(View.VISIBLE);
                }
            }
        }.start();
    }

    /**
     * Disables the EditTexts for the question answers.
     */
    private void disableEditTexts() {
        question1Answer.setEnabled(false);
        question2Answer.setEnabled(false);
        question3Answer.setEnabled(false);
    }

    /**
     * Called when the view hierarchy associated with the fragment is being removed.
     * This is a good place to cancel the timer.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        // Cancel the timer if the fragment is destroyed
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}