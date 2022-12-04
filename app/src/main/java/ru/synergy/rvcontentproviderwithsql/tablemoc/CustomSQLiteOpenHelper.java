package ru.synergy.rvcontentproviderwithsql.tablemoc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class CustomSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "CustomSqLiteOpenHelper";

    public CustomSQLiteOpenHelper(Context context) {
        super(context, "db.db", null, 1);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TabbleItems.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL(TabbleItems.DROP_TABLE);
    onCreate(db);

    }
}
