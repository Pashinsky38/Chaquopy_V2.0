package libby.pashinsky.chaquopy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Set click listener for the sign-up button
        binding.newSignUpButton.setOnClickListener(v -> {
            String name = binding.newName.getText().toString().trim();
            String email = binding.newEmail.getText().toString().trim();
            String password = binding.newPassword.getText().toString().trim();
            String reEnteredPassword = binding.newReEnterPassword.getText().toString().trim();
            String phoneNumber = binding.newPhoneNumber.getText().toString().trim();

            // Validate input fields
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
                    reEnteredPassword.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(getActivity(), "All fields must be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if passwords match
            if (!password.equals(reEnteredPassword)) {
                Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a UserDetails object, now named newUser
            UserDetails newUser = new UserDetails(name, email, password, phoneNumber);

            // Insert user data into the database
            HelperDB helperDB = new HelperDB(getActivity());
            //Now we use the newUser object to get the data
            boolean isInserted = helperDB.insertUser(newUser.getName(), newUser.getEmail(), newUser.getPassword(), newUser.getPhoneNumber());
            if (isInserted) {
                Toast.makeText(getActivity(), "Registered successfully", Toast.LENGTH_SHORT).show();
                navigateToLoginFragment();
            } else {
                Toast.makeText(getActivity(), "Failed to register user", Toast.LENGTH_SHORT).show();
            }
        });

        binding.newLoginButton.setOnClickListener(v -> navigateToLoginFragment());

        return view;
    }
    /**
     * Navigates to the LoginFragment.
     * Replaces the current fragment in the fragment_register container with the LoginFragment.
     */
    private void navigateToLoginFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_register, new LoginFragment());
        transaction.addToBackStack(null); // Add to back stack for navigation history
        transaction.commit();
    }
}