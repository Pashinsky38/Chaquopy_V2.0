package libby.pashinsky.chaquopy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

/**
 * A helper class for managing the user database.
 * Provides methods for creating, upgrading, and interacting with the database.
 */
public class HelperDB extends SQLiteOpenHelper {

    // Database constants
    private static final String DATABASE_NAME = "user_management.db";
    private static final int DATABASE_VERSION = 56;
    public static final String USERS_TABLE = "Users";
    public static final String USER_NAME = "UserName";
    public static final String USER_EMAIL = "UserEmail";
    public static final String USER_PWD = "UserPassword";
    public static final String USER_PHONE = "UserPhone";
    public static final String CORRECT_ANSWERS = "CorrectAnswers";
    public static final String TOTAL_TRIES = "TotalTries";

    /**
     * Constructor for the HelperDB class.
     *
     * @param context The application context.
     */
    public HelperDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     * This is where the database schema is defined.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the users table with the specified columns and data types
        String createTable = "CREATE TABLE " + USERS_TABLE + " (" +
                USER_NAME + " TEXT, " +
                USER_EMAIL + " TEXT PRIMARY KEY, " +
                USER_PWD + " TEXT, " +
                USER_PHONE + " TEXT, " +
                CORRECT_ANSWERS + " INTEGER DEFAULT 0, " +
                TOTAL_TRIES + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    /**
     * Called when the database needs to be upgraded.
     * This method is typically used to add new tables or columns to the database.
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the older table if it exists and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        onCreate(db);
    }

    /**
     * Inserts a new user into the database.
     *
     * @param userName     The user's name.
     * @param userEmail    The user's email address.
     * @param userPassword The user's password.
     * @param userPhone    The user's phone number.
     * @return True if the insertion was successful, false otherwise.
     */
    public boolean insertUser(String userName, String userEmail, String userPassword, String userPhone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USER_NAME, userName);
        values.put(USER_EMAIL, userEmail);
        values.put(USER_PWD, userPassword);
        values.put(USER_PHONE, userPhone);
        values.put(CORRECT_ANSWERS, 0); // Initialize with zero correct answers
        values.put(TOTAL_TRIES, 0); // Initialize with zero tries

        // Check if user already exists
        Cursor cursor = db.query(USERS_TABLE, null, USER_EMAIL + "=?", new String[]{userEmail}, null, null, null);
        boolean userExists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }

        long result;
        if (userExists) {
            // Update existing user
            result = db.update(USERS_TABLE, values, USER_EMAIL + "=?", new String[]{userEmail});
        } else {
            // Insert new user
            result = db.insert(USERS_TABLE, null, values);
        }

        return result != -1; // Return true if operation was successful, false otherwise
    }

    /**
     * Retrieves the password for the specified email address.
     *
     * @param email The email address to search for.
     * @return The password associated with the email address, or null if not found.
     */
    @SuppressLint("Range")
    public String getPasswordByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String password = null;

        // Query the database to get the password for the specified email
        Cursor cursor = db.query(USERS_TABLE, new String[]{USER_PWD}, USER_EMAIL + "=?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            // Retrieve the password from the cursor
            password = cursor.getString(cursor.getColumnIndex(USER_PWD));
            cursor.close();
        }

        return password; // Return the password (or null if not found)
    }

    /**
     * Updates the correct answers count for a specific user by adding the specified amount.
     *
     * @param email The email address of the user.
     * @param correctAnswersToAdd The number of correct answers to add.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateCorrectAnswers(String email, int correctAnswersToAdd) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First, ensure the user exists
        if (!userExists(email)) {
            // If the user doesn't exist, create them
            insertUser("User", email, "", "");
        }

        // Get current count
        int currentCorrectAnswers = getCorrectAnswers(email);

        // Update with direct SQL to avoid concurrency issues
        String updateQuery = "UPDATE " + USERS_TABLE + " SET " + CORRECT_ANSWERS + " = " +
                CORRECT_ANSWERS + " + ? WHERE " + USER_EMAIL + " = ?";
        db.execSQL(updateQuery, new Object[]{correctAnswersToAdd, email});

        // Verify update
        int newCount = getCorrectAnswers(email);

        // Check if the update was successful
        return newCount > currentCorrectAnswers;
    }

    /**
     * Updates the total tries count for a specific user by adding the specified amount.
     *
     * @param email The email address of the user.
     * @param triesToAdd The number of tries to add.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateTotalTries(String email, int triesToAdd) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First, ensure the user exists
        if (!userExists(email)) {
            // If the user doesn't exist, create them
            insertUser("User", email, "", "");
        }

        // Get current count
        int currentTries = getTotalTries(email);

        // Update with direct SQL to avoid concurrency issues
        String updateQuery = "UPDATE " + USERS_TABLE + " SET " + TOTAL_TRIES + " = " +
                TOTAL_TRIES + " + ? WHERE " + USER_EMAIL + " = ?";
        db.execSQL(updateQuery, new Object[]{triesToAdd, email});

        // Verify update
        int newCount = getTotalTries(email);

        // Check if the update was successful
        return newCount > currentTries;
    }

    /**
     * Retrieves the number of correct answers for the specified email address.
     *
     * @param email The email address to search for.
     * @return The number of correct answers associated with the email address, or 0 if not found.
     */
    @SuppressLint("Range")
    public int getCorrectAnswers(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        int correctAnswers = 0;

        // Query the database to get the correct answers for the specified email
        Cursor cursor = db.query(USERS_TABLE, new String[]{CORRECT_ANSWERS}, USER_EMAIL + "=?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            // Retrieve the correct answers from the cursor
            correctAnswers = cursor.getInt(cursor.getColumnIndex(CORRECT_ANSWERS));
            cursor.close();
        }

        return correctAnswers;
    }

    /**
     * Retrieves the total number of tries for the specified email address.
     *
     * @param email The email address to search for.
     * @return The total tries associated with the email address, or 0 if not found.
     */
    @SuppressLint("Range")
    public int getTotalTries(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        int totalTries = 0;

        // Query the database to get the total tries for the specified email
        Cursor cursor = db.query(USERS_TABLE, new String[]{TOTAL_TRIES}, USER_EMAIL + "=?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            // Retrieve the total tries from the cursor
            totalTries = cursor.getInt(cursor.getColumnIndex(TOTAL_TRIES));
            cursor.close();
        }

        return totalTries;
    }

    /**
     * Checks if a user with the specified email exists in the database.
     *
     * @param email The email address to check.
     * @return True if the user exists, false otherwise.
     */
    public boolean userExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean exists;

        Cursor cursor = db.query(USERS_TABLE, new String[]{USER_EMAIL}, USER_EMAIL + "=?", new String[]{email}, null, null, null);
        exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }

        return exists;
    }

    /**
     * Retrieves the email of the first user found in the database.
     * This is useful when there's only one user or we need to select a default user.
     *
     * @return The email of the first user in the database, or empty string if no users exist.
     */
    @SuppressLint("Range")
    public String getFirstUserEmail() {
        SQLiteDatabase db = this.getReadableDatabase();
        String email = "";

        // Query all users and just get the first one
        Cursor cursor = db.query(USERS_TABLE, new String[]{USER_EMAIL}, null, null, null, null, null, "1");

        if (cursor != null && cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndex(USER_EMAIL));
            cursor.close();
        }

        return email;
    }
}