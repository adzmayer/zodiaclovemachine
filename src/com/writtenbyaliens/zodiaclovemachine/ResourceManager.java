package com.writtenbyaliens.zodiaclovemachine;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.debug.Debug;

import android.content.Context;

public class ResourceManager {

	// Module variables
	private static ResourceManager INSTANCE;

	// Public variables
	public ITextureRegion zodiacCircle;
	public ITextureRegion sparkle;
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
	private Camera mCamera;

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
				pEngine.getTextureManager(), 1120, 1120);

		zodiacCircle = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "zodiacAltered4.png");

		sparkle = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "sparkle01.png");

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

}
