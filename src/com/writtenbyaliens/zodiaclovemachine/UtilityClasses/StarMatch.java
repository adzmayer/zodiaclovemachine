package com.writtenbyaliens.zodiaclovemachine.UtilityClasses;

import java.util.List;

public class StarMatch {

	private int mStarSign1;
	private int mStarSign2;
	private List<String> mParagraphsList;
	private int mCurrentCompatibility;

	public int getStarSign1() {
		return mStarSign1;
	}

	public void setStarSign1(int mStarSign1) {
		this.mStarSign1 = mStarSign1;
	}

	public int getStarSign2() {
		return mStarSign2;
	}

	public void setStarSign2(int mStarSign2) {
		this.mStarSign2 = mStarSign2;
	}

	public List<String> getParagraphsList() {
		return mParagraphsList;
	}

	public void setParagraphsList(List<String> mParagraph) {
		this.mParagraphsList = mParagraph;
	}

	public int getCurrentCompatibility() {
		return mCurrentCompatibility;
	}

	public void setCurrentCompatibility(int mCurrentCompatibility) {
		this.mCurrentCompatibility = mCurrentCompatibility;
	}

}
