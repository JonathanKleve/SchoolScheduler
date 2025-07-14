package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myapplication.activities.MainActivity;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SchedulerDatabase.db";
    private static final int DATABASE_VERSION = 2;
    private static DatabaseHelper instance;
    private SQLiteDatabase database;

    //table and column names for Terms
    public static final String TABLE_TERMS = "terms";
    public static final String COLUMN_TERM_ID = "id";
    public static final String COLUMN_TERM_TITLE = "title";
    public static final String COLUMN_TERM_START = "start";
    public static final String COLUMN_TERM_END = "end";

    //table and column names for Instructors
    public static final String TABLE_INSTRUCTORS = "instructors";
    public static final String COLUMN_INSTRUCTOR_ID = "id";
    public static final String COLUMN_INSTRUCTOR_NAME = "name";
    public static final String COLUMN_INSTRUCTOR_PHONE = "phone";
    public static final String COLUMN_INSTRUCTOR_EMAIL = "email";


    //table and column names for Courses
    public static final String TABLE_COURSES = "courses";
    public static final String COLUMN_COURSE_ID = "id";
    public static final String COLUMN_COURSE_TITLE = "title";
    public static final String COLUMN_COURSE_TYPE = "course_type";
    public static final String COLUMN_COURSE_START = "start";
    public static final String COLUMN_COURSE_END = "end";
    public static final String COLUMN_COURSE_STATUS = "status";
    public static final String COLUMN_COURSE_INSTRUCTOR_ID = "instructor_id";
    public static final String COLUMN_COURSE_NOTES = "notes";
    public static final String COLUMN_COURSE_TERM_ID = "term_id";


    // Table and column names for Assessments
    public static final String TABLE_ASSESSMENTS = "assessments";
    public static final String COLUMN_ASSESSMENT_ID = "id";
    public static final String COLUMN_ASSESSMENT_TITLE = "title";
    public static final String COLUMN_ASSESSMENT_TYPE = "assessment_type";
    public static final String COLUMN_ASSESSMENT_START = "start";
    public static final String COLUMN_ASSESSMENT_END = "end";
    public static final String COLUMN_ASSESSMENT_COURSE_ID = "course_id";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); // DatabaseHelper gets the Context.
        Log.d("DatabaseHelper", "DatabaseHelper constructor called");
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }
    public synchronized SQLiteDatabase getDatabase() {
        if (database == null || !database.isOpen()) {
            Log.d("DatabaseHelper", "Getting a new/opened database instance");
            database = getWritableDatabase(); // Or getReadableDatabase() depending on needs
        }
        Log.d("DatabaseHelper", "Returning database instance: " + database.hashCode());
        return database;
    }

    public synchronized void closeDatabase() {
        if (database != null && database.isOpen()) {
            Log.d("DatabaseHelper", "Closing the long-lived database instance");
            database.close();
            database = null;
        } else {
            Log.d("DatabaseHelper", "closeDatabase() called but database is null or not open");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DatabaseHelper", "Creating database tables");
        //Create the terms table
        String CREATE_TERMS_TABLE = "CREATE TABLE " + TABLE_TERMS + "("
                + COLUMN_TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TERM_TITLE + " TEXT,"
                + COLUMN_TERM_START + " TEXT,"
                + COLUMN_TERM_END + " TEXT" + ")";
        db.execSQL(CREATE_TERMS_TABLE);

        //Create the instructors table
        String CREATE_INSTRUCTORS_TABLE = "CREATE TABLE " + TABLE_INSTRUCTORS + "("
                + COLUMN_INSTRUCTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_INSTRUCTOR_NAME + " TEXT,"
                + COLUMN_INSTRUCTOR_PHONE + " TEXT,"
                + COLUMN_INSTRUCTOR_EMAIL + " TEXT" + ")";
        db.execSQL(CREATE_INSTRUCTORS_TABLE);

        //Create the courses table
        String CREATE_COURSES_TABLE = "CREATE TABLE " + TABLE_COURSES + "("
                + COLUMN_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_COURSE_TITLE + " TEXT,"
                + COLUMN_COURSE_TYPE + " TEXT,"
                + COLUMN_COURSE_START + " TEXT,"
                + COLUMN_COURSE_END + " TEXT,"
                + COLUMN_COURSE_STATUS + " TEXT,"
                + COLUMN_COURSE_INSTRUCTOR_ID + " INTEGER,"
                + COLUMN_COURSE_NOTES + " TEXT,"
                + COLUMN_COURSE_TERM_ID + " INTEGER,"
                + "FOREIGN KEY (" + COLUMN_COURSE_INSTRUCTOR_ID + ") REFERENCES " + TABLE_INSTRUCTORS + "(" + COLUMN_INSTRUCTOR_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY (" + COLUMN_COURSE_TERM_ID + ") REFERENCES " + TABLE_TERMS + "(" + COLUMN_TERM_ID + ") ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
        db.execSQL(CREATE_COURSES_TABLE);

        //Create the assessments table
        String CREATE_ASSESSMENTS_TABLE = "CREATE TABLE " + TABLE_ASSESSMENTS + "("
                + COLUMN_ASSESSMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ASSESSMENT_TITLE + " TEXT,"
                + COLUMN_ASSESSMENT_TYPE + " TEXT,"
                + COLUMN_ASSESSMENT_START + " TEXT,"
                + COLUMN_ASSESSMENT_END + " TEXT,"
                + COLUMN_ASSESSMENT_COURSE_ID + " INTEGER,"
                + "FOREIGN KEY (" + COLUMN_ASSESSMENT_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + COLUMN_COURSE_ID + ") ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
        db.execSQL(CREATE_ASSESSMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Handle database upgrades (e.g., dropping and recreating tables)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTRUCTORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENTS);
        onCreate(db);
    }

    public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = super.getReadableDatabase();
        Log.d("DatabaseHelper", "Getting readable database: " + db.hashCode());
        return db;
    }

    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = super.getWritableDatabase();
        Log.d("DatabaseHelper", "Getting writable database: " + db.hashCode());
        return db;
    }
}