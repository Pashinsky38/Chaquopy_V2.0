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

import libby.pashinsky.chaquopy.databinding.FragmentRegisterBinding;

/**
 * A fragment representing the registration screen.
 * Allows users to create a new account by providing their details.
 */
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    /**
     * Required empty public constructor.
     */
    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view.
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
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored into the view.
     * It sets up the click listeners for the buttons and handles the user registration process.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpButtonListeners();
    }

    /**
     * Sets up click listeners for the buttons in the fragment.
     * - The sign-up button triggers user registration.
     */
    private void setUpButtonListeners() {
        binding.newSignUpButton.setOnClickListener(v -> handleSignUp());
    }

    /**
     * Handles the user sign-up process by validating input, creating a new UserDetails object,
     * inserting the user into the database, and providing feedback via Toast messages.
     */
    private void handleSignUp() {
        String name = binding.newName.getText().toString().trim();
        String email = binding.newEmail.getText().toString().trim();
        String password = binding.newPassword.getText().toString().trim();
        String reEnteredPassword = binding.newReEnterPassword.getText().toString().trim();
        String phoneNumber = binding.newPhoneNumber.getText().toString().trim();

        // Validate input fields
        if (validateInput(name, email, password, reEnteredPassword, phoneNumber)) {
            if (!password.equals(reEnteredPassword)) {
                showToast("Passwords do not match");
            } else {
                registerUser(name, email, password, phoneNumber);
            }
        }
    }

    /**
     * Validates the input fields to ensure no fields are empty.
     *
     * @param name              The user's name.
     * @param email             The user's email.
     * @param password          The user's password.
     * @param reEnteredPassword The password re-entered by the user.
     * @param phoneNumber       The user's phone number.
     * @return true if all input fields are valid; false otherwise.
     */
    private boolean validateInput(String name, String email, String password, String reEnteredPassword, String phoneNumber) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
                reEnteredPassword.isEmpty() || phoneNumber.isEmpty()) {
            showToast("All fields must be filled");
            return false;
        }
        return true;
    }

    /**
     * Registers the user by inserting their details into the database.
     *
     * @param name        The user's name.
     * @param email       The user's email.
     * @param password    The user's password.
     * @param phoneNumber The user's phone number.
     */
    private void registerUser(String name, String email, String password, String phoneNumber) {
        UserDetails newUser = new UserDetails(name, email, password, phoneNumber);
        HelperDB helperDB = new HelperDB(getActivity());
        boolean isInserted = helperDB.insertUser(newUser.getName(), newUser.getEmail(), newUser.getPassword(), newUser.getPhoneNumber());

        if (isInserted) {
            showToast("Registered successfully");
            navigateToIntroductionActivity();
        } else {
            showToast("Failed to register user");
        }
    }

    /**
     * Displays a toast message with the provided text.
     *
     * @param message The message to display in the toast.
     */
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigates to the IntroductionActivity.
     * Starts the IntroductionActivity and clears the back stack to prevent
     * the user from navigating back to the registration screen.
     */
    private void navigateToIntroductionActivity() {
        Intent intent = new Intent(getActivity(), Introduction.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}