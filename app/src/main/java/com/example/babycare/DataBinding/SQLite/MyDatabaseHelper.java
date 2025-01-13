package com.example.babycare.DataBinding.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.babycare.DataBinding.Model.BabyProfileModel;
import com.example.babycare.DataBinding.Model.ChatModel;
import com.example.babycare.DataBinding.Model.MessageModel;
import com.google.firebase.Timestamp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private Context context;
    private final String DATABASE_NAME;
    private static final int DATABASE_VERSION_PROPERTIES = 1;
    private static final String TABLE_NAME_1 = "CurrentUser";
    private static final String TABLE_NAME_2 = "BabyProfile";
    private static final String TABLE_NAME_3 = "Chats";
    private static final String TABLE_NAME_4 = "Messages";

    public MyDatabaseHelper(@Nullable Context context, String databasename) {
        super(context, databasename, null, DATABASE_VERSION_PROPERTIES);
        this.context = context;
        this.DATABASE_NAME = databasename;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create CurrentUser table
        db.execSQL("CREATE TABLE IF NOT EXISTS CurrentUser (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "phoneNumber TEXT, " +
                "profilePic TEXT, " +
                "documentID TEXT, " +
                "username TEXT, " +
                "type TEXT, " +
                "email TEXT, " +
                "createdAt TEXT)"
        );
        // Create BabyProfile table
        db.execSQL("CREATE TABLE IF NOT EXISTS BabyProfile (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "documentID TEXT, " +
                "fatherID TEXT, " +          // Store Father's documentID
                "motherID TEXT, " +          // Store Mother's documentID
                "guardianID TEXT, " +        // Store Guardian's documentID
                "name TEXT, " +
                "dob TEXT, " +
                "height TEXT, " +
                "weight TEXT, " +
                "bloodType TEXT, " +
                "allergies TEXT, " +
                "profilePic TEXT " +
                ");"
        );

        // Create Chats table
        String createChatsTable = "CREATE TABLE IF NOT EXISTS Chats (" +
                "chatID TEXT PRIMARY KEY, " +
                "lastMessageID TEXT, " +
                "p_senderID TEXT, " +
                "p_userName TEXT, " +
                "p_profilePic TEXT, " +
                "createdAt TEXT " +
                ");";
        db.execSQL(createChatsTable);

        // Create Messages table
        String createMessagesTable = "CREATE TABLE IF NOT EXISTS Messages (" +
                "messageID TEXT PRIMARY KEY, " +
                "chatID TEXT, " +
                "attachments TEXT, " +
                "message TEXT, " +
                "senderID TEXT, " +
                "timestamp TEXT, " +
                "FOREIGN KEY(chatID) REFERENCES Chats(chatID) " +
                ");";
        db.execSQL(createMessagesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_3);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_4);

        // Recreate tables
        onCreate(db);
    }

    //check whether empty or not
    public Cursor readAllDataCurrentUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_1;

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor getCurrentUserByDocumentID(String documentID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_1 + " WHERE documentID = ?";
        Cursor cursor = db.rawQuery(query, new String[]{documentID});

        if (cursor != null && cursor.moveToFirst()) {
            Log.d(TAG, "Current user found for documentID: " + documentID);
        } else {
            Log.d(TAG, "No user found for documentID: " + documentID);
        }

        return cursor; // Return the cursor for further processing
    }

    public Cursor getBabyByDocumentID(String parentID) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Modify the query to match any of the parent IDs
        String query = "SELECT * FROM " + TABLE_NAME_2 +
                " WHERE fatherID = ? OR motherID = ? OR guardianID = ?";

        // Execute the query with the parentID for all three conditions
        Cursor cursor = db.rawQuery(query, new String[]{parentID, parentID, parentID});

        if (cursor != null && cursor.moveToFirst()) {
            Log.d(TAG, "Current user found for parentID: " + parentID);
        } else {
            Log.d(TAG, "No user found for parentID: " + parentID);
        }

        return cursor; // Return the cursor for further processing
    }


    public ArrayList<BabyProfileModel> getBabyProfilesByParentID(String parentID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<BabyProfileModel> babyProfileList = new ArrayList<>();

        // Query to fetch babies matching the parent ID (father, mother, or guardian)
        String query = "SELECT * FROM " + TABLE_NAME_2 +
                " WHERE fatherID = ? OR motherID = ? OR guardianID = ?";

        // Execute the query
        Cursor cursor = db.rawQuery(query, new String[]{parentID, parentID, parentID});

        if (cursor != null) {
            // Iterate through the results
            while (cursor.moveToNext()) {
                // Extract data from the cursor
                String documentID = cursor.getString(cursor.getColumnIndexOrThrow("documentID"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String dob = cursor.getString(cursor.getColumnIndexOrThrow("dob"));
                String height = cursor.getString(cursor.getColumnIndexOrThrow("height"));
                String weight = cursor.getString(cursor.getColumnIndexOrThrow("weight"));
                String bloodType = cursor.getString(cursor.getColumnIndexOrThrow("bloodType"));
                String fatherId = cursor.getString(cursor.getColumnIndexOrThrow("fatherID"));
                String motherId = cursor.getString(cursor.getColumnIndexOrThrow("motherID"));
                String guardianId = cursor.getString(cursor.getColumnIndexOrThrow("guardianID"));
                String allergies = cursor.getString(cursor.getColumnIndexOrThrow("allergies"));
                String profilePic = cursor.getString(cursor.getColumnIndexOrThrow("profilePic"));

                // Create a new BabyProfileModel object
                BabyProfileModel babyProfile = new BabyProfileModel(
                        documentID,
                        name,
                        dob,
                        height,
                        weight,
                        bloodType,
                        fatherId,
                        motherId,
                        guardianId,
                        allergies,
                        profilePic
                );

                // Add the object to the list
                babyProfileList.add(babyProfile);
            }
            cursor.close(); // Close the cursor after use
        }

        // Return the list of baby profiles
        return babyProfileList;
    }



    void deleteRowByID(String condition, int tableNum) {
        // Get a writable database reference
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = "ID = ?"; //reference col
        String[] selectionArgs = {condition}; //find the certain row in the reference col
        if (tableNum == 1) {
            int deletedRows = db.delete(TABLE_NAME_1, selection, selectionArgs);
        } else {
            int deletedRows = db.delete(TABLE_NAME_2, selection, selectionArgs);
        }
    }

    void deleteRowByDocID(int tableNum, String documentID) {
        // Get a writable database reference
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = "documentID = ?"; //reference col
        String[] selectionArgs = {documentID}; //row ID in the col reference
        if (tableNum == 1) {
            int deletedRows = db.delete(TABLE_NAME_1, selection, selectionArgs);
        } else {
            int deletedRows = db.delete(TABLE_NAME_2, selection, selectionArgs);
        }
    }

    public void deleteAllDataFromCurrentUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_NAME_1, null, null); // `null` indicates no `WHERE` clause, so all rows will be deleted.

        if (deletedRows > 0) {
            Log.d(TAG, "Successfully deleted all data from CurrentUser table.");
        } else {
            Log.w(TAG, "No data found to delete in CurrentUser table.");
        }
    }

    public void deleteAllDataFromBabyProfile() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_NAME_2, null, null); // `null` indicates no `WHERE` clause, so all rows will be deleted.

        if (deletedRows > 0) {
            Log.d(TAG, "Successfully deleted all data from BabyProfile table.");
        } else {
            Log.w(TAG, "No data found to delete in BabyProfile table.");
        }
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();

        int deletedRowsCurrentUser = db.delete(TABLE_NAME_1, null, null);
        int deletedRowsBabyProfile = db.delete(TABLE_NAME_2, null, null);
        int deletedRowsChats = db.delete(TABLE_NAME_3, null, null);
        int deletedRowsMessages = db.delete(TABLE_NAME_4, null, null);

        Log.d(TAG, "Deleted " + deletedRowsCurrentUser + " rows from CurrentUser table.");
        Log.d(TAG, "Deleted " + deletedRowsBabyProfile + " rows from BabyProfile table.");
        Log.d(TAG, "Deleted " + deletedRowsChats + " rows from Chats table.");
        Log.d(TAG, "Deleted " + deletedRowsMessages + " rows from Messages table.");


        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME_1 + "';");
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME_2 + "';");
    }

    void editRowByID(int tableNum, String ID, String editCol, String newValue) {
        // Get a writable database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String selection = "ID = ?";//reference col
        String[] selectionArgs = {ID};//row ID in the col reference

        values.put(editCol, newValue);//col to change

        if (tableNum == 1) {
            int count = db.update(TABLE_NAME_1, values, selection, selectionArgs);
        } else {
            int count = db.update(TABLE_NAME_2, values, selection, selectionArgs);
        }
    }

    void editRowByDocID(int tableNum, String documentID, String editCol, String newValue) {
        // Get a writable database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String selection = "documentID = ?";//reference col
        String[] selectionArgs = {documentID};//row ID in the col reference

        values.put(editCol, newValue);//col to change

        if (tableNum == 1) {
            int count = db.update(TABLE_NAME_1, values, selection, selectionArgs);
        } else {
            int count = db.update(TABLE_NAME_2, values, selection, selectionArgs);
        }
    }

    public void addCurrentUser(String documentID, String username, String profilePic, String phoneNumber, String email, String createdAt, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", 1); // Ensure the ID is always 1
        values.put("documentID", documentID);
        values.put("username", username);
        values.put("profilePic", profilePic);
        values.put("phoneNumber", phoneNumber);
        values.put("email", email);
        values.put("createdAt", createdAt);
        values.put("type", type);

        long result = db.insert(TABLE_NAME_1, null, values);
        if (result == -1) {
            Log.e(TAG, "Failed to insert data into CurrentUser table.");
        } else {
            Log.d(TAG, "Successfully inserted data into CurrentUser table. Row ID: " + result);
        }
    }

    public void updateUserName(String documentID, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", newName);

        int rowsAffected = db.update(TABLE_NAME_1, values, "documentID = ?", new String[]{documentID});
        if (rowsAffected > 0) {
            Log.d(TAG, "Successfully updated username for documentID: " + documentID);
        } else {
            Log.e(TAG, "Failed to update username for documentID: " + documentID);
        }
    }

    public void updateUserEmail(String documentID, String newEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", newEmail);

        int rowsAffected = db.update(TABLE_NAME_1, values, "documentID = ?", new String[]{documentID});
        if (rowsAffected > 0) {
            Log.d(TAG, "Successfully updated email for documentID: " + documentID);
        } else {
            Log.e(TAG, "Failed to update email for documentID: " + documentID);
        }
    }

    public void updateUserPhoneNumber(String documentID, String newPhoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phoneNumber", newPhoneNumber);

        int rowsAffected = db.update(TABLE_NAME_1, values, "documentID = ?", new String[]{documentID});
        if (rowsAffected > 0) {
            Log.d(TAG, "Successfully updated phone number for documentID: " + documentID);
        } else {
            Log.e(TAG, "Failed to update phone number for documentID: " + documentID);
        }
    }

    public void updateUserProfilePic(String documentID, String newProfilePic) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("profilePic", newProfilePic);

        int rowsAffected = db.update(TABLE_NAME_1, values, "documentID = ?", new String[]{documentID});
        if (rowsAffected > 0) {
            Log.d(TAG, "Successfully updated profile picture for documentID: " + documentID);
        } else {
            Log.e(TAG, "Failed to update profile picture for documentID: " + documentID);
        }
    }


    public void addBabyProfile(String documentID, String fatherID, String motherID, String guardianID, String name, String dob, String height, String weight, String bloodType, String allergies, String profilePic) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("documentID", documentID);
        values.put("fatherID", fatherID);
        values.put("motherID", motherID);
        values.put("guardianID", guardianID);
        values.put("name", name);
        values.put("dob", dob);
        values.put("height", height);
        values.put("weight", weight);
        values.put("bloodType", bloodType);
        values.put("allergies", allergies);
        values.put("profilePic", profilePic);

        long result = db.insert(TABLE_NAME_2, null, values);
        if (result == -1) {
            Log.e(TAG, "Failed to insert data into BabyProfile table.");
        } else {
            Log.d(TAG, "Successfully inserted data into BabyProfile table. Row ID: " + result);
        }
    }
    public void deleteBabyProfile(String documentID) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete the row(s) that match the documentID
        int result = db.delete(TABLE_NAME_2, "documentID = ?", new String[]{documentID});

        if (result == 0) {
            Log.e(TAG, "Failed to delete data from BabyProfile table. No matching documentID found.");
        } else {
            Log.d(TAG, "Successfully deleted data from BabyProfile table. Rows affected: " + result);
        }
    }


    public void addBabyProfile(Map<String, Object> babyProfile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Extract values from the map
        String documentID = (String) babyProfile.get("documentID");
        String name = (String) babyProfile.get("name");
        String dob = (String) babyProfile.get("dob");
        String height = (String) babyProfile.get("height");
        String weight = (String) babyProfile.get("weight");
        String bloodType = (String) babyProfile.get("bloodType");
        Map<String, Object> parent = (Map<String, Object>) babyProfile.get("parent");
        String fatherID = (String) parent.get("fatherId");
        String motherID = (String) parent.get("motherId");
        String guardianID = (String) parent.get("guardianId");
        String allergies = formatAllergies( (ArrayList<String>) babyProfile.get("allergies"));
        String profilePic = (String) babyProfile.get("profilePic");

        // Add values to ContentValues
        values.put("documentID", documentID);
        values.put("name", name);
        values.put("dob", dob);
        values.put("height", height);
        values.put("weight", weight);
        values.put("bloodType", bloodType);
        values.put("fatherID", fatherID);
        values.put("motherID", motherID);
        values.put("guardianID", guardianID);
        values.put("allergies", allergies);
        values.put("profilePic", profilePic);

        // Insert into the database
        long result = db.insert(TABLE_NAME_2, null, values);
        if (result == -1) {
            Log.e(TAG, "Failed to insert data into BabyProfile table.");
        } else {
            Log.d(TAG, "Successfully inserted data into BabyProfile table. Row ID: " + result);
        }
    }

    public String formatAllergies(List<String> allergiesList) {
        String allergies ="";
        if (allergiesList != null && !allergiesList.isEmpty()) {
            // Convert the ArrayList into a comma-separated string
            allergies = String.join(", ", allergiesList);
        } else {
            // Set to null or empty string if the list is null or empty
            allergies = "";
        }
        return allergies;
    }


    public void updateBabyProfile(String documentID, String fatherID, String motherID, String guardianID, String name, String dob, String height, String weight, String bloodType, String allergies, String profilePic) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Add updated values to the ContentValues object
        values.put("fatherID", fatherID);
        values.put("motherID", motherID);
        values.put("guardianID", guardianID);
        values.put("name", name);
        values.put("dob", dob);
        values.put("height", height);
        values.put("weight", weight);
        values.put("bloodType", bloodType);
        values.put("allergies", allergies);
        values.put("profilePic", profilePic);

        // Update the record that matches the given documentID
        int result = db.update(TABLE_NAME_2, values, "documentID = ?", new String[]{documentID});

        // Log the result
        if (result == 0) {
            Log.e(TAG, "Failed to update data in BabyProfile table for documentID: " + documentID);
        } else {
            Log.d(TAG, "Successfully updated data in BabyProfile table for documentID: " + documentID + ". Rows affected: " + result);
        }
    }


    public void addChat(String chatID, String lastMessageID, String p_senderID, String p_userName, String p_profilePic, String createdAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values1 = new ContentValues();

        values1.put("chatID", chatID);
        values1.put("lastMessageID", lastMessageID);
        values1.put("p_senderID", p_senderID);
        values1.put("p_userName", p_userName);
        values1.put("p_profilePic", p_profilePic);
        values1.put("createdAt", createdAt);

        long result1 = db.insert(TABLE_NAME_3, null, values1);
        if (result1 == -1) {
            Log.e(TAG, "Failed to insert data into Chats table.");
        } else {
            Log.d(TAG, "Successfully inserted data into Chats table. Row ID: " + result1);
        }

    }

    public void addChatList(List<ChatModel> chatsToInsert) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction(); // Start a transaction for better performance

            for (ChatModel chat : chatsToInsert) {

                String chatID = (String) chat.getDocumentID();

                String lastMessageID = (String) chat.getLastMessage().get("messageId");

                String p_senderID = (String) chat.getParticipants().get("userId");
                String p_userName = (String) chat.getParticipants().get("userName");
                String p_profilePic = (String) chat.getParticipants().get("profilePic");

                String createdAt = (String) chat.getTimestamp();

                ContentValues values1 = new ContentValues();

                values1.put("chatID", chatID);
                values1.put("lastMessageID", lastMessageID);
                values1.put("p_senderID", p_senderID);
                values1.put("p_userName", p_userName);
                values1.put("p_profilePic", p_profilePic);
                values1.put("createdAt", createdAt);

                long result1 = db.insert(TABLE_NAME_3, null, values1);
                if (result1 == -1) {
                    Log.e(TAG, "Failed to insert data into Chats table.");
                } else {
                    Log.d(TAG, "Successfully inserted data into Chats table. Row ID: " + result1);
                }


            }

            db.setTransactionSuccessful(); // Mark the transaction as successful
        } catch (Exception e) {
            Log.e(TAG, "Error inserting messages: " + e.getMessage());
        } finally {
            db.endTransaction(); // End the transaction
            db.close();
        }
    }

    public void addMessage(String messageID, String chatID, String attachments, String message, String senderID, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values1 = new ContentValues();

        values1.put("messageID", messageID);
        values1.put("chatID", chatID);
        values1.put("attachments", attachments);
        values1.put("message", message);
        values1.put("senderID", senderID);
        values1.put("timestamp", timestamp);

        long result1 = db.insert(TABLE_NAME_4, null, values1);
        if (result1 == -1) {
            Log.e(TAG, "Failed to insert data into Messages table.");
        } else {
            Log.d(TAG, "Successfully inserted data into Messages table. Row ID: " + result1);
            ContentValues values2 = new ContentValues();
            String selection = "chatID = ?";//reference col
            String[] selectionArgs = {chatID};//row ID in the col reference
            values2.put("lastMessageID", messageID);//col to change
            int count = db.update(TABLE_NAME_3, values2, selection, selectionArgs);
        }

    }

    public void addMessageList(List<MessageModel> messagesToInsert) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction(); // Start a transaction for better performance

            for (MessageModel message : messagesToInsert) {
                ContentValues values1 = new ContentValues();

                values1.put("messageID", message.getMessageID());
                values1.put("chatID", message.getChatID());
                values1.put("attachments", String.join(",", message.getAttachments())); // Store attachments as a comma-separated string
                values1.put("message", message.getMessage());
                values1.put("senderID", message.getSenderID());
                values1.put("timestamp", message.getTimestamp());

                long result1 = db.insert(TABLE_NAME_4, null, values1);
                if (result1 == -1) {
                    Log.e(TAG, "Failed to insert message with ID: " + message.getMessageID());
                } else {
                    Log.d(TAG, "Successfully inserted message with ID: " + message.getMessageID());

                    ContentValues values2 = new ContentValues();
                    String selection = "chatID = ?";
                    String[] selectionArgs = {message.getChatID()};
                    values2.put("lastMessageID", message.getMessageID());

                    int count = db.update(TABLE_NAME_3, values2, selection, selectionArgs);
                    if (count == 0) {
                        Log.e(TAG, "Failed to update lastMessageID for chatID: " + message.getChatID());
                    }
                }
            }
            db.setTransactionSuccessful(); // Mark the transaction as successful
        } catch (Exception e) {
            Log.e(TAG, "Error inserting messages: " + e.getMessage());
        } finally {
            db.endTransaction(); // End the transaction
            db.close();
        }

    }
    public String getLatestTimestampForChat(String chatId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String latestTimestamp = null;

        // Query to get the maximum timestamp for the specified chatID
        Cursor cursor = db.rawQuery(
                "SELECT MAX(timestamp) FROM Messages WHERE chatID = ?",
                new String[]{chatId}
        );

        if (cursor.moveToFirst()) {
            latestTimestamp = cursor.getString(0); // Get the latest timestamp
        }
        cursor.close();
        db.close();

        return latestTimestamp; // Returns null if no messages exist
    }

    public List<MessageModel> getAllMessagesForChat(String chatId) {
        List<MessageModel> messages = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // Define the query to fetch messages for the given chatId
        String query = "SELECT * FROM Messages WHERE chatID = ? ORDER BY timestamp ASC";  // Ordering by timestamp to get messages in the correct order
        Cursor cursor = db.rawQuery(query, new String[]{chatId});

        // Loop through the cursor and retrieve message data
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve the data from each column in the row
                String messageId = cursor.getString(cursor.getColumnIndexOrThrow("messageID"));
                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String senderId = cursor.getString(cursor.getColumnIndexOrThrow("senderID"));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
                String attachments = cursor.getString(cursor.getColumnIndexOrThrow("attachments"));

                // Create MessageModel from the retrieved data
                MessageModel messageModel = new MessageModel(
                        messageId,
                        timestamp,
                        senderId,
                        message,
                        // Assuming attachments is a comma-separated string, you can adjust if it's stored differently
                        attachments != null ? new ArrayList<>(Arrays.asList(attachments.split(","))) : new ArrayList<>(),
                        chatId
                );

                // Add the message to the list
                messages.add(messageModel);
            } while (cursor.moveToNext());
        }

        // Close the cursor
        if (cursor != null) {
            cursor.close();
        }

        // Return the list of messages
        return messages;
    }





}
