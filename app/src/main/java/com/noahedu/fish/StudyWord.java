package com.noahedu.fish;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.noahedu.FetchWordLibData.WordItem;
import com.noahedu.FetchWordLibData.Dds.JniDds;
import com.noahedu.SoundPlayer.SoundPlayer;
import com.noahedu.SoundPlayer.SoundPlayerParam;
import com.noahedu.fish.param.GameConstant;
import com.noahedu.fish.tool.PlaySound;
import com.noahedu.fish.widget.StudyWordInfoView;

public class StudyWord extends Activity {
	static {
		try {
			System.loadLibrary("JniDdsWordLib");
			System.loadLibrary("JniDdsRightlessWordLib");
		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
	}

	private SoundPlayer mSoundPlayerGuide = new SoundPlayer();
	private SoundPlayer mSoundPlayerWord = new SoundPlayer();

	private int mSubjectId = 0;// 0为中文，1为英文

	private String mSoundFilePath = null;

	private ImageView mIvDuo, mIvMsg;

	private LinearLayout mLL;

	private JniDds mJniDds = null;

	private String mFilePath = null;

	/**
	 * 学过的所有单词序号
	 */
	private ArrayList<Integer> mWordIndexList = new ArrayList<Integer>();

	private int mIndex = 0;

	private int mTotalNum = 0;

	private boolean mInitFlag = false;

	private int[] mMsgResId = { R.drawable.msg_chinese, R.drawable.msg_english };

	private String[] mSoundPaths = { GameConstant.SOUND_FISHXB_306,
			GameConstant.SOUND_FISHXB_307 };

	private boolean mIsAllGamePassed = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.studyword);

