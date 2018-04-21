package com.bignerdranch.andorid.deutschcrime;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
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
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
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
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;

    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mSuspectButton;
    private Button mReportButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

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
        // 사진 파일 위치 저장하기
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
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
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        //인텐트 필터 테스터를 위한 코드 -> CATEGORY_HOME 플래그를 추가하여 테스트해보기
        //pickContact.addCategory(Intent.CATEGORY_HOME);
        mSuspectButton=(Button)v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }
        // 전송하기 버튼 에서 참조 얻는 리스너 구현하기
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // 액션을 정의하는 상수 문자열을 인자로 받는 Intent 생성자를 사용
                // 생성해야할 암시적 인텐트의 종류에 따라 다른 생성자를 사용할 수 있음
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                // 암시적 인텐트가 사용될 때마다 매번 선택기가 나타나도록 하기.
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });
        // 장치에 적합한 앱이 없을 경우 운영체제가 일치하는 액티비티를 찾을 수 없으므로 앱이 중단됨
        // 해결책으로 안드로이드 운영체제의 일부인 PackageManager 를 확인하는 방법
        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_camera);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 인텐트를 받아 실행될 카메라 앱이 없거나 또는 사진을 저장할 위치가 없는 경우 버튼을 비활성화
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if(canTakePhoto){
            //Uri uri = Uri.fromFile(mPhotoFile);
            Uri uri = FileProvider.getUriForFile(getContext(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               startActivityForResult(captureImage, REQUEST_PHOTO);
           }
        });

        mPhotoView = (ImageView)v.findViewById(R.id.crime_photo);
        updatePhotoView();
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
            updateDate();
        }else if(requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            // 값을 반환할 쿼리 필드를 지정한다.
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            // 쿼리를 수행한다. 여기서 contactUri는
            // SQL 의 "where" 절에 해당
            Cursor c = getActivity().getContentResolver()
            .query(contactUri, queryFields, null, null, null);
            try{
                // 쿼리의 결과 데이터가 있는지 재확인한다.
                if(c.getCount() == 0){
                    return;
                }
                // 첫번째 데이터 행(row)의 첫 번째 열(column)을 추출
                // 그것이 함께할 사람의 이름
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            }finally {
                c.close();
            }
        }else if(requestCode == REQUEST_PHOTO){
            updatePhotoView();
        }
    }
    private void updateDate(){
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport(){
        String solvedString = null;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        }else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    private void updatePhotoView(){
        if(mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    public void returnResult(){
        getActivity().setResult(Activity.RESULT_OK, null);
    }

}

