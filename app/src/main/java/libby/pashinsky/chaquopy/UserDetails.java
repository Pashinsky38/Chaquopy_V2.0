package libby.pashinsky.chaquopy;

import androidx.annotation.NonNull;

/**
 * A class representing user details.
 * Stores user information such as name, email, password, and phone number.
 */
public class UserDetails {
    // Member variables to store user details
    private String name;
    private String email;
    private String password;
    private String phoneNumber;

    /**
     * Constructor for user registration.
     *
     * @param name The user's name.
     * @param email The user's email address.
     * @param password The user's password.
     * @param phoneNumber The user's phone number.
     */
    public UserDetails(String name, String email, String password, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Constructor for user login.
     *
     * @param email The user's email address.
     * @param password The user's password.
     */
    public UserDetails(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Returns a string representation of the UserDetails object.
     *
     * @return A string containing the user's name, email, password, and phone number.
     */
    @NonNull
    @Override
    public String toString() {
        return "Name: " + name + "\nEmail: " + email + "\nPassword: " + password + "\nPhone Number: " + phoneNumber;
    }

    /**
     * Returns the user's name.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name The user's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the user's email address.
     *
     * @return The user's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email The user's email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the user's password.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password The user's password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the user's phone number.
     *
     * @return The user's phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number.
     *
     * @param phoneNumber The user's phone number.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}