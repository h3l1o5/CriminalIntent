package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 123 on 2016/10/19.
 */

public class CrimeListFragment extends Fragment {
	public static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
	private RecyclerView mCrimeRecyclerView;
	private CrimeAdapter mAdapter;
	private boolean mSubtitleVisible;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);  /*tell FragmentManager that CrimeListFragment needs to receive menu callbacks , then FM will call
		                           *Fragment.onCreateOptionsMenu(Menu,MenuInflater)
		                           */
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
		mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
		mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		if (savedInstanceState != null) {
			mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
		} // only working when rotating screen. when back to this view from press the back button on the tool bar, not working.

		updateUI();
		return view;
	}

	@Override
	public void onResume(){
		super.onResume();
		updateUI();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu,inflater); // does nothing, but calling it is a convention
		inflater.inflate(R.menu.fragment_crime_list,menu);

		MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
		if (mSubtitleVisible) {
			subtitleItem.setTitle(R.string.hide_subtitle);
		}else{
			subtitleItem.setTitle(R.string.show_subtitle);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_new_crime:
				Crime crime = new Crime();
				CrimeLab.get(getActivity()).addCrime(crime);
				Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getID());
				startActivity(intent);
				return true; // once you have handled the MenuItem, you should return true to indicate that no further processing is necessary.
			case R.id.menu_item_show_subtitle:
				mSubtitleVisible = !mSubtitleVisible;
				getActivity().invalidateOptionsMenu(); // re-creation of the action items
				updateSubtitle();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void updateSubtitle(){
		CrimeLab crimeLab = CrimeLab.get(getActivity());
		int crimeCount = crimeLab.getCrimes().size();
		String subtitle = crimeCount + " crimes";

		if (!mSubtitleVisible) {
			subtitle = null;
		}

		AppCompatActivity activity = (AppCompatActivity) getActivity();
		activity.getSupportActionBar().setSubtitle(subtitle);
	}

	private void updateUI(){
		CrimeLab crimeLab = CrimeLab.get(getActivity());
		List<Crime> crimes = crimeLab.getCrimes();
		if(mAdapter == null) {
			mAdapter = new CrimeAdapter(crimes);
			mCrimeRecyclerView.setAdapter(mAdapter);
		}else{
			mAdapter.notifyDataSetChanged();
		}
		updateSubtitle();
	}

	private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
		private List<Crime> mCrimes;

		public CrimeAdapter(List<Crime> crimes) {
			mCrimes = crimes;
		}

		@Override
		public void onBindViewHolder(CrimeHolder holder, int position) {
			Crime crime = mCrimes.get(position);
			holder.bindCrime(crime);
		}

		@Override
		public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
			View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
			return new CrimeHolder(view);
		}

		@Override
		public int getItemCount(){
			return mCrimes.size();
		}
	}

	private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		private Crime mCrime;
		private TextView mTitleTextView;
		private TextView mDateTextView;
		private CheckBox mSolvedCheckBox;

		public void bindCrime(Crime crime) {
			mCrime = crime;
			mTitleTextView.setText(mCrime.getTitle());
			mDateTextView.setText(DateFormat.getLongDateFormat(getContext()).format(mCrime.getDate())+"  "+DateFormat.getTimeFormat(getContext()).format(mCrime.getDate()));
			mSolvedCheckBox.setChecked(mCrime.isSolved());
		}

		public CrimeHolder(View itemView) {
			super(itemView);
			itemView.setOnClickListener(this);
			mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
			mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
			mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
			mSolvedCheckBox.setEnabled(false);
		}

		@Override
		public void onClick(View view) {
			Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getID());
			startActivity(intent);
		}
	}
}
