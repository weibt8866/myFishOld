package com.noahedu.fish.anim;

import com.noahedu.fish.R;
import com.noahedu.fish.tool.Tools;
import com.noahedu.fish.tool.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;

public class ObjectMgr {
	public ObjectMgr(Context context, int mObjectId) {
		this.mObjectId = mObjectId;
		this.context = context;
		init();
	}

	int mObjectId;
	Context context;
	Bitmap bmpObject = null;

	private static final int[][] mObjectResIdArrays = {
			{ R.drawable.boat_front_1, R.drawable.boat_front_2,
					R.drawable.boat_front_3, R.drawable.boat_front_4,
					R.drawable.boat_front_5, R.drawable.boat_front_6,
					R.drawable.boat_front_7, R.drawable.boat_front_8,
					R.drawable.boat_front_9, R.drawable.boat_front_10,
					R.drawable.boat_front_11 },
			{ R.drawable.waves_a_1, R.drawable.waves_a_2, R.drawable.waves_a_3,
					R.drawable.waves_a_4, R.drawable.waves_a_5,
					R.drawable.waves_a_6, R.drawable.waves_a_7,
					R.drawable.waves_a_8, R.drawable.waves_a_9,
					R.drawable.waves_a_10, R.drawable.waves_a_11,
					R.drawable.waves_a_12, R.drawable.waves_a_13,
					R.drawable.waves_a_14, R.drawable.waves_a_15,
					R.drawable.waves_a_16, R.drawable.waves_a_17,
					R.drawable.waves_a_18, R.drawable.waves_a_19,
					R.drawable.waves_a_20 },
			{ R.drawable.waves_b_1, R.drawable.waves_b_2, R.drawable.waves_b_3,
					R.drawable.waves_b_4, R.drawable.waves_b_5,
					R.drawable.waves_b_6, R.drawable.waves_b_7,
					R.drawable.waves_b_8, R.drawable.waves_b_9,
					R.drawable.waves_b_10, R.drawable.waves_b_11,
					R.drawable.waves_b_12, R.drawable.waves_b_13,
					R.drawable.waves_b_14, R.drawable.waves_b_15,
					R.drawable.waves_b_16, R.drawable.waves_b_17,
					R.drawable.waves_b_18, R.drawable.waves_b_19,
					R.drawable.waves_b_20 },
			{ R.drawable.duo_normal_1, R.drawable.duo_normal_2,
					R.drawable.duo_normal_3, R.drawable.duo_normal_4,
					R.drawable.duo_normal_5, R.drawable.duo_normal_6,
					R.drawable.duo_normal_7, R.drawable.duo_normal_8,
					R.drawable.duo_normal_9 },
			{ R.drawable.duo_happy_1, R.drawable.duo_happy_2,
					R.drawable.duo_happy_3, R.drawable.duo_happy_4,
					R.drawable.duo_happy_5, R.drawable.duo_happy_6,
					R.drawable.duo_happy_7 },
			{ R.drawable.duo_lost_1, R.drawable.duo_lost_2,
					R.drawable.duo_lost_3, R.drawable.duo_lost_4,
					R.drawable.duo_lost_5, R.drawable.duo_lost_6,
					R.drawable.duo_lost_7, R.drawable.duo_lost_8 },
			{ R.drawable.shake_1, R.drawable.shake_2, R.drawable.shake_3,
					R.drawable.shake_4, R.drawable.shake_5, R.drawable.shake_6 },
			{ R.drawable.sweaty_1, R.drawable.sweaty_2, R.drawable.sweaty_3,
					R.drawable.sweaty_4, R.drawable.sweaty_5,
					R.drawable.sweaty_6 },
			{ R.drawable.speak_1, R.drawable.speak_2, R.drawable.speak_3,
					R.drawable.speak_4, R.drawable.speak_5, R.drawable.speak_6,
					R.drawable.speak_7, R.drawable.speak_8, R.drawable.speak_9 } };

	public final int[][] mObjectXY = { {Utils.pxToDp(544 - 128), Utils.pxToDp(147) },
									{ Utils.pxToDp(0), Utils.pxToDp(210) },
									{ Utils.pxToDp(0), Utils.pxToDp(240) },
									{ Utils.pxToDp(298), Utils.pxToDp(55) },
									{ Utils.pxToDp(298), Utils.pxToDp(55) },
									{ Utils.pxToDp(298), Utils.pxToDp(55) },
									{ Utils.pxToDp(298), Utils.pxToDp(55) },
									{ Utils.pxToDp(298), Utils.pxToDp(55) },
									{ Utils.pxToDp(298), Utils.pxToDp(55) } };

	private void init() {
		startAnim();
	}

