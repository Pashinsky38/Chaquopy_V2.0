package libby.pashinsky.chaquopy;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import libby.pashinsky.chaquopy.databinding.FragmentVariablesBinding;

/**
 * A Fragment that allows users to run Python code related to variables using the Chaquopy library.
 * It provides an interface to input Python code, execute it, and view the output.
 * It also includes a button to navigate to the {@link BasicsPracticeFragment}.
 */
public class VariablesFragment extends Fragment {

    private FragmentVariablesBinding binding;

    /**
     * Required empty public constructor for the VariablesFragment.
     */
    public VariablesFragment() {
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
        binding = FragmentVariablesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * Initializes the Python environment, sets up the click listeners for the buttons,
     * and handles the navigation to the {@link BasicsPracticeFragment}.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializePython();
        setupButtonListeners();
    }

    /**
     * Initializes the Python environment using the Chaquopy library.
     */
    private void initializePython() {
        Context context = requireContext().getApplicationContext();
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }
    }

    /**
     * Sets up the click listeners for the buttons in the fragment.
     * - Sets a click listener for the run code button to execute Python code.
     * - Sets a click listener for the navigation button to go to the BasicsPracticeFragment.
     * - Sets a click listener for the text-to-speech button to read the explanation.
     */
    private void setupButtonListeners() {
        binding.runCodeButtonVariables.setOnClickListener(v -> runPythonCode());

        Button goToPracticeButton = binding.goToPracticeButton;
        goToPracticeButton.setOnClickListener(v -> navigateToBasicsPracticeFragment());

        // Set up click listener for the text-to-speech button
        binding.textToSpeechButtonVariables.setOnClickListener(v -> {
            // Get the explanation text to speak
            String textToSpeak = binding.variablesExplanation.getText().toString();
            // Start the TextToSpeechService to speak the explanation text
            TextToSpeechService.startService(requireContext(), textToSpeak);
        });
    }

    /**
     * Navigates to the BasicsPracticeFragment.
     * Replaces the current fragment with BasicsPracticeFragment and adds it to the back stack.
     */
    public void navigateToBasicsPracticeFragment() {
        // Stop any ongoing text-to-speech before navigating
        TextToSpeechService.stopSpeaking(requireContext());

        BasicsPracticeFragment basicsPracticeFragment = new BasicsPracticeFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_basics_practice, basicsPracticeFragment);
        transaction.addToBackStack("BasicsPracticeFragment");
        transaction.commit();
    }

    /**
     * Executes the Python code entered by the user in the code editor.
     * It retrieves the code, runs it using Chaquopy, and displays the result in the output TextView.
     */
    private void runPythonCode() {
        // Get Python code from the editor
        String pythonCode = binding.codeEditorVariables.getText().toString();

        // Execute Python code using Chaquopy
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("pythonRunner"); // "pythonRunner" is the Python file name
        PyObject result = pyObject.callAttr("main", pythonCode); // Call a function named "main" in the Python file

        // Display the result
        binding.outputTextVariables.setText(result.toString());
    }

    /**
     * Called when the fragment is no longer in use.
     * This is called after {@link #onStop()} and before {@link #onDetach()}.
     * Stops any ongoing text-to-speech when the fragment is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the TextToSpeechService when the fragment is destroyed
        TextToSpeechService.stopSpeaking(requireContext());
    }
}