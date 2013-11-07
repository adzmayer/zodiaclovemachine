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

	private static ResourceManager INSTANCE;

	// GFX
	public ITextureRegion mZodiacCircle;
	public ITextureRegion mSparkle;
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
				pEngine.getTextureManager(), 1000, 1000);

		mZodiacCircle = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "zodiacAltered4.png");

		mSparkle = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "sparkle01.png");

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
		BuildableBitmapTextureAtlas bitmapTextureAtlas = (BuildableBitmapTextureAtlas) mZodiacCircle
				.getTexture();
		bitmapTextureAtlas.unload();

		bitmapTextureAtlas = (BuildableBitmapTextureAtlas) mSparkle
				.getTexture();
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
