package com.mihailproductions.jobapplier.Handlers;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.mihailproductions.jobapplier.model.Student;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ApplicationsManager";
    private static final String TABLE_Students = "Students";

    private static final String KEY_ID = "id";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_COLLEGE = "college";
    private static final String KEY_SKILLS = "skills";
    private static final String KEY_PHONE = "phone";

    private static final String TAG = "DBHelper";

    private static DatabaseHandler sInstance;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_Students_TABLE = "CREATE TABLE " + TABLE_Students + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_LASTNAME + " TEXT,"
                + KEY_FIRSTNAME + " TEXT," + KEY_EMAIL + " TEXT,"
                + KEY_COLLEGE + " TEXT," + KEY_SKILLS + " TEXT,"
                + KEY_PHONE + " INTEGER" + ")";
        db.execSQL(CREATE_Students_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Students);
        onCreate(db);
    }

    //CRUD Operations
    public void addStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_LASTNAME, student.getLastName());
            values.put(KEY_FIRSTNAME, student.getFirstName());
            values.put(KEY_EMAIL, student.getEmail());
            values.put(KEY_COLLEGE, student.getCollege());
            values.put(KEY_SKILLS, student.getSkills());
            values.put(KEY_PHONE, student.getPhone());
            db.insert(TABLE_Students, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add student to database");
        } finally {
            db.endTransaction();
        }
    }

    public Student getStudent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Student student = null;
// Table - Columns - Where or selection - selection args(null) - groupBy(null) - having(null) - OrderBy(null)
        Cursor cursor = db.query(TABLE_Students, new String[]{KEY_ID,
                        KEY_LASTNAME, KEY_FIRSTNAME, KEY_EMAIL, KEY_COLLEGE, KEY_SKILLS, KEY_PHONE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                student = new Student(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), Integer.parseInt(cursor.getString(6)));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get applicants from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return student;
    }

    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<Student>();
        String selectQuery = "SELECT * FROM " + TABLE_Students;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Student student = null;
        try {
            if (cursor.moveToFirst()) {
                do {
                    student = new Student();
                    student.setId(Integer.parseInt(cursor.getString(0)));
                    student.setLastName(cursor.getString(1));
                    student.setFirstName(cursor.getString(2));
                    student.setEmail(cursor.getString(3));
                    student.setCollege(cursor.getString(4));
                    student.setSkills(cursor.getString(5));
                    student.setPhone(Integer.parseInt(cursor.getString(6)));
                    studentList.add(student);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get students from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return studentList;
    }

    public void updateStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_LASTNAME, student.getLastName());
            values.put(KEY_FIRSTNAME, student.getFirstName());
            values.put(KEY_EMAIL, student.getEmail());
            values.put(KEY_COLLEGE, student.getCollege());
            values.put(KEY_SKILLS, student.getSkills());
            values.put(KEY_PHONE, student.getPhone());
            db.update(TABLE_Students, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(student.getId())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update student");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_Students, KEY_ID + " = ?",
                    new String[]{String.valueOf(student.getId())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete student");
        } finally {
            db.endTransaction();
        }
    }

    public int getStudentsCount() {
        int count = 0;
        String countQuery = "SELECT * FROM " + TABLE_Students;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        try {
            count = cursor.getCount();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get students from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return count;
    }
}