	public static final int MSG_ANIM = 1;
	private static final int ANIM_DURATION = 50;
	private int mAnimIndex = 0;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ANIM:
				bmpObject = BitmapFactory.decodeResource(
						context.getResources(),
						mObjectResIdArrays[mObjectId][mAnimIndex]);
				mAnimIndex++;
				if (mAnimIndex >= mObjectResIdArrays[mObjectId].length) {
					if (mObjectId == 4 || mObjectId == 5) {
						if (mOnAnimEndListener != null) {
							mOnAnimEndListener.OnEndListener();
						}
					} else {
						mAnimIndex = 0;
						if (mHandler != null) {
							mHandler.sendEmptyMessageDelayed(MSG_ANIM,
									ANIM_DURATION);
						}
					}
				} else {
					if (mHandler != null) {
						mHandler.sendEmptyMessageDelayed(MSG_ANIM,
								ANIM_DURATION);
					}
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public interface OnAnimEndListener {
		public void OnEndListener();
	}

	private OnAnimEndListener mOnAnimEndListener = null;

	public void setOnAnimEndListener(OnAnimEndListener mOnAnimEndListener) {
		this.mOnAnimEndListener = mOnAnimEndListener;
	}

	public void startAnim() {
		mAnimIndex = 0;
		if (mHandler != null) {
			mHandler.sendEmptyMessage(MSG_ANIM);
		}
	}

	public void stopAnim() {
		if (mHandler != null) {
			mHandler.removeMessages(MSG_ANIM);
		}
	}

	/**
	 * 回收Bitmap
	 */
	public void recycleObject() {
		Tools.safeRecycle(bmpObject);
	}

	/**
	 * 小朵正常状态
	 */
	public void showDuoNormal() {
		showDuoState(3);
	}

	/**
	 * 小朵答对状态
	 */
	public void showDuoHappy() {
		showDuoState(4);
	}

	/**
	 * 小朵答错状态
	 */
	public void showDuoLost() {
		showDuoState(5);
	}

	/**
	 * 小朵收鱼钩
	 */
	public void showDuoShake() {
		showDuoState(6);
	}

	/**
	 * 小朵吃力的拉鱼钩
	 */
	public void showDuoSweaty() {
		showDuoState(7);
	}

	/**
	 * 小朵说话
	 */
	public void showDuoSpeak() {
		showDuoState(8);
	}

	private void showDuoState(int mStateId) {
		stopAnim();
		mObjectId = mStateId;
		startAnim();
	}

	/**
	 * 波浪
	 * 
	 * @param canvas
	 * @param paint
	 */
	public void draw(Canvas canvas, Paint paint) {
		if (bmpObject != null) {
			canvas.drawBitmap(bmpObject, mObjectXY[mObjectId][0],
					mObjectXY[mObjectId][1], paint);
		}
	}

	/**
	 * 
	 * @param canvas
	 * @param paint
	 */
	public void drawRotate(Canvas canvas, Paint paint, float degree) {
		if (bmpObject != null) {
			canvas.drawBitmap(Tools.rotateBitmap(bmpObject, degree),
					mObjectXY[mObjectId][0], mObjectXY[mObjectId][1], paint);
		}
	}

	/**
	 * 
	 * @param canvas
	 * @param paint
	 * @param delX
	 *            水平方向的偏移
	 */
	public void draw(Canvas canvas, Paint paint, int delX) {
		if (bmpObject != null) {
			canvas.drawBitmap(bmpObject, mObjectXY[mObjectId][0] + delX,
					mObjectXY[mObjectId][1], paint);
		}
	}

	/**
	 * 
	 * @param canvas
	 * @param paint
	 * @param delX
	 *            水平方向的偏移
	 */
	public void drawRotate(Canvas canvas, Paint paint, float degree, int delX,
			int delY) {
		if (bmpObject != null) {
			canvas.drawBitmap(Tools.rotateBitmap(bmpObject, degree),
					mObjectXY[mObjectId][0] + delX, mObjectXY[mObjectId][1]
							+ delY, paint);
		}
	}

	/**
	 * 
	 * @param canvas
	 * @param left
	 * @param top
	 * @param paint
	 * @param delX
	 *            水平方向的偏移
	 */
	public void draw(Canvas canvas, int left, int top, Paint paint, int delX) {
		if (bmpObject != null) {
			canvas.drawBitmap(bmpObject, left + delX, top, paint);
		}
	}

	public void drawRotate(Canvas canvas, int left, int top, Paint paint,
			float degree, int delX, int delY) {
		if (bmpObject != null) {
			canvas.drawBitmap(Tools.rotateBitmap(bmpObject, degree), left
					+ delX, top + delY, paint);
		}
	}
}
