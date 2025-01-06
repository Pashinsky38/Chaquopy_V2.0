package libby.pashinsky.chaquopy;

import androidx.annotation.NonNull;

public class UserDetails {
    // Member variables to store user details
    private String name;
    private String email;
    private String password;
    private String phoneNumber;

    // Constructor for user registration
    public UserDetails(String name, String email, String password, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    // Constructor for user login (only requires email and password)
    public UserDetails(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Method to return a string representation of UserDetails
    @NonNull
    @Override
    public String toString() {
        return "Name: " + name + "\nEmail: " + email + "\nPassword: " + password + "\nPhone Number: " + phoneNumber;
    }

    // Getters and setters for all member variables
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
