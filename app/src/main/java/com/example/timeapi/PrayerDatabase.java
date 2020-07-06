package com.example.timeapi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.TimedMetaData;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.timeapi.datas.Timings;

import java.util.ArrayList;
import java.util.List;

public class PrayerDatabase extends SQLiteOpenHelper {

    public static String DBName = "prayer";

    public static final String TABLE_NAME="timetable";
    public static final String _id="id";
    public static final String ASR="Asr";// this Asr value was wrong, it would be asr
    public static final String DHUHR="duhr";
    public static final String FAJR="fajar";
    public static final String MAHGRIB="mahgrib";
    public static final String ISHA="isha";

    public static String CREATE_LOGIN_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("+_id+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
            ASR+" TEXT NOT NULL,"+
            DHUHR+" TEXT NOT NULL,"+
            FAJR+" TEXT NOT NULL,"+
            MAHGRIB+" TEXT NOT NULL,"+
            ISHA+" TEXT NOT NULL);";


    public PrayerDatabase(Context context) {
        super(context, DBName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_LOGIN_TABLE);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }








    //insert customer code into order main table
    boolean insert(Timings timings){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ASR,timings.getAsr());
        values.put(DHUHR,timings.getDhuhr());
        values.put(FAJR,timings.getFajr());
        values.put(MAHGRIB,timings.getMaghrib());
        values.put(ISHA,timings.getIsha());
        return db.insert(TABLE_NAME,null, values)>0; // what is return db.insert in >0 ? meaning

        // if the data insert it give positive number if not insert negative  number, so positive number give and boolean true?
    }


//    boolean update(Timings timings){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(ASR,timings.getAsr());
//        values.put(DHUHR,timings.getDhuhr());
//        values.put(FAJR,timings.getFajr());
//        values.put(MAHGRIB,timings.getMaghrib());
//        values.put(ISHA,timings.getIsha());
//        return db.update(TABLE_NAME,values,"",null);
//
//    }


    //delete dunction
    public void delete(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+TABLE_NAME);
    }




    //get ordered customer from order main tbl
    ArrayList<Timings> getTimming() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Timings> arrayList = new ArrayList<>();
        try {

            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
            cursor.moveToFirst();

            // what is cursor.isAfterLast() , i didn't use this..
            while (cursor.isAfterLast() == false) {

                arrayList.add(new Timings( cursor.getString(1), cursor.getString(2), cursor.getString(3),"", cursor.getString(4),
                        cursor.getString(5),"","",""));

                cursor.moveToNext();
            }
            return arrayList;
        } catch (Exception ex) {
            Log.d("done",ex.toString());
            return arrayList;
        }
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
