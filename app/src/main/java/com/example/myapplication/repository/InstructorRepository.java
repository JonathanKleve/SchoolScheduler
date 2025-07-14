package com.example.myapplication.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.datamodel.Course;
import com.example.myapplication.datamodel.Instructor;

import java.util.ArrayList;
import java.util.List;

public class InstructorRepository {
    private DatabaseHelper databaseHelper;
    private MutableLiveData<List<Instructor>> allInstructorsLiveData = new MutableLiveData<>();

    public InstructorRepository(Context context) {
        this.databaseHelper = DatabaseHelper.getInstance(context);
        loadAllInstructors(); // Load initial data
    }

    public LiveData<List<Instructor>> getAllInstructors() {
        return allInstructorsLiveData;
    }

    public LiveData<Instructor> getInstructorById(int instructorId) {
        MutableLiveData<Instructor> instructorLiveData = new MutableLiveData<>();
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            Cursor cursor = db.query(DatabaseHelper.TABLE_INSTRUCTORS, null, DatabaseHelper.COLUMN_INSTRUCTOR_ID + " = ?", new String[]{String.valueOf(instructorId)}, null, null, null);
            Instructor instructor = null;
            if (cursor.moveToFirst()) {
                instructor = mapCursorToInstructor(cursor);
            }
            cursor.close();
            instructorLiveData.postValue(instructor);
        }).start();
        return instructorLiveData;
    }

    public long insertInstructor(Instructor instructor) {
        SQLiteDatabase db = databaseHelper.getDatabase();
        ContentValues values = mapInstructorToContentValues(instructor);
        long id = db.insert(DatabaseHelper.TABLE_INSTRUCTORS, null, values);
        loadAllInstructors(); // Reload the list after insertion
        return id;
    }

    public void updateInstructor(Instructor instructor) {
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            ContentValues values = mapInstructorToContentValues(instructor);
            db.update(DatabaseHelper.TABLE_INSTRUCTORS, values, DatabaseHelper.COLUMN_INSTRUCTOR_ID + " = ?", new String[]{String.valueOf(instructor.getId())});
            loadAllInstructors(); // Reload the list after update
        }).start();
    }

    public void deleteInstructor(Instructor instructor) {
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            db.delete(DatabaseHelper.TABLE_INSTRUCTORS, DatabaseHelper.COLUMN_INSTRUCTOR_ID + " = ?", new String[]{String.valueOf(instructor.getId())});
            loadAllInstructors(); // Reload the list after deletion
        }).start();
    }

    private void loadAllInstructors() {
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            Cursor cursor = null;
            List<Instructor> instructorsList = new ArrayList<>();
            try {
                cursor = db.query(DatabaseHelper.TABLE_INSTRUCTORS, null, null, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Instructor instructor = mapCursorToInstructor(cursor);
                        instructorsList.add(instructor);
                    } while (cursor.moveToNext());
                    allInstructorsLiveData.postValue(instructorsList);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }).start();
    }

    private Instructor mapCursorToInstructor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_INSTRUCTOR_ID));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_INSTRUCTOR_NAME));
        String phone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_INSTRUCTOR_PHONE));
        String email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_INSTRUCTOR_EMAIL));
        return new Instructor(id, name, phone, email);
    }

    private ContentValues mapInstructorToContentValues(Instructor instructor) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_INSTRUCTOR_NAME, instructor.getName());
        values.put(DatabaseHelper.COLUMN_INSTRUCTOR_PHONE, instructor.getPhone());
        values.put(DatabaseHelper.COLUMN_INSTRUCTOR_EMAIL, instructor.getEmail());
        return values;
    }
}