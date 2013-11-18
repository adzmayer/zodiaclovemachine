package com.writtenbyaliens.zodiaclovemachine;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

public class ResourceManager {

	// Module variables
	private static ResourceManager INSTANCE;

	// Public variables
	public ITextureRegion zodiacCircle;
	public ITextureRegion sparkle;
	public ITextureRegion cloud;
	public ITextureRegion aquarius;
	public ITextureRegion aries;
	public ITextureRegion cancer;
	public ITextureRegion capricorn;
	public ITextureRegion gemini;
	public ITextureRegion leo;
	public ITextureRegion libra;
	public ITextureRegion pisces;
	public ITextureRegion sagittarius;
	public ITextureRegion scorpio;
	public ITextureRegion taurus;
	public ITextureRegion virgo;
	public ITextureRegion heart;
	public ITextureRegion scroll;
	private Camera mCamera;
	public Font font;
	public Font smallFont;

	public static ResourceManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ResourceManager();
		}
		return INSTANCE;
	}

	/**
	 * Loads the game textures
	 * 
	 * @param pEngine
	 * @param pContext
	 */
	public synchronized void loadGameTextures(Engine pEngine, Context pContext) {

		// Set up the graphic textures
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		BuildableBitmapTextureAtlas bitmapTextureAtlas = new BuildableBitmapTextureAtlas(
				pEngine.getTextureManager(), 1840, 2180);

		zodiacCircle = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "zodiacAltered4.png");

		sparkle = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "sparkle01.png");

		cloud = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "smoke01.png");

		heart = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "heart.png");

		aquarius = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "aquarius.png");

		aries = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "aries.png");

		cancer = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "cancer.png");

		capricorn = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "capricorn.png");

		gemini = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "gemini.png");

		leo = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "leo.png");

		libra = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "libra.png");

		pisces = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "pisces.png");

		sagittarius = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "sagittarius.png");

		scorpio = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "scorpio.png");

		taurus = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "taurus.png");

		virgo = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "virgo.png");

		scroll = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "scroll.png");

		try {
			bitmapTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 1));
			bitmapTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	public synchronized void unloadGameTextures() {
		// call unload to remove the corresponding texture atlas from memory
		BuildableBitmapTextureAtlas bitmapTextureAtlas = (BuildableBitmapTextureAtlas) zodiacCircle
				.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) sparkle.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) aquarius
				.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) aries.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) cancer.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) capricorn
				.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) gemini.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) leo.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) libra.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) pisces.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) sagittarius
				.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) scorpio.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) taurus.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) virgo.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) heart.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) scroll.getTexture();
		bitmapTextureAtlas.unload();

		// ... Continue to unload all textures related to the 'Game' scene

		// Once all textures have been unloaded, attempt to invoke the Garbage
		// Collector
		System.gc();
	}

	public Camera getCamera() {
		return mCamera;
	}

	public void setCamera(Camera mCamera) {
		this.mCamera = mCamera;
	}

	public void loadFont(Engine pEngine) {
		font = FontFactory.create(pEngine.getFontManager(),
				pEngine.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 32f, true,
				Color.WHITE);
		font.load();

		/*
		 * Prepare the mFont object for the most common characters used. This
		 * will eliminate the need for the garbage collector to run when using a
		 * letter/number that's never been used before
		 */
		font.prepareLetters("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXY@Z,.1234567890"
				.toCharArray());

		smallFont = FontFactory.create(pEngine.getFontManager(),
				pEngine.getTextureManager(), 256, 256,
				Typeface.create(Typeface.SERIF, Typeface.NORMAL), 16f, true,
				Color.argb(255, 122, 69, 3));
		smallFont.load();

		smallFont
				.prepareLetters("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXY@Z,.1234567890"
						.toCharArray());

	}

}
