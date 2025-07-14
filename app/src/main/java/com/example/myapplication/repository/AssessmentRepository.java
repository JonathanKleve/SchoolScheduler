package com.example.myapplication.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.datamodel.Assessment;
import com.example.myapplication.datamodel.Instructor;

public class AssessmentRepository {
    private DatabaseHelper databaseHelper;
    private MutableLiveData<List<Assessment>> allAssessmentsLiveData = new MutableLiveData<>();

    public AssessmentRepository(Context context) {
        this.databaseHelper = DatabaseHelper.getInstance(context);
        loadAllAssessments(); // Load initial data
    }

    public LiveData<List<Assessment>> getAllAssessments() {
        return allAssessmentsLiveData;
    }

    public LiveData<Assessment> getAssessmentById(int assessmentId) {
        MutableLiveData<Assessment> assessmentLiveData = new MutableLiveData<>();
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            Cursor cursor = null;
            Assessment assessment = null;
            try {
                cursor = db.query(DatabaseHelper.TABLE_ASSESSMENTS, null, DatabaseHelper.COLUMN_ASSESSMENT_ID + " = ?", new String[]{String.valueOf(assessmentId)}, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    assessment = mapCursorToAssessment(cursor);
                }
                assessmentLiveData.postValue(assessment);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }).start();
        return assessmentLiveData;
    }

    public long insertAssessment(Assessment assessment) {
            SQLiteDatabase db = databaseHelper.getDatabase();
            ContentValues values = mapAssessmentToContentValues(assessment);
            long id = db.insert(DatabaseHelper.TABLE_ASSESSMENTS, null, values);
            loadAllAssessments(); // Reload the list after insertion
            return id;
    }

    public void updateAssessment(Assessment assessment) {
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            ContentValues values = mapAssessmentToContentValues(assessment);
            db.update(DatabaseHelper.TABLE_ASSESSMENTS, values, DatabaseHelper.COLUMN_ASSESSMENT_ID + " = ?", new String[]{String.valueOf(assessment.getId())});
            loadAllAssessments(); // Reload the list after update
        }).start();
    }

    public void deleteAssessment(Assessment assessment) {
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            db.delete(DatabaseHelper.TABLE_ASSESSMENTS, DatabaseHelper.COLUMN_ASSESSMENT_ID + " = ?", new String[]{String.valueOf(assessment.getId())});
            loadAllAssessments(); // Reload the list after deletion
        }).start();
    }

    private void loadAllAssessments() {
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            Cursor cursor = null;
            List<Assessment> assessments = new ArrayList<>();
            try {
                cursor = db.query(DatabaseHelper.TABLE_ASSESSMENTS, null, null, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Assessment assessment = mapCursorToAssessment(cursor);
                        assessments.add(assessment);
                    } while (cursor.moveToNext());
                    allAssessmentsLiveData.postValue(assessments);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }).start();
    }

    private Assessment mapCursorToAssessment(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ASSESSMENT_ID));
        String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ASSESSMENT_TITLE));
        String type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ASSESSMENT_TYPE));
        LocalDate start = LocalDate.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ASSESSMENT_START)), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate end = LocalDate.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ASSESSMENT_END)), DateTimeFormatter.ISO_LOCAL_DATE);
        int courseId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ASSESSMENT_COURSE_ID));
        return new Assessment(id, title, type, start, end, courseId);
    }

    private ContentValues mapAssessmentToContentValues(Assessment assessment) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ASSESSMENT_TITLE, assessment.getTitle());
        values.put(DatabaseHelper.COLUMN_ASSESSMENT_TYPE, assessment.getType());
        values.put(DatabaseHelper.COLUMN_ASSESSMENT_START, assessment.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE));
        values.put(DatabaseHelper.COLUMN_ASSESSMENT_END, assessment.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE));
        values.put(DatabaseHelper.COLUMN_ASSESSMENT_COURSE_ID, assessment.getCourseId());
        return values;
    }
}