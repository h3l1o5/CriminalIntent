package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by 123 on 2016/10/19.
 */

public class CrimeListActivity extends SingleFragmentActivity {
	@Override
	protected Fragment createFragment(){
		return new CrimeListFragment();
	}
}
