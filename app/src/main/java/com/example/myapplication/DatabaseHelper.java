package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ProfileDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PROFILE = "profile";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_BRANCH = "branch";
    private static final String COLUMN_ROLL_NO = "roll_no";
    private static final String COLUMN_REG_NO = "reg_no";
    private static final String COLUMN_EMAIL = "email";

    // Create table query
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_PROFILE + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_IMAGE + " BLOB,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_BRANCH + " TEXT,"
            + COLUMN_ROLL_NO + " TEXT,"
            + COLUMN_REG_NO + " TEXT,"
            + COLUMN_EMAIL + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        onCreate(db);
    }

    // Convert Bitmap to byte array
    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // Convert byte array to Bitmap
    private Bitmap getBitmapFromBytes(byte[] imageBytes) {
        if (imageBytes == null) return null;
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    // Insert or update profile
    public long saveProfile(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_IMAGE, getBytesFromBitmap(profile.getImage()));
        values.put(COLUMN_NAME, profile.getName());
        values.put(COLUMN_BRANCH, profile.getBranch());
        values.put(COLUMN_ROLL_NO, profile.getRollNo());
        values.put(COLUMN_REG_NO, profile.getRegNo());
        values.put(COLUMN_EMAIL, profile.getEmail());

        // Check if profile exists
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILE, null);
        long result;

        if (cursor != null && cursor.getCount() > 0) {
            // Update existing profile (assuming only one profile exists)
            result = db.update(TABLE_PROFILE, values, null, null);
        } else {
            // Insert new profile
            result = db.insert(TABLE_PROFILE, null, values);
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return result;
    }

    // Get profile data
    public Profile getProfile() {
        SQLiteDatabase db = this.getReadableDatabase();
        Profile profile = new Profile();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILE, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Get column indices safely
                int imageIndex = cursor.getColumnIndex(COLUMN_IMAGE);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int branchIndex = cursor.getColumnIndex(COLUMN_BRANCH);
                int rollNoIndex = cursor.getColumnIndex(COLUMN_ROLL_NO);
                int regNoIndex = cursor.getColumnIndex(COLUMN_REG_NO);
                int emailIndex = cursor.getColumnIndex(COLUMN_EMAIL);

                // Check if indices are valid (-1 means column doesn't exist)
                if (imageIndex >= 0) {
                    byte[] imageBytes = cursor.getBlob(imageIndex);
                    profile.setImage(getBitmapFromBytes(imageBytes));
                }

                if (nameIndex >= 0) {
                    profile.setName(cursor.getString(nameIndex));
                }

                if (branchIndex >= 0) {
                    profile.setBranch(cursor.getString(branchIndex));
                }

                if (rollNoIndex >= 0) {
                    profile.setRollNo(cursor.getString(rollNoIndex));
                }

                if (regNoIndex >= 0) {
                    profile.setRegNo(cursor.getString(regNoIndex));
                }

                if (emailIndex >= 0) {
                    profile.setEmail(cursor.getString(emailIndex));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return profile;
    }

    // Optional: Method to check if profile exists
    public boolean isProfileExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILE, null);
            exists = cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return exists;
    }

    // Optional: Method to delete profile
    public void deleteProfile() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROFILE, null, null);
        db.close();
    }
}