package com.bignerdranch.android.criminalintent;

import java.util.UUID;

/**
 * Created by 123 on 2016/10/18.
 */

public class Crime {
	private UUID mID;
	private String mTitle;

	public Crime(){
		mID = UUID.randomUUID();
	}

	public UUID getID() {
		return mID;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}
}
