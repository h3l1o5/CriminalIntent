package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.List;
import java.util.UUID;

/**
 * Created by Martian on 2016/10/21.
 */

public class CrimePagerActivity extends FragmentActivity {
	public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime.id";
	private ViewPager mViewPager;
	private List<Crime> mCrimes;

	public static Intent newIntent(Context packageContext, UUID crimeID) {
		Intent intent = new Intent(packageContext, CrimePagerActivity.class);
		intent.putExtra(EXTRA_CRIME_ID, crimeID);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crime_pager);
		UUID crimeID = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

		mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);

		mCrimes = CrimeLab.get(this).getCrimes();
		FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentPagerAdapter(fm) {
			@Override
			public Fragment getItem(int position) {
				Crime crime = mCrimes.get(position);
				return CrimeFragment.newInstance(crime.getID());
			}

			@Override
			public int getCount() {
				return mCrimes.size();
			}
		});
		for (int i = 0; i < mCrimes.size(); i++) {
			if (mCrimes.get(i).getID().equals(crimeID)) {
				mViewPager.setCurrentItem(i);
				break;
			}
		}
	}
}
