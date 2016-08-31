package com.noahedu.fish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.noahedu.fish.db.FishDb;
import com.noahedu.fish.db.ScoreInfo;
import com.noahedu.fish.param.GameConstant;
import com.noahedu.fish.tool.Utils;

public class HighScore extends Activity {
	private ImageButton mBtnClose, mBtnStart;
	private LinearLayout mBlackBg;
	private FishDb db = new FishDb(this);
	private List<ScoreInfo> scoreList = new ArrayList<ScoreInfo>();
	private AbsoluteLayout scoreLL;
	private LinearLayout[] mSocreViews;
	private int mScoreNum = 0;
	public static final String tag = "HighScore";
	private int[] mLayoutY = { 0, 70, 132, 200, 268 };
	private boolean mIsShowGame = false;
	private boolean btnCanClick = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.highscore);

		findViews();
	}

	private void findViews() {
		mBtnClose = (ImageButton) findViewById(R.id.high_score_close);
		mBtnStart = (ImageButton) findViewById(R.id.high_score_start);
		mBtnClose.setOnClickListener(onClickListener);
		mBtnStart.setOnClickListener(onClickListener);
		scoreLL = (AbsoluteLayout) findViewById(R.id.high_score_list);
		mBlackBg = (LinearLayout) findViewById(R.id.high_score_black_bg);

		init();
	}

	private void init() {
		if (null == mSoundPool) {
			initSoundPool();
		}
	}

	private void getScore() {
		if ((scoreList != null) && (scoreList.size() > 0)) {
			for (int i = 0; i < scoreList.size(); i++) {
				mSocreViews[i].removeAllViews();
			}
		}
		if (scoreList != null) {
			scoreList.clear();
		}
		if (db != null) {
			db.open();
			scoreList = db.queryAllScores();
			if ((scoreList != null) && (scoreList.size() > 5)) {
				scoreList = scoreList.subList(0, 5);
			}
			mScoreNum = scoreList.size();
			mSocreViews = new LinearLayout[mScoreNum];
			for (int i = 0; i < mScoreNum; i++) {
				mSocreViews[i] = new LinearLayout(this);
				mSocreViews[i].addView(Utils.getNumImage(Utils.mHighScoreResId,
						scoreList.get(i).getScore(), this, true));
				@SuppressWarnings("deprecation")
				AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
						AbsoluteLayout.LayoutParams.WRAP_CONTENT,
						AbsoluteLayout.LayoutParams.WRAP_CONTENT, 0,
						Utils.pxToDp(mLayoutY[i]));
				mSocreViews[i].setLayoutParams(params);
				scoreLL.addView(mSocreViews[i]);
			}
		}
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.high_score_close:
				doClose();
				break;
			case R.id.high_score_start:
				doStart();
				break;
			default:
				break;
			}
		}
	};

	private void doClose() {
		HighScore.this.finish();
	}

	private void doStart() {
		if (!btnCanClick) {
			btnCanClick = true;
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

	private void playBtnClickSound() {
		mSoundPool.play(mSoundMap.get(1), 1, 1, 1, 0, 1);
	}

	private SoundPool mSoundPool = null;
	private Map<Integer, Integer> mSoundMap = new HashMap<Integer, Integer>();

	private void initSoundPool() {
		mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		mSoundMap.put(1, mSoundPool.load(this, R.raw.yx_81, 1));
	}

	private void enterGame() {
		Intent intent = new Intent();
		intent.setClass(HighScore.this, Fish.class);
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
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if (db != null) {
			db.close();
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		if (mIsShowGame) {
			mIsShowGame = false;
			enterGame();
		}
		getScore();
		btnCanClick = false;
		super.onResume();
	}

	@Override
	protected void onPause() {

		super.onPause();
	}
}
