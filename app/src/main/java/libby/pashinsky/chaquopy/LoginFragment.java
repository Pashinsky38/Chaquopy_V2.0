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
import androidx.fragment.app.FragmentTransaction;

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
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     *                           The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored into the view.
     * Initializes the database helper and sets up click listeners for the buttons.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new HelperDB(getActivity());
        setUpButtonListeners();
    }

    /**
     * Sets up click listeners for the login, sign-up, and skip buttons.
     * - Login button attempts to authenticate the user.
     * - Sign-up button navigates to the RegisterFragment.
     * - Skip button navigates to the HomePage activity.
     */
    private void setUpButtonListeners() {
        binding.buttonLogin.setOnClickListener(v -> handleLogin());
        binding.buttonSignUp.setOnClickListener(v -> navigateToRegisterFragment());
        binding.gotoHomePageButton.setOnClickListener(v -> navigateToHomePage());
    }

    /**
     * Handles the login process by validating the user's credentials.
     * If valid, it navigates to the Introduction activity.
     * Otherwise, it displays a Toast message.
     */
    private void handleLogin() {
        String emailLogin = binding.EnterEmail.getText().toString().trim();
        String passwordLogin = binding.EnterPassword.getText().toString().trim();

        // Check if email and password are empty
        if (emailLogin.isEmpty() || passwordLogin.isEmpty()) {
            showToast("Email and password must not be empty");
            return;
        }

        // Authenticate user
        if (isUserRegistered(emailLogin, passwordLogin)) {
            navigateToIntroduction();
        } else {
            showToast("Invalid email or password");
        }
    }

    /**
     * Checks if the user is registered in the database.
     *
     * @param email    The user's email address.
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

    /**
     * Navigates to the RegisterFragment.
     * Replaces the current fragment in the fragment_register container with the RegisterFragment.
     */
    private void navigateToRegisterFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_register, new RegisterFragment());
        transaction.addToBackStack(null); // Add to back stack for navigation history
        transaction.commit();
    }

    /**
     * Navigates to the HomePage activity.
     * Starts the HomePage activity using an Intent.
     */
    private void navigateToHomePage() {
        Intent intent = new Intent(requireActivity(), HomePage.class);
        startActivity(intent);
    }

    /**
     * Displays a toast message with the provided text.
     *
     * @param message The message to display in the toast.
     */
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
