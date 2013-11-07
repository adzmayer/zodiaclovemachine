package com.writtenbyaliens.zodiaclovemachine;

import java.io.IOException;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.LayoutGameActivity;

import android.util.Log;
import android.widget.TextView;

import com.writtenbyaliens.zodiaclovemachine.UtilityClasses.fPoint;

public class MainActivity extends LayoutGameActivity implements
		IOnSceneTouchListener {

	// Variables
	final int mCameraWidth = 480;
	final int mCameraHeight = 800;
	private Camera mCamera;
	private Scene mScene;
	private GameManager gameManager;
	private boolean mSpinning = false;
	private fPoint mSelectedSign;
	private String selectedZodiacName;

	private TextView txtView;

	// Entities
	private Entity mLayer;
	private Sprite mSpriteZodiac;

	// ----------------------------------------------------------
	// Andengine lifecycle
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
		mScene.setOnSceneTouchListener(this);

		addSprites();

		pOnCreateSceneCallback.onCreateSceneFinished(mScene);

		txtView = (TextView) this.findViewById(R.id.ad_view);

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

	// ----------------------------------------------------------
	// Sprite methods
	// ----------------------------------------------------------

	private void addSprites() {

		final float positionX = mCameraWidth * 0.5f;
		final float positionY = mCameraHeight * 0.5f;

		/* Add our marble sprite to the bottom left side of the Scene initially */
		mSpriteZodiac = new Sprite(positionX, positionY,
				ResourceManager.getInstance().mZodiacCircle,
				mEngine.getVertexBufferObjectManager());

		mSpriteZodiac.setHeight(mCameraWidth);
		mSpriteZodiac.setWidth(mCameraWidth);

		// Attach the zodiac to the Scene
		mLayer.attachChild(mSpriteZodiac);

	}

	// --------------------------------------------------------------------------
	// Listeners
	// --------------------------------------------------------------------------

	RotationModifier rotationModifier = new RotationModifier(0.5f, 0, 360) {
		@Override
		protected void onModifierStarted(IEntity pItem) {
			super.onModifierStarted(pItem);
			// Your action after starting modifier
		}

		@Override
		protected void onModifierFinished(IEntity pItem) {
			super.onModifierFinished(pItem);
			// Your action after finishing modifier
		}
	};

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		Log.d("onSceneTouchEvent",
				"x:" + pSceneTouchEvent.getX() + "  y:"
						+ pSceneTouchEvent.getY() + " Action:"
						+ pSceneTouchEvent.getAction());

		mSelectedSign = new fPoint(pSceneTouchEvent.getX(),
				pSceneTouchEvent.getY());

		if (pSceneTouchEvent.getAction() == 1) {

			selectedZodiacName = Utils.returnZodiacSign(mSelectedSign);

			if (!selectedZodiacName.equals("")) {

				txtView.post(new Runnable() {
					@Override
					public void run() {
						txtView.setText(selectedZodiacName);
					}
				});

			}
		}

		/*
		 * if (Utils.returnZodiacSign(selectedSign).equals("gemini")) {
		 * Log.d("onSceneTouchEvent", "Gemini selected");
		 * 
		 * if (pSceneTouchEvent.getAction() == 1) { if (mSpinning) {
		 * mSpriteZodiac.clearEntityModifiers(); mSpinning = false; } else {
		 * mSpriteZodiac .registerEntityModifier(new LoopEntityModifier(
		 * rotationModifier)); mSpinning = true; } }
		 * 
		 * }
		 */

		return true;
	}
}
