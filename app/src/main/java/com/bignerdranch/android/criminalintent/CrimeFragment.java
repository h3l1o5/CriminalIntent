package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Martian on 2016/10/19.
 */

public class CrimeFragment extends Fragment {
	public static final String ARG_CRIME_ID = "crime_id";
	public static final String DIALOG_DATE = "DialogDate";
	public static final int REQUEST_DATE = 0;
	public static final int REQUEST_CONTACT = 1;

	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	private Button mReportButton;
	private Button mSuspectButton;

	public static CrimeFragment newInstance(UUID crimeID) {
		Bundle args = new Bundle();
		args.putSerializable(ARG_CRIME_ID,crimeID);

		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UUID crimeID = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeID);
	}

	@Override
	public void onPause(){
		super.onPause();
		CrimeLab.get(getActivity()).updateCrime(mCrime);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, container, false);
		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// This space intentionally left blank
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mCrime.setTitle(s.toString());
			}

			@Override
			public void afterTextChanged(Editable s) {
				// This space intentionally left blank
			}
		});

		mDateButton = (Button) v.findViewById(R.id.crime_date);
		updateDate();
		mDateButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				FragmentManager manager = getFragmentManager();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
				dialog.show(manager, DIALOG_DATE);
			}
		});

		mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCrime.setSolved(isChecked);
			}
		});

		mReportButton = (Button) v.findViewById(R.id.send_crime_report);
		mReportButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
				intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
				intent = Intent.createChooser(intent, getString(R.string.send_report));
				startActivity(intent);
			}
		});

		final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		mSuspectButton = (Button) v.findViewById(R.id.choose_suspect);
		mSuspectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(pickContact,REQUEST_CONTACT);
			}
		});

		if (mCrime.getSuspect() != null) {
			mSuspectButton.setText(mCrime.getSuspect());
		}

		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mCrime.setDate(date);
			updateDate();
		} else if (requestCode == REQUEST_CONTACT && data != null) {
			Uri contactUri = data.getData();
			// Specify which fields you want your query to return value for.
			String[] queryFields = new String[]{
					ContactsContract.Contacts.DISPLAY_NAME
			};
			// Perform your query - the contactUri is like a "where" clause here.
			Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

			try{
				// Double-check that you actually got results.
				if(c.getCount() == 0) return;

				// Pull out the first column of the first row of data that is your suspect's name.
				c.moveToFirst();
				String suspect = c.getString(0);
				mCrime.setSuspect(suspect);
				mSuspectButton.setText(suspect);
			}finally {
				c.close();
			}

		}
	}

	private void updateDate() {
		mDateButton.setText(DateFormat.getLongDateFormat(getContext()).format(mCrime.getDate()));
	}

	private String getCrimeReport(){
		String solvedString = null;
		if (mCrime.isSolved()) {
			solvedString = getString(R.string.crime_report_solved);
		}else{
			solvedString = getString(R.string.crime_report_unsolved);
		}

		String dateFormat = "yyyy-MM-dd(E)";
		String dateString = DateFormat.format(dateFormat,mCrime.getDate()).toString();

		String suspect = mCrime.getSuspect();
		if (suspect == null) {
			suspect = getString(R.string.crime_report_no_suspect);
		}else{
			suspect = getString(R.string.crime_report_suspect, suspect);
		}

		String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

		return report;
	}
}
