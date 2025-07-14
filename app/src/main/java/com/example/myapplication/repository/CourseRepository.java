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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {

    private DatabaseHelper databaseHelper;
    private MutableLiveData<List<Course>> allCoursesLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Course>> allCoursesByTermIdLiveData = new MutableLiveData<>();

    public CourseRepository(Context context) {
        this.databaseHelper = DatabaseHelper.getInstance(context);
        loadAllCourses(); // Load initial data
    }

    public LiveData<List<Course>> getAllCourses() {
        return allCoursesLiveData;
    }

    public LiveData<Course> getCourseById(int courseId) {
        MutableLiveData<Course> courseLiveData = new MutableLiveData<>();
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            Cursor cursor = null;
            Course course = null;
            try{
                cursor = db.query(DatabaseHelper.TABLE_COURSES, null, DatabaseHelper.COLUMN_COURSE_ID + " = ?", new String[]{String.valueOf(courseId)}, null, null, null);
                if(cursor != null && cursor.moveToFirst()) {
                    course = mapCursorToCourse(cursor);
                }
                courseLiveData.postValue(course);
            } finally {
                if (cursor !=null){
                    cursor.close();
                }
            }
        }).start();
        return courseLiveData;
    }

    public void loadCoursesByTermId(int termId) {
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            Cursor cursor = null;
            List<Course> coursesList = new ArrayList<>();
            try {
                cursor = db.query(DatabaseHelper.TABLE_COURSES, null, DatabaseHelper.COLUMN_COURSE_TERM_ID + " = ?", new String[]{String.valueOf(termId)}, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Course course = mapCursorToCourse(cursor);
                        coursesList.add(course);
                    } while (cursor.moveToNext());
                }
                allCoursesByTermIdLiveData.postValue(coursesList);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }).start();
    }
    public LiveData<List<Course>> getCoursesByTermId () {
        return allCoursesByTermIdLiveData;
    }

    public long insertCourse(Course course) {
        SQLiteDatabase db = databaseHelper.getDatabase();
        ContentValues values = mapCourseToContentValues(course);
        long id = db.insert(DatabaseHelper.TABLE_COURSES, null, values);
        loadAllCourses(); // Reload the list after insertion
        return id;
    }

    public void updateCourse(Course course) {
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            ContentValues values = mapCourseToContentValues(course);
            db.update(DatabaseHelper.TABLE_COURSES, values, DatabaseHelper.COLUMN_COURSE_ID + " = ?", new String[]{String.valueOf(course.getId())});
            loadAllCourses(); // Reload the list after update
        }).start();
    }

    public void deleteCourse(Course course) {
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            db.delete(DatabaseHelper.TABLE_COURSES, DatabaseHelper.COLUMN_COURSE_ID + " = ?", new String[]{String.valueOf(course.getId())});
            loadAllCourses(); // Reload the list after deletion
        }).start();
    }

    private void loadAllCourses() {
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            Cursor cursor = null;
            List<Course> coursesList = new ArrayList<>();
            try {
                cursor = db.query(DatabaseHelper.TABLE_COURSES, null, null, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Course course = mapCursorToCourse(cursor);
                        coursesList.add(course);
                    } while (cursor.moveToNext());
                }
                allCoursesLiveData.postValue(coursesList);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }).start();
    }

    private Course mapCursorToCourse(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_ID));
        String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_TITLE));
        LocalDate start = LocalDate.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_START)), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate end = LocalDate.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_END)), DateTimeFormatter.ISO_LOCAL_DATE);
        String status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_STATUS));
        int instructorId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_INSTRUCTOR_ID));
        String notes = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_NOTES));
        int termId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_TERM_ID));

        return new Course(id, title, start, end, status, instructorId, notes, termId);
    }

    private ContentValues mapCourseToContentValues(Course course) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_COURSE_TITLE, course.getTitle());
        values.put(DatabaseHelper.COLUMN_COURSE_START, course.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE));
        values.put(DatabaseHelper.COLUMN_COURSE_END, course.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE));
        values.put(DatabaseHelper.COLUMN_COURSE_STATUS, course.getStatus());
        values.put(DatabaseHelper.COLUMN_COURSE_INSTRUCTOR_ID, course.getInstructorId());
        values.put(DatabaseHelper.COLUMN_COURSE_NOTES, course.getNotes());
        values.put(DatabaseHelper.COLUMN_COURSE_TERM_ID, course.getTermId());

        return values;
    }
}