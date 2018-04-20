package com.bignerdranch.andorid.deutschcrime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

/**
 * Created by 최문경 on 2017-10-06.
 */

public class CrimeFragment extends Fragment {
    //Crime 인스턴스의 멤버 변수 추가
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CRIME = 1;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;

    //newInstance(UUID)메서드 작성하기
    //CrimeActivity에서 CrimeFragment.newInstance(UUID)를 호출하여 CrimeFragment를 생성할 것.
    //이때 자신의 엑스트라(CrimeActivity)에서 가져온 UUID를 메서드 인자로 전달한다.
    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //메서드들이 액티비티에서와 다르게 public인 이유는 프레그먼트를 호스팅하는 모든 액티비티에서 호출될 것이기 때문이다.
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // 메뉴 콜백 호출 받기
        setHasOptionsMenu(true);
        //mCrime = new Crime();
        //UUID crimeId = (UUID)getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);
        //프래그먼트가 자신에게 전달된 인자를 액세스하려면 Fragment 클래스의 메서드인 getArgument()를 호출한 후
        //Bundle의 get메서드들(타입별로 다름) 중 하나를 호출하면 된다.
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }
    // 범죄 인스턴스는 CrimeFragment에서 변경되므로
    // CrimeFragment가 일시중단될 때는 데이터 베이스에 저장하도록
    // onPause()메서드 오버라이드하기
    @Override
    public void onPause(){
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        // 메뉴파일의 리소스를 인자로 전달
        inflater.inflate(R.menu.detail_fragment_menu, menu);
    }
    // 메뉴 선택에 응답하기
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_show_choose_delete:
                UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
                mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                // 호스팅하는 액티비티를 받아와서 종료 -> 상위 액티비티로 전환됨.
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
/*
    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        if(!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
*/

    //onCreateView 메서드 오버라이드하기
    @Override
    //onCreate 메서드가 아니라 onCreateView 메서드에서 프래그먼트 뷰 생성&구성.
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                //이 메서드의 실행 코드는 여기서는 필요없음
            }
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count){
                mCrime.setTitle(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s){
                //이 메서드의 실행코드는 여기서는 필요없음
            }
        });
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate();
        //mDataButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                //DatePickerFragment dialog = new DatePickerFragment();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        mSolvedCheckBox=(CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //범죄 해결 여부 속성 값을 설정한다.
                mCrime.setSolved(isChecked);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_DATE){
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
        }
    }
    private void updateDate(){
        mDateButton.setText(mCrime.getDate().toString());
    }

    public void returnResult(){
        getActivity().setResult(Activity.RESULT_OK, null);
    }

}

