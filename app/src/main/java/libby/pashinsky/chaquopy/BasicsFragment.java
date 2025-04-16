package libby.pashinsky.chaquopy;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import libby.pashinsky.chaquopy.databinding.FragmentBasicsBinding;

/**
 * A Fragment that demonstrates basic Chaquopy integration for running Python code.
 */
public class BasicsFragment extends Fragment {

    private FragmentBasicsBinding binding;

    /**
     * Required empty public constructor.
     */
    public BasicsFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBasicsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called immediately after {@link #onCreateView} has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once they know that their view hierarchy has been completely created.
     * The fragment's view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializePython();
        setupButtonListeners();
    }

    /**
     * Initializes the Python environment using Chaquopy.
     * This method checks if Python is already started and starts it if necessary.
     */
    private void initializePython() {
        Context context = requireContext().getApplicationContext();
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }
    }

    /**
     * Sets up all button click listeners for the fragment.
     */
    private void setupButtonListeners() {
        // Set click listener for the Run button
        binding.runCodeButton.setOnClickListener(v -> runPythonCode());

        // Set click listener for the Next button
        binding.nextButton.setOnClickListener(v -> navigateToVariablesFragment());
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

    /**
     * Navigates to the VariablesFragment.
     * Replaces the current fragment.
     */
    private void navigateToVariablesFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_variables, new VariablesFragment());
        transaction.addToBackStack("VariablesFragment"); // Add a tag
        transaction.commit();
    }
}