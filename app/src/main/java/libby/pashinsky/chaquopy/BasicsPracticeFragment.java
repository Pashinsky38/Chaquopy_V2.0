package libby.pashinsky.chaquopy;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    private FragmentBasicsPracticeBinding binding;
    private CountDownTimer countDownTimer;
    private Button showSolutionButton;
    private boolean allAnswersCorrect = false;
    private TextView resultsText;
    private boolean solutionsShown = false;
    private EditText question1Answer;
    private EditText question2Answer;
    private EditText question3Answer;

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

        Button checkAnswersButton = binding.checkAnswersButton;
        resultsText = binding.resultsText;
        showSolutionButton = binding.showSolutionButton;
        question1Answer = binding.question1Answer;
        question2Answer = binding.question2Answer;
        question3Answer = binding.question3Answer;

        // Initially hide the "Show Solution" button
        showSolutionButton.setVisibility(View.GONE);

        /**
         * Sets a click listener on the checkAnswersButton.
         * When clicked, it retrieves the user's answers, checks them against the correct answers,
         * and displays the results. If all answers are correct, it disables the EditTexts and stops the timer.
         * If some answers are incorrect, it shows the "Show Solution" button.
         */
        checkAnswersButton.setOnClickListener(v -> {
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
                resultsText.setText(getString(R.string.all_answers_correct));
                allAnswersCorrect = true;
                // Stop the timer if all answers are correct
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                // Hide the "Show Solution" button if all answers are correct
                showSolutionButton.setVisibility(View.GONE);
                // Disable the EditTexts
                disableEditTexts();
            } else {
                resultsText.setText(getString(R.string.some_answers_incorrect));
                // Show the "Show Solution" button if some answers are incorrect
                if (!solutionsShown) {
                    showSolutionButton.setVisibility(View.VISIBLE);
                }
            }
        });

        /**
         * Sets a click listener on the showSolutionButton.
         * When clicked, it displays the solutions to the questions and keeps the button visible.
         */
        showSolutionButton.setOnClickListener(v -> {
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
        });

        // Start the countdown timer
        startCountdownTimer();
    }

    /**
     * Starts a countdown timer that, upon finishing, shows the "Show Solution" button
     * if all answers are not correct and solutions haven't been shown.
     */
    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(30000, 1000) {
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
     * This is a good place to clean up references to the binding object and cancel the timer.
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