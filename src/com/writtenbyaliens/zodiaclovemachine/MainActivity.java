package com.writtenbyaliens.zodiaclovemachine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.particle.BatchedSpriteParticleSystem;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.CircleOutlineParticleEmitter;
import org.andengine.entity.particle.emitter.RectangleParticleEmitter;
import org.andengine.entity.particle.initializer.AccelerationParticleInitializer;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.ExpireParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.UncoloredSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.LayoutGameActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.writtenbyaliens.zodiaclovemachine.Entities.ScrollableEntity;
import com.writtenbyaliens.zodiaclovemachine.UtilityClasses.Constants;
import com.writtenbyaliens.zodiaclovemachine.UtilityClasses.StarMatch;
import com.writtenbyaliens.zodiaclovemachine.UtilityClasses.StarSign;
import com.writtenbyaliens.zodiaclovemachine.UtilityClasses.fPoint;

public class MainActivity extends LayoutGameActivity implements
		TextToSpeech.OnInitListener {

	// Variables
	final int mCameraWidth = 480;
	final int mCameraHeight = 800;
	private Camera mCamera;
	private Scene mScene;
	private boolean mSpinning = false;
	private fPoint mSelectedSignCoords;
	private int mSelectedZodiacId;
	private int mResult;
	private TextToSpeech tts;
	private boolean firstChoiceJustAdded = false;
	private boolean bothChoicesMade = false;
	private List<StarSign> starSigns;
	private List<StarMatch> starMatches;
	private int mSelected1;
	private int mSelected2;

	// Views
	private AdView adView;
	private RelativeLayout layout;

	// Scroll variables
	private static ScrollDetector mScrollDetector;
	private static boolean manualScrolling;
	private static boolean creditsFinished;

	// Entities
	private Entity mLayerBackground;
	private Entity mLayer; // Zodiac ring, heart,
	private Entity mLayerText; // Text for selected signs and hints
	private Entity mLayerScroll; // scroll
	private Entity mLayerScrollText; // scroll result text
	private Sprite mSpriteZodiac;
	private Sprite mSpriteFirstChoice;
	private Sprite mSpriteSecondChoice;
	private Sprite mSpriteHeart;
	private Sprite mSpriteScroll;
	private List<Sprite> mHeartList;
	private BatchedSpriteParticleSystem mParticleSystemFirstChoice;
	private BatchedSpriteParticleSystem mParticleSystemSecondChoice;
	private BatchedSpriteParticleSystem mParticleSystemBackground;
	private BatchedSpriteParticleSystem mParticleSystemClouds;
	private BatchedSpriteParticleSystem mParticleSystemRing;
	private Text mSelectedFirst;
	private Text mSelectedSecond;
	private Text mTextResult;

	// Constants
	private static final int SELECTED_SIGN_1_X = 80;
	private static final int SELECTED_SIGN_1_Y = 690;
	private static final int SELECTED_SIGN_2_X = 400;
	private static final int SELECTED_SIGN_2_Y = 690;
	private static final String AD_UNIT_ID = "ca-app-pub-2152755479374802/8133900975";

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

		// Get star sign data from assets
		loadStarSignData();

		// Return the engineOptions object, passing it to the engine
		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws IOException {

		// Load the game texture resources
		ResourceManager.getInstance().loadGameTextures(mEngine, this);
		ResourceManager.getInstance().loadFont(mEngine);
		ResourceManager.getInstance().setCamera(mCamera);

		pOnCreateResourcesCallback.onCreateResourcesFinished();

	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws IOException {

		mScene = new Scene();

		initVariables();
		initLayers();
		initEntities();

		pOnCreateSceneCallback.onCreateSceneFinished(mScene);

		// Set up advert here. Access this through runables - see old version in

		// Create an ad.
		adView = new AdView(this);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(AD_UNIT_ID);

		// Add the AdView to the view hierarchy. The view will have no size
		// until the ad is loaded.

		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				adView.setAdListener(new AdListener() {
					public void onAdLoaded() {
						addAdvert();
					}

				});

			}
		});

		// Create an ad request. Check logcat output for the hashed
		// device ID to
		// get test ads on a physical device.
		final AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				"525D136FC5897CDB2F35C54DC0E7659F").build();
		// Start loading the ad in the background.
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adView.loadAd(adRequest);
			}
		});

	}

	private void addAdvert() {

		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				layout = (RelativeLayout) findViewById(R.id.root_view);
				LayoutParams lParams = new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				adView.setLayoutParams(lParams);
				layout.addView(adView);
			}
		});
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
	// Initialisation
	// ----------------------------------------------------------

	private void initVariables() {

		manualScrolling = false;
		creditsFinished = false;
		bothChoicesMade = false;
		firstChoiceJustAdded = false;
		mSpinning = false;
	}

	private void initLayers() {

		// Set background
		mLayerBackground = new Entity();
		mScene.attachChild(mLayerBackground);
		addBackground();

		// Set zodiac
		mLayer = new Entity();
		mScene.attachChild(mLayer);
		mScene.setOnSceneTouchListener(onSceneTouchListener);

		// Set text
		mLayerText = new Entity();
		mScene.attachChild(mLayerText);

		// Set result scroll
		mLayerScroll = new Entity();
		mScene.attachChild(mLayerScroll);

		mLayerScrollText = new Entity();
		mScene.attachChild(mLayerScrollText);

	}

	private void initEntities() {
		buildSprites();
		buildText();
		buildStarSigns();
	}

	private void reset() {
		mScene.detachChildren();
		initVariables();
		initLayers();
		initEntities();
	}

	// ----------------------------------------------------------
	// Sprite methods
	// ----------------------------------------------------------

	private void buildSprites() {

		final float positionX = mCameraWidth * 0.5f;
		final float positionY = mCameraHeight * 0.5f;

		// Add our zodiac ring sprite to the centre

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

		mSpriteScroll = new Sprite(positionX, positionY,
				ResourceManager.getInstance().scroll,
				mEngine.getVertexBufferObjectManager());

		mSpriteScroll.setHeight(mCameraWidth);
		mSpriteScroll.setWidth(mCameraWidth * 0.75f);

		// Attach the zodiac to the Scene
		mLayer.attachChild(mSpriteZodiac);

	}

	private void buildText() {

		mSelectedFirst = new Text(0, 0, ResourceManager.getInstance().font, "",
				11, mEngine.getVertexBufferObjectManager()) {

		};

		mSelectedFirst.setTag(Constants.TEXT_SELECTED_FIRST);

		mSelectedSecond = new Text(0, 0, ResourceManager.getInstance().font,
				"", 11, mEngine.getVertexBufferObjectManager()) {

		};

		mSelectedSecond.setTag(Constants.TEXT_SELECTED_SECOND);

	}

	// --------------------------------------------------------------------------
	// Listeners
	// --------------------------------------------------------------------------

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			reset();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	IOnSceneTouchListener onSceneTouchListener = new IOnSceneTouchListener() {

		@Override
		public boolean onSceneTouchEvent(Scene pScene,
				TouchEvent pSceneTouchEvent) {

			fPoint centreSign;

			Log.d("onSceneTouchEvent",
					"x:" + pSceneTouchEvent.getX() + "  y:"
							+ pSceneTouchEvent.getY() + " Action:"
							+ pSceneTouchEvent.getAction());

			// Get point touched
			mSelectedSignCoords = new fPoint(pSceneTouchEvent.getX(),
					pSceneTouchEvent.getY());

			// Check to see if it is within the centre circle, if not select a
			// sign
			if (Utils.isTouchedInCircle(96, mSelectedSignCoords)) {
				if (bothChoicesMade) {
					Log.d("onSceneTouchEvent", "centre touched");
					bothChoicesMade = false;
					showSparklesAndSpin();

					if (mSpriteHeart != null) {
						mLayer.detachChild(mSpriteHeart);
					}

					TimerHandler cloudTimerHandler;
					TimerHandler removeEntitiesTimerHandler;

					mEngine.registerUpdateHandler(removeEntitiesTimerHandler = new TimerHandler(
							5f, new ITimerCallback() {
								@Override
								public void onTimePassed(
										final TimerHandler pTimerHandler) {

									if (mSpriteZodiac != null) {
										mLayer.detachChild(mSpriteZodiac);
									}

									if (mParticleSystemRing != null) {
										mLayer.detachChild(mParticleSystemRing);
									}

									showScroll();

								}
							}));

					mEngine.registerUpdateHandler(cloudTimerHandler = new TimerHandler(
							2f, new ITimerCallback() {
								@Override
								public void onTimePassed(
										final TimerHandler pTimerHandler) {

									if (mParticleSystemBackground != null) {
										mLayerBackground
												.detachChild(mParticleSystemBackground);
									}

									if (mSelectedFirst != null) {
										mLayerText.detachChild(mSelectedFirst);
									}

									if (mSelectedSecond != null) {
										mLayerText.detachChild(mSelectedSecond);
									}

									if (mSpriteFirstChoice != null) {
										mScene.detachChild(mSpriteFirstChoice);
									}

									if (mSpriteSecondChoice != null) {
										mScene.detachChild(mSpriteSecondChoice);
									}

									if (mParticleSystemSecondChoice != null) {
										mLayer.detachChild(mParticleSystemSecondChoice);
									}

									if (mParticleSystemFirstChoice != null) {
										mLayer.detachChild(mParticleSystemFirstChoice);
									}

									createWhiteClouds();
								}
							}));
				}
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
									mLayer.detachChild(mParticleSystemSecondChoice);
								}

							}

							firstChoiceJustAdded = false;
							bothChoicesMade = true;
							showSparklesSecondChoice((int) centreSign.x,
									(int) centreSign.y);
							updateChoice(false);

							// Show heart
							// Attach the zodiac to the Scene
							if (mLayer.getChildByTag(Constants.HEART) == null) {
								mLayer.attachChild(mSpriteHeart);

								// Animate it

								ScaleModifier modifierHeartGrow = new ScaleModifier(
										0.3f, 0.8f, 1.0f) {
									@Override
									protected void onModifierStarted(
											IEntity pItem) {
										super.onModifierStarted(pItem);

									}

									@Override
									protected void onModifierFinished(
											IEntity pItem) {
										super.onModifierFinished(pItem);
										Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
										v.vibrate(25);
									}
								};

								ScaleModifier modifierHeartShrink = new ScaleModifier(
										0.3f, 1.0f, 0.8f) {
									@Override
									protected void onModifierStarted(
											IEntity pItem) {
										super.onModifierStarted(pItem);
										// Your action after starting modifier
									}

									@Override
									protected void onModifierFinished(
											IEntity pItem) {
										super.onModifierFinished(pItem);
										Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
										v.vibrate(25);
									}
								};

								final LoopEntityModifier scaleInOutModifier = new LoopEntityModifier(
										new SequenceEntityModifier(
												new DelayModifier(0.3f),
												modifierHeartGrow,
												modifierHeartShrink));
								mSpriteHeart
										.registerEntityModifier(scaleInOutModifier);

							}

						} else {

							if (bothChoicesMade) {

								/* Remove any particles from scene */
								if (mParticleSystemFirstChoice != null) {
									mLayer.detachChild(mParticleSystemFirstChoice);
								}

								if (mParticleSystemSecondChoice != null) {
									mLayer.detachChild(mParticleSystemSecondChoice);
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

	};

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
		final float minSpawnRate = 50;
		final float maxSpawnRate = 100;
		final int maxParticleCount = 100;

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
		mLayer.attachChild(mParticleSystemFirstChoice);

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
		final float minSpawnRate = 50;
		final float maxSpawnRate = 100;
		final int maxParticleCount = 100;

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
		mLayer.attachChild(mParticleSystemSecondChoice);

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
		final float maxSpawnRate = 15;
		final int maxParticleCount = 15;

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
						5));

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
		mLayerBackground.attachChild(mParticleSystemBackground);

	}

	private void updateChoice(boolean isFirstChoice) {

		ITextureRegion zodiacTexture;

		zodiacTexture = getStarSignById(mSelectedZodiacId).getZodiacTexture()
				.deepCopy();

		if (mSelectedZodiacId != 0) {

			// Set the text of the zodiac name

			// Assign the correct texture to the sprite

			if (isFirstChoice) {
				mSelected1 = mSelectedZodiacId;

				// Create selection sprites
				mSpriteFirstChoice = new Sprite(SELECTED_SIGN_1_X,
						SELECTED_SIGN_1_Y, zodiacTexture,
						mEngine.getVertexBufferObjectManager());

				mSpriteFirstChoice.setTag(Constants.FIRST_CHOICE);

				// Fade it in to the correct corner

				// Add it to the scene if it isn't already
				if (mScene.getChildByTag(Constants.FIRST_CHOICE) != null) {
					mScene.detachChild(Constants.FIRST_CHOICE);
				}
				mScene.attachChild(mSpriteFirstChoice);

				createExplosion(mEngine, new VertexBufferObjectManager(),
						new fPoint(SELECTED_SIGN_1_X, SELECTED_SIGN_1_Y));

				setText(getStarSignById(mSelectedZodiacId).getName(),
						new fPoint(SELECTED_SIGN_1_X, 774), mSelectedFirst,
						Constants.TEXT_SELECTED_FIRST);

			} else {
				mSelected2 = mSelectedZodiacId;

				// Create selection sprites
				mSpriteSecondChoice = new Sprite(SELECTED_SIGN_2_X,
						SELECTED_SIGN_2_Y, zodiacTexture,
						mEngine.getVertexBufferObjectManager());

				mSpriteSecondChoice.setTag(Constants.SECOND_CHOICE);

				// Reverse the bitmap in the y axis
				mSpriteSecondChoice.setWidth(-mSpriteSecondChoice.getWidth());

				// Add it to the scene if it isn't already
				if (mScene.getChildByTag(Constants.SECOND_CHOICE) != null) {
					mScene.detachChild(Constants.SECOND_CHOICE);
				}
				mScene.attachChild(mSpriteSecondChoice);

				createExplosion(mEngine, new VertexBufferObjectManager(),
						new fPoint(SELECTED_SIGN_2_X, 700));

				setText(getStarSignById(mSelectedZodiacId).getName(),
						new fPoint(SELECTED_SIGN_2_X, 774), mSelectedSecond,
						Constants.TEXT_SELECTED_SECOND);
			}

		}

	}

	private void showSparklesAndSpin() {

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
		mParticleSystemRing = new BatchedSpriteParticleSystem(particleEmitter,
				minSpawnRate, maxSpawnRate, maxParticleCount,
				ResourceManager.getInstance().sparkle,
				mEngine.getVertexBufferObjectManager());

		/* Add an acceleration initializer to the particle system */
		mParticleSystemRing
				.addParticleInitializer(new AccelerationParticleInitializer<UncoloredSprite>(
						100f, -100f, 100f, -100f));

		/* Add an expire initializer to the particle system */
		mParticleSystemRing
				.addParticleInitializer(new ExpireParticleInitializer<UncoloredSprite>(
						2f));

		/* Add a particle modifier to the particle system */
		mParticleSystemRing
				.addParticleModifier(new ScaleParticleModifier<UncoloredSprite>(
						0f, 3f, 0.2f, 1.5f));

		/* Attach the particle system to the Scene */
		mParticleSystemRing.setTag(Constants.SPARKLE_RING);
		mLayer.attachChild(mParticleSystemRing);

		// Spin!
		mSpriteZodiac.registerEntityModifier(new LoopEntityModifier(
				rotationModifier));
		mSpinning = true;

	}

	private void createExplosion(final Engine engine,
			VertexBufferObjectManager vertexBufferObjectManager, fPoint zodiac) {

		final SpriteParticleSystem particleSystem = new SpriteParticleSystem(
				new RectangleParticleEmitter(zodiac.x, zodiac.y, 0.5f, 0.5f),
				100, 200, 50, ResourceManager.getInstance().sparkle,
				vertexBufferObjectManager);

		particleSystem
				.addParticleInitializer(new VelocityParticleInitializer<Sprite>(
						-1000, 1000, -1000, 1000));

		particleSystem
				.addParticleInitializer(new ColorParticleInitializer<Sprite>(
						1.0f, 0.0f, 0.0f));

		particleSystem
				.addParticleInitializer(new ExpireParticleInitializer<Sprite>(
						1.0f));

		particleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(
				0.1f, 1.0f, 0.5f, 0f));

		particleSystem.registerEntityModifier(new DelayModifier(1.0f) {

			@Override
			protected void onModifierFinished(IEntity pItem) {

				engine.runOnUpdateThread(new Runnable() {

					@Override
					public void run() {

						mScene.detachChild(particleSystem);

					}

				});

			}

		});

		mScene.attachChild(particleSystem);

	}

	private void createWhiteClouds() {

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
		final float maxSpawnRate = 15;
		final int maxParticleCount = 15;

		/* Create the particle system */
		mParticleSystemClouds = new BatchedSpriteParticleSystem(
				particleEmitter, minSpawnRate, maxSpawnRate, maxParticleCount,
				ResourceManager.getInstance().cloud,
				mEngine.getVertexBufferObjectManager());

		/* Add an acceleration initializer to the particle system */
		mParticleSystemClouds
				.addParticleInitializer(new AccelerationParticleInitializer<UncoloredSprite>(
						-360f, 360f, -360f, 360f));

		/* Add an expire initializer to the particle system */
		mParticleSystemClouds
				.addParticleInitializer(new ExpireParticleInitializer<UncoloredSprite>(
						5f));

		/* Add a particle modifier to the particle system */
		mParticleSystemClouds
				.addParticleModifier(new ScaleParticleModifier<UncoloredSprite>(
						0f, 1f, 0.2f, 4f));

		/* Define the alpha values */
		final float minAlpha = 0.7f;
		final float maxAlpha = 1;

		AlphaParticleInitializer<UncoloredSprite> alphaParticleInitializer = new AlphaParticleInitializer<UncoloredSprite>(
				minAlpha, maxAlpha);

		mParticleSystemClouds.addParticleInitializer(alphaParticleInitializer);

		mParticleSystemClouds.registerEntityModifier(new DelayModifier(5.0f) {

			@Override
			protected void onModifierFinished(IEntity pItem) {

				mEngine.runOnUpdateThread(new Runnable() {

					@Override
					public void run() {

						mScene.detachChild(mParticleSystemClouds);

					}

				});

			}

		});

		/* Attach the particle system to the Scene */
		mScene.attachChild(mParticleSystemClouds);

	}

	private void setText(String text, fPoint position, Text textObject,
			int textType) {

		textObject.setText(text);

		textObject.setY(position.y);

		if (mLayerText.getChildByTag(textObject.getTag()) == null) {
			mLayerText.attachChild(textObject);

		}

		Log.d("Mainactivity",
				"height:"
						+ mLayerText.getChildByTag(textObject.getTag())
								.getHeight()
						+ " width:"
						+ mLayerText.getChildByTag(textObject.getTag())
								.getWidth());
		textObject.setX(position.x);

	}

	private void showScroll() {

		// Show blue cloud background
		addBackground();

		// Show scroll background
		mSpriteScroll.registerEntityModifier(new ScaleModifier(2f, 0.1f, 1.3f,
				0.1f, 1.3f));

		mLayerBackground.attachChild(mSpriteScroll);

		// Show text. Find the height of it an divide by 2 and add the header to
		// find its y position
		Entity text = new Entity();
		addText(text);

		// Shoe the choices made
		addChoices(text);

		// Add the compatibility
		addCompatibility(text);

		// Create the scrollable area
		ScrollableEntity scrollableArea = new ScrollableEntity(
				mCameraWidth * 0.5f, mCameraHeight * 0.5f, 368, 500, text);

		mLayerBackground.attachChild(scrollableArea);
		mScene.registerTouchArea(scrollableArea);

	}

	/**
	 * @param text
	 * 
	 *            Adds the compatibility text to the scroll
	 */
	private void addText(Entity text) {
		mTextResult = new Text(180, 0, ResourceManager.getInstance().smallFont,
				Utils.getNormalizedText(
						ResourceManager.getInstance().smallFont,
						Utils.getMatchResult(mSelected1, mSelected2), 310f),
				mEngine.getVertexBufferObjectManager());

		mTextResult.setY(700 - (mTextResult.getHeight() / 2));
		mTextResult.registerEntityModifier(new AlphaModifier(10, 0f, 1f));
		mTextResult.registerEntityModifier(new ScaleModifier(2f, 0.1f, 1f,
				0.1f, 1f));

		// Create the content for the scroll. This will be inserted into the
		// scrollable area

		text.attachChild(mTextResult);

	}

	/**
	 * @param text
	 * 
	 *            Adds the correct hearts to the scroll
	 * 
	 */
	private void addCompatibility(Entity text) {

		Sprite heartSprite;

		for (int x = 1; x < 6; x++) {
			if (x > GameManager.getInstance().getCurrentCompatibility()) {
				heartSprite = new Sprite((50 * x) + 30, 690,
						ResourceManager.getInstance().heartEmpty,
						mEngine.getVertexBufferObjectManager());
			} else {
				heartSprite = new Sprite((50 * x) + 30, 690,
						ResourceManager.getInstance().heartFull,
						mEngine.getVertexBufferObjectManager());
			}

			heartSprite.registerEntityModifier(new ScaleModifier(2f, 0.1f, 1f,
					0.1f, 1f));
			heartSprite.registerEntityModifier(new AlphaModifier(7, 0f, 1f));

			text.attachChild(heartSprite);
		}

	}

	private void addChoices(Entity text) {
		// Show first choice
		mSpriteFirstChoice.setX(100);
		mSpriteFirstChoice.setY(800);
		mSpriteFirstChoice.registerEntityModifier(new ScaleModifier(2f, 0.1f,
				1f, 0.1f, 1f));
		mSpriteFirstChoice
				.registerEntityModifier(new AlphaModifier(10, 0f, 1f));
		text.attachChild(mSpriteFirstChoice);

		// Show second choice
		mSpriteSecondChoice.setX(260);
		mSpriteSecondChoice.setY(800);
		mSpriteSecondChoice.registerEntityModifier(new ScaleModifier(2f, 0.1f,
				1f, 0.1f, 1f));
		mSpriteSecondChoice
				.registerEntityModifier(new AlphaModifier(10, 0f, 1f));
		text.attachChild(mSpriteSecondChoice);
	}

	// --------------------------------------------------------------------------
	// Speech and sound
	// --------------------------------------------------------------------------

	private void speakOut(String words) {
		tts.speak(words, TextToSpeech.QUEUE_FLUSH, null);
	}

	// --------------------------------------------------------------------------
	// Data
	// --------------------------------------------------------------------------

	private void loadStarSignData() {

		// Load get the signs data from the assets and stick it into a JSON
		// object
		JSONObject obj = null;
		JSONArray jArry = null;
		StarMatch starMatch;

		try {
			obj = new JSONObject(Utils.loadJSONFromAsset("text/star_signs",
					this));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			jArry = obj.getJSONArray("signs");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (jArry != null) {

			starMatches = new ArrayList<StarMatch>();

			for (int i = 0; i < jArry.length(); i++) {

				JSONObject jStarMatch;
				JSONArray jParagraphs;

				try {
					jStarMatch = jArry.getJSONObject(i);
					starMatch = new StarMatch();
					starMatch.setStarSign1(jStarMatch
							.getInt(Constants.JSONKeys.STAR_SIGN_1));
					starMatch.setStarSign2(jStarMatch
							.getInt(Constants.JSONKeys.STAR_SIGN_2));
					starMatch.setCurrentCompatibility(jStarMatch
							.getInt(Constants.JSONKeys.COMPATIBILITY));

					// Get and populate paragraphs
					jParagraphs = jStarMatch
							.getJSONArray(Constants.JSONKeys.PARAGRAPHS);

					if (jParagraphs != null) {

						JSONObject jParagraph;
						List<String> paragraphsList = new ArrayList<String>();

						for (int j = 0; j < jParagraphs.length(); j++) {
							jParagraph = jParagraphs.getJSONObject(j);
							paragraphsList.add(jParagraph
									.getString(Constants.JSONKeys.TEXT));
						}

						starMatch.setParagraphsList(paragraphsList);

					}

					starMatches.add(starMatch);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			GameManager.getInstance().setStarMatches(starMatches);
		}

	}
	// --------------------------------------------------------------------------
	// Handler
	// --------------------------------------------------------------------------

}
