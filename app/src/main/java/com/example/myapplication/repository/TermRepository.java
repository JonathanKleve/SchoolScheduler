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
import com.example.myapplication.datamodel.Term;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TermRepository {
    private DatabaseHelper databaseHelper;
    private MutableLiveData<List<Term>> allTermsLiveData = new MutableLiveData<>();

    public TermRepository(Context context) {
        this.databaseHelper = DatabaseHelper.getInstance(context);
        loadAllTerms(); // Load initial data when the repository is created
    }

    public LiveData<List<Term>> getAllTerms() {
        return allTermsLiveData;
    }

    public LiveData<Term> getTermById(int termId) {
        MutableLiveData<Term> termLiveData = new MutableLiveData<>();
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            Cursor cursor = null;
            Term term = null;
            try {
                cursor = db.query(DatabaseHelper.TABLE_TERMS, null, DatabaseHelper.COLUMN_TERM_ID + " = ?", new String[]{String.valueOf(termId)}, null, null, null);
                if(cursor != null && cursor.moveToFirst()) {
                    term = mapCursorToTerm(cursor);
                }
                termLiveData.postValue(term);
            } finally {
                if (cursor !=null){
                    cursor.close();
                }
            }
        }).start();
        return termLiveData;
    }

    public long insertTerm(Term term) {
        SQLiteDatabase db = databaseHelper.getDatabase();
        ContentValues values = mapTermToContentValues(term);
        long id = db.insert(DatabaseHelper.TABLE_TERMS, null, values);
        loadAllTerms(); // Reload the list after insertion
        return id;
    }

    public void updateTerm(Term term) {
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            ContentValues values = mapTermToContentValues(term);
            db.update(DatabaseHelper.TABLE_TERMS, values, DatabaseHelper.COLUMN_TERM_ID + " = ?", new String[]{String.valueOf(term.getId())});
            loadAllTerms(); // Reload the list after update
        }).start();
    }

    public void deleteTerm(Term term) {
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            db.delete(DatabaseHelper.TABLE_TERMS, DatabaseHelper.COLUMN_TERM_ID + " = ?", new String[]{String.valueOf(term.getId())});
            loadAllTerms(); // Reload the list after deletion
        }).start();
    }

    private void loadAllTerms() {
        new Thread(() -> {
            SQLiteDatabase db = databaseHelper.getDatabase();
            Cursor cursor = null;
            List<Term> termsList = new ArrayList<>();

            try {
                cursor = db.query(DatabaseHelper.TABLE_TERMS, null, null, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Term term = mapCursorToTerm(cursor);
                        termsList.add(term);
                    } while (cursor.moveToNext());
                }
                allTermsLiveData.postValue(termsList);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }).start();
    }

    private Term mapCursorToTerm(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TERM_ID));
        String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TERM_TITLE));
        LocalDate start = LocalDate.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TERM_START)), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate end = LocalDate.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TERM_END)), DateTimeFormatter.ISO_LOCAL_DATE);
        return new Term(id, title, start, end);
    }

    private ContentValues mapTermToContentValues(Term term) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TERM_TITLE, term.getTitle());
        values.put(DatabaseHelper.COLUMN_TERM_START, term.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE));
        values.put(DatabaseHelper.COLUMN_TERM_END, term.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return values;
    }
}