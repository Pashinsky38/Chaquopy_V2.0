package libby.pashinsky.chaquopy;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * A helper class for navigating between fragments and activities.
 * Provides methods for replacing fragments and starting activities.
 */
public class NavigationHelper {

    /**
     * Navigates to the specified fragment.
     * Replaces the current fragment in the specified container with the given fragment.
     * Adds the transaction to the back stack for navigation history.
     *
     * @param fragmentManager The FragmentManager used for the transaction.
     * @param containerId The ID of the container where the fragment should be placed.
     * @param fragment The fragment to navigate to.
     */
    private static void navigateToFragment(FragmentManager fragmentManager, int containerId, Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(null); // Add to back stack for navigation history
        transaction.commit();
    }

    /**
     * Navigates to the LoginFragment.
     * Replaces the current fragment in the fragment_login container with the LoginFragment.
     *
     * @param fragmentManager The FragmentManager used for the transaction.
     */
    public static void navigateToLoginFragment(FragmentManager fragmentManager) {
        navigateToFragment(fragmentManager, R.id.fragment_login, new LoginFragment());
    }

    /**
     * Navigates to the RegisterFragment.
     * Replaces the current fragment in the fragment_register container with the RegisterFragment.
     *
     * @param fragmentManager The FragmentManager used for the transaction.
     */
    public static void navigateToRegisterFragment(FragmentManager fragmentManager) {
        navigateToFragment(fragmentManager, R.id.fragment_register, new RegisterFragment());
    }

    /**
     * Navigates to the HomePage activity.
     * Starts the HomePage activity using an Intent.
     *
     * @param context The context used to start the activity.
     */
    public static void navigateToHomePage(Context context) {
        context.startActivity(new Intent(context, HomePage.class));
    }

    /**
     * Navigates to the LoginFragment from the RegisterFragment.
     * Replaces the current fragment in the fragment_register container with the LoginFragment.
     *
     * @param fragmentManager The FragmentManager used for the transaction.
     */
    public static void navigateToLoginFromRegister(FragmentManager fragmentManager) {
        navigateToFragment(fragmentManager, R.id.fragment_register, new LoginFragment());
    }
}