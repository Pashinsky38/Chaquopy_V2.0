package michal.shefer.chaquipy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class HelperDB extends SQLiteOpenHelper {

    // Database constants
    private static final String DATABASE_NAME = "user_management.db";
    private static final int DATABASE_VERSION = 1;
    public static final String USERS_TABLE = "Users";
    public static final String USER_NAME = "UserName";
    public static final String USER_EMAIL = "UserEmail";
    public static final String USER_PWD = "UserPassword";
    public static final String USER_PHONE = "UserPhone";

    // Constructor
    public HelperDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method to create the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the users table with the specified columns and data types
        String createTable = "CREATE TABLE " + USERS_TABLE + " (" +
                USER_NAME + " TEXT, " +
                USER_EMAIL + " TEXT PRIMARY KEY, " +
                USER_PWD + " TEXT, " +
                USER_PHONE + " TEXT)";
        db.execSQL(createTable);
    }

    // Method to upgrade the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the older table if it exists and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        onCreate(db);
    }

    // Method to insert user details into the database
    public boolean insertUser(String userName, String userEmail, String userPassword, String userPhone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USER_NAME, userName);
        values.put(USER_EMAIL, userEmail);
        values.put(USER_PWD, userPassword);
        values.put(USER_PHONE, userPhone);

        // Insert the user details into the database
        long result = db.insert(USERS_TABLE, null, values);
        return result != -1; // Return true if insertion was successful, false otherwise
    }

    // Method to get the password by email
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
}