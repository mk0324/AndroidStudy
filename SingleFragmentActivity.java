package com.bignerdranch.andorid.deutschcrime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by 최문경 on 2017-12-04.
 */
//이 클래스를 FragmentActivity의 서브클래스로 지정, 추상 클래스이므로 abstract.
public abstract class SingleFragmentActivity extends AppCompatActivity {
    //프레그먼트 인스턴스 생성에 사용되는 추상메서드, CrimeActivity코드와의 유일한 차이점
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        //호출하려면 제일 먼저 FragmentManager인스턴트를 얻는다.
        FragmentManager fm = getSupportFragmentManager();
        //FragmentManager에 프래그먼트를 관리하도록 넘겨주는 코드를 추가
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
