package libby.pashinsky.chaquopy;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import libby.pashinsky.chaquopy.databinding.FragmentVariablesBinding;

public class VariablesFragment extends Fragment {

    private FragmentVariablesBinding binding;

    public VariablesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVariablesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Python
        Context context = requireContext().getApplicationContext();
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }

        // Set click listener for the Run button
        binding.runCodeButtonVariables.setOnClickListener(v -> runPythonCode());

        // Find the button in the layout
        Button goToPracticeButton = binding.goToPracticeButton;

        // Set click listener for the button using a lambda expression
        goToPracticeButton.setOnClickListener(v -> {
            // Create a new instance of BasicsPracticeFragment
            BasicsPracticeFragment basicsPracticeFragment = new BasicsPracticeFragment();

            // Get the FragmentManager
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

            // Replace the current fragment with BasicsPracticeFragment
            transaction.replace(R.id.fragment_basics_practice, basicsPracticeFragment);

            // Add the transaction to the back stack (optional, but recommended for navigation)
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();

            // Make fragment_basics_practice visible
            FrameLayout practiceContainer = requireActivity().findViewById(R.id.fragment_basics_practice);
            practiceContainer.setVisibility(View.VISIBLE);

            // Hide fragment_container_view
            FrameLayout variablesContainer = requireActivity().findViewById(R.id.fragment_variables);
            variablesContainer.setVisibility(View.GONE);
        });
    }

    private void runPythonCode() {
        // Get Python code from EditText using View Binding
        String pythonCode = binding.codeEditorVariables.getText().toString();

        // Execute Python code using Chaquopy
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("pythonRunner"); // "pythonRunner" is the Python file name
        PyObject result = pyObject.callAttr("main", pythonCode); // Call a function named "main" in the Python file

        // Display the result in the TextView using View Binding
        binding.outputTextVariables.setText(result.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}