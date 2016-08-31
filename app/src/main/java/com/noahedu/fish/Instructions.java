package com.noahedu.fish;

import java.util.HashMap;
import java.util.Map;

import com.noahedu.fish.param.GameConstant;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Instructions extends Activity {

	private ImageView mIvClownfish;
	private ImageButton mBtnClose, mBtnStart;
	private LinearLayout mBlackBg;
	private boolean mIsShowGame = false;
	private boolean btnCanClick = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instructions);

		findViews();
	}

	private void findViews() {
		mIvClownfish = (ImageView) findViewById(R.id.ins_clownfish);
		mBtnClose = (ImageButton) findViewById(R.id.ins_close);
		mBtnStart = (ImageButton) findViewById(R.id.ins_start);
		mBtnClose.setOnClickListener(onClickListener);
		mBtnStart.setOnClickListener(onClickListener);
		mBlackBg = (LinearLayout) findViewById(R.id.ins_black_bg);
		init();
	}

	private AnimationDrawable ad = null;

	private void init() {
		if (null == mSoundPool) {
			initSoundPool();
		}

		mIvClownfish.setBackgroundResource(R.anim.anim_clownfish);
		ad = (AnimationDrawable) mIvClownfish.getBackground();
		// 设置连续播放
		ad.setOneShot(false);
		mIvClownfish.getViewTreeObserver().addOnPreDrawListener(opd);
	}

	// onCreate里不能直接播放动画
	OnPreDrawListener opd = new OnPreDrawListener() {
		public boolean onPreDraw() {
			ad.start();
			return true;
		}
	};

	private View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ins_close:
				doClose();
				break;
			case R.id.ins_start:
				doStart();
				break;
			default:
				break;
			}
		}
	};

	private void doClose() {
		Instructions.this.finish();
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
		intent.setClass(Instructions.this, Fish.class);
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
	protected void onResume() {
		if (mIsShowGame) {
			mIsShowGame = false;
			enterGame();
		}
		btnCanClick = false;
		super.onResume();
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}
}
