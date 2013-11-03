package com.writtenbyaliens.zodiaclovemachine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;
 
public class MainActivity extends BaseGameActivity {
       
        final int mCameraWidth = 800;  
        final int mCameraHeight = 480;
        public Scene mScene;
 
               
                @Override
                public EngineOptions onCreateEngineOptions() {
                        Camera mCamera = new Camera(0, 0, mCameraWidth, mCameraHeight);
                        final EngineOptions engineOptions = new EngineOptions(true, org.andengine.engine.options.ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(mCameraWidth, mCameraHeight), mCamera);
                        return engineOptions;
                }
               
                @Override
                public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
 
                        pOnCreateResourcesCallback.onCreateResourcesFinished();
                }
               
                @Override
                public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
                        mScene = new Scene();
 
                        pOnCreateSceneCallback.onCreateSceneFinished(mScene);
                }
               
                @Override
                public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) {
 
                        pOnPopulateSceneCallback.onPopulateSceneFinished();
                }
               
}
 