package com.writtenbyaliens.zodiaclovemachine;

import java.util.List;

import com.writtenbyaliens.zodiaclovemachine.UtilityClasses.StarMatch;

public class GameManager {
	private static GameManager INSTANCE;

	private String mMySign;
	private String mTheirSign;
	private List<StarMatch> mStarMatches;
	private int mCurrentCompatibility;

	// The constructor does not do anything for this singleton
	GameManager() {
	}

	public static GameManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GameManager();
		}
		return INSTANCE;
	}

	public String getMySign() {
		return mMySign;
	}

	public void setMySign(String mMySign) {
		this.mMySign = mMySign;
	}

	public String getTheirSign() {
		return mTheirSign;
	}

	public void setTheirSign(String mTheirSign) {
		this.mTheirSign = mTheirSign;
	}

	public List<StarMatch> getStarMatches() {
		return mStarMatches;
	}

	public void setStarMatches(List<StarMatch> starMatches) {
		this.mStarMatches = starMatches;
	}

	public int getCurrentCompatibility() {
		return mCurrentCompatibility;
	}

	public void setCurrentCompatibility(int mCurrentCompatibility) {
		this.mCurrentCompatibility = mCurrentCompatibility;
	}

}
