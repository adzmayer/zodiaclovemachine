package com.writtenbyaliens.zodiaclovemachine;

import java.io.IOException;
import java.util.Locale;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.particle.BatchedSpriteParticleSystem;
import org.andengine.entity.particle.emitter.CircleOutlineParticleEmitter;
import org.andengine.entity.particle.initializer.AccelerationParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.ExpireParticleInitializer;
import org.andengine.entity.particle.initializer.GravityParticleInitializer;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.UncoloredSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.LayoutGameActivity;

import android.content.Context;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import com.writtenbyaliens.zodiaclovemachine.UtilityClasses.Constants;
import com.writtenbyaliens.zodiaclovemachine.UtilityClasses.fPoint;

public class MainActivity extends LayoutGameActivity implements
		IOnSceneTouchListener, TextToSpeech.OnInitListener {

	// Variables
	final int mCameraWidth = 480;
	final int mCameraHeight = 800;
	private Camera mCamera;
	private Scene mScene;
	private GameManager mGameManager;
	private boolean mSpinning = false;
	private fPoint mSelectedSignCoords;
	private int mSelectedZodiacId;
	private int mResult;
	// private boolean touchLock = false;
	private TextView txtView;
	private TextToSpeech tts;
	private boolean firstChoiceJustAdded = false;
	private boolean bothChoicesMade = false;

	// Entities
	private Entity mLayer;
	private Entity mLayerBackground;
	private Sprite mSpriteZodiac;
	private BatchedSpriteParticleSystem mParticleSystemFirstChoice;
	private BatchedSpriteParticleSystem mParticleSystemSecondChoice;

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

		// Set up tts
		tts = new TextToSpeech(this, this);

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

		mLayerBackground = new Entity();
		mScene.attachChild(mLayerBackground);

		mLayer = new Entity();
		mScene.attachChild(mLayer);
		mScene.setOnSceneTouchListener(this);

		addBackground();
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

	@Override
	public void onDestroy() {
		// Don't forget to shutdown!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
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
	// Background methods
	// --------------------------------------------------------------------------

	private void addBackground() {

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

		fPoint centreSign;

		Log.d("onSceneTouchEvent",
				"x:" + pSceneTouchEvent.getX() + "  y:"
						+ pSceneTouchEvent.getY() + " Action:"
						+ pSceneTouchEvent.getAction());

		// Get point touched
		mSelectedSignCoords = new fPoint(pSceneTouchEvent.getX(),
				pSceneTouchEvent.getY());

		// Check to see if it is within the centre circle, if not select a sign
		if (Utils.isTouchedInCircle(96, mSelectedSignCoords)) {
			Log.d("onSceneTouchEvent", "centre touched");
		} else {

			mSelectedZodiacId = Utils.returnZodiacSign(mSelectedSignCoords);

			if (pSceneTouchEvent.getAction() == 0 && mSelectedZodiacId != 0) {

				if (mSpinning == false) {
					centreSign = getStarSignCentrePoint(mSelectedZodiacId);

					if (firstChoiceJustAdded) {

						Log.d("onSceneTouchEvent", "firstChoiceJustAdded");

						if (bothChoicesMade) {

							Log.d("onSceneTouchEvent", "bothChoicesMade");

							/* Remove any particles from scene */
							if (mParticleSystemSecondChoice != null) {
								mScene.detachChild(mParticleSystemSecondChoice);
							}

						}

						firstChoiceJustAdded = false;
						bothChoicesMade = true;
						showSparklesSecondChoice((int) centreSign.x,
								(int) centreSign.y);

					} else {

						Log.d("onSceneTouchEvent", "secondChoiceJustAdded");

						if (bothChoicesMade) {

							Log.d("onSceneTouchEvent", "bothChoicesMade");

							/* Remove any particles from scene */
							if (mParticleSystemFirstChoice != null) {
								mScene.detachChild(mParticleSystemFirstChoice);

							}

						}

						firstChoiceJustAdded = true;
						showSparkles((int) centreSign.x, (int) centreSign.y);
					}

				}

			}

		}

		return true;
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			// set Language
			mResult = tts.setLanguage(Locale.US);
			// tts.setPitch(5); // set pitch level
			// tts.setSpeechRate(2); // set speech speed rate
			if (mResult == TextToSpeech.LANG_MISSING_DATA
					|| mResult == TextToSpeech.LANG_NOT_SUPPORTED) {
			} else {
				// speakOut(getStarSignName(mSelectedZodiacId));
			}
		} else {
			Log.e("TTS", "Initilization Failed");
		}
	}

	// --------------------------------------------------------------------------
	// Form methods
	// --------------------------------------------------------------------------

	private String getStarSignName(int selectedZodiacId) {

		if (selectedZodiacId != 0) {
			switch (selectedZodiacId) {
			case Constants.ZodiacSigns.GEMINI:
				return (getResources().getString(R.string.Gemini));
			case Constants.ZodiacSigns.CANCER:
				return (getResources().getString(R.string.Cancer));
			case Constants.ZodiacSigns.LEO:
				return (getResources().getString(R.string.Leo));
			case Constants.ZodiacSigns.VIRGO:
				return (getResources().getString(R.string.Virgo));
			case Constants.ZodiacSigns.LIBRA:
				return (getResources().getString(R.string.Libra));
			case Constants.ZodiacSigns.SCORPIO:
				return (getResources().getString(R.string.Scorpio));
			case Constants.ZodiacSigns.SAGITTARIUS:
				return (getResources().getString(R.string.Sagittarius));
			case Constants.ZodiacSigns.CAPRICORN:
				return (getResources().getString(R.string.Capricorn));
			case Constants.ZodiacSigns.AQUARIUS:
				return (getResources().getString(R.string.Aquarius));
			case Constants.ZodiacSigns.PISCES:
				return (getResources().getString(R.string.Pisces));
			case Constants.ZodiacSigns.ARIES:
				showSparklesAndSpin();
				return (getResources().getString(R.string.Aries));
			case Constants.ZodiacSigns.TAURUS:
				return (getResources().getString(R.string.Taurus));
			}
		}

		return "";

	}

	private fPoint getStarSignCentrePoint(int selectedZodiacId) {

		fPoint centre = new fPoint();

		if (selectedZodiacId != 0) {
			switch (selectedZodiacId) {
			case Constants.ZodiacSigns.GEMINI:
				centre.x = Constants.ZodiacSignCentrePoints.GEMINI_X;
				centre.y = Constants.ZodiacSignCentrePoints.GEMINI_Y;
				return (centre);
			case Constants.ZodiacSigns.CANCER:
				centre.x = Constants.ZodiacSignCentrePoints.CANCER_X;
				centre.y = Constants.ZodiacSignCentrePoints.CANCER_Y;
				return (centre);
			case Constants.ZodiacSigns.LEO:
				centre.x = Constants.ZodiacSignCentrePoints.LEO_X;
				centre.y = Constants.ZodiacSignCentrePoints.LEO_Y;
				return (centre);
			case Constants.ZodiacSigns.VIRGO:
				centre.x = Constants.ZodiacSignCentrePoints.VIRGO_X;
				centre.y = Constants.ZodiacSignCentrePoints.VIRGO_Y;
				return (centre);
			case Constants.ZodiacSigns.LIBRA:
				centre.x = Constants.ZodiacSignCentrePoints.LIBRA_X;
				centre.y = Constants.ZodiacSignCentrePoints.LIBRA_Y;
				return (centre);
			case Constants.ZodiacSigns.SCORPIO:
				centre.x = Constants.ZodiacSignCentrePoints.SCORPIO_X;
				centre.y = Constants.ZodiacSignCentrePoints.SCORPIO_Y;
				return (centre);
			case Constants.ZodiacSigns.SAGITTARIUS:
				centre.x = Constants.ZodiacSignCentrePoints.SAGITTARIUS_X;
				centre.y = Constants.ZodiacSignCentrePoints.SAGITTARIUS_Y;
				return (centre);
			case Constants.ZodiacSigns.CAPRICORN:
				centre.x = Constants.ZodiacSignCentrePoints.CAPRICORN_X;
				centre.y = Constants.ZodiacSignCentrePoints.CAPRICORN_Y;
				return (centre);
			case Constants.ZodiacSigns.AQUARIUS:
				centre.x = Constants.ZodiacSignCentrePoints.AQUARIUS_X;
				centre.y = Constants.ZodiacSignCentrePoints.AQUARIUS_Y;
				return (centre);
			case Constants.ZodiacSigns.PISCES:
				centre.x = Constants.ZodiacSignCentrePoints.PISCES_X;
				centre.y = Constants.ZodiacSignCentrePoints.PISCES_Y;
				return (centre);
			case Constants.ZodiacSigns.ARIES:
				centre.x = Constants.ZodiacSignCentrePoints.ARIES_X;
				centre.y = Constants.ZodiacSignCentrePoints.ARIES_Y;
				return (centre);
			case Constants.ZodiacSigns.TAURUS:
				centre.x = Constants.ZodiacSignCentrePoints.TAURUS_X;
				centre.y = Constants.ZodiacSignCentrePoints.TAURUS_Y;
				return (centre);
			}
		}

		return centre;

	}

	private void showSparkles(int particleSpawnCenterX, int particleSpawnCenterY) {

		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(25);

		speakOut(getStarSignName(mSelectedZodiacId));

		/* Define the radius of the circle for the particle emitter */
		final float particleEmitterRadius = 40;

		/* Create the particle emitter */
		CircleOutlineParticleEmitter particleEmitter = new CircleOutlineParticleEmitter(
				particleSpawnCenterX, particleSpawnCenterY,
				particleEmitterRadius);

		/* Define the particle system properties */
		final float minSpawnRate = 100;
		final float maxSpawnRate = 150;
		final int maxParticleCount = 200;

		/* Create the particle system */
		mParticleSystemFirstChoice = new BatchedSpriteParticleSystem(
				particleEmitter, minSpawnRate, maxSpawnRate, maxParticleCount,
				ResourceManager.getInstance().mSparkle,
				mEngine.getVertexBufferObjectManager());

		/* Add an acceleration initializer to the particle system */
		mParticleSystemFirstChoice
				.addParticleInitializer(new AccelerationParticleInitializer<UncoloredSprite>(
						25f, -25f, 3000f, 5000f));

		/* Add an expire initializer to the particle system */
		mParticleSystemFirstChoice
				.addParticleInitializer(new ExpireParticleInitializer<UncoloredSprite>(
						0.2f));

		/* Add a particle modifier to the particle system */
		mParticleSystemFirstChoice
				.addParticleModifier(new ScaleParticleModifier<UncoloredSprite>(
						0f, 0.5f, 0.2f, 0.8f));

		/* Define min/max values for particle colors */
		final float minRed = 2f;
		final float maxRed = 8f;
		final float minGreen = 0f;
		final float maxGreen = 0f;
		final float minBlue = 8f;
		final float maxBlue = 8f;

		ColorParticleInitializer<UncoloredSprite> colorParticleInitializer = new ColorParticleInitializer<UncoloredSprite>(
				minRed, maxRed, minGreen, maxGreen, minBlue, maxBlue);
		mParticleSystemFirstChoice
				.addParticleInitializer(colorParticleInitializer);

		/* Attach the particle system to the Scene */
		mScene.attachChild(mParticleSystemFirstChoice);

	}

	private void showSparklesSecondChoice(int particleSpawnCenterX,
			int particleSpawnCenterY) {

		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(25);

		speakOut(getStarSignName(mSelectedZodiacId));

		/* Define the radius of the circle for the particle emitter */
		final float particleEmitterRadius = 40;

		/* Create the particle emitter */
		CircleOutlineParticleEmitter particleEmitter = new CircleOutlineParticleEmitter(
				particleSpawnCenterX, particleSpawnCenterY,
				particleEmitterRadius);

		/* Define the particle system properties */
		final float minSpawnRate = 100;
		final float maxSpawnRate = 150;
		final int maxParticleCount = 200;

		/* Create the particle system */
		mParticleSystemSecondChoice = new BatchedSpriteParticleSystem(
				particleEmitter, minSpawnRate, maxSpawnRate, maxParticleCount,
				ResourceManager.getInstance().mSparkle,
				mEngine.getVertexBufferObjectManager());

		/* Add an acceleration initializer to the particle system */
		mParticleSystemSecondChoice
				.addParticleInitializer(new AccelerationParticleInitializer<UncoloredSprite>(
						25f, -25f, 3000f, 5000f));

		/* Add an expire initializer to the particle system */
		mParticleSystemSecondChoice
				.addParticleInitializer(new ExpireParticleInitializer<UncoloredSprite>(
						0.2f));

		/* Add a particle modifier to the particle system */
		mParticleSystemSecondChoice
				.addParticleModifier(new ScaleParticleModifier<UncoloredSprite>(
						0f, 0.5f, 0.2f, 0.8f));

		/* Define min/max values for particle colors */
		final float minRed = 3f;
		final float maxRed = 6f;
		final float minGreen = 9f;
		final float maxGreen = 9f;
		final float minBlue = 2f;
		final float maxBlue = 2f;

		ColorParticleInitializer<UncoloredSprite> colorParticleInitializer = new ColorParticleInitializer<UncoloredSprite>(
				minRed, maxRed, minGreen, maxGreen, minBlue, maxBlue);
		mParticleSystemSecondChoice
				.addParticleInitializer(colorParticleInitializer);

		/* Attach the particle system to the Scene */
		mScene.attachChild(mParticleSystemSecondChoice);

	}

	private void showSparklesAndSpin() {

		if (mParticleSystemFirstChoice != null) {
			mScene.detachChild(mParticleSystemFirstChoice);

		}

		/* Define the center point of the particle system spawn location */
		final int particleSpawnCenterX = (int) (mCameraWidth * 0.5f);
		final int particleSpawnCenterY = (int) (mCameraHeight * 0.5f);

		/* Define the radius of the circle for the particle emitter */
		final float particleEmitterRadius = 240;

		/* Create the particle emitter */
		CircleOutlineParticleEmitter particleEmitter = new CircleOutlineParticleEmitter(
				particleSpawnCenterX, particleSpawnCenterY,
				particleEmitterRadius);

		/* Define the particle system properties */
		final float minSpawnRate = 60;
		final float maxSpawnRate = 200;
		final int maxParticleCount = 300;

		/* Create the particle system */
		BatchedSpriteParticleSystem particleSystem = new BatchedSpriteParticleSystem(
				particleEmitter, minSpawnRate, maxSpawnRate, maxParticleCount,
				ResourceManager.getInstance().mSparkle,
				mEngine.getVertexBufferObjectManager());

		/* Add an acceleration initializer to the particle system */
		particleSystem
				.addParticleInitializer(new AccelerationParticleInitializer<UncoloredSprite>(
						25f, -25f, 50f, 100f));

		/* Add an expire initializer to the particle system */
		particleSystem
				.addParticleInitializer(new ExpireParticleInitializer<UncoloredSprite>(
						2f));

		/* Add a particle modifier to the particle system */
		particleSystem
				.addParticleModifier(new ScaleParticleModifier<UncoloredSprite>(
						0f, 3f, 0.2f, 1f));

		/* Add a gravity modifier to the particle system */
		particleSystem
				.addParticleInitializer(new GravityParticleInitializer<UncoloredSprite>());

		/* Attach the particle system to the Scene */
		mScene.attachChild(particleSystem);

		// Spin!
		mSpriteZodiac.registerEntityModifier(new LoopEntityModifier(
				rotationModifier));
		mSpinning = true;

		/*
		 * 
		 * if (mSpinning) { mSpriteZodiac.clearEntityModifiers(); mSpinning =
		 * false; } else { mSpriteZodiac.registerEntityModifier(new
		 * LoopEntityModifier( rotationModifier)); mSpinning = true; }
		 */

	}

	private void speakOut(String words) {
		tts.speak(words, TextToSpeech.QUEUE_FLUSH, null);
	}

}
