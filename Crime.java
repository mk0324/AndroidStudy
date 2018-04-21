package com.bignerdranch.andorid.deutschcrime;

//import java.lang.Object;
//import java.text.Format;
//import android.text.format.DateFormat;
import java.util.Date;
import java.util.UUID;
//CriminalIntent 앱의 모델 계층인 Crime 클래스
/**
 * Created by 최문경 on 2017-10-06.
 */

public class Crime {

    //UUID는 Universally Unique Identifier 의 줄임말로, 128 비트의 고유한 값을 나타내는 자바 클래스
    //randomUUID()는 UUID의 static메서드인 randomUUID()를 호츌하여 임의의 UUID 값을 갖는 UUID 객체를 생성.
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    //private DateFormat mDateFormat;

    public Crime(){
        this(UUID.randomUUID());
        //고유한 식별자를 생성한다.
        //mId = UUID.randomUUID();
        //인자가 없는 디폴트 생성자를 사용해서 현재의 시스템 날짜를 받는다.
        //mDate = new Date();
    }
    // 또 다른 Crime 생성자 추가하기
    public Crime(UUID id){
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() { return mDate; }

    public void setDate(Date date) {
       // mDate = new Date();
       // date = mDateFormat.getDateInstance().format(mDate);
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solves) {
        mSolved = solves;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getPhotoFilename(){
        return "IMG_" + getId().toString() + ".jpg";
    }
}
