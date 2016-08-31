package com.noahedu.fish;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noahedu.FetchWordLibData.WordItem;
import com.noahedu.FetchWordLibData.Dds.JniDds;
import com.noahedu.SoundPlayer.SoundPlayer;
import com.noahedu.SoundPlayer.SoundPlayerParam;
import com.noahedu.SoundPlayer.SoundPlayerParam.onStatusListener;
import com.noahedu.fish.anim.FishMgr;
import com.noahedu.fish.anim.ObjectMgr;
import com.noahedu.fish.anim.ObjectMgr.OnAnimEndListener;
import com.noahedu.fish.db.FishDb;
import com.noahedu.fish.engine.GifView;
import com.noahedu.fish.param.GameConstant;
import com.noahedu.fish.tool.PlaySound;
import com.noahedu.fish.tool.PointXY;
import com.noahedu.fish.tool.RectData;
import com.noahedu.fish.tool.Tools;
import com.noahedu.fish.tool.Utils;
import com.noahedu.fish.widget.TimeBarView;

public class Fish extends BaseActivity {
	private final float CLICK_BMP_H = Utils.getMyResource().getDimension(R.dimen.click_bmp_h);
	private final float CLICK_BMP_X_S = Utils.getMyResource().getDimension(R.dimen.click_bmp_x_s);
	private final float CLICK_BMP_Y_S = Utils.getMyResource().getDimension(R.dimen.click_bmp_y_s);
	private final float CLICK_BMP_MAX_W = Utils.getMyResource().getDimension(R.dimen.click_bmp_max_w);


	static {
		try {
			System.loadLibrary("JniDdsWordLib");
			System.loadLibrary("JniDdsRightlessWordLib");
		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
	}

	public static final String TAG = "Fish";

	private ImageView mIvResource, mIvVoice, mIvSuspended, mIvFishStart,
			mIvTimeBar2;

	private AbsoluteLayout mClickStudyAbl;

	private LinearLayout fvLL, numsLL, levelLL, scoreLL, numsClickStudyLL,
			mLLTimeBar1;

	/**
	 * 关卡
	 */
	private int[] mLevelResIds = {R.drawable.level1, R.drawable.level2,
			R.drawable.level3, R.drawable.level4, R.drawable.level5,
			R.drawable.level6};
	/**
	 * 当前关卡
	 */
	private int mCurrentLevel = 0;

	/**
	 * 钓到的鱼总条数
	 */
	private int mFishCatchedTotalNums = 0;

	/**
	 * 杂物数量
	 */
	private int mOtherObjectNums = 0;

	/**
	 * 获得分数
	 */
	private int mTotalScore = 0;

	/**
	 * 钓鱼区域
	 */
	private FishView fv = null;

	/**
	 * 时间进度条长度
	 */
	private int mTimeBarWidth = 327;

	/**
	 * 游戏是否结束
	 */
	private boolean mIsFinished = false;

	private SoundPlayer mSoundPlayer = new SoundPlayer();

	private SoundPlayerParam param = new SoundPlayerParam();

	private FishDb db = new FishDb(this);

	/**
	 * 游戏是否成功
	 */
	private boolean mIsGameSuccess = false;

	/**
	 * 判断是否从点学入口进入
	 */
	private boolean mIsClickStudy = false;

	/**
	 * 点学资源路径
	 */
	private String mClickFilePath;

	/**
	 * 单元号
	 */
	private int mCurrentUnit;

	/**
	 * 有版权词汇
	 */
	private JniDds mJniDds = null;

	/**
	 * 每单元第一个单词的序号
	 */
	private int mIndexStart = 0;

	/**
	 * 每单元里的单词数
	 */
	private int numsInUnit = 0;

	/**
	 * 本单元所有单词序号
	 */
	private List<Integer> mALLWordIndexList = new ArrayList<Integer>();

	/**
	 * 学过的所有单词序号
	 */
	private Set<Integer> mWordIndexSet = new HashSet<Integer>();
	private ArrayList<Integer> mWordIndexList = new ArrayList<Integer>();

	private int mClickStudyIndex = 0;

	/**
	 * 全部通关
	 */
	private boolean mIsAllGamePassed = false;

	/**
	 * 只显示背景
	 */
	private boolean mIsDisplayBackground = true;

	/**
	 * 钓起来放到船上的杂物的图片
	 */
	private List<Bitmap> mDebrisBmpList = null;

	private boolean canHookTouchDown = false;

	private boolean bCreateNewFish = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fish);

