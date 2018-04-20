package com.bignerdranch.andorid.deutschcrime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.andorid.deutschcrime.database.CrimeBaseHelper;
import com.bignerdranch.andorid.deutschcrime.database.CrimeCursorWrapper;
import com.bignerdranch.andorid.deutschcrime.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 싱글톤!!!!!!!! singleton
 * Created by 최문경 on 2017-12-04.
 */
//싱글톤을 생성할 때는 private생성자와 get()메서드를 각각 하나씩 갖는 클래스를 생성해야 한다.
public class CrimeLab {

    //안드로이드 작명 규칙에서는 static 변수의 접두사로 s를 사용
    //다른 클래스에서 CrimeLab 인스턴스를 생성할 때는 이 생성자를 호출할 수 없음 -> 반드시 get 메서드 호출
    private static CrimeLab sCrimeLab;
    // List 타입의 객체 참조를 저장
    // private List<Crime> mCrimes;
    // SQLiteDatabase 열기
    private Context mContext;
    private SQLiteDatabase mDatabase;

    //이 경우는 클래스가 자신의 인스턴스인 sCrimeLab을 가지고 있으므로 get()메서드에서 기존 인스턴스를 반환한다.
    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context){
        // SQLiteDatabase 열기
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase();
        //CrimeLab의 생성자에서 Crime객체를 저장하는 비어있는 List를 생성한다.
        //ArrayList 도 List 타입이므로 저장가능.
        //객체 참조를 저장하는 변수의 타입은 여기처럼 상위 타입으로 선언하는 것이 좋다.
        //mCrimes = new ArrayList<>();
    }
    public void addCrime(Crime c){
        //mCrimes.add(c);
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }
    //생성된 List를 반환하는 getCrimes() 메서드
    public List<Crime> getCrimes(){
        // return mCrimes;
        //return new ArrayList();
        //Crime 리스트 반환하기
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return crimes;
    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);

        return values;
    }

    // 데이터 쿼리하기
    //private Cursor queryCrimes(String whereClause, String[] whereArgs){
    // 커서래퍼를 사용하도록 수정하기
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,  // null인 경우 테이블의 모든 열을 의미
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        //return cursor;
        return new CrimeCursorWrapper(cursor);
    }

    //지정된 ID를 갖는 Crime 객체를 반환하는 메서드
    public Crime getCrime(UUID id){
        //for (Crime crime : mCrimes){
            //if(crime.getId().equals(id)){
                //return crime;
        //    }
        //}
        //return null;
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ? ",
                new String[] {id.toString()}
        );

        try{
            if (cursor.getCount()==0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        }finally{
            cursor.close();
        }
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = ? ",
                new String[] {uuidString});
    }
}
