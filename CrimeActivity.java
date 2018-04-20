package com.bignerdranch.andorid.deutschcrime;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    //public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

    //명시적 인텐트 생성 후 문자열의 키와 그 키의 값을 인자로 전달
    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }
    @Override
    protected Fragment createFragment(){
        //return new CrimeFragment();
        //객체 고유의 식별자를 변수에 저장하여 전달
        UUID crimeId = (UUID)getIntent()
                .getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }
}
