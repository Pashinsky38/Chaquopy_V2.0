package libby.pashinsky.chaquopy;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

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

        // Initialize Python
        Context context = requireContext().getApplicationContext();
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }

        // Set click listener for the Run button
        binding.runCodeButton.setOnClickListener(v -> runPythonCode());

        // Set click listener for the Next button
        binding.nextButton.setOnClickListener(v -> {
            VariablesFragment variablesFragment = new VariablesFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_variables, variablesFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            // Hide
            FrameLayout variablesContainer = requireActivity().findViewById(R.id.fragment_variables);
            variablesContainer.setVisibility(View.VISIBLE);

            // Hide fragment_basics
            FrameLayout basicsContainer = requireActivity().findViewById(R.id.fragment_basics);
            basicsContainer.setVisibility(View.GONE);
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

    /**
     * Called when the view previously created by {@link #onCreateView} has been detached from the fragment.
     * The next time the fragment needs to be displayed, a new view will be created.
     * This is called after the fragment's view and before {@link #onDestroy()}.
     * It is called *regardless* of whether {@link #onCreateView} returned a non-null view.
     * Internally it is called after the view's state has been saved but before it has been removed from its parent.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // To avoid memory leaks
    }
}