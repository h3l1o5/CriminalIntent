package com.bignerdranch.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.CrimeDbSchema.CrimeBaseHelper;
import database.CrimeDbSchema.CrimeDbSchema;


/**
 * Created by 123 on 2016/10/19.
 */

public class CrimeLab {
	private static CrimeLab sCrimeLab;
	//private List<Crime> mCrimes;
	private Context mContext;
	private SQLiteDatabase mDatabase;

	public static CrimeLab get(Context context) {
		if (sCrimeLab == null) {
			sCrimeLab = new CrimeLab(context);
		}
		return sCrimeLab;
	}

	private CrimeLab(Context context) {
		mContext = context.getApplicationContext();
		mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
	//	mCrimes = new ArrayList<>();
	}

	public void addCrime(Crime crime) {
	//	mCrimes.add(crime);
		ContentValues values = getContentValues(crime);
		mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
	}

	public List<Crime> getCrimes(){
	//	return mCrimes;
		List<Crime> crimes = new ArrayList<>();

		CrimeCursorWrapper cursor = queryCrimes(null, null);

		try{
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				crimes.add(cursor.getCrime());
				cursor.moveToNext();
			}
		}finally {
			cursor.close();
		}
		return crimes;
	}

	public Crime getCrime(UUID id) {
		CrimeCursorWrapper cursor = queryCrimes(
				CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
				new String[] { id.toString() }
		);
		try {
			if (cursor.getCount() == 0) {
				return null;
			}
			cursor.moveToFirst();
			return cursor.getCrime();
		} finally {
			cursor.close();
		}
	}

	public void updateCrime(Crime crime) {
		String uuidString = crime.getID().toString();
		ContentValues values = getContentValues(crime);

		mDatabase.update(CrimeDbSchema.CrimeTable.NAME,values, CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",new String[] { uuidString});
	}

	private static ContentValues getContentValues(Crime crime) {
		ContentValues values = new ContentValues();
		values.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getID().toString());
		values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
		values.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
		values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.isSolved() ? 1:0);
		values.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT, crime.getSuspect());

		return values;
	}

	private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
		Cursor cursor = mDatabase.query(
				CrimeDbSchema.CrimeTable.NAME,
				null,
				whereClause,
				whereArgs,
				null,
				null,
				null
		);
		return new CrimeCursorWrapper(cursor);
	}
}
