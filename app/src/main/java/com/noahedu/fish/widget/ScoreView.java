package com.noahedu.fish.widget;

import com.noahedu.fish.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class ScoreView extends LinearLayout {

	private int score;

	private LinearLayout ll;

	private int[] mScoreNumResId = { R.drawable.list0, R.drawable.list1,
			R.drawable.list2, R.drawable.list3, R.drawable.list4,
			R.drawable.list5, R.drawable.list6, R.drawable.list7,
			R.drawable.list8, R.drawable.list9 };

	public ScoreView(Context context, int score) {
		super(context);
		this.score = score;
		init();
	}

	public ScoreView(Context context, AttributeSet attrs, int score) {
		super(context, attrs);
		this.score = score;
		init();
	}

	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.score_view, null);
		findViews();
	}

	private void findViews() {
		ll = (LinearLayout) findViewById(R.id.score_view_ll);
	}

	private void s() {
		int i = score / 100;
	}
}
