package com.noahedu.fish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.noahedu.SoundPlayer.SoundPlayer;
import com.noahedu.fish.db.FishDb;
import com.noahedu.fish.db.ScoreInfo;
import com.noahedu.fish.param.GameConstant;
import com.noahedu.fish.tool.PlaySound;

public class FishMain extends BaseActivity {
	private ImageView mIvVoice;
	private ImageButton mBtnStart, mBtnInstructions, mBtnHighestScore;
	private LinearLayout mBlackBg;
	private SoundPlayer mSoundPlayer = new SoundPlayer();
	private FishDb db = new FishDb(this);
	private List<ScoreInfo> scoreList = new ArrayList<ScoreInfo>();

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

	private boolean mIsShowGame = false;

	private boolean mIsGuideEnd = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fishmain);
		doInit();
	}

	private void doInit() {
		if (db != null) {
			db.open();
		}
		if (null == mSoundPool) {
			initSoundPool();
		}
		findViews();
		playGuideSound();
	}

	@Override
	public void applyOverrideConfiguration(Configuration overrideConfiguration) {
		super.applyOverrideConfiguration(overrideConfiguration);
	}

	private void findViews() {
		mIvVoice = (ImageView) findViewById(R.id.main_voice);
		mBtnStart = (ImageButton) findViewById(R.id.main_start);
		mBtnInstructions = (ImageButton) findViewById(R.id.main_instructions);
		mBtnHighestScore = (ImageButton) findViewById(R.id.main_highest_score);
		mBlackBg = (LinearLayout) findViewById(R.id.main_black_bg);
		mIvVoice.setOnClickListener(onClickListener);
		mBtnStart.setOnClickListener(onClickListener);
		mBtnInstructions.setOnClickListener(onClickListener);
		mBtnHighestScore.setOnClickListener(onClickListener);

        String action = getIntent().getAction();
		
		if (action != null && action.endsWith("fishing")) {
			mIsClickStudy = true;
			mClickFilePath = getIntent().getStringExtra("path");
			mCurrentUnit = getIntent().getIntExtra("unit", 0);
		} else {
			mIsClickStudy = false;
		}

		saveClickStudyInfo(mClickFilePath, mCurrentUnit);
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.main_voice:
				changeVoice();
				break;
			case R.id.main_start:
				doStart();
				break;
			case R.id.main_instructions:
				showInstructions();
				break;
			case R.id.main_highest_score:
				showHighScores();
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

	private void changeVoice() {
		if (mIsVoiceOn) {
			mIvVoice.setBackgroundResource(R.drawable.voice2);
			mIsVoiceOn = false;
			stopBackGround();
		} else {
			mIvVoice.setBackgroundResource(R.drawable.voice1);
			mIsVoiceOn = true;
			playBackGround();
		}
	}

	private void showInstructions() {
		if (!btnCanClick) {
			btnCanClick = true;
			mIsGuideEnd = true;
			playBtnClickSound();
			Intent intent = new Intent();
			intent.setClass(FishMain.this, Instructions.class);
			startActivity(intent);
		}
	}

	private void showHighScores() {
		if (!btnCanClick) {
			mIsGuideEnd = true;
			if (db != null) {
				scoreList = db.queryAllScores();
			}
			if ((scoreList != null) && (scoreList.size() > 0)) {
				btnCanClick = true;
				playBtnClickSound();
				Intent intent = new Intent();
				intent.setClass(FishMain.this, HighScore.class);
				startActivity(intent);
			} else {
				showNullRecordDialog();
			}
		}
	}

	private void showNullRecordDialog() {
		AlertDialog.Builder builder = new Builder(this);
		Resources resources = getResources();
		builder.setMessage(resources.getString(R.string.no_record_exists));
		builder.setTitle(resources.getString(R.string.remind));
		builder.setPositiveButton(resources.getString(R.string.sure),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create();
		builder.show();
	}

	private boolean btnCanClick = false;

	private void doStart() {
		if (!btnCanClick) {
			btnCanClick = true;
			mIsGuideEnd = true;
			playBtnClickSound();
			getEnterState();
			if (!mIsEntered) {
				mBlackBg.setBackgroundResource(R.drawable.black_bg);
				mIsShowGame = true;
				setEnterState(true);
				showStartStageView();
			} else {
				enterGame();
			}
		}
	}

	private void enterGame() {
		Intent intent = new Intent();
		intent.setClass(FishMain.this, Fish.class);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
		mHandler.sendEmptyMessageDelayed(MSG_SHOW_BG_BRIGHT, 2000);
	}

	private static final int MSG_SHOW_BG_BRIGHT = 1;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_BG_BRIGHT:
				mBlackBg.setBackgroundResource(0);
				break;
			}
			super.handleMessage(msg);
		}
	};

	private SharedPreferences spf = null;

	/**
	 * 进入后，第一次操作需要播放开场动画
	 */
	private boolean mIsEntered = false;

	private void setEnterState(boolean ifentered) {
		spf = getSharedPreferences("fish_file", MODE_PRIVATE);
		spf.edit().putBoolean("ifentered", ifentered).commit();
	}

	private void getEnterState() {
		spf = getSharedPreferences("fish_file", MODE_PRIVATE);
		mIsEntered = spf.getBoolean("ifentered", false);
	}

	private void saveClickStudyInfo(String path, int unit) {
		spf = getSharedPreferences("fish_file", MODE_PRIVATE);
		if (mIsClickStudy) {
			spf.edit().putBoolean("isclickstudy", true)
					.putString("filepath", path).putInt("unit", unit).commit();
		} else {
			spf.edit().putBoolean("isclickstudy", false).commit();
		}
	}

	/**
	 * 进入后，第一次开始游戏前，播放开场动画
	 */
	private void showStartStageView() {
		playFlash(GameConstant.SWF_START);
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
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|Intent.FLAG_ACTIVITY_NEW_TASK );
		startActivity(intent);
	}

	private void playBtnClickSound() {
		mSoundPool.play(mSoundMap.get(1), 1, 1, 1, 0, 1);
	}

	private SoundPool mSoundPool = null;
	private Map<Integer, Integer> mSoundMap = new HashMap<Integer, Integer>();

	private void initSoundPool() {
		mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		mSoundMap.put(1, mSoundPool.load(this, R.raw.yx_81, 1));
	}

	private void playGuideSound() {
		PlaySound.playSound(mSoundPlayer, GameConstant.SOUND_XB_309);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (db != null) {
			db.close();
		}
		setEnterState(false);
		finish();
		if (FishMain.this.isTaskRoot()) {
			gc();
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(newBase);
	}

	/**
	 * 判断一个Service是否在运行
	 * 
	 * @param ctx
	 * @param className
	 *            Service完整的类名
	 * @return
	 */
	public static boolean isServiceRunning(Context ctx, String className) {
		boolean isrunning = false;
		ActivityManager mActivityManager = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> serviceInfos = (ArrayList<ActivityManager.RunningServiceInfo>) mActivityManager
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
		BGService.setResFilePath(GameConstant.SOUND_yx_79);
		startService(new Intent("com.noahedu.fish.BGService"));
	}

	/**
	 * 停止背景声音Service
	 */
	private void stopBackGround() {
		stopService(new Intent("com.noahedu.fish.BGService"));
	}

	private void gc() {
		System.runFinalization();
		System.gc();
		System.exit(0);// 此方法会导致app进程完全退出，若不需要完全退出的app可以不调此方法
	}

	@Override
	protected void onPause() {
		isResumed = true;
		stopBackGround();
		if (mSoundPlayer != null) {
			try {
				mSoundPlayer.pause();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onPause();
	}

	private boolean isResumed = false;

	@Override
	protected void onResume() {
		if (mIsShowGame) {
			mIsShowGame = false;
			enterGame();
		}
		if (mIsVoiceOn) {
			playBackGround();
		}
		if (isResumed) {
			btnCanClick = false;
			if (!mIsShowGame) {
				if (!mIsGuideEnd) {
					if (mSoundPlayer != null) {
						try {
							mSoundPlayer.resume();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		isResumed = false;
		super.onResume();
	}
}