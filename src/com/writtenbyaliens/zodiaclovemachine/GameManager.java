package com.writtenbyaliens.zodiaclovemachine;

public class GameManager {
	private static GameManager INSTANCE;

	private String mMySign;
	private String mTheirSign;
	
	// The constructor does not do anything for this singleton
	GameManager(){
	}

	public static GameManager getInstance(){
	  if(INSTANCE == null){
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
}
