package com.writtenbyaliens.zodiaclovemachine.Entities;

import org.andengine.entity.IEntity;
import org.andengine.entity.clip.ClipEntity;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;

import android.util.Log;

public class ScrollableEntity extends ClipEntity implements
		IOnSceneTouchListener, IScrollDetectorListener, ITouchArea {

	private SurfaceScrollDetector scrollDetector;
	private IEntity contentEntity;
	private float myBottom;

	public ScrollableEntity(float x, float y, int width, int height,
			final IEntity content) {
		super(x, y, width, height);

		this.scrollDetector = new SurfaceScrollDetector(this);

		setContent(content);
	}

	public void setContent(IEntity content) {
		if (contentEntity != null && contentEntity.hasParent()) {
			contentEntity.detachSelf();
			contentEntity.dispose();
			Log.d("setContent", "detaching");
		}
		contentEntity = content;
		attachChild(contentEntity);
		if (contentEntity != null) {
			contentEntity.setPosition(contentEntity.getX(), -368);
		}

		myBottom = contentEntity.getFirstChild().getHeight() / 2 - 368;

	}

	@Override
	public void onScrollStarted(ScrollDetector detector, int pointerID,
			float distanceX, float distanceY) {
	}

	@Override
	public void onScroll(ScrollDetector detector, int pointerID,
			float distanceX, float distanceY) {

		if (contentEntity != null) {

			float y = contentEntity.getY() - (distanceY * 0.45f);

			Log.d("onScroll", "distancey:" + y);
			Log.d("onScroll", "contentEntity:"
					+ contentEntity.getFirstChild().getHeight());
			if (y >= -368 & y < myBottom) {
				contentEntity.setPosition(contentEntity.getX(), y);
			} else {
				if (y < -368) {
					y = -368;
				}

				if (y > myBottom) {
					y = myBottom;
				}

				contentEntity.setPosition(contentEntity.getX(), y);
			}

		}
	}

	@Override
	public void onScrollFinished(ScrollDetector detector, int pointerID,
			float distanceX, float distanceY) {
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {

		scrollDetector.onTouchEvent(pSceneTouchEvent);

		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
				pTouchAreaLocalY);

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// Never called
		return false;
	}

}
