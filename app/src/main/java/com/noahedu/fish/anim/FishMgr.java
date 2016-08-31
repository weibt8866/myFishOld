package com.noahedu.fish.anim;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;

import com.noahedu.fish.R;
import com.noahedu.fish.param.GameConstant;
import com.noahedu.fish.tool.PointXY;
import com.noahedu.fish.tool.RectData;
import com.noahedu.fish.tool.Tools;
import com.noahedu.fish.tool.Utils;

/**
 * 鱼的管理类
 * 
 * @author Zhang
 * 
 */
public class FishMgr {
	/**
	 * 每种鱼的长度（7～13是杂物）
	 */
	public static final int[] mFishLenth = {Utils.pxToDp(96), Utils.pxToDp(78),
											Utils.pxToDp(92), Utils.pxToDp(110),
			Utils.pxToDp(163), Utils.pxToDp(229),
			Utils.pxToDp(123), Utils.pxToDp(67),
			Utils.pxToDp(49), Utils.pxToDp(72),
			Utils.pxToDp(128), Utils.pxToDp(97),
			Utils.pxToDp(93), Utils.pxToDp(87) };
	/**
	 * 每种鱼热点区域坐标（从左向右，7～13是杂物）
	 */
	final int[][] mRextXY = { { Utils.pxToDp(50), Utils.pxToDp(0) },
			{ Utils.pxToDp(43), Utils.pxToDp(16) },
			{ Utils.pxToDp(0), Utils.pxToDp(25) },
			{ Utils.pxToDp(60), Utils.pxToDp(0) },
			{ Utils.pxToDp(83), Utils.pxToDp(0) },
			{ Utils.pxToDp(136), Utils.pxToDp(20) },
			{ Utils.pxToDp(76), Utils.pxToDp(0) },

			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) } };

	/**
	 * 每种鱼热点区域长度和宽度（从左向右，7～13是杂物）
	 */
	final int[][] mRectWH = { { Utils.pxToDp(46), Utils.pxToDp(56) },
			{ Utils.pxToDp(36), Utils.pxToDp(59) },
			{ Utils.pxToDp(92), Utils.pxToDp(60) },
			{ Utils.pxToDp(50), Utils.pxToDp(80) },
			{ Utils.pxToDp(80), Utils.pxToDp(88) },
			{ Utils.pxToDp(59), Utils.pxToDp(53) },
			{ Utils.pxToDp(48), Utils.pxToDp(50) },

			{ Utils.pxToDp(67), Utils.pxToDp(102) },
			{ Utils.pxToDp(49), Utils.pxToDp(76) },
			{ Utils.pxToDp(72), Utils.pxToDp(65) },
			{ Utils.pxToDp(128), Utils.pxToDp(88) },
			{ Utils.pxToDp(97), Utils.pxToDp(106) },
			{ Utils.pxToDp(93), Utils.pxToDp(92) },
			{ Utils.pxToDp(87), Utils.pxToDp(82) } };

	/**
	 * 每种鱼热点区域坐标（从右向左，7～13是杂物）
	 */
	final int[][] mRextXYReverse = { { Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(36), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) },

			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) },
			{ Utils.pxToDp(0), Utils.pxToDp(0) } };

	/**
	 * 每种鱼热点区域长度和宽度（从右向左，7～13是杂物）
	 */
	final int[][] mRectWHReverse = { { Utils.pxToDp(46), Utils.pxToDp(56) },
			{ Utils.pxToDp(36), Utils.pxToDp(59) },
			{ Utils.pxToDp(92), Utils.pxToDp(60) },
			{ Utils.pxToDp(50), Utils.pxToDp(80) },
			{ Utils.pxToDp(80), Utils.pxToDp(88) },
			{ Utils.pxToDp(59), Utils.pxToDp(53) },
			{ Utils.pxToDp(48), Utils.pxToDp(50) },

			{ Utils.pxToDp(67), Utils.pxToDp(102) },
			{ Utils.pxToDp(49), Utils.pxToDp(76) },
			{ Utils.pxToDp(72), Utils.pxToDp(65) },
			{ Utils.pxToDp(128), Utils.pxToDp(88) },
			{ Utils.pxToDp(97), Utils.pxToDp(106) },
			{ Utils.pxToDp(93), Utils.pxToDp(92) },
			{ Utils.pxToDp(87), Utils.pxToDp(82) } };

	/**
	 * 鱼的动画（7～13是杂物）
	 */
	private static final int[][] mFishResIdArrays = {
			{ R.drawable.fish1_1, R.drawable.fish1_2, R.drawable.fish1_3,
					R.drawable.fish1_4, R.drawable.fish1_5, R.drawable.fish1_6,
					R.drawable.fish1_7, R.drawable.fish1_8, R.drawable.fish1_9,
					R.drawable.fish1_10, R.drawable.fish1_11 },
			{ R.drawable.fish2_1, R.drawable.fish2_2, R.drawable.fish2_3,
					R.drawable.fish2_4, R.drawable.fish2_5, R.drawable.fish2_6,
					R.drawable.fish2_7, R.drawable.fish2_8, R.drawable.fish2_9,
					R.drawable.fish2_10, R.drawable.fish2_11 },
			{ R.drawable.fish3_1, R.drawable.fish3_2, R.drawable.fish3_3,
					R.drawable.fish3_4, R.drawable.fish3_5, R.drawable.fish3_6,
					R.drawable.fish3_7, R.drawable.fish3_8, R.drawable.fish3_9,
					R.drawable.fish3_10, R.drawable.fish3_11 },
			{ R.drawable.fish4_1, R.drawable.fish4_2, R.drawable.fish4_3,
					R.drawable.fish4_4, R.drawable.fish4_5, R.drawable.fish4_6,
					R.drawable.fish4_7, R.drawable.fish4_8, R.drawable.fish4_9,
					R.drawable.fish4_10, R.drawable.fish4_11 },
			{ R.drawable.fish5_1, R.drawable.fish5_2, R.drawable.fish5_3,
					R.drawable.fish5_4, R.drawable.fish5_5, R.drawable.fish5_6,
					R.drawable.fish5_7, R.drawable.fish5_8, R.drawable.fish5_9,
					R.drawable.fish5_10, R.drawable.fish5_11 },
			{ R.drawable.fish6_1, R.drawable.fish6_2, R.drawable.fish6_3,
					R.drawable.fish6_4, R.drawable.fish6_5, R.drawable.fish6_6,
					R.drawable.fish6_7, R.drawable.fish6_8, R.drawable.fish6_9,
					R.drawable.fish6_10, R.drawable.fish6_11 },
			{ R.drawable.fish7_1, R.drawable.fish7_2, R.drawable.fish7_3,
					R.drawable.fish7_4, R.drawable.fish7_5, R.drawable.fish7_6,
					R.drawable.fish7_7, R.drawable.fish7_8, R.drawable.fish7_9,
					R.drawable.fish7_10, R.drawable.fish7_11 },
			{ R.drawable.items_1 }, { R.drawable.items_2 },
			{ R.drawable.items_3 }, { R.drawable.items_4 },
			{ R.drawable.items_5 }, { R.drawable.items_6 },
			{ R.drawable.items_7 } };

	public FishMgr(Context context, int mCurrentLevel, int mFishTypeId,
			boolean mIsClickStudy, int mWordIndex, String mStrWord) {
		this.mCurrentLevel = mCurrentLevel;
		this.mFishTypeId = mFishTypeId;
		this.mIsClickStudy = mIsClickStudy;
		this.mWordIndex = mWordIndex;
		this.mStrWord = mStrWord;
		this.context = context;
		init(context);
	}

	private int mCurrentLevel;

	/**
	 * 鱼的类别（7～13是杂物）
	 */
	private int mFishTypeId;

	private boolean mIsClickStudy = false;

	private int mWordIndex = 0;

	private String mStrWord;
	/**
	 * 鱼或杂物移动的方向（默认从左向右）
	 */
	private boolean mIsLeft = true;
	public RectData rd;
	private Bitmap bmpFish;
	private Bitmap bmpBlank;
	private Context context;
	private Random r = new Random();

	private void init(Context context) {
		if (r.nextInt(2) == 0) {
			mIsLeft = true;
		} else {
			mIsLeft = false;
		}
		setRectData();
		bmpBlank = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.res_blank);
		startFishAnim();
	}

	/**
	 * 设置响应鱼钩的热点范围（从左向右）
	 */
	private void setRectData() {
		rd = new RectData();
		int[] xy = null;
		int[] wh = null;
		if (mIsLeft) {
			xy = mRextXY[mFishTypeId];
			wh = mRectWH[mFishTypeId];
		} else {
			xy = mRextXYReverse[mFishTypeId];
			wh = mRectWHReverse[mFishTypeId];
		}

		rd.setX(xy[0]);
		rd.setY(xy[1]);
		rd.setWidth(wh[0]);
		rd.setHeight(wh[1]);
	}

	public static final int MSG_ANIM = 1;
	private int mAnimIndex = 0;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ANIM:
				bmpFish = BitmapFactory.decodeResource(context.getResources(),
						mFishResIdArrays[mFishTypeId][mAnimIndex]);
				mAnimIndex++;
				if (mAnimIndex >= mFishResIdArrays[mFishTypeId].length) {
					mAnimIndex = 0;
				}

				if (mHandler != null) {
					mHandler.sendEmptyMessageDelayed(MSG_ANIM, 100);
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void startFishAnim() {
		if (mHandler != null) {
			mHandler.sendEmptyMessage(MSG_ANIM);
		}
	}

	public void stopFishAnim() {
		if (mHandler != null) {
			mHandler.removeMessages(MSG_ANIM);
		}
	}

	/**
	 * 鱼或杂物游动的纵坐标范围
	 */
	static final int Y = Utils.pxToDp(150);
	static final int mRandomY = Utils.pxToDp(310);

	public PointXY initPosition() {
		PointXY xy = new PointXY();
		int x = 0, y = 0;
		if (mIsLeft) {
			x = -(mFishLenth[mFishTypeId]);
		} else {
			x = Utils.getScreenW() + mFishLenth[mFishTypeId];
		}
		y = Y + r.nextInt(mRandomY);
		xy.setX(x);
		xy.setY(y);
		return xy;
	}

	/**
	 * 设置鱼或杂物被钓起来时的坐标
	 * 
	 * @param mHookX
	 *            鱼钩X坐标
	 * @param mHookY
	 *            鱼钩Y坐标
	 */
	public int getCatchedX(int mHookX, int mHookY) {
		int hx = mHookX + Utils.pxToDp(17);
		int fx = 0;
		if (mIsLeft) {
			fx = hx - mRextXY[mFishTypeId][0] - (mRectWH[mFishTypeId][0] / 2);
		} else {
			fx = hx - mRextXYReverse[mFishTypeId][0]
					- (mRectWHReverse[mFishTypeId][0] / 2);
		}
		return fx;
	}

	/**
	 * 回收Bitmap
	 */
	public void recycleFish() {
		if (mIsClickStudy) {
			Tools.safeRecycle(bmpBlank);
		}
		Tools.safeRecycle(bmpFish);
		Tools.safeRecycle(bmpTmp);
	}

	Bitmap bmpTmp;

	public void draw(Canvas canvas, Paint paint, int left, int top) {
		int x, y;
		if (bmpFish != null) {
			paint.setTextSize(Utils.pxToDp(16));
			if (mIsLeft) {
				canvas.drawBitmap(bmpFish, left, top, paint);
				if (mIsClickStudy) {
					if (isFish()) {
						x = left + mLeftXY[mFishTypeId][0];
						y = top + mLeftXY[mFishTypeId][1];
						canvas.drawBitmap(bmpBlank, x, y, paint);
						canvas.drawText(
								mStrWord,
								x
										+ Utils.pxToDp(3)
										+ (Utils.pxToDp(94) - getWordWidth()
												* mStrWord.length()) / 2,
								y + Utils.pxToDp(20), paint);
					}
				}
			} else {
				bmpTmp = Tools.convertBitmap(bmpFish);
				canvas.drawBitmap(bmpTmp, left, top, paint);
				if (mIsClickStudy) {
					if (isFish()) {
						x = left + mRightXY[mFishTypeId][0];
						y = top + mRightXY[mFishTypeId][1];
						canvas.drawBitmap(bmpBlank, x, y, paint);
						canvas.drawText(
								mStrWord,
								x
										+ Utils.pxToDp(3)
										+ (Utils.pxToDp(94) - getWordWidth()
												* mStrWord.length()) / 2,
								y + Utils.pxToDp(20), paint);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @return 单个字母或汉字的宽度
	 */
	private int getWordWidth() {
		int delWidth = 0;
		if (String.valueOf(mStrWord.charAt(0)).matches("[A-Za-z]")) {
			delWidth = 6 + 2;
		} else {
			delWidth = 12 + 4;
		}
		return Utils.pxToDp(delWidth);
	}

	/**
	 * 内容显示框相对鱼的坐标（从左向右）
	 */
	private final int[][] mLeftXY = { { Utils.pxToDp(-42), Utils.pxToDp(7) },
			{ Utils.pxToDp(-20), Utils.pxToDp(-10) },
			{ Utils.pxToDp(-15), Utils.pxToDp(60) },
			{ Utils.pxToDp(-25), Utils.pxToDp(30) },
			{ Utils.pxToDp(-2), Utils.pxToDp(3) },
			{ Utils.pxToDp(15), Utils.pxToDp(18) },
			{ Utils.pxToDp(-20), Utils.pxToDp(0) } };

	/**
	 * 内容显示框相对鱼的坐标（从右向左）
	 */
	private final int[][] mRightXY = { { Utils.pxToDp(38), Utils.pxToDp(7) },
			{ Utils.pxToDp(0), Utils.pxToDp(-10) },
			{ Utils.pxToDp(5), Utils.pxToDp(60) },
			{ Utils.pxToDp(40), Utils.pxToDp(30) },
			{ Utils.pxToDp(70), Utils.pxToDp(3) },
			{ Utils.pxToDp(115), Utils.pxToDp(18) },
			{ Utils.pxToDp(44), Utils.pxToDp(0) } };

	/**
	 * 获取鱼或杂物游动的方向
	 * 
	 * @return 从左向右或从右向左
	 */
	public boolean getFishDirection() {
		return mIsLeft;
	}

	/**
	 * 判断物体是否为鱼类，否则为杂物
	 * 
	 * @return
	 */
	public boolean isFish() {
		if (mFishTypeId < 7) {
			return true;
		} else {
			return false;
		}
	}

	public int getWordIndex() {
		if (mIsClickStudy) {
			return mWordIndex;
		} else {
			return 0;
		}
	}

	/**
	 * 获取每种鱼的分数
	 * 
	 * @return
	 */
	public int getFishScores() {
		return mFishScores[mFishTypeId];
	}

	/**
	 * 获取鱼或杂物的图片
	 * 
	 * @return
	 */
	public Bitmap getBitmap() {
		return BitmapFactory.decodeResource(context.getResources(),
				mFishResIdArrays[mFishTypeId][0]);
	}

	/**
	 * 获取鱼或杂物游动的速度
	 * 
	 * @return
	 */
	public int getFishSpeed() {
		if (mCurrentLevel < 3) {
			return mSpeed[mFishTypeId];
		} else {
			return mSpeed2[mFishTypeId];
		}
	}

	/**
	 * 获取每种鱼的重量
	 * 
	 * @return
	 */
	public int getFishWeight() {
		return mFishWeight[mFishTypeId];
	}

	/**
	 * 鱼及其他杂物游动的速度1～3关
	 */
	private static final int[] mSpeed = { Utils.pxToDp(2), Utils.pxToDp(3), Utils.pxToDp(3),
			Utils.pxToDp(4), Utils.pxToDp(2), Utils.pxToDp(2),
			Utils.pxToDp(3), Utils.pxToDp(3), Utils.pxToDp(3),
			Utils.pxToDp(3), Utils.pxToDp(2), Utils.pxToDp(2),
			Utils.pxToDp(2), Utils.pxToDp(2) };

	/**
	 * 鱼及其他杂物游动的速度4～6关
	 */
	private static final int[] mSpeed2 = { Utils.pxToDp(3), Utils.pxToDp(5), Utils.pxToDp(5),
			Utils.pxToDp(6), Utils.pxToDp(3), Utils.pxToDp(3),
			Utils.pxToDp(5), Utils.pxToDp(5), Utils.pxToDp(5),
			Utils.pxToDp(5), Utils.pxToDp(3), Utils.pxToDp(3),
			Utils.pxToDp(3), Utils.pxToDp(3) };

	/**
	 * 鱼的重量
	 */
	private static final int[] mFishWeight = { 1, 1, 1, 2, 3, 3, 2, 1, 1, 1, 2,
			3, 3, 2 };

	/**
	 * 每种鱼的分数
	 */
	public static final int[] mFishScores = { 100, 100, 100, 200, 300, 300,
			200, 0, 0, 0, 0, 0, 0, 0 };
}
