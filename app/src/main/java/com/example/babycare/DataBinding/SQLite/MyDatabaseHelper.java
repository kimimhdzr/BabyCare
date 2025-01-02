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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/*
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
                "createdAt TEXT)");
        // Create BabyProfile table
        db.execSQL("CREATE TABLE IF NOT EXISTS BabyProfile (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "documentID TEXT, " +
                "parentID TEXT, " +
                "name TEXT, " +
                "dob TEXT, " +
                "height TEXT, " +
                "weight TEXT, " +
                "bloodType TEXT, " +
                "allergies TEXT, " +
                "profilePic TEXT)");

        // Create Chats table
        String createChatsTable = "CREATE TABLE IF NOT EXISTS Chats (" +
                "chatID TEXT PRIMARY KEY, " +
                "lastMessageID TEXT, " +
                "p_senderID TEXT, " +
                "p_userName TEXT, " +
                "p_profilePic TEXT, " +
                "createdAt TEXT, " +
                "FOREIGN KEY(lastMessageID) REFERENCES Messages(messageID) ON DELETE SET NULL " +
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
                "FOREIGN KEY(chatID) REFERENCES Chats(chatID), " +
                "FOREIGN KEY(senderID) REFERENCES Participants(participantID)" +
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
        String query = "SELECT * FROM " + TABLE_NAME_2 + " WHERE parentID = ?";
        Cursor cursor = db.rawQuery(query, new String[]{parentID});

        if (cursor != null && cursor.moveToFirst()) {
            Log.d(TAG, "Current user found for parentID: " + parentID);
        } else {
            Log.d(TAG, "No user found for parentID: " + parentID);
        }

        return cursor; // Return the cursor for further processing
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

        Log.d(TAG, "Deleted " + deletedRowsCurrentUser + " rows from CurrentUser table.");
        Log.d(TAG, "Deleted " + deletedRowsBabyProfile + " rows from BabyProfile table.");
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

    public void addBabyProfile(String documentID, String parentID, String name, String dob, String height, String weight, String bloodType, String allergies, String profilePic) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("documentID", documentID);
        values.put("parentID", parentID);
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
    public void addChat(String chatID, String participantID, String createdAt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values1 = new ContentValues();

        values1.put("participantID", "");
        values1.put("userName", participantID);
        values1.put("profilePic", createdAt);

        long result2 = db.insert(TABLE_NAME_3, null, values1);
        if (result2 == -1) {
            Log.e(TAG, "Failed to insert data into Chats table.");
        } else {
            Log.d(TAG, "Successfully inserted data into Chats table. Row ID: " + result2);
        }


        ContentValues values2 = new ContentValues();

        values2.put("chatID", chatID);
        values2.put("lastMessageID", "");
        values2.put("participantID", participantID);
        values2.put("createdAt", createdAt);
        /*
        long result2 = db.insert(TABLE_NAME_3, null, values2);
        if (result2 == -1) {
            Log.e(TAG, "Failed to insert data into Chats table.");
        } else {
            Log.d(TAG, "Successfully inserted data into Chats table. Row ID: " + result2);
        }
    }


}
*/