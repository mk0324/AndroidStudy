package com.bignerdranch.andorid.deutschcrime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by 최문경 on 2017-12-04.
 */

public class CrimeListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    // 서브 타이틀의 가시성 정보 보존하기
    private boolean mSubtitleVisible;
    private static final int REQUEST_CRIME = 1;

    // 운영체제가 Activity의 onCreateOptionMenu 콜백 메서드를 호출했을때
    // FragmentManager 는 Fragment.onCreateOptionsMenu를 호출하는 책임을 갖는다
    // 그러나 호출을 받는 사실은 명시적으로 FragmentManager 에게 알려주어야 한다.
    // setHasOptionsMenu 가 그 메서드이다.
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI();
        return view;
    }
    //onResume()에서 리스트 다시 로드하기
    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        // 메뉴파일의 리소스를 인자로 전달
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }


    // 메뉴 선택에 응답하기
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity
                        .newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                // 사용자가 액션항목을 선택했을 때 액션항목을 다시 생성하도록 코드 추가한 것
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // 툴바의 서브타이틀 설정하기
    private void updateSubtitle(){
        // 호스팅하는 액티비티의 인스턴스 상태를 전달하여 범죄리스트를 받아옴,????
        // CrimeLab 은 싱글톤 객체이기 때문에 get()메서드를 통해서 접근 필요하다.
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        // getCrimes() 메서드가 리스트를 반환하는 메서드이므로 갯수 반환가능
        int crimeCount = crimeLab.getCrimes().size();
        // getString()메서드를 사용하여 서브타이틀 문자열(앱 이름 아래에 생김)을 생성한다.
        // 이때 범죄 건수를 두 번째 인자로 받는다.
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        if(!mSubtitleVisible){
            subtitle = null;
        }
        // CrimeListFragment 를 호스팅하는 액티비티의 타입을 AppCompatActivity 로 캐스팅(변환)한다.
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(mAdapter==null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }else{
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
            //mAdapter.notifyItemChanged();
        }
        updateSubtitle();
    }

    //ViewHolder구현하기
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        public CrimeHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            //뷰 홀더에서 각 뷰의 객체 찾기
            mTitleTextView = (TextView)itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView)itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
        }
        //뷰와 데이터 결합하기
        public void bindCrime(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }
        //각 뷰는 자신과 연관된 ViewHolder를 갖고 있으므로 ViewHolder에서 리스너 구현!
        @Override
        public void onClick(View view) {
            //Toast.makeText(getActivity(),mCrime.getTitle() + " 선택됨!", Toast.LENGTH_SHORT).show();
            //CrimeActivity의 인스턴스를 시작시키는 코드
            //Intent intent = new Intent(getActivity(), CrimeActivity.class);
            //Crime 객체 ID를 전달하는 newIntent()메서드를 사용하도록 변경.
            //Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getId());
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivityForResult(intent, REQUEST_CRIME);
            //Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            //startActivity(intent);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==REQUEST_CRIME){
            //결과처리코드
        }
    }
    //어댑터 구현하기
    //어댑터를 홀더에 연결하기
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }
        @Override
        //여기서 뷰 홀더
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType){
            //
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            //뷰 객체를 생성하여 범죄리스트 레이아웃(범죄제목, 범죄 날짜, 해결여부) 인플레이트
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            //범죄 객체 레이아웃 인플레이트 된 view를 가진 뷰 홀더
            //onBindViewHolder() 메서드가 ViewHolder와 데이터 위치를 인자로 받아서
            //범죄 데이터를 찾은 후 ViewHolder의 TextView를 변경한다.
            return new CrimeHolder(view);
        }
        @Override
        //ViewHolder와 데이터 위치를 인자로 받아서
        //범죄 데이터를 찾은 후 ViewHolder의 TextView를 변경한다.
        //리스트에 항목 데이터를 보여줄 필요가 있을 때 마다 호출되어 onCreateViewHolder보다 훨씬 많이 호출된다.
        public void onBindViewHolder(CrimeHolder holder, int position){
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }
        @Override
        public int getItemCount(){
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }
    }
}
