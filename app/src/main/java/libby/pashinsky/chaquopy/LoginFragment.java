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

/**
 * A fragment representing the login screen.
 * Allows users to enter their credentials and log in to the application.
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding; // View binding for the fragment
    private HelperDB dbHelper; // Database helper for user authentication

    /**
     * Required empty public constructor.
     */
    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which is the default implementation).
     * This will be called between {@link #onCreate} and {@link #onActivityCreated}.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
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

    /**
     * Checks if the user is registered in the database.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return True if the user is registered, false otherwise.
     */
    private boolean isUserRegistered(String email, String password) {
        String storedPassword = dbHelper.getPasswordByEmail(email);
        return password.equals(storedPassword);
    }

    /**
     * Navigates to the Introduction activity.
     * Starts the Introduction activity using an Intent.
     */
    private void navigateToIntroduction() {
        Intent intent = new Intent(requireActivity(), Introduction.class);
        startActivity(intent);
    }
}