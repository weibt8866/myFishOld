package com.noahedu.fish.widget;

import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.noahedu.fish.R;
import com.noahedu.fish.engine.GifView;
import com.noahedu.fish.tool.Utils;

public class StudyWordInfoView extends LinearLayout {

	public StudyWordInfoView(Context context, AttributeSet attrs,
			byte[] mPicture, byte[] mWord, byte[] mExplain) {
		super(context, attrs);
		this.context = context;
		this.mPicture = mPicture;
		this.mWord = mWord;
		this.mExplain = mExplain;
		init();
	}

	public StudyWordInfoView(Context context, byte[] mPicture, byte[] mWord,
			byte[] mExplain) {
		super(context);
		this.context = context;
		this.mPicture = mPicture;
		this.mWord = mWord;
		this.mExplain = mExplain;
		init();
	}

	private Context context;
	private LinearLayout mLLPic;
	private AlwaysMarqueeTextView mTvWord, mTvExplain;
	private byte[] mPicture, mWord, mExplain;

	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.studywordinfo, this, true);
		findViews();
		setWordInfo();
	}

	private void findViews() {
		mLLPic = (LinearLayout) findViewById(R.id.info_pic);
		mTvWord = (AlwaysMarqueeTextView) findViewById(R.id.info_word);
		mTvExplain = (AlwaysMarqueeTextView) findViewById(R.id.info_explain);
	}

	private void setWordInfo() {
		if (mPicture.length > 1) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, Utils.pxToDp(150));
			ImageView iv = new ImageView(context);
			Bitmap bmp = BitmapFactory.decodeByteArray(mPicture, 0,
					mPicture.length);
			iv.setImageBitmap(bmp);
			mLLPic.addView(iv, params);
		}
		try {
			if (mWord != null && mWord.length > 1) {
				mTvWord.setText(new String(mWord, "GBK"));
			}
			if (mExplain != null && mExplain.length > 1) {
				mTvExplain.setText(new String(mExplain, "GBK"));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
