package com.example.myapplication.AlarmPlayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SqliteDatabase extends SQLiteOpenHelper {
    private	static final int DATABASE_VERSION =	5;
    private	static final String	DATABASE_NAME = "timeapps";
    private	static final String TABLE_MYNOTES = "mynotes";

    private static final String COLUMN_ID = "ids";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DETAIL = "detail";

    public SqliteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String	CREATE_CONTACTS_TABLE = "CREATE	TABLE " + TABLE_MYNOTES + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_TITLE + " TEXT," + COLUMN_DETAIL + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MYNOTES);
        onCreate(db);
    }

    public ArrayList<MyNotes> listMyNotes(){
        String sql = "select * from " + TABLE_MYNOTES;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<MyNotes> storeMyNotes = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                int ids = Integer.parseInt(cursor.getString(0));
                String title = cursor.getString(1);
                String detail = cursor.getString(2);
                storeMyNotes.add(new MyNotes(ids, title, detail));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return storeMyNotes;
    }

    public void addMyNotes(MyNotes mynotes){
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, mynotes.getTitle());
        values.put(COLUMN_DETAIL, mynotes.getDetail());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_MYNOTES, null, values);
    }

    public void updateMyNotes(MyNotes mynotes){
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, mynotes.getTitle());
        values.put(COLUMN_DETAIL, mynotes.getDetail());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_MYNOTES, values, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(mynotes.getIds())});
    }

    public MyNotes findMyNotes(String name){
        String query = "Select * FROM "	+ TABLE_MYNOTES + " WHERE " + COLUMN_TITLE + " = " + "title";
        SQLiteDatabase db = this.getWritableDatabase();
        MyNotes mynotes = null;
        Cursor cursor = db.rawQuery(query,	null);
        if	(cursor.moveToFirst()){
            int id = Integer.parseInt(cursor.getString(0));
            String mynotesName = cursor.getString(1);
            String mynotesNo = cursor.getString(2);
            mynotes = new MyNotes(id, mynotesName, mynotesNo);
        }
        cursor.close();
        return mynotes;
    }

    public void deleteContact(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MYNOTES, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(id)});
    }
}
