package com.example.inverseai;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class User {
    private final String username, password; //the user name and password for this user

    /**
     * Constructor used to create a new user.
     *
     * @param username The username
     * @param password The password
     */
    User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Constructor for retrieving an already existing user
     *
     * @param UserString The string representing the User,
     *                   to be obtained by the retrieveUser method.
     */
    public User(String UserString) {
        String delimiter = "[ ,]+";
        String[] info = UserString.split(delimiter);
        username = info[0];
        password = info[1];
    }

    /**
     * Retrieves the String contents of a particular User from saved data.
     *
     * @param context  The context calling this method.
     * @param username The username of the User to retrieve.
     * @return The String UserString.
     * @throws FileNotFoundException Throws file does not exist error.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String retrieveUser(Context context, String username) throws FileNotFoundException {
        FileInputStream fis = context.openFileInput(username);
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("");
                line = reader.readLine();
            }
        } catch (IOException e) {
            return "";
            // error occurred when opening raw file for reading.
        }
        return stringBuilder.toString();
    }

    /**
     * Saves this User information to phone storage.
     *
     * @param context The activity context calling this method.
     * @throws IOException Throws file creation error.
     */
    public void saveUser(Context context) throws IOException {
        String fileContents = this.toString();
        try (FileOutputStream fos = context.openFileOutput(username, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        }
    }

    /**
     * @return Returns username and password of user as a string.
     */
    @Override
    @NonNull
    public String toString() {
        return username + " " + password;
    }

    /**
     * @return Returns password of user as a string.
     */
    public String getPassword() {
        return password;
    }
}