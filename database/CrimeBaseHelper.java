package com.bignerdranch.andorid.deutschcrime.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.andorid.deutschcrime.database.CrimeDbSchema.CrimeTable;

// 데이터베이스 생성을 도와주는 DatabaseHelper 생성하기
public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        // 테이블 생성 코드 추가하기
        db.execSQL("create table " + CrimeTable.NAME + "(" +
                // 각 행의 고유한 ID로 사용되는 _id는 자동 생성!
                "_id integer primary key autoincrement, " +
                CrimeTable.Cols.UUID + ", " +
                CrimeTable.Cols.TITLE + ", " +
                CrimeTable.Cols.DATE + ", " +
                CrimeTable.Cols.SOLVED +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
