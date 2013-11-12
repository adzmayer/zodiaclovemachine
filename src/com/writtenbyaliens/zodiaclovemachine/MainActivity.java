package com.writtenbyaliens.zodiaclovemachine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.particle.BatchedSpriteParticleSystem;
import org.andengine.entity.particle.emitter.CircleOutlineParticleEmitter;
import org.andengine.entity.particle.initializer.AccelerationParticleInitializer;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.ExpireParticleInitializer;
import org.andengine.entity.particle.initializer.GravityParticleInitializer;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.UncoloredSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;

import android.content.Context;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.writtenbyaliens.zodiaclovemachine.UtilityClasses.Constants;
import com.writtenbyaliens.zodiaclovemachine.UtilityClasses.StarSign;
import com.writtenbyaliens.zodiaclovemachine.UtilityClasses.StopParticleModifier;
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
	private TextToSpeech tts;
	private boolean firstChoiceJustAdded = false;
	private boolean bothChoicesMade = false;
	private ArrayList<StarSign> starSigns;

	// Entities
	private Entity mLayer;
	private Sprite mSpriteZodiac;
	private Sprite mSpriteFirstChoice;
	private Sprite mSpriteSecondChoice;
	private Sprite mSpriteHeart;
	private BatchedSpriteParticleSystem mParticleSystemFirstChoice;
	private BatchedSpriteParticleSystem mParticleSystemSecondChoice;
	private BatchedSpriteParticleSystem mParticleSystemBackground;
	private BatchedSpriteParticleSystem mParticleSystemSelection;

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

		addBackground();

		mLayer = new Entity();
		mScene.attachChild(mLayer);
		mScene.setOnSceneTouchListener(this);

		buildSprites();
		buildStarSigns();

		pOnCreateSceneCallback.onCreateSceneFinished(mScene);

		// Set up advert here. Access this through runables - see old version in
		// GIT
		// txtView = (TextView) this.findViewById(R.id.ad_view);

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

	private void buildSprites() {

		final float positionX = mCameraWidth * 0.5f;
		final float positionY = mCameraHeight * 0.5f;

		/* Add our marble sprite to the bottom left side of the Scene initially */
		mSpriteZodiac = new Sprite(positionX, positionY,
				ResourceManager.getInstance().zodiacCircle,
				mEngine.getVertexBufferObjectManager());

		mSpriteZodiac.setHeight(mCameraWidth);
		mSpriteZodiac.setWidth(mCameraWidth);

		mSpriteHeart = new Sprite(positionX, positionY,
				ResourceManager.getInstance().heart,
				mEngine.getVertexBufferObjectManager());

		mSpriteHeart.setHeight(160);
		mSpriteHeart.setWidth(160);
		mSpriteHeart.setTag(Constants.HEART);

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

		fPoint centreSign;

		Log.d("onSceneTouchEvent",
				"x:" + pSceneTouchEvent.getX() + "  y:"
						+ pSceneTouchEvent.getY() + " Action:"
						+ pSceneTouchEvent.getAction());

		// Get point touched
		mSelectedSignCoords = new fPoint(pSceneTouchEvent.getX(),
				pSceneTouchEvent.getY());

		// Check to see if it is within the centre circle, if not select a sign
		if (Utils.isTouchedInCircle(96, mSelectedSignCoords) && bothChoicesMade) {
			Log.d("onSceneTouchEvent", "centre touched");
			showSparklesAndSpin();
		} else {

			mSelectedZodiacId = Utils.returnZodiacSign(mSelectedSignCoords);

			if (pSceneTouchEvent.getAction() == 0 && mSelectedZodiacId != 0) {

				if (mSpinning == false) {
					centreSign = Utils
							.getStarSignCentrePoint(mSelectedZodiacId);

					if (firstChoiceJustAdded) {

						Log.d("onSceneTouchEvent", "firstChoiceJustAdded");

						if (bothChoicesMade) {
							/* Remove any particles from scene */
							if (mParticleSystemSecondChoice != null) {
								mScene.detachChild(mParticleSystemSecondChoice);
							}

						}

						firstChoiceJustAdded = false;
						bothChoicesMade = true;
						showSparklesSecondChoice((int) centreSign.x,
								(int) centreSign.y);
						updateChoice(false);

						// Show heart
						// Attach the zodiac to the Scene
						if (mScene.getChildByTag(Constants.HEART) == null) {
							mLayer.attachChild(mSpriteHeart);

							// Animate it
							final LoopEntityModifier scaleInOutModifier = new LoopEntityModifier(
									new SequenceEntityModifier(
											new DelayModifier(0.3f),
											new ScaleModifier(0.3f, 0.8f, 1.0f),
											new ScaleModifier(0.3f, 1.0f, 0.8f)));
							mSpriteHeart
									.registerEntityModifier(scaleInOutModifier);

						}

					} else {

						if (bothChoicesMade) {

							/* Remove any particles from scene */
							if (mParticleSystemFirstChoice != null) {
								mScene.detachChild(mParticleSystemFirstChoice);
							}

						}

						firstChoiceJustAdded = true;
						showSparkles((int) centreSign.x, (int) centreSign.y);
						updateChoice(true);
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
			mResult = tts.setLanguage(Locale.getDefault());
			tts.setPitch(1f); // set pitch level
			tts.setSpeechRate(1.5f); // set speech speed rate

			if (mResult == TextToSpeech.LANG_MISSING_DATA
					|| mResult == TextToSpeech.LANG_NOT_SUPPORTED) {
			}
		} else {
			Log.e("TTS", "Initilization Failed");
		}
	}

	// --------------------------------------------------------------------------
	// Zodiac methods
	// --------------------------------------------------------------------------

	private void buildStarSigns() {
		starSigns = new ArrayList<StarSign>();

		StarSign starSign = new StarSign(Constants.ZodiacSigns.AQUARIUS,
				getResources().getString(R.string.Aquarius),
				Utils.getStarSignCentrePoint(Constants.ZodiacSigns.AQUARIUS),
				ResourceManager.getInstance().aquarius);
		starSigns.add(starSign);

		starSign = new StarSign(Constants.ZodiacSigns.ARIES, getResources()
				.getString(R.string.Aries),
				Utils.getStarSignCentrePoint(Constants.ZodiacSigns.ARIES),
				ResourceManager.getInstance().aries);
		starSigns.add(starSign);

		starSign = new StarSign(Constants.ZodiacSigns.CANCER, getResources()
				.getString(R.string.Cancer),
				Utils.getStarSignCentrePoint(Constants.ZodiacSigns.CANCER),
				ResourceManager.getInstance().cancer);
		starSigns.add(starSign);

		starSign = new StarSign(Constants.ZodiacSigns.CAPRICORN, getResources()
				.getString(R.string.Capricorn),
				Utils.getStarSignCentrePoint(Constants.ZodiacSigns.CAPRICORN),
				ResourceManager.getInstance().capricorn);
		starSigns.add(starSign);

		starSign = new StarSign(Constants.ZodiacSigns.GEMINI, getResources()
				.getString(R.string.Gemini),
				Utils.getStarSignCentrePoint(Constants.ZodiacSigns.GEMINI),
				ResourceManager.getInstance().gemini);
		starSigns.add(starSign);

		starSign = new StarSign(Constants.ZodiacSigns.LEO, getResources()
				.getString(R.string.Leo),
				Utils.getStarSignCentrePoint(Constants.ZodiacSigns.LEO),
				ResourceManager.getInstance().leo);
		starSigns.add(starSign);

		starSign = new StarSign(Constants.ZodiacSigns.LIBRA, getResources()
				.getString(R.string.Libra),
				Utils.getStarSignCentrePoint(Constants.ZodiacSigns.LIBRA),
				ResourceManager.getInstance().libra);
		starSigns.add(starSign);

		starSign = new StarSign(Constants.ZodiacSigns.PISCES, getResources()
				.getString(R.string.Pisces),
				Utils.getStarSignCentrePoint(Constants.ZodiacSigns.PISCES),
				ResourceManager.getInstance().pisces);
		starSigns.add(starSign);

		starSign = new StarSign(
				Constants.ZodiacSigns.SAGITTARIUS,
				getResources().getString(R.string.Sagittarius),
				Utils.getStarSignCentrePoint(Constants.ZodiacSigns.SAGITTARIUS),
				ResourceManager.getInstance().sagittarius);
		starSigns.add(starSign);

		starSign = new StarSign(Constants.ZodiacSigns.SCORPIO, getResources()
				.getString(R.string.Scorpio),
				Utils.getStarSignCentrePoint(Constants.ZodiacSigns.SCORPIO),
				ResourceManager.getInstance().scorpio);
		starSigns.add(starSign);

		starSign = new StarSign(Constants.ZodiacSigns.TAURUS, getResources()
				.getString(R.string.Taurus),
				Utils.getStarSignCentrePoint(Constants.ZodiacSigns.TAURUS),
				ResourceManager.getInstance().taurus);
		starSigns.add(starSign);

		starSign = new StarSign(Constants.ZodiacSigns.VIRGO, getResources()
				.getString(R.string.Virgo),
				Utils.getStarSignCentrePoint(Constants.ZodiacSigns.VIRGO),
				ResourceManager.getInstance().virgo);
		starSigns.add(starSign);

	}

	private StarSign getStarSignById(int id) {
		for (StarSign starSign : starSigns) {
			if (starSign.getId() == id) {
				return starSign;
			}
		}
		return null;
	}

	// --------------------------------------------------------------------------
	// Graphics
	// --------------------------------------------------------------------------

	private void showSparkles(int particleSpawnCenterX, int particleSpawnCenterY) {

		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(25);

		speakOut(getStarSignById(mSelectedZodiacId).getName());

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
				ResourceManager.getInstance().sparkle,
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

		speakOut(getStarSignById(mSelectedZodiacId).getName());

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
				ResourceManager.getInstance().sparkle,
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
		final float minRed = 40f;
		final float maxRed = 40f;
		final float minGreen = 0f;
		final float maxGreen = 0f;
		final float minBlue = 40f;
		final float maxBlue = 40f;

		ColorParticleInitializer<UncoloredSprite> colorParticleInitializer = new ColorParticleInitializer<UncoloredSprite>(
				minRed, maxRed, minGreen, maxGreen, minBlue, maxBlue);
		mParticleSystemSecondChoice
				.addParticleInitializer(colorParticleInitializer);

		/* Attach the particle system to the Scene */
		mScene.attachChild(mParticleSystemSecondChoice);

	}

	private void selectionParticleEffect(fPoint location) {

		/* Define the radius of the circle for the particle emitter */
		final float particleEmitterRadius = 40;

		/* Create the particle emitter */
		CircleOutlineParticleEmitter particleEmitter = new CircleOutlineParticleEmitter(
				(int) location.x, (int) location.y, particleEmitterRadius);

		/* Define the particle system properties */
		final float minSpawnRate = 25;
		final float maxSpawnRate = 25;
		final int maxParticleCount = 25;

		/* Create the particle system */
		mParticleSystemSelection = new BatchedSpriteParticleSystem(
				particleEmitter, minSpawnRate, maxSpawnRate, maxParticleCount,
				ResourceManager.getInstance().sparkle,
				mEngine.getVertexBufferObjectManager());

		/* Add an acceleration initializer to the particle system */
		mParticleSystemSelection
				.addParticleInitializer(new AccelerationParticleInitializer<UncoloredSprite>(
						-13360f, 13360f, -13360f, 13360f));

		/* Add an expire initializer to the particle system */
		mParticleSystemSelection
				.addParticleInitializer(new ExpireParticleInitializer<UncoloredSprite>(
						2f));

		/* Add a particle modifier to the particle system */
		mParticleSystemSelection
				.addParticleModifier(new ScaleParticleModifier<UncoloredSprite>(
						0f, 2f, 0.1f, 4f));

		mParticleSystemSelection.addParticleModifier(new StopParticleModifier(
				mParticleSystemSelection, 0.1f));

		/* Add a gravity modifier to the particle system */
		mParticleSystemSelection
				.addParticleInitializer(new GravityParticleInitializer<UncoloredSprite>());

		/* Define min/max values for particle colors */
		final float minRed = 255f;
		final float maxRed = 255f;
		final float minGreen = 255f;
		final float maxGreen = 255f;
		final float minBlue = 200f;
		final float maxBlue = 244f;

		ColorParticleInitializer<UncoloredSprite> colorParticleInitializer = new ColorParticleInitializer<UncoloredSprite>(
				minRed, maxRed, minGreen, maxGreen, minBlue, maxBlue);
		mParticleSystemSelection
				.addParticleInitializer(colorParticleInitializer);

		/* Define the alpha values */
		final float minAlpha = 0.5f;
		final float maxAlpha = 1;

		AlphaParticleInitializer<UncoloredSprite> alphaParticleInitializer = new AlphaParticleInitializer<UncoloredSprite>(
				minAlpha, maxAlpha);
		mParticleSystemSelection
				.addParticleInitializer(alphaParticleInitializer);

		/* Attach the particle system to the Scene */
		mScene.attachChild(mParticleSystemSelection);
	}

	private void addBackground() {

		final int particleSpawnCenterX = (int) (mCameraWidth * 0.5f);
		final int particleSpawnCenterY = (int) (mCameraHeight * 0.5f);

		/* Define the radius of the circle for the particle emitter */
		final float particleEmitterRadius = 50;

		/* Create the particle emitter */
		CircleOutlineParticleEmitter particleEmitter = new CircleOutlineParticleEmitter(
				particleSpawnCenterX, particleSpawnCenterY,
				particleEmitterRadius);

		/* Define the particle system properties */
		final float minSpawnRate = 1;
		final float maxSpawnRate = 20;
		final int maxParticleCount = 20;

		/* Create the particle system */
		mParticleSystemBackground = new BatchedSpriteParticleSystem(
				particleEmitter, minSpawnRate, maxSpawnRate, maxParticleCount,
				ResourceManager.getInstance().cloud,
				mEngine.getVertexBufferObjectManager());

		/* Add an acceleration initializer to the particle system */
		mParticleSystemBackground
				.addParticleInitializer(new AccelerationParticleInitializer<UncoloredSprite>(
						-360f, 360f, -360f, 360f));

		/* Add an expire initializer to the particle system */
		mParticleSystemBackground
				.addParticleInitializer(new ExpireParticleInitializer<UncoloredSprite>(
						4));

		/* Add a particle modifier to the particle system */
		mParticleSystemBackground
				.addParticleModifier(new ScaleParticleModifier<UncoloredSprite>(
						0f, 1f, 0.2f, 4f));

		/* Define the alpha values */
		final float minAlpha = 0.7f;
		final float maxAlpha = 1;

		AlphaParticleInitializer<UncoloredSprite> alphaParticleInitializer = new AlphaParticleInitializer<UncoloredSprite>(
				minAlpha, maxAlpha);

		mParticleSystemBackground
				.addParticleInitializer(alphaParticleInitializer);

		/* Define min/max values for particle colors */
		final float minRed = 0f;
		final float maxRed = 0f;
		final float minGreen = 0f;
		final float maxGreen = 0f;
		final float minBlue = 2f;
		final float maxBlue = 2f;

		ColorParticleInitializer<UncoloredSprite> colorParticleInitializer = new ColorParticleInitializer<UncoloredSprite>(
				minRed, maxRed, minGreen, maxGreen, minBlue, maxBlue);
		mParticleSystemBackground
				.addParticleInitializer(colorParticleInitializer);

		/* Attach the particle system to the Scene */
		mScene.attachChild(mParticleSystemBackground);

	}

	private void updateChoice(boolean isFirstChoice) {

		ITextureRegion zodiacTexture;

		zodiacTexture = getStarSignById(mSelectedZodiacId).getZodiacTexture()
				.deepCopy();

		if (mSelectedZodiacId != 0) {

			// Set the text of the zodiac name

			// Assign the correct texture to the sprite

			if (isFirstChoice) {
				// Create selection sprites
				mSpriteFirstChoice = new Sprite(80, 700, zodiacTexture,
						mEngine.getVertexBufferObjectManager());

				mSpriteFirstChoice.setTag(Constants.FIRST_CHOICE);

				// Fade it in to the correct corner

				// Add it to the scene if it isn't already
				if (mScene.getChildByTag(Constants.FIRST_CHOICE) != null) {
					mScene.detachChild(Constants.FIRST_CHOICE);
				}
				mScene.attachChild(mSpriteFirstChoice);

				selectionParticleEffect(new fPoint(80, 700));

			} else {
				// Create selection sprites

				mSpriteSecondChoice = new Sprite(400, 700, zodiacTexture,
						mEngine.getVertexBufferObjectManager());

				mSpriteSecondChoice.setTag(Constants.SECOND_CHOICE);

				// Reverse the bitmap in the y axis
				mSpriteSecondChoice.setWidth(-mSpriteSecondChoice.getWidth());

				// Add it to the scene if it isn't already
				if (mScene.getChildByTag(Constants.SECOND_CHOICE) != null) {
					mScene.detachChild(Constants.SECOND_CHOICE);
				}
				mScene.attachChild(mSpriteSecondChoice);
				selectionParticleEffect(new fPoint(400, 700));
			}

		}

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
				ResourceManager.getInstance().sparkle,
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

	}

	// --------------------------------------------------------------------------
	// Speech
	// --------------------------------------------------------------------------

	private void speakOut(String words) {
		tts.speak(words, TextToSpeech.QUEUE_FLUSH, null);
	}

}