		doInit();
		findViews();
		duoSpeak();
	}

	private void doInit() {
		mIsAllGamePassed = getIntent().getBooleanExtra("mIsAllGamePassed",
				false);
		mSubjectId = getIntent().getIntExtra(Fish.STUDY_SUBJECT_ID, 0);
		mFilePath = getIntent().getStringExtra(Fish.STUDY_FILE_PATH);
		mWordIndexList = getIntent().getIntegerArrayListExtra(
				Fish.STUDY_WORD_LIST);
		mTotalNum = mWordIndexList.size();
		mSoundFilePath = mSoundPaths[mSubjectId];

		if (mJniDds == null) {
			mJniDds = new JniDds();
		}

		mInitFlag = mJniDds.JniDds_init(mFilePath);
	}

	private void findViews() {
		mIvDuo = (ImageView) findViewById(R.id.study_duo);
		mIvMsg = (ImageView) findViewById(R.id.study_msg);
		mLL = (LinearLayout) findViewById(R.id.study_word_list);
		mIvMsg.setBackgroundResource(mMsgResId[mSubjectId]);
	}

	private AnimationDrawable ad = null;

	private void duoSpeak() {
		playGuideSound(mSoundFilePath);
		mIvDuo.setBackgroundResource(R.anim.duo_speak);
		ad = (AnimationDrawable) mIvDuo.getBackground();
		// 设置连续播放
		ad.setOneShot(true);
		mIvDuo.getViewTreeObserver().addOnPreDrawListener(opd);
	}

	// onCreate里不能直接播放动画
	OnPreDrawListener opd = new OnPreDrawListener() {
		public boolean onPreDraw() {
			ad.start();
			return true;
		}
	};

	private void duoRead() {
		ad = (AnimationDrawable) getResources().getDrawable(R.anim.duo_speak);
		mIvDuo.setBackgroundDrawable(ad);
		if (ad != null) {
			ad.start();
		}
	}

	private void playGuideSound(String mFilePath) {
		if (null == mSoundPlayerGuide) {
			mSoundPlayerGuide = new SoundPlayer();
		}
		if (null != mSoundPlayerGuide) {
			SoundPlayerParam param = new SoundPlayerParam();
			param.setFilePath(mFilePath, 0, 0);
			param.setOnStatusListener(new SoundPlayerParam.onStatusListener() {
				public void onStatusNotify(int status, int curTime) {
					if (status == SoundPlayer.Status.STOPPED) {
						mHandler.sendEmptyMessage(MSG_SHOW_WORD_INFO);
					}
				}
			});
			try {
				mSoundPlayerGuide.stop();
				if (mSoundPlayerGuide.prepare(param)) {
					mSoundPlayerGuide.play();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static final int MSG_SHOW_WORD_INFO = 1;
	private static final int MSG_FINISH = 2;
	private static final int MSG_PLAY_SWF = 3;
	private boolean mIsShowWord = false;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_WORD_INFO:
				mIsShowWord = true;
				showWordInfoList();
				break;
			case MSG_FINISH:
				finish();
				break;
			case MSG_PLAY_SWF:
				mIsPlayFlash = true;
				playFlash(GameConstant.SWF_END);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	private void showWordInfoList() {
		mIvMsg.setBackgroundResource(0);
		duoRead();
		setStudyWordInfo();
	}

	private byte[] mWord, mExplain, mSound, mPicture = null;

	private void setStudyWordInfo() {
		if (mTotalNum > 4) {
			if (mIndex > 3) {
				mLL.removeViewAt(0);
			}
		}
		int mWordIndex = mWordIndexList.get(mIndex);
		byte[] mp3Name = null;
		WordItem mItem = null;
		if (mInitFlag) {
			mItem = mJniDds.JniDds_wordInfo(mWordIndex);
			mp3Name = mItem.wordSoundName;
			mSound = mJniDds.JniDds_wordSound();
			mPicture = mJniDds.JniDds_wordPic();

			if (mSubjectId == 0) {
				mWord = mItem.wordPhonetic;
				mExplain = mItem.wordHead;
			} else {
				mWord = mItem.wordHead;
				mExplain = mItem.wordExplain;
			}

			StudyWordInfoView mInfoView = new StudyWordInfoView(this, mPicture,
					mWord, mExplain);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			mLL.addView(mInfoView, params);

			try {
				playWordSound(mSound);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void playWordSound(byte[] sound) throws Exception {
		if (null == mSoundPlayerWord) {
			mSoundPlayerWord = new SoundPlayer();
		}
		if (null != mSoundPlayerWord) {
			SoundPlayerParam param = new SoundPlayerParam();
			param.setBuffer(sound, 0, sound.length);
			param.setOnStatusListener(new SoundPlayerParam.onStatusListener() {
				public void onStatusNotify(int status, int curTime) {
					if (status == SoundPlayer.Status.STOPPED) {
						mIndex++;
						if (mIndex < mTotalNum) {
							mHandler.sendEmptyMessageDelayed(
									MSG_SHOW_WORD_INFO, 2000);
						} else {
							mIsShowWord = false;
							doExit();
						}
					}
				}
			});
			mSoundPlayerWord.stop();
			if (mSoundPlayerWord.prepare(param)) {
				mSoundPlayerWord.play();
			}
		}
	}

	private void doExit() {
		if (mIsAllGamePassed) {
			mHandler.sendEmptyMessageDelayed(MSG_PLAY_SWF, 2000);
		} else {
			mHandler.sendEmptyMessageDelayed(MSG_FINISH, 2000);
		}
	}

	private boolean mIsPlayFlash = false;

	private String getStr(int resId) {
		return getResources().getString(resId);
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
								setResult(1);
								finish();
							}
						})
				.setNegativeButton(getStr(R.string.cancel),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								setResult(2);
								finish();

							}
						}).create();
		dialog.show();
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				finish();
			}
		});
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
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	protected void onPause() {
		isResumed = true;
		if (mSoundPlayerGuide != null) {
			try {
				mSoundPlayerGuide.pause();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (mSoundPlayerWord != null) {
			try {
				mSoundPlayerWord.pause();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (mHandler != null) {
			mHandler.removeMessages(MSG_SHOW_WORD_INFO);
		}
		super.onPause();
	}

	private boolean isResumed = false;

	@Override
	protected void onResume() {
		if (mIsPlayFlash) {
			showRedoDialog();
			mIsPlayFlash = false;
		}
		if (isResumed) {
			if (mSoundPlayerGuide != null) {
				try {
					mSoundPlayerGuide.resume();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (mSoundPlayerWord != null) {
				try {
					mSoundPlayerWord.resume();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (mIsShowWord) {
				if (mIndex < mTotalNum) {
					mHandler.sendEmptyMessageDelayed(MSG_SHOW_WORD_INFO, 1000);
				} else {
					doExit();
				}
			}
			isResumed = false;
		}
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			doPause();
			if (mIsAllGamePassed) {
				showRedoDialog();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void doPause() {
		PlaySound.stopSound(mSoundPlayerGuide);
		PlaySound.stopSound(mSoundPlayerWord);
		if (mHandler != null) {
			mHandler.removeMessages(MSG_SHOW_WORD_INFO);
			mHandler.removeMessages(MSG_FINISH);
			mHandler.removeMessages(MSG_PLAY_SWF);
		}
	}

	private void doRecycle() {
		PlaySound.releaseSound(mSoundPlayerGuide);
		PlaySound.releaseSound(mSoundPlayerWord);
		if (mWordIndexList != null) {
			mWordIndexList.clear();
			mWordIndexList = null;
		}
	}

	@Override
	protected void onDestroy() {
		doRecycle();
		super.onDestroy();
	}
}
