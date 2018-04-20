package com.bignerdranch.andorid.deutschcrime;

import android.support.v4.app.Fragment;

/**
 * Created by 최문경 on 2017-12-04.
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }
}
