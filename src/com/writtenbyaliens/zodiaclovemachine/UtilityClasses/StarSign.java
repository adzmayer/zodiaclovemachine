package com.writtenbyaliens.zodiaclovemachine.UtilityClasses;

import java.util.List;

import org.andengine.opengl.texture.region.ITextureRegion;

public class StarSign {

	int id;
	String name;
	fPoint buttonLocation;
	ITextureRegion zodiacTexture;

	public StarSign(int id, String name, fPoint buttonLocation,
			ITextureRegion zodiacTexture) {

		this.id = id;
		this.name = name;
		this.buttonLocation = buttonLocation;
		this.zodiacTexture = zodiacTexture;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public fPoint getButtonLocation() {
		return buttonLocation;
	}

	public void setButtonLocation(fPoint buttonLocation) {
		this.buttonLocation = buttonLocation;
	}

	public ITextureRegion getZodiacTexture() {
		return zodiacTexture;
	}

	public void setZodiacTexture(ITextureRegion zodiacTexture) {
		this.zodiacTexture = zodiacTexture;
	}

	public List<StarSign> getCompatibleSigns() {
		// TODO Check constants file for compatible signs, build an array and
		// return them

		// Get ids

		// Build array

		return null;

	}

}
