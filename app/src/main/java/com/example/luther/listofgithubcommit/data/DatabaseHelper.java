package com.example.luther.listofgithubcommit.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String dbName = "raxaDB";
    private static DatabaseHelper mInstance = null;

    public DatabaseHelper(Context context) {
        super(context, dbName, null, 1);
    }

    public static synchronized DatabaseHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx);
        }
        return mInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_GIT_DETAILS = "CREATE TABLE " + DataContract.GitDetails.TABLE_NAME + " (" +
                "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DataContract.GitDetails.USER_UUID + " TEXT UNIQUE NOT NULL, " +
                DataContract.GitDetails.REPOSITORY_NAME + " TEXT, " +
                DataContract.GitDetails.DESCRIPTION + " TEXT, " +
                DataContract.GitDetails.LANGUAGE_USED + " TEXT, " +
                DataContract.GitDetails.WATCHERS + " TEXT, " +
                DataContract.GitDetails.BUGS + " TEXT " +
                " );";

        db.execSQL(SQL_CREATE_GIT_DETAILS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DataContract.GitDetails.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}