		doInit();
		findViews();
		fv = new FishView(this);
		fvLL.addView(fv);
		doFishInit();
	}

	private void doInit() {
		getClickStudyInfo();
		if (db != null) {
			db.open();
		}
	}

	private void findViews() {
		mDebrisBmpList = new ArrayList<Bitmap>();
		mIvResource = (ImageView) findViewById(R.id.resource);
		mIvVoice = (ImageView) findViewById(R.id.voice);
		mIvVoice.setOnClickListener(onClickListener);
		mIvSuspended = (ImageView) findViewById(R.id.suspended);
		mIvSuspended.setOnClickListener(onClickListener);
		mIvFishStart = (ImageView) findViewById(R.id.fish_start);
		mLLTimeBar1 = (LinearLayout) findViewById(R.id.fish_timebar1);
		if (mLLTimeBar1 != null) {
			mLLTimeBar1.removeAllViews();
			mLLTimeBar1.addView(new TimeBarView(getApplicationContext(),
					mTimeBarWidth));
		}
		mIvTimeBar2 = (ImageView) findViewById(R.id.fish_timebar2);

		fvLL = (LinearLayout) findViewById(R.id.ll_fish_view);
		numsLL = (LinearLayout) findViewById(R.id.ll_fish_nums);
		levelLL = (LinearLayout) findViewById(R.id.ll_fish_level);
		scoreLL = (LinearLayout) findViewById(R.id.ll_fish_score);

		mClickStudyAbl = (AbsoluteLayout) findViewById(R.id.click_study_abl);
		numsClickStudyLL = (LinearLayout) findViewById(R.id.click_study_ll_fish_nums);
	}

	private SharedPreferences spf = null;

	private void getClickStudyInfo() {
		spf = getSharedPreferences("fish_file", MODE_PRIVATE);
		mIsClickStudy = spf.getBoolean("isclickstudy", false);

		if (mIsClickStudy) {
			mClickFilePath = spf.getString("filepath", null);
			mCurrentUnit = spf.getInt("unit", 0);

			getClickStudyWordInfoByUnit(mCurrentUnit);
		}
	}

	/**
	 * 获取每单元所有单词信息
	 *
	 * @param unit
	 */
	private void getClickStudyWordInfoByUnit(int unit) {
		if (mJniDds == null) {
			mJniDds = new JniDds();
		}

		boolean mInitFlag = mJniDds.JniDds_init(mClickFilePath);

		if (mInitFlag) {
			mIndexStart = mJniDds.JniDds_startIndex(mCurrentUnit);
			numsInUnit = mJniDds.JniDds_wordNum(mCurrentUnit);
			for (int j = mIndexStart - 1; j < mIndexStart + numsInUnit - 1; j++) {
				mALLWordIndexList.add(j);
			}
		}
	}

	private Animation mAnimStart = null;

	private void setFishNums() {
		LinearLayout ll = null;
		if (mIsClickStudy) {
			ll = numsClickStudyLL;
		} else {
			ll = numsLL;
		}
		ll.removeAllViews();
		ll.addView(Utils.getNumImage(Utils.mScoreResId, mFishCatchedTotalNums,
				Fish.this, false));
	}

	private void setLevelViews() {
		levelLL.removeAllViews();
		levelLL.addView(Utils.getNumImage(Utils.mScoreResId, mCurrentLevel + 1,
				Fish.this, false));
	}

	private void setScoreViews() {
		scoreLL.removeAllViews();
		scoreLL.addView(Utils.getNumImage(Utils.mScoreResId, mTotalScore,
				Fish.this, true));
	}

	private void doResultView() {
		unShowViews();
		if (mIsGameSuccess) {
			iScaleResId = R.drawable.win;
		} else {
			playSound(GameConstant.SOUND_FISHXB_308);
			iScaleResId = R.drawable.over;
		}
		mIsLevel = false;
		mIsStart = false;
		mIsTimeStart = false;
		mIsResult = true;
		initAnimBmp();
		mHandlerScale.sendEmptyMessage(MSG_BMP_ANIM);
	}

	private int iScaleResId = 0;
	Bitmap source = null;
	Bitmap bmpAnim = null;
	float scale = 0.01f;
	int animIndex = 0;

	private void initAnimBmp() {
		source = BitmapFactory.decodeResource(getResources(), iScaleResId);
	}

	private void setAnimBitmap() {
		bmpAnim = getAnimSmall(source, scale, scale);
		mIvFishStart.setImageBitmap(bmpAnim);
	}

	public static Bitmap getAnimSmall(Bitmap source, float sx, float sy) {
		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}

	protected static final int MSG_BMP_ANIM = 0;

	private boolean mIsLevel = false;
	private boolean mIsStart = false;
	private boolean mIsResult = false;

	private Handler mHandlerScale = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_BMP_ANIM:
					setAnimBitmap();
					animIndex++;
					if (animIndex < 50) {
						scale += 0.02f;
						mHandlerScale.sendEmptyMessageDelayed(MSG_BMP_ANIM, 5);
					} else {
						scale = 0.01f;
						animIndex = 0;
						mIvFishStart.setImageBitmap(null);
						mHandlerScale.removeMessages(MSG_BMP_ANIM);
						if (mIsLevel) {
							mIsLevel = false;
							mHandler.sendEmptyMessageDelayed(MSG_ANIM_LEVEL_END,
									200);
						}
						if (mIsStart) {
							mIsStart = false;
							mIvFishStart.setImageBitmap(null);
							showFishView();
							showMsg1();
						}
						if (mIsResult) {
							mIsResult = false;
							if (mCurrentLevel > 5) {
								mIsSuspended = true;
								saveScore();
								mIsAllGamePassed = true;
								mCurrentLevel = 0;
								mTotalScore = 0;
								mIvFishStart.setImageBitmap(null);
								if (mIsClickStudy) {
									mHandler.sendEmptyMessageDelayed(
											MSG_SHOW_STUDY_WORD, 200);
								} else {
									playFlash(GameConstant.SWF_END);
								}
							} else {
								if (mIsGameSuccess) {
									if (mIsClickStudy) {
										if (mCurrentLevel == 3) {
											mHandler.sendEmptyMessageDelayed(
													MSG_SHOW_STUDY_WORD, 200);
										} else {
											mHandler.sendEmptyMessageDelayed(
													MSG_ANIM_RESULT_END, 200);
										}
									} else {
										mHandler.sendEmptyMessageDelayed(
												MSG_ANIM_RESULT_END, 200);
									}
								} else {
									mHandler.sendEmptyMessageDelayed(MSG_GAME_QUIT,
											200);
								}
							}
						}
					}
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};

	private void doFishInit() {
		playSound(GameConstant.SOUND_FISHXB_296);
		if (mDebrisBmpList != null) {
			mDebrisBmpList.clear();
		}
		if (mCurrentLevel < 6) {
			iScaleResId = mLevelResIds[mCurrentLevel];
		}
		mIsLevel = true;
		mIsStart = false;
		mIsTimeStart = false;
		mIsResult = false;
		initAnimBmp();
		mHandlerScale.sendEmptyMessage(MSG_BMP_ANIM);
	}

	private void showLevel() {
		iScaleResId = R.drawable.gamestart;
		mIsLevel = false;
		mIsStart = true;
		mIsResult = false;
		initAnimBmp();
		mHandlerScale.sendEmptyMessage(MSG_BMP_ANIM);
	}

	private static final int MSG_ANIM_SHOW_RESULT = 100;
	private static final int MSG_ANIM_RESULT_END = 101;
	private static final int MSG_ANIM_LEVEL_END = 102;
	private static final int MSG_ANIM_TIME = 103;
	private static final int MSG_GAME_QUIT = 104;
	private static final int MSG_UNSHOW_DIALOG = 105;
	private static final int MSG_SHOW_CLICK_STUDY_INFO = 106;
	private static final int MSG_SHOW_CLICK_STUDY_PIC = 107;
	private static final int MSG_UNSHOW_CLICK_STUDY_PIC = 108;
	private static final int MSG_SHOW_STUDY_WORD = 109;
	private static final int MSG_RECYCLE_BMPS = 110;
	private static final int MSG_B_CREATE_FISH = 111;
	private static final int[] _DURATION = {1000, 1200, 1400, 1600, 1800, 2000};
	private static final int[] _CLICK_STUDY_DURATION = {1100, 1400, 1700,
			2000, 2300, 2600};

	/**
	 * 点击画面放下鱼钩吧
	 */
	private void showMsg1() {
		mIsShowDialog1 = true;
		// showDialogMsg();
		// playSound(GameConstant.SOUND_XB_310);
		if (mSoundPlayer != null && param != null) {
			param.setFilePath(GameConstant.SOUND_XB_310, 0, 0);
			param.setOnStatusListener(new onStatusListener() {
				@Override
				public void onStatusNotify(int status, int curTime) {
					if (status == SoundPlayer.Status.STOPPED) {
						mIsShowDialog1 = false;
					}
				}
			});
			try {
				mSoundPlayer.prepare(param);
				mSoundPlayer.play();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private boolean mIsShowDialog1 = false;
	private boolean mIsShowDialog3 = false;

	/**
	 * 太重了，猛地点击画面吧
	 */
	private void showMsg3() {
		mIsShowDialog3 = true;
		// showDialogMsg();
		// playSound(GameConstant.SOUND_XB_311);

		if (mSoundPlayer != null && param != null) {
			param.setFilePath(GameConstant.SOUND_XB_311, 0, 0);
			param.setOnStatusListener(new onStatusListener() {
				@Override
				public void onStatusNotify(int status, int curTime) {
					if (status == SoundPlayer.Status.STOPPED) {
						// mIsShowDialog3 = false;
					}
				}
			});
			try {
				mSoundPlayer.prepare(param);
				mSoundPlayer.play();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void showDialogMsg() {
		mHandler.sendEmptyMessageDelayed(MSG_UNSHOW_DIALOG, 3000);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_ANIM_SHOW_RESULT:
					doResultView();
					break;
				case MSG_ANIM_RESULT_END:
					doFishInit();
					break;
				case MSG_ANIM_LEVEL_END:
					showLevel();
					break;
				case MSG_ANIM_TIME:
					mTimeBarWidth -= 6;
					if (mLLTimeBar1 != null) {
						mLLTimeBar1.removeAllViews();
						mLLTimeBar1.addView(new TimeBarView(
								getApplicationContext(), mTimeBarWidth));
					}
					if (mTimeBarWidth > 6) {
						if (mCurrentLevel < 6) {
							if (mIsClickStudy) {
								mHandler.sendEmptyMessageDelayed(MSG_ANIM_TIME,
										_CLICK_STUDY_DURATION[mCurrentLevel]);
							} else {
								mHandler.sendEmptyMessageDelayed(MSG_ANIM_TIME,
										_DURATION[mCurrentLevel]);
							}
						}
						if (mTimeBarWidth == 63) {
							playSound(GameConstant.SOUND_XB_314);
						}
					} else {
						mTimeBarWidth = 0;
						mIsFinished = true;
						mIsGameSuccess = false;
						if (mHandler != null) {
							mHandler.removeMessages(MSG_ANIM_TIME);
							mHandler.sendEmptyMessage(MSG_ANIM_SHOW_RESULT);
						}
					}
					break;
				case MSG_GAME_QUIT:
					doDestroy();
					finish();
					break;
				case MSG_RECYCLE_BMPS:
					if (fv != null) {
						fv.recycleAllBmps();
					}
					break;
				case MSG_UNSHOW_DIALOG:
					mIsShowDialog1 = false;
					mIsShowDialog3 = false;
					break;
				case MSG_SHOW_CLICK_STUDY_INFO:
					showClickStudyInfo();
					break;
				case MSG_SHOW_CLICK_STUDY_PIC:
					showClickStudyWordPicture();
					break;
				case MSG_UNSHOW_CLICK_STUDY_PIC:
					unShowClickStudyPicture();
					break;
				case MSG_SHOW_STUDY_WORD:
					showStudyWord();
					break;
				case MSG_B_CREATE_FISH:
					bCreateNewFish = false;
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};

	public static final String STUDY_FILE_PATH = "study_file_path";
	public static final String STUDY_WORD_LIST = "study_word_list";
	public static final String STUDY_SUBJECT_ID = "study_subject_id";
	private int mSubjectId = 0;// 0为中文，1为英文
	private boolean mIsShowStudyWord = false;

	private void showStudyWord() {
		mIsShowStudyWord = true;
		if (mWordIndexList != null) {
			mWordIndexList.clear();
		}
		for (Iterator<Integer> it = mWordIndexSet.iterator(); it.hasNext(); ) {
			mWordIndexList.add(it.next());
		}
		Intent intent = new Intent();
		intent.setClass(Fish.this, StudyWord.class)
				.putExtra("mIsAllGamePassed", mIsAllGamePassed)
				.putExtra(STUDY_SUBJECT_ID, mSubjectId)
				.putExtra(STUDY_FILE_PATH, mClickFilePath)
				.putIntegerArrayListExtra(STUDY_WORD_LIST, mWordIndexList);
		startActivityForResult(intent, 0);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			// 重新开始
			if (resultCode == 1) {

			}
			// 取消
			else if (resultCode == 2) {
				finish();
			}
		}
	}

	private byte[] bHead, bItem1, bItem2, mClickStudySound,
			mClickStudyPicture = null;

	private String mStrHead, mStrItem1, mStrItem2 = null;

	private void showClickStudyInfo() {
		WordItem mItem = mJniDds.JniDds_wordInfo(mClickStudyIndex);
		byte[] mp3Name = null;
		try {
			bHead = mItem.wordHead;
			if (bHead != null && bHead.length > 0) {
				mStrHead = new String(bHead, "GBK");
				if (String.valueOf(new String(bHead, "GBK").charAt(0)).matches(
						"[A-Za-z]")) {
					mSubjectId = 1;
					// english
					mStrItem1 = mStrHead;
					bItem2 = mItem.wordExplain;

					if (bItem2 != null && bItem2.length > 0) {
						mStrItem2 = new String(bItem2, "GBK");
					}
				} else {
					mSubjectId = 0;
					// chinese
					bItem1 = mItem.wordPhonetic;
					if (bItem1 != null && bItem1.length > 0) {
						mStrItem1 = new String(bItem1, "GBK");
					}
					mStrItem2 = mStrHead;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		bShowClickStudyWord = true;

		mp3Name = mItem.wordSoundName;
		mClickStudySound = mJniDds.JniDds_wordSound();
		mClickStudyPicture = mJniDds.JniDds_wordPic();
		try {
			playWordSound(mClickStudySound);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void playWordSound(byte[] sound) throws Exception {
		// 播放单词发音
		if (null == mSoundPlayer) {
			mSoundPlayer = new SoundPlayer();
		}
		if (null != mSoundPlayer) {
			param.setBuffer(sound, 0, sound.length);
			param.setOnStatusListener(new onStatusListener() {
				public void onStatusNotify(int status, int curTime) {
					if (status == SoundPlayer.Status.STOPPED) {
						mHandler.sendEmptyMessage(MSG_SHOW_CLICK_STUDY_PIC);
					}
				}
			});
			mSoundPlayer.stop();
			if (mSoundPlayer.prepare(param)) {
				mSoundPlayer.play();
			}
		}
	}

	private Bitmap bmpClickStudy = null;
	private int clickBmpWidth = 0;
	private int clickBmpHeight = 0;
	float w = 0;

	private void showClickStudyWordPicture() {
		bShowClickStudyWord = false;
		if (mClickStudyPicture.length > 1) {
			bmpClickStudy = BitmapFactory.decodeByteArray(mClickStudyPicture,
					0, mClickStudyPicture.length);
			clickBmpWidth = bmpClickStudy.getWidth();
			clickBmpHeight = bmpClickStudy.getHeight();
			int height = 0;
			if (clickBmpHeight > CLICK_BMP_H) {
				float h = CLICK_BMP_H;
				w = clickBmpWidth * CLICK_BMP_H / clickBmpHeight;
				Bitmap outBmp = Bitmap.createScaledBitmap(bmpClickStudy, (int) w, (int) h, true);
				bmpClickStudy = outBmp;
			} else {
				height = clickBmpHeight;
				bmpClickStudy = Bitmap.createBitmap(bmpClickStudy, 0, 0,
						clickBmpWidth, height);
			}
			bShowClickStudyPic = true;
		}
		mHandler.sendEmptyMessageDelayed(MSG_UNSHOW_CLICK_STUDY_PIC, 2000);
	}

	private boolean bShowClickStudyPic = false;
	private boolean bShowClickStudyWord = false;

	private GifView mGifView = null;

	void setGifData(byte[] mPicture) {
		if (mGifView == null) {
			mGifView = new GifView(this);
		}
		mGifView.setGifData(mPicture);
	}

	private void unShowClickStudyPicture() {
		bShowClickStudyPic = false;
		fv.showNextCatchFish();
	}

	private void showFishView() {
		mIsTimeStart = true;
		mFishCatchedTotalNums = 0;
		showViews();
		nextLevelPlay();
	}

	private void unShowViews() {
		stopMsgs();
		if (mIsClickStudy) {
			mClickStudyAbl.setVisibility(View.GONE);
		} else {
			mIvResource.setVisibility(View.GONE);
			numsLL.setVisibility(View.GONE);
		}
		mIvSuspended.setVisibility(View.GONE);
		mIvVoice.setVisibility(View.GONE);
		levelLL.setVisibility(View.GONE);
		mLLTimeBar1.setVisibility(View.GONE);
		mIvTimeBar2.setVisibility(View.GONE);
		scoreLL.setVisibility(View.GONE);
	}

	private void nextLevelPlay() {
		mOtherObjectNums = 0;
		mFishCatchedTotalNums = 0;
		mTimeBarWidth = 327;
		mIsFinished = false;
		if (mIsClickStudy) {
			if (mCurrentLevel == 0 || mCurrentLevel == 3) {
				if (mWordIndexSet != null) {
					mWordIndexSet.clear();
				}
			}
		}

		resumeGame();
	}

	private void stopMsgs() {
		if (mHandler != null) {
			mHandler.removeMessages(MSG_ANIM_TIME);
			mHandler.removeMessages(MSG_ANIM_SHOW_RESULT);
			mHandler.removeMessages(MSG_ANIM_RESULT_END);
			mHandler.removeMessages(MSG_ANIM_LEVEL_END);
			mHandler.removeMessages(MSG_GAME_QUIT);
			mHandler.removeMessages(MSG_UNSHOW_DIALOG);
			mHandler.removeMessages(MSG_SHOW_CLICK_STUDY_INFO);
			mHandler.removeMessages(MSG_SHOW_CLICK_STUDY_PIC);
			mHandler.removeMessages(MSG_UNSHOW_CLICK_STUDY_PIC);
			mHandler.removeMessages(MSG_SHOW_STUDY_WORD);
		}
	}

	private void showViews() {
		canHookTouchDown = true;
		setFishNums();
		setLevelViews();
		setScoreViews();
		if (mIsClickStudy) {
			mClickStudyAbl.setVisibility(View.VISIBLE);
		} else {
			mIvResource.setVisibility(View.VISIBLE);
			numsLL.setVisibility(View.VISIBLE);
		}
		mIvSuspended.setVisibility(View.VISIBLE);
		mIvVoice.setVisibility(View.VISIBLE);
		levelLL.setVisibility(View.VISIBLE);
		mLLTimeBar1.setVisibility(View.VISIBLE);
		mIvTimeBar2.setVisibility(View.VISIBLE);
		scoreLL.setVisibility(View.VISIBLE);
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.voice:
					changeVoice();
					break;
				case R.id.suspended:
					changeState();
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 是否有背景声音
	 */
	private boolean mIsVoiceOn = true;

	private boolean mIsSuspended = false;

	/**
	 * 改变游戏状态
	 */
	private void changeState() {
		if (!mIsSuspended) {
			stopBackGround();
			mIvSuspended.setBackgroundResource(R.drawable.suspended2);
			mIsPause = true;
			mIsSuspended = true;
			pauseGame();
			canHookTouchDown = false;
			mIvVoice.setEnabled(false);
			mIvVoice.setBackgroundResource(R.drawable.voice3);
		} else {
			mIvSuspended.setBackgroundResource(R.drawable.suspended1);
			mIsSuspended = false;
			mIsPause = false;
			bCreateNewFish = true;
			resumeGame();
			mHandler.sendEmptyMessageDelayed(MSG_B_CREATE_FISH, 3000);
			canHookTouchDown = true;
			if (mIsVoiceOn) {
				mIvVoice.setBackgroundResource(R.drawable.voice1);
				playBackGround();
			} else {
				mIvVoice.setBackgroundResource(R.drawable.voice2);
				stopBackGround();
			}
			mIvVoice.setEnabled(true);
		}
	}

	private boolean mIsTimeStart = false;

	private boolean mIsPause = false;

	/**
	 * 暂停游戏
	 */
	private void pauseGame() {
		if (mSoundPlayer != null) {
			try {
				mSoundPlayer.pause();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (mIsLevel || mIsStart || mIsResult) {
			if (mHandlerScale != null) {
				mHandlerScale.removeMessages(MSG_BMP_ANIM);
			}
		}

		stopMsgs();
		if (fv != null) {
			fv.setGamePause();
		}
	}

	private void changeVoice() {
		if (mIsVoiceOn) {
			mIvVoice.setBackgroundResource(R.drawable.voice2);
			stopBackGround();
			mIsVoiceOn = false;
		} else {
			mIvVoice.setBackgroundResource(R.drawable.voice1);
			mIsVoiceOn = true;
			playBackGround();
		}
	}

	/**
	 * 恢复游戏
	 */
	private void resumeGame() {
		if (mSoundPlayer != null) {
			try {
				mSoundPlayer.resume();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (mIsLevel || mIsStart || mIsResult) {
			if (mHandlerScale != null) {
				mHandlerScale.sendEmptyMessageDelayed(MSG_BMP_ANIM, 5);
			}
		}

		if (mHandler != null) {
			if (mIsTimeStart) {
				if (mIsClickStudy) {
					mHandler.sendEmptyMessageDelayed(MSG_ANIM_TIME,
							_CLICK_STUDY_DURATION[mCurrentLevel]);
				} else {
					mHandler.sendEmptyMessageDelayed(MSG_ANIM_TIME,
							_DURATION[mCurrentLevel]);
				}
			}
		}
		if (fv != null) {
			fv.setGameResume();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showQuitDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private String getStr(int resId) {
		return getResources().getString(resId);
	}

	/**
	 * 播放flash课件
	 */
	private void playFlash(String filePath) {
		Bundle bundle = new Bundle();
		bundle.putString("path", filePath);
		bundle.putLong("offset", 0l);
		bundle.putLong("size", -1l);
		bundle.putByteArray("keyBuffer", null);
		Intent intent = new Intent("android.intent.action.FlashPlayerLandscape");
		intent.putExtras(bundle);
		/*intent.setClassName("com.noahedu.flash", "com.amuse.player.FlashPlayer");*/
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void redo() {
		mCurrentLevel = 0;
		mIsFinished = false;
		playBackGround();
		doFishInit();
	}

	/**
	 * 是否重新开始游戏
	 */
	private void showRedoDialog() {
		Dialog dialog = new AlertDialog.Builder(this)
				.setTitle(getStr(R.string.remind))
				.setMessage(getStr(R.string.redo))
				.setPositiveButton(getStr(R.string.sure),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								dialog.dismiss();
								redo();
							}
						})
				.setNegativeButton(getStr(R.string.cancel),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								dialog.dismiss();
								finish();

							}
						}).create();
		dialog.show();
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				// finish();
			}
		});
	}

	private void showQuitDialog() {
		if (!mIsTimeStart) {
			return;
		}

		mIsSuspended = true;


		stopBackGround();
		pauseGame();
		Dialog dialog = new AlertDialog.Builder(this)
				.setTitle(getStr(R.string.remind))
				.setMessage(getStr(R.string.quit))
				.setPositiveButton(getStr(R.string.sure),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								dialog.dismiss();
								bQuit = true;
								mHandler.sendEmptyMessage(MSG_GAME_QUIT);
							}
						})
				.setNegativeButton(getStr(R.string.cancel),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								dialog.dismiss();
								// cancel();
							}
						}).create();
		dialog.show();
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				cancel();
			}
		});
	}

	private void cancel() {
		if (!bQuit) {
			if (!mIsPause) {
				changeState();
				bCreateNewFish = true;
				mHandler.sendEmptyMessageDelayed(MSG_B_CREATE_FISH, 3000);
			}
		}
	}

	/**
	 * 保存分数记录
	 */
	private void saveScore() {
		if (db != null) {
			db.insertScore(mTotalScore);
		}
	}

	@Override
	protected void onPause() {
		isResumed = true;
		pauseGame();
		stopBackGround();
		super.onPause();
	}

	private boolean isResumed = false;

	private boolean bQuit = false;

	@Override
	protected void onResume() {
		if (!bQuit) {
			if (!mIsSuspended) {
				if (mIsVoiceOn) {
					playBackGround();
				}
			}

			if (isResumed) {
				if (!mIsSuspended) {
					bCreateNewFish = true;
					resumeGame();
					mHandler.sendEmptyMessageDelayed(MSG_B_CREATE_FISH, 3000);
					if (bShowClickStudyPic) {
						mHandler.sendEmptyMessageDelayed(MSG_UNSHOW_CLICK_STUDY_PIC, 100);
					}
				}
			}

			if (mIsAllGamePassed) {
				if (mIsClickStudy) {
					redo();
				} else {
					showRedoDialog();
				}
				mIsAllGamePassed = false;
			} else {
				if (mIsClickStudy) {
					if (mIsShowStudyWord) {
						mHandler.sendEmptyMessage(MSG_ANIM_RESULT_END);
						mIsShowStudyWord = false;
					}
				}
			}

			isResumed = false;
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	private void doDestroy() {
		if (!mIsAllGamePassed) {
			saveScore();
		}
		if (db != null) {
			db.close();
			db = null;
		}
		if (mSoundPlayer != null) {
			try {
				mSoundPlayer.recycle();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (mHandler != null) {
			mHandler.sendEmptyMessageDelayed(MSG_RECYCLE_BMPS, 1000);
		}
	}

	/**
	 * 判断一个Service是否在运行
	 *
	 * @param ctx
	 * @param className Service完整的类名
	 * @return
	 */
	public static boolean isServiceRunning(Context ctx, String className) {
		boolean isrunning = false;
		ActivityManager mActivityManager = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> serviceInfos = (ArrayList<RunningServiceInfo>) mActivityManager
				.getRunningServices(30);
		if (!(serviceInfos.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceInfos.size(); i++) {
			if (serviceInfos.get(i).service.getClassName().toString()
					.equals(className)) {
				isrunning = true;
				break;
			}
		}
		return isrunning;
	}

	/**
	 * 启动背景声音Service
	 */
	private void playBackGround() {
		if (isServiceRunning(this, "com.noahedu.fish.BGService")) {
			stopService(new Intent("com.noahedu.fish.BGService"));
		}
		BGService.setResFilePath(GameConstant.SOUND_yx_80);
		startService(new Intent("com.noahedu.fish.BGService"));
	}

	/**
	 * 停止背景声音Service
	 */
	private void stopBackGround() {
		stopService(new Intent("com.noahedu.fish.BGService"));
	}

	private void playSound(String filePath) {
		PlaySound.playSound(mSoundPlayer, filePath);
	}



	/**
	 * 钓鱼实现类
	 */
	private class FishView extends SurfaceView implements
			SurfaceHolder.Callback {
		SurfaceHolder holder;
		Canvas canvas;
		Paint paint;
		/**
		 * 背景
		 */
		Bitmap bmpBack;

		/**
		 * 鱼钩
		 */
		Bitmap bmpHook;

		/**
		 * 小船
		 */
		Bitmap bmpBoat;

		/**
		 * 错误的结果
		 */
		Bitmap bmpWrong;

		/**
		 * 信息提示对话框
		 */
		Bitmap bmpDialog1, bmpDialog1_reverse, bmpDialog3, bmpDialog3_reverse;

		/**
		 * 显示字或单词的图片等的背景图
		 */
		Bitmap bmpResource;

		/**
		 * 控制所有物体移动的线程
		 */
		boolean bFlag = false;
		/**
		 * 控制鱼钩下探的线程
		 */
		boolean bTouchFlag = false;
		/**
		 * 是否下沉鱼钩
		 */
		boolean isTouchDown = false;
		/**
		 * 所有的鱼
		 */
		List<FishMgr> mFishList = null;
		/**
		 * 所有的鱼的坐标
		 */
		List<PointXY> mXY = null;
		/**
		 * 被钓起来的鱼
		 */
		List<FishMgr> mFishCatchedList = null;

		/**
		 * 被钓起来的鱼的坐标
		 */
		List<PointXY> mXYCatched = null;
		Random r = new Random();

		/**
		 * 小船
		 */
		private ObjectMgr mBoatFront = null;
		/**
		 * 波浪（前）
		 */
		private ObjectMgr mWaveA = null;
		/**
		 * 波浪（后）
		 */
		private ObjectMgr mWaveB = null;
		/**
		 * 小朵
		 */
		private ObjectMgr mDuo = null;
		/**
		 * 小船的初始横坐标
		 */
		private final int mBoatInitX = Utils.pxToDp(544 - 128);

		private int mDel = 128;
		/**
		 * 小船的初始纵坐标
		 */
		private final int mBoatInitY = Utils.pxToDp(147);
		/**
		 * 小船的rect
		 */
		private Rect mBoatRect;
		/**
		 * 小船的点击点
		 */
		private Point point = new Point();
		/**
		 * 判断是否点击在小船上，否则拖动无效
		 */
		private boolean canDrag = false;
		/**
		 * 点击点离小船左上角的水平距离
		 */
		private int offsetX = 0;

		private Context context;

		public FishView(Context context) {
			super(context);
			this.context = context;
			init(context);
		}

		public FishView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			this.context = context;
			init(context);
		}

		public FishView(Context context, AttributeSet attrs) {
			super(context, attrs);
			this.context = context;
			init(context);
		}

		private Bitmap getImage(int imageResId) {
			return BitmapFactory.decodeResource(getResources(), imageResId);
		}

		private void init(Context context) {
			holder = this.getHolder();
			holder.addCallback(this);
			this.setFocusable(true);
			bmpBack = getImage(R.drawable.bg);
			bmpHook = getImage(R.drawable.hook);
			bmpBoat = getImage(R.drawable.boat_front_1);
			bmpWrong = getImage(R.drawable.wrong);
			bmpDialog1 = getImage(R.drawable.dialog1);
			bmpDialog3 = getImage(R.drawable.dialog3);
			bmpDialog1_reverse = getImage(R.drawable.dialog1_reverse);
			bmpDialog3_reverse = getImage(R.drawable.dialog3_reverse);

			bmpResource = getImage(R.drawable.resource);

			mBoatFront = new ObjectMgr(context, 0);
			mWaveA = new ObjectMgr(context, 1);
			mWaveB = new ObjectMgr(context, 2);
			mDuo = new ObjectMgr(context, 3);

			mFishList = new ArrayList<FishMgr>();
			mXY = new ArrayList<PointXY>();
			mFishCatchedList = new ArrayList<FishMgr>();
			mXYCatched = new ArrayList<PointXY>();

			mBoatRect = new Rect(mBoatInitX, mBoatInitY, mBoatInitX
					+ bmpBoat.getWidth(), mBoatInitY + bmpBoat.getHeight());

			if (null == mSoundPool) {
				initSoundPool();
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
			bFlag = true;
			SCREEN_WIDTH = this.getWidth();
			draw();
		}

		public void startGame() {
			if (mHandlerFish != null) {
				mHandlerFish.sendEmptyMessage(MSG_FISH);
			}

			new Thread(new FishThread()).start();
		}

		/**
		 * 游戏暂停
		 */
		public void setGamePause() {
			doRecycle();
		}

		boolean mIsAnimFirst = false;

		/**
		 * 游戏开始或恢复
		 */
		public void setGameResume() {
			bFlag = true;
			bTouchFlag = true;
			mIsDisplayBackground = false;
			startGame();
			if (mIsAnimFirst) {
				startAnims();
			}
			mIsAnimFirst = true;
		}

		public void initFishs() {
			recycleAllFishs();
			if (mFishList != null) {
				mFishList.clear();
			}
			if (mXY != null) {
				mXY.clear();
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
								   int height) {
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			doRecycle();
			// recycleAllBmps();
		}

		private void doRecycle() {
			bFlag = false;
			bTouchFlag = false;
			removeMsgs();
			stopAnim();
		}

		private void removeMsgs() {
			if (mHandlerFish != null) {
				mHandlerFish.removeMessages(MSG_FISH);
				mHandlerFish.removeMessages(MSG_HOOK);
				mHandlerFish.removeMessages(MSG_FRESH_NUM);
				mHandlerFish.removeMessages(MSG_RIGHT);
				mHandlerFish.removeMessages(MSG_WRONG);
				mHandlerFish.removeMessages(MSG_GAME_OVER);
				mHandlerFish.removeMessages(MSG_GAME_PASS);
				mHandlerFish.removeMessages(MSG_DRAW);
				mHandlerFish.removeMessages(MSG_RECYCLE);
				mHandlerFish.removeMessages(MSG_HOOK_UP);
				mHandlerFish.removeMessages(MSG_BOAT_MOVE);
				stopMsgs();
			}
		}

		/**
		 * 回收Bitmap资源（surfaceDestroyed里面）；
		 */
		public void recycleAllBmps() {
			Tools.safeRecycle(bmpBack);
			Tools.safeRecycle(bmpHook);
			Tools.safeRecycle(bmpBoat);
			Tools.safeRecycle(bmpWrong);
			Tools.safeRecycle(bmpDialog1);
			Tools.safeRecycle(bmpDialog3);
			Tools.safeRecycle(bmpDialog1_reverse);
			Tools.safeRecycle(bmpDialog3_reverse);
			Tools.safeRecycle(bmpResource);
			mBoatFront.recycleObject();
			mBoatFront.recycleObject();
			mWaveA.recycleObject();
			mWaveB.recycleObject();
			mDuo.recycleObject();
			recycleAllFishs();
			recycleAllCatchedFish();

			Log.d("Fish", "!!!!!!!!!!!!!!!!!!!!!!!!!!!GC");

			System.gc();
		}

		private void recycleAllFishs() {
			FishMgr fishRecycle = null;
			if ((mFishList != null) && (mFishList.size() > 0)) {
				for (int i = 0; i < mFishList.size(); i++) {
					fishRecycle = mFishList.get(i);
					fishRecycle.recycleFish();
				}
			}
		}

		private void recycleAllCatchedFish() {
			FishMgr fishRecycle = null;
			if ((mFishCatchedList != null) && (mFishCatchedList.size() > 0)) {
				for (int j = 0; j < mFishCatchedList.size(); j++) {
					fishRecycle = mFishCatchedList.get(j);
					fishRecycle.recycleFish();
				}
			}
		}

		private void stopAnim() {
			stopFishsAnim();
			stopObjectsAnim();
		}

		private void stopFishsAnim() {
			if (mFishList != null && mFishList.size() > 0) {
				for (int i = 0; i < mFishList.size(); i++) {
					mFishList.get(i).stopFishAnim();
				}
			}

			if (mFishCatchedList != null && mFishCatchedList.size() > 0) {
				for (int i = 0; i < mFishCatchedList.size(); i++) {
					mFishCatchedList.get(i).stopFishAnim();
				}
			}
		}

		private void stopObjectsAnim() {
			mBoatFront.stopAnim();
			mWaveA.stopAnim();
			mWaveB.stopAnim();
			mDuo.stopAnim();
		}

		private void startAnims() {
			startFishsAnim();
			startObjectsAnim();
		}

		private void startFishsAnim() {
			for (int i = 0; i < mFishList.size(); i++) {
				mFishList.get(i).startFishAnim();
			}
		}

		private void startObjectsAnim() {
			mBoatFront.startAnim();
			mWaveA.startAnim();
			mWaveB.startAnim();
			mDuo.startAnim();
		}

		/**
		 * 小船是否因为超重沉没
		 */
		private boolean mIsBoatSink = false;

		/**
		 * 小船沉没时钓到的杂物的数量
		 */
		private static final int BOAT_SINK_NUM = 3;

		/**
		 * 小船沉没时翻转角度
		 */
		private int degree = 0;

		private static final int MSG_FISH = 1;
		private static final int MSG_HOOK = 2;
		private static final int MSG_FRESH_NUM = 3;
		private static final int MSG_RIGHT = 4;
		private static final int MSG_WRONG = 5;
		private static final int MSG_GAME_OVER = 6;
		private static final int MSG_GAME_PASS = 7;
		private static final int MSG_DRAW = 8;
		private static final int MSG_RECYCLE = 9;
		private static final int MSG_HOOK_UP = 10;
		private static final int MSG_BOAT_MOVE = 11;

		private Handler mHandlerFish = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case MSG_FISH:
						createFishs();
						break;
					case MSG_HOOK:
						isTouchDown = true;
						bTouchFlag = true;
						doTouchDown();
						break;
					case MSG_FRESH_NUM:
						setFishNums();
						setScoreViews();
						break;
					case MSG_RIGHT:
						doRight();
						break;
					case MSG_WRONG:
						doWrong();
						break;
					case MSG_GAME_OVER:
						doGameOver();
						break;
					case MSG_GAME_PASS:
						doGamePass();
						break;
					case MSG_DRAW:
						draw();
						break;
					case MSG_RECYCLE:
						doRecycle();
						initFishs();
						break;
					case MSG_HOOK_UP:
						isTouchDown = false;
						canDrag = false;
						break;
					case MSG_BOAT_MOVE:
						doBoatMove(mBoatMoveX);
						break;
					default:
						break;
				}
				super.handleMessage(msg);
			}
		};

		/**
		 * 小船沉没
		 */
		private void doBoatSink() {
			mIsBoatSink = true;
		}

		private void cannotSetHook() {
			canHookTouchDown = false;
		}

		private void canSetHook() {
			canHookTouchDown = true;
		}

		/**
		 * 钓起来的鱼或杂物是否超重
		 */
		private boolean mIsOverWeight = false;

		/**
		 * 钓起来的物品超重时
		 */
		private void doOverWeight() {
			if (mIsOverWeight) {
				if (mHookDeep > 0) {
					mHookDeep -= HOOK_MOVE_V;
				}
			}
		}

		private void createFishs() {
			if (!bCreateNewFish) {
				FishMgr fishs = null;
				if (mIsClickStudy) {
					WordItem mItem = new WordItem();
					String mWord = null;
					int mIndex = mALLWordIndexList.get(r.nextInt(numsInUnit));
					mItem = mJniDds.JniDds_wordInfo(mIndex);
					try {
						mWord = new String(mItem.wordHead, "GBK");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					/* 第一，四关只有鱼，没有杂物 */
					if ((mCurrentLevel == 0) || (mCurrentLevel == 3)) {
						fishs = new FishMgr(getContext(), mCurrentLevel,
								r.nextInt(7), true, mIndex, mWord);
					}
					/* 其他关鱼和杂物都有 1:2 */
					else {
						// 生成杂物
						if (r.nextInt(3) == 0) {
							fishs = new FishMgr(getContext(), mCurrentLevel,
									7 + r.nextInt(7), true, mIndex, mWord);
						}
						// 生成鱼
						else {
							fishs = new FishMgr(getContext(), mCurrentLevel,
									r.nextInt(7), true, mIndex, mWord);
						}
					}
				} else {
					/* 第一，四关只有鱼，没有杂物 */
					if ((mCurrentLevel == 0) || (mCurrentLevel == 3)) {
						fishs = new FishMgr(getContext(), mCurrentLevel,
								r.nextInt(7), false, 0, "");
					}
					/* 其他关鱼和杂物都有 1:2 */
					else {
						// 生成杂物
						if (r.nextInt(3) == 0) {
							fishs = new FishMgr(getContext(), mCurrentLevel,
									7 + r.nextInt(7), false, 0, "");
						}
						// 生成鱼
						else {
							fishs = new FishMgr(getContext(), mCurrentLevel,
									r.nextInt(7), false, 0, "");
						}
					}
				}

				mFishList.add(fishs);
				mXY.add(fishs.initPosition());
			}

			if (mHandlerFish != null) {
				mHandlerFish.sendEmptyMessageDelayed(MSG_FISH, 2500);
			}
		}

		private void addWordIndex() {
			if (mWordIndexSet != null) {
				mWordIndexSet.add(mClickStudyIndex);
			}
		}

		private int mHookDelDeep = 0;

		private void doRight() {
			cannotSetHook();
			playSound(GameConstant.SOUND_RIGHT[r.nextInt(3)]);
			mDuo.setOnAnimEndListener(new OnAnimEndListener() {
				@Override
				public void OnEndListener() {
					mIsShowResult = false;
					mDuo.showDuoNormal();
					mHookDelDeep = 0;
					doNextCatchFishView();
				}
			});
		}

		private void doNextCatchFishView() {
			if (mIsClickStudy) {
				mHandler.sendEmptyMessageDelayed(MSG_SHOW_CLICK_STUDY_INFO,
						1000);
			} else {
				showNextCatchFish();
			}
		}

		private void showNextCatchFish() {
			if ((mFishCatchedList != null) && (mFishCatchedList.size() > 0)) {
				doFishCatchView();
			} else {
				initCatchFish();
				judgeGamePass();
			}
		}

		private boolean mIsShowResult = false;

		private void doWrong() {
			cannotSetHook();
			if (mOtherObjectNums > 3) {
				mOtherObjectNums = 3;
			}
			playSound(GameConstant.SOUND_WRONG[mOtherObjectNums - 1]);
			mIsShowResult = true;
			mDuo.showDuoLost();
			mDuo.setOnAnimEndListener(new OnAnimEndListener() {
				@Override
				public void OnEndListener() {
					mIsShowResult = false;
					mDuo.showDuoNormal();
					if ((mFishCatchedList != null)
							&& (mFishCatchedList.size() > 0)) {
						doFishCatchView();
					} else {
						initCatchFish();
						// 钓到的杂物超过3个，则船翻掉，游戏失败
						if (mOtherObjectNums >= BOAT_SINK_NUM) {
							doBoatSink();
						} else {
							judgeGamePass();
						}
					}
				}
			});
		}

		private void stopTime() {
			if (mHandler != null) {
				mHandler.removeMessages(MSG_ANIM_TIME);
			}
		}

		private int mCatchNum = 0;

		private int mCatchFishNum = 0;
		private int mCatchDebris = 0;

		/**
		 * 判断过关或沉船时，时间停止
		 */
		private void judge() {
			mCatchFishNum = 0;
			mCatchDebris = 0;
			for (int i = 0; i < mFishCatchedList.size(); i++) {
				FishMgr fish = mFishCatchedList.get(i);
				if (fish.isFish()) {
					mCatchFishNum++;
				} else {
					mCatchDebris++;
				}
			}
			if ((mCatchFishNum + mFishCatchedTotalNums) >= (mCurrentLevel + 2)) {
				stopTime();
			}
			if ((mCatchDebris + mOtherObjectNums) >= BOAT_SINK_NUM) {
				stopTime();
			}
		}

		private void doFishCatchView() {
			judge();
			if (mFishCatchedList != null) {
				mCatchNum = mFishCatchedList.size();
			}
			if (mCatchNum > 0) {
				FishMgr fish = null;
				fish = mFishCatchedList.get(0);
				if (fish != null) {
					fish.stopFishAnim();
				}
				// 钓起来的是鱼
				if ((fish != null) && (fish.isFish())) {
					mFishCatchedTotalNums++;
					if ((mCurrentLevel <= 5)
							&& (mFishCatchedTotalNums >= mCurrentLevel + 2)) {
						canHookTouchDown = false;
					}
					mTotalScore += fish.getFishScores();
					mHookDelDeep = 15;
					mDuo.showDuoHappy();
					mHandlerFish.sendEmptyMessage(MSG_RIGHT);
					mHandlerFish.sendEmptyMessage(MSG_FRESH_NUM);
					if (mIsClickStudy) {
						mClickStudyIndex = fish.getWordIndex();
						addWordIndex();
					}
					playSoundPool(3);
				}
				// 钓起来的是杂物
				else {
					mOtherObjectNums++;
					if (mOtherObjectNums >= BOAT_SINK_NUM) {
						canHookTouchDown = false;
					}
					if (mDebrisBmpList != null) {
						mDebrisBmpList.add(fish.getBitmap());
					}
					mHandlerFish.sendEmptyMessage(MSG_WRONG);
					playSoundPool(4);
				}
				if ((mFishCatchedList != null) && (mFishCatchedList.size() > 0)) {
					FishMgr mgr = mFishCatchedList.get(0);
					mFishCatchedList.remove(0);
					mgr.recycleFish();
				}
			}
		}

		private void judgeGamePass() {
			mHandlerFish.sendEmptyMessageDelayed(MSG_GAME_PASS, 100);
		}

		private void initCatchFish() {
			mCatchNum = 0;
			if (mFishCatchedList != null) {
				mFishCatchedList.clear();
			}
		}

		/**
		 * 游戏过关
		 */
		public void doGamePass() {
			if ((mCurrentLevel <= 5)
					&& (mFishCatchedTotalNums >= mCurrentLevel + 2)) {
				mCurrentLevel++;
				doGameSuccess();
			} else {
				canSetHook();
			}
		}

		/**
		 * 闯关成功
		 */
		private void doGameSuccess() {
			mIsFinished = true;
			mIsGameSuccess = true;
			if (mLLTimeBar1 != null) {
				mLLTimeBar1.removeAllViews();
				mLLTimeBar1.addView(new TimeBarView(getContext(), 327));
			}
			mHandlerFish.sendEmptyMessageDelayed(MSG_RECYCLE, 200);
			mHandler.sendEmptyMessage(MSG_ANIM_SHOW_RESULT);
		}

		/**
		 * 闯关失败
		 */
		private void doGameOver() {
			mIsFinished = true;
			mIsGameSuccess = false;
			mHandlerFish.sendEmptyMessageDelayed(MSG_RECYCLE, 200);
			mHandler.sendEmptyMessage(MSG_ANIM_SHOW_RESULT);
		}

		private int mTotalWeight = 0;

		private FishMgr mFishMgrOutOfScreen = null;

		/**
		 * 钓鱼线程
		 */
		class FishThread implements Runnable {
			@Override
			public void run() {
				while (bFlag && !mIsFinished) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					/********************** 小船的移动 ****************************/
					switch (mCurrentLevel) {
						// 1～2关：小船静止不动
						case 0:
						case 1:
							mBoatDelX = 0;
							break;
						// 3～4关：小船自动左右移动
						case 2:
						case 3:
							if (mToRight) {
								mBoatDelX += BOAT_MOVE_V;
								if (mBoatDelX >= mBoatDelRight) {
									mToRight = false;
								}
							} else {
								mBoatDelX -= BOAT_MOVE_V;
								if (mBoatDelX < mBoatDelLeft) {
									mToRight = true;
								}
							}
							break;
						// 5～6：小船手动移动
						case 4:
						case 5:
							mBoatDelX = 0;
							break;
					}
					/********************** 放下鱼钩 ****************************/
					if (bTouchFlag) {
						if (isTouchDown) {
							if (mHookDeep >= mHookDeepMaxY) {
								mHookDeep = mHookDeepMaxY;
							} else {
								mHookDeep += HOOK_MOVE_V;
							}
						} else {
							if (mHookDeep <= 0) {
								mTotalWeight = 0;
								mHookDeep = mHookDeepMinY;
								bTouchFlag = false;
								mIsOverWeight = false;
								mIsShowDialog3 = false;
								doFishCatchView();
							} else {
								if (!mIsOverWeight) {
									mHookDeep -= HOOK_MOVE_V;
								}
							}
						}
					}

					int mHookCurrentX = 0;
					int mHookCurrentY = 0;
					if (mCurrentLevel < 4) {
						mHookCurrentX = mHookX + mBoatDelX;
					} else {
						mHookCurrentX = mBoatRect.left - Utils.pxToDp(130);
					}
					mHookCurrentY = mHookY + mHookDeep;

					/********************** 鱼和杂物游动 ****************************/
					//只能逆向遍历，因为中途会删掉一些item，破坏了链表
					for (int i = mXY.size() - 1; i >= 0; i--) {
						if ((mFishList != null) && (mFishList.size() > 0)) {
							boolean isRemove = false;
							// 从左向右
							if (mFishList.get(i).getFishDirection()) {
								if ((mXY.get(i).getX()) > SCREEN_WIDTH) {
									mFishMgrOutOfScreen = mFishList.get(i);
									mFishList.remove(i);
									mXY.remove(i);
									mFishMgrOutOfScreen.recycleFish();
									isRemove = true;
								} else {
									mXY.get(i).setX(
											mXY.get(i).getX()
													+ mFishList.get(i)
													.getFishSpeed());
								}
							}
							// 从右向左
							else {
								if ((mXY.get(i).getX()) < -SCREEN_WIDTH) {
									mFishMgrOutOfScreen = mFishList.get(i);
									mFishList.remove(i);
									mXY.remove(i);
									mFishMgrOutOfScreen.recycleFish();
									isRemove = true;
								} else {
									mXY.get(i).setX(
											mXY.get(i).getX()
													+ (-1)
													* mFishList.get(i)
													.getFishSpeed());
								}
							}
							/********************** 被钓起来的鱼和杂物 ****************************/
							if (isRemove == true) {
								continue;
							}
							RectData rd = mFishList.get(i).rd;
							if (!mIsOverWeight) {
								// 判断是否有鱼或杂物被钓起来
								if (Tools.isCollsionWithRect(mXY.get(i).getX()
												+ rd.getX(), (mHookY + mXY.get(i)
												.getY()) + rd.getY(), rd.getWidth(),
										rd.getHeight(), mHookCurrentX,
										mHookCurrentY, mHookW, mHookH)) {
									playSoundPool(2);
									FishMgr fish = mFishList.get(i);
									mFishCatchedList.add(fish);
									for (int j = 0; j < mFishCatchedList.size(); j++) {
										mTotalWeight += mFishCatchedList.get(j)
												.getFishWeight();
									}
									if (mTotalWeight >= 3) {
										mIsOverWeight = true;
										showMsg3();
									}
									// 被钓起来鱼的坐标
									mXYCatched.add(mXY.get(i));
									mFishList.remove(i);
									mXY.remove(i);
								}
							}
						}
					}

					/********************** 被钓起来的鱼和杂物坐标 ****************************/
					if ((mFishCatchedList != null)
							&& (mFishCatchedList.size() > 0)) {
						for (int j = 0; j < mFishCatchedList.size(); j++) {
							int mCurrentX;
							mCurrentX = mFishCatchedList.get(j).getCatchedX(
									mHookCurrentX, mHookCurrentY);
							mXYCatched.get(j).setX(mCurrentX);
							mXYCatched.get(j).setY(mHookDeep);
						}
					}

					/********************** 被钓起来杂物数量>3，小船沉没 ****************************/
					if (!isTouchDown) {
						if (mHookDeep <= 0) {
							if (mIsBoatSink) {
								if (degree < 30) {
									degree++;
								} else {
									degree = 30;
								}
								degree = 0;
								mBoatDelY += Utils.pxToDp(10);
								if (mBoatDelY >= mMaxDelY) {
									mBoatDelY = mMaxDelY;
									mIsFinished = true;
									mHandlerFish.sendEmptyMessageDelayed(
											MSG_GAME_OVER, 1000);
								}
							}
						}
					}
					/** 绘制 ***/
					draw();
				}
			}
		}

		/**
		 * 屏幕宽度
		 */
		int SCREEN_WIDTH;
		/**
		 * 鱼钩的初始横坐标
		 */
		private final int mHookX = Utils.pxToDp(414 - mDel);
		/**
		 * 鱼钩线的初始横坐标
		 */
		private final int mLineX = Utils.pxToDp(428 - mDel);
		/**
		 * 鱼钩和鱼钩线的初始纵坐标
		 */
		final int mHookY = Utils.pxToDp(105);
		/**
		 * 鱼钩下探深度
		 */
		int mHookDeep;
		/**
		 * 鱼钩最浅下探深度
		 */
		private final int mHookDeepMinY = 0;
		/**
		 * 鱼钩最深下探深度
		 */
		private final int mHookDeepMaxY = Utils.pxToDp(340 + 160);
		/**
		 * 鱼钩宽度
		 */
		private final int mHookW = Utils.pxToDp(34);
		/**
		 * 鱼钩高度
		 */
		private final int mHookH = Utils.pxToDp(60);
		/**
		 * 3，4级时船在水平方向的自动移动（左或右）
		 */
		private int mBoatDelX = 0;
		/**
		 * 沉船时船在垂直方向移动
		 */
		private int mBoatDelY = 0;
		/**
		 * 沉船时，船在垂直方向移动的最大距离
		 */
		private final int mMaxDelY = Utils.pxToDp(320);
		/**
		 * 船的整体与左边的距离
		 */
		private final int mBoatDelLeft = Utils.pxToDp(-298);
		/**
		 * 船的整体与右边的距离
		 */
		private final int mBoatDelRight = Utils.pxToDp(318);
		/**
		 * 小船自动移动时，船体默认移动方向：向右
		 */
		private boolean mToRight = true;

		private Bitmap bmpDebris = null;
		private int left = Utils.pxToDp(426 - mDel + 280);
		private int top = Utils.pxToDp(55 + 80);

		//		private final float BOAT_MOVE_V = context.getResources().getDimension(R.dimen.boat_move_v);
		private final float BOAT_MOVE_V = Utils.getMyResource().getDimension(R.dimen.boat_move_v);
		private final float HOOK_MOVE_V = Utils.getMyResource().getDimension(R.dimen.hook_move_v);
		private final float DIALOG_DX = Utils.getMyResource().getDimension(R.dimen.dialog_dx);
		private final float RESULT_DX1 = Utils.getMyResource().getDimension(R.dimen.result_dx1);
		private final float RESULT_DX2 = Utils.getMyResource().getDimension(R.dimen.result_dx2);
		private final float BOAT_X_MIN = Utils.getMyResource().getDimension(R.dimen.boat_x_min);
		/**
		 * 对话框横坐标
		 */
		private float mDialogX;

		/**
		 * 结果横坐标
		 */
		private float mResultX;

		/**
		 * 对话框和结果纵坐标
		 */
		private final int mDialogY = Utils.pxToDp(60);

		public final int mMaxLeft = Utils.pxToDp(550);
		public final int mMaxBoatDelX = Utils.pxToDp(140);

		private void draw() {
			canvas = holder.lockCanvas();
			paint = new Paint();
			paint.setStrokeWidth(3);
			paint.setColor(Color.BLUE);
			if (canvas == null)
				return;

			if (mIsDisplayBackground) {
				canvas.drawBitmap(bmpBack, 0, 0, paint);
			} else {
				canvas.drawBitmap(bmpBack, 0, 0, paint);
				// 游戏结束
				if (!mIsFinished) {
					// 波浪（前）
					mWaveA.draw(canvas, paint);
					if (mIsClickStudy) {
						canvas.drawBitmap(bmpResource, Utils.pxToDp(5), Utils.pxToDp(10), paint);

						if (bShowClickStudyWord) {
							paint.setTextSize(Utils.pxToDp(30));
							if (mStrItem1 != null && mStrItem1.length() > 0) {
								canvas.drawText(mStrItem1, Utils.pxToDp(25 + (277 - mStrItem1.length() * 15) / 2), Utils.pxToDp(120), paint);
							}
							if (mStrItem2 != null && mStrItem2.length() > 0) {
								canvas.drawText(mStrItem2, Utils.pxToDp(25 + (277 - mStrItem2.length() * 30) / 2), Utils.pxToDp(160), paint);
							}
							paint.setTextSize(Utils.pxToDp(16));
						}

						if (bShowClickStudyPic) {
/*							if (clickBmpHeight > CLICK_BMP_H) {
								canvas.drawBitmap(bmpClickStudy, 15 + (303 - w) / 2, Utils.pxToDp(90), paint);
							} else {
								canvas.drawBitmap(bmpClickStudy, 15 + (303 - clickBmpWidth) / 2, Utils.pxToDp(90), paint);
							}*/
							canvas.drawBitmap(bmpClickStudy, CLICK_BMP_X_S + (CLICK_BMP_MAX_W - bmpClickStudy.getWidth()) / 2, CLICK_BMP_Y_S, paint);
						}
					}
					/* 1,2,3,4关 */
					if (mCurrentLevel < 4) {
						// 小朵
						if (mIsBoatSink) {
							mDuo.drawRotate(canvas, paint, degree, mBoatDelX,
									mBoatDelY);
						} else {
							mDuo.draw(canvas, paint, mBoatDelX);
						}
						// 钓到船上的杂物
						if (mDebrisBmpList != null && mDebrisBmpList.size() > 0) {
							for (int i = 0; i < mDebrisBmpList.size(); i++) {
								bmpDebris = mDebrisBmpList.get(i);
								if (mIsBoatSink) {
									canvas.drawBitmap(bmpDebris, left
											+ mBoatDelX, top + mBoatDelY, paint);
								} else {
									canvas.drawBitmap(bmpDebris, left
											+ mBoatDelX, top, paint);
								}
							}
						}
						// 小船
						if (mIsBoatSink) {
							mBoatFront.drawRotate(canvas, paint, degree,
									mBoatDelX, mBoatDelY);
						} else {
							mBoatFront.draw(canvas, paint, mBoatDelX);
						}
						// 波浪（后）
						mWaveB.draw(canvas, paint);
						// 鱼钩
						if (mIsBoatSink) {
							canvas.drawBitmap(
									Tools.rotateBitmap(bmpHook, degree), mHookX
											+ mBoatDelX, mHookY + mHookDeep
											+ mBoatDelY, paint);
						} else {
							canvas.drawBitmap(bmpHook, mHookX + mBoatDelX,
									mHookY + mHookDeep + mHookDelDeep, paint);
						}
						// 鱼钩线
						canvas.drawLine(mLineX + mBoatDelX, mHookY
								+ mHookDelDeep, mLineX + mBoatDelX, mHookY
								+ mHookDeep + mHookDelDeep, paint);
					}
					/* 5,6关 */
					else {
						// 小朵
						if (mIsBoatSink) {
							mDuo.drawRotate(canvas, mBoatRect.left - Utils.pxToDp(118),
									mBoatRect.top - Utils.pxToDp(92), paint, degree,
									mBoatDelX, mBoatDelY);
						} else {
							mDuo.draw(canvas, mBoatRect.left - Utils.pxToDp(118),
									mBoatRect.top - Utils.pxToDp(92), paint, mBoatDelX);
						}
						// 钓到船上的杂物
						if (mDebrisBmpList != null && mDebrisBmpList.size() > 0) {
							for (int i = 0; i < mDebrisBmpList.size(); i++) {
								bmpDebris = mDebrisBmpList.get(i);
								if (mIsBoatSink) {
									canvas.drawBitmap(
											bmpDebris,
											mBoatRect.left - Utils.pxToDp(118) + mBoatDelX
													+ Utils.pxToDp(280),
											mBoatRect.top - Utils.pxToDp(92) + mBoatDelY + Utils.pxToDp(80),
											paint);
								} else {
									canvas.drawBitmap(bmpDebris, mBoatRect.left
													- Utils.pxToDp(118) + mBoatDelX + Utils.pxToDp(280),
											mBoatRect.top - Utils.pxToDp(92) + Utils.pxToDp(80), paint);
								}
							}
						}
						// 小船
						if (mIsBoatSink) {
							mBoatFront.drawRotate(canvas, mBoatRect.left,
									mBoatRect.top, paint, degree, mBoatDelX,
									mBoatDelY);
						} else {
							mBoatFront.draw(canvas, mBoatRect.left,
									mBoatRect.top, paint, mBoatDelX);
						}
						// 波浪（后）
						mWaveB.draw(canvas, paint);
						// 鱼钩
						if (mIsBoatSink) {
							canvas.drawBitmap(
									Tools.rotateBitmap(bmpHook, degree),
									mBoatRect.left - Utils.pxToDp(130) + mBoatDelX, mHookY
											+ mHookDeep + mBoatDelY, paint);
						} else {
							canvas.drawBitmap(bmpHook, mBoatRect.left - Utils.pxToDp(130),
									mHookY + mHookDeep + mHookDelDeep, paint);
						}
						// 鱼钩线
						canvas.drawLine(mBoatRect.left - Utils.pxToDp(116), mHookY
								+ mHookDelDeep, mBoatRect.left - Utils.pxToDp(116), mHookY
								+ mHookDeep + mHookDelDeep, paint);
					}

					if (mCurrentLevel < 4) {
						if (mCurrentLevel > 1) {
							if (mBoatDelX > mMaxBoatDelX) {
								mDialogX = mHookX + mBoatDelX - DIALOG_DX;
							} else {
								mDialogX = mHookX + mBoatDelX + DIALOG_DX;
							}
						} else {
							mDialogX = mHookX + mBoatDelX + DIALOG_DX;
						}

						mResultX = mHookX + mBoatDelX - RESULT_DX2;
					} else {
						if (mBoatRect.left >= mMaxLeft) {
							mDialogX = mBoatRect.left - DIALOG_DX;
						} else {
							mDialogX = mBoatRect.left - RESULT_DX1 + DIALOG_DX;
						}

						mResultX = mBoatRect.left - RESULT_DX1 - RESULT_DX2;
					}

					// 结果显示
					if (mIsShowResult) {
						canvas.drawBitmap(bmpWrong, mResultX, mDialogY, paint);
					}

					// 信息提示对话框1
					if (mIsShowDialog1) {
						if (mCurrentLevel < 4) {
							if (mCurrentLevel > 1) {
								if (mBoatDelX > mMaxBoatDelX) {
									canvas.drawBitmap(bmpDialog1_reverse,
											mDialogX, mDialogY, paint);
								} else {
									canvas.drawBitmap(bmpDialog1, mDialogX,
											mDialogY, paint);
								}
							} else {
								canvas.drawBitmap(bmpDialog1, mDialogX,
										mDialogY, paint);
							}
						} else {
							if (mBoatRect.left >= mMaxLeft) {
								canvas.drawBitmap(bmpDialog1_reverse, mDialogX,
										mDialogY, paint);
							} else {
								canvas.drawBitmap(bmpDialog1, mDialogX,
										mDialogY, paint);
							}
						}
					}

					// 信息提示对话框3
					if (mIsShowDialog3) {
						if (mCurrentLevel < 4) {
							if (mCurrentLevel > 1) {
								if (mBoatDelX > mMaxBoatDelX) {
									canvas.drawBitmap(bmpDialog3_reverse,
											mDialogX, mDialogY, paint);
								} else {
									canvas.drawBitmap(bmpDialog3, mDialogX,
											mDialogY, paint);
								}
							} else {
								canvas.drawBitmap(bmpDialog3, mDialogX,
										mDialogY, paint);
							}
						} else {
							if (mBoatRect.left >= mMaxLeft) {
								canvas.drawBitmap(bmpDialog3_reverse, mDialogX,
										mDialogY, paint);
							} else {
								canvas.drawBitmap(bmpDialog3, mDialogX,
										mDialogY, paint);
							}
						}
					}

					paint.setColor(Color.BLACK);

					// 游动的鱼
					if ((mFishList != null) && (mFishList.size() > 0)) {
						for (int i = 0; i < mFishList.size(); i++) {
							mFishList.get(i).draw(canvas, paint,
									mXY.get(i).getX(),
									mHookY + mXY.get(i).getY());
						}
					}

					// 被钓起来的鱼或垃圾
					if ((mFishCatchedList != null)
							&& (mFishCatchedList.size()) > 0) {
						for (int j = 0; j < mFishCatchedList.size(); j++) {
							mFishCatchedList.get(j).draw(canvas, paint,
									mXYCatched.get(j).getX(),
									mHookY + mXYCatched.get(j).getY());
						}
					}
				}
			}

			holder.unlockCanvasAndPost(canvas);
		}

		public void doHookAction() {
			// 鱼钩下探
			if (!canDrag) {
				isTouchDown = false;
				bTouchFlag = false;
				mHandlerFish.sendEmptyMessageDelayed(MSG_HOOK, 100);
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (mIsOverWeight) {
						doOverWeight();
					} else {
						if (mCurrentLevel >= 4) {
							point.x = (int) event.getX();
							point.y = (int) event.getY();
							// 判断是否点击在小船上，否则无法拖动小船
							if (mBoatRect.contains(point.x, point.y)) {
								canDrag = true;
								offsetX = point.x - mBoatRect.left;
							}
						}
						if (canHookTouchDown) {
							doHookAction();
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					canDrag = false;
					mHandlerFish.sendEmptyMessageDelayed(MSG_HOOK_UP, 100);
					break;
				case MotionEvent.ACTION_MOVE:
					mBoatMoveX = (int) event.getX();
					mHandlerFish.sendEmptyMessage(MSG_BOAT_MOVE);
					break;
				default:
					break;
			}
			return true;
		}

		private int mBoatMoveX;

		/**
		 * 小船的水平移动
		 */
		private void doBoatMove(int mCurrentX) {
			if (canDrag) {
				mBoatRect.left = mCurrentX - offsetX;
				mBoatRect.right = mBoatRect.left + bmpBoat.getWidth();
				if (mBoatRect.left < BOAT_X_MIN) {
					mBoatRect.left = (int) BOAT_X_MIN;
					mBoatRect.right = mBoatRect.left + bmpBoat.getWidth();
				}
				if (mBoatRect.right > getMeasuredWidth()) {
					mBoatRect.right = getMeasuredWidth();
					mBoatRect.left = mBoatRect.right - bmpBoat.getWidth();
				}
			}
		}

		/**
		 * 放下鱼钩
		 */
		private void doTouchDown() {
			if (mHookDeep <= 0) {
				playSoundPool(1);
			}
		}

		private SoundPool mSoundPool = null;
		private Map<Integer, Integer> mSoundMap = new HashMap<Integer, Integer>();

		private void playSoundPool(int mSoundID) {
			if (mSoundPool != null) {
				mSoundPool.play(mSoundMap.get(mSoundID), 1, 1, 1, 0, 1);
			}
		}

		private void initSoundPool() {
			// 创建 SoundPool对象设置最多容纳10个音频。音频的品质为5
			mSoundPool = new soundpool(10, AudioManager.STREAM_SYSTEM, 5);
			// load方法加载音频文件返回对应的ID
			mSoundMap.put(1, mSoundPool.load(context, R.raw.yx_83, 1));// 下钩的音效
			mSoundMap.put(2, mSoundPool.load(context, R.raw.yx_84, 1));// 鱼钩触碰到物体吸附住的音效
			mSoundMap.put(3, mSoundPool.load(context, R.raw.yx_86, 1));// 调到鱼时候的激励音效
			mSoundMap.put(4, mSoundPool.load(context, R.raw.yx_87, 1));// 钓到杂物时的可惜音效
		}
	}
}