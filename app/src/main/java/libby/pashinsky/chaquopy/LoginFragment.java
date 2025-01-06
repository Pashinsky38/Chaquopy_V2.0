package libby.pashinsky.chaquopy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import libby.pashinsky.chaquopy.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding; // View binding for the fragment
    private HelperDB dbHelper; // Database helper for user authentication

    public LoginFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        dbHelper = new HelperDB(getActivity());

        // Set click listener for the login button
        binding.buttonLogin.setOnClickListener(v -> {
            String emailLogin = binding.EnterEmail.getText().toString().trim();
            String passwordLogin = binding.EnterPassword.getText().toString().trim();

            // Check if email and password are empty
            if (emailLogin.isEmpty() || passwordLogin.isEmpty()) {
                Toast.makeText(getActivity(), "Email and password must not be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Authenticate user
            if (isUserRegistered(emailLogin, passwordLogin)) {
                // Navigate to Introduction activity if login is successful
                navigateToIntroduction();
            } else {
                Toast.makeText(getActivity(), "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for the sign up button
        binding.buttonSignUp.setOnClickListener(v -> NavigationHelper.navigateToRegisterFragment(getActivity().getSupportFragmentManager()));
        // Set click listener for the skip button
        binding.gotoHomePageButton.setOnClickListener(v -> NavigationHelper.navigateToHomePage(getActivity()));

        return view;
    }

    // Check if the user is registered in the database
    private boolean isUserRegistered(String email, String password) {
        String storedPassword = dbHelper.getPasswordByEmail(email);
        return password.equals(storedPassword);
    }

    private void navigateToIntroduction() {
        Intent intent = new Intent(requireActivity(), Introduction.class);
        startActivity(intent);
    }
}