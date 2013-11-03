package com.writtenbyaliens.zodiaclovemachine;

import java.io.IOException;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.ui.activity.LayoutGameActivity;

public class MainActivity extends LayoutGameActivity {

	final int mCameraWidth = 480;
	final int mCameraHeight = 800;
	private Camera mCamera;
	private Scene mScene;
	private GameManager gameManager;

	private Entity mLayer;

	private Sprite mSprite;

	// ----------------------------------------------------------
	// Andengine start up methods
	// ----------------------------------------------------------

	@Override
	public EngineOptions onCreateEngineOptions() {
		// Define our mCamera object
		mCamera = new Camera(0, 0, mCameraWidth, mCameraHeight);

		// Declare & Define our engine options to be applied to our Engine
		// object
		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(),
				mCamera);

		// It is necessary in a lot of applications to define the following
		// wake lock options in order to disable the device's display
		// from turning off during gameplay due to inactivity
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);

		// Return the engineOptions object, passing it to the engine
		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws IOException {

		// Load the game texture resources
		ResourceManager.getInstance().loadGameTextures(mEngine, this);
		ResourceManager.getInstance().setCamera(mCamera);

		pOnCreateResourcesCallback.onCreateResourcesFinished();

	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws IOException {
		mScene = new Scene();

		mLayer = new Entity();
		mScene.attachChild(mLayer);

		addSprites();

		pOnCreateSceneCallback.onCreateSceneFinished(mScene);

	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback)
			throws IOException {

		pOnPopulateSceneCallback.onPopulateSceneFinished();

	}

	@Override
	protected int getLayoutID() {
		return R.layout.activity_main;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.gameSurfaceView;
	}

	private void addSprites() {

		final float positionX = mCameraWidth * 0.5f;
		final float positionY = mCameraHeight * 0.5f;

		/* Add our marble sprite to the bottom left side of the Scene initially */
		Sprite mSprite = new Sprite(positionX, positionY,
				ResourceManager.getInstance().mZodiacCircle,
				mEngine.getVertexBufferObjectManager());

		mSprite.setHeight(400);
		mSprite.setWidth(400);

		/* Attach the zodiac to the Scene */
		mLayer.attachChild(mSprite);

	}
}
