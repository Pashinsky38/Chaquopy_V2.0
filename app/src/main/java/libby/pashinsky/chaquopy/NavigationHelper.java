package libby.pashinsky.chaquopy;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class NavigationHelper {

    private static void navigateToFragment(FragmentManager fragmentManager, int containerId, Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(null); // Add to back stack for navigation history
        transaction.commit();
    }

    // Navigates to the LoginFragment.
    public static void navigateToLoginFragment(FragmentManager fragmentManager) {
        navigateToFragment(fragmentManager, R.id.fragment_login, new LoginFragment());
    }

    // Navigates to the RegisterFragment.
    public static void navigateToRegisterFragment(FragmentManager fragmentManager) {
        navigateToFragment(fragmentManager, R.id.fragment_register, new RegisterFragment());
    }

    public static void navigateToHomePage(Context context) {
        context.startActivity(new Intent(context, HomePage.class));
    }


    public static void navigateToLoginFromRegister(FragmentManager fragmentManager) {
        navigateToFragment(fragmentManager, R.id.fragment_register, new LoginFragment());
    }
}