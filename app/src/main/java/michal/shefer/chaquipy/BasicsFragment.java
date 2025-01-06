package michal.shefer.chaquipy;

import android.content.Context;
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

import michal.shefer.chaquipy.databinding.FragmentBasicsBinding;

public class BasicsFragment extends Fragment {

    private FragmentBasicsBinding binding;

    public BasicsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBasicsBinding.inflate(inflater, container, false);
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
        binding.runCodeButton.setOnClickListener(v -> runPythonCode());


    }

    private void runPythonCode() {
        // Get Python code from EditText using View Binding
        String pythonCode = binding.codeEditor.getText().toString();

        // Execute Python code using Chaquopy
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("test"); // Assuming "test" is your Python file name
        PyObject result = pyObject.callAttr("main", pythonCode); // Call a function named "main" in your Python file

        // Display the result in the TextView using View Binding
        binding.outputText.setText(result.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // To avoid memory leaks
    }
}