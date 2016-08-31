package com.noahedu.fish.tool;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.noahedu.fish.R;

public class Utils {

	private  static DisplayMetrics dm = new DisplayMetrics();
	private  static  int screen_w = 0;
	private static Resources res = null;

	/**
	 * 数字图片（分数）
	 */
	public static final int[] mScoreResId = { R.drawable.score0,
			R.drawable.score1, R.drawable.score2, R.drawable.score3,
			R.drawable.score4, R.drawable.score5, R.drawable.score6,
			R.drawable.score7, R.drawable.score8, R.drawable.score9 };

	/**
	 * 数字图片（最高分）
	 */
	public static final int[] mHighScoreResId = { R.drawable.list0, R.drawable.list1,
			R.drawable.list2, R.drawable.list3, R.drawable.list4,
			R.drawable.list5, R.drawable.list6, R.drawable.list7,
			R.drawable.list8, R.drawable.list9 };

	private static int m, n;

	/**
	 * 根据数字得到对应的图片
	 * 
	 * @param num
	 * @param ctx
	 * @return
	 */
	public static LinearLayout getNumImage(int[] mImgResId, int num,
			Context ctx, boolean mIsScore) {
		LinearLayout ll = new LinearLayout(ctx);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		if (mIsScore) {
			num = num / 100;
		}
		if (num < 10) {
			ImageView iv = new ImageView(ctx);
			iv.setImageResource(mImgResId[num]);
			ll.addView(iv);
		} else {
			m = num / 10;
			n = num % 10;
			ImageView iv1 = new ImageView(ctx);
			ImageView iv2 = new ImageView(ctx);
			iv1.setImageResource(mImgResId[m]);
			iv2.setImageResource(mImgResId[n]);
			ll.addView(iv1);
			ll.addView(iv2);
		}
		if (mIsScore) {
			if (num > 0) {
				ImageView iv01 = new ImageView(ctx);
				iv01.setImageResource(mImgResId[0]);
				ImageView iv02 = new ImageView(ctx);
				iv02.setImageResource(mImgResId[0]);
				ll.addView(iv01);
				ll.addView(iv02);
			}
		}
		return ll;
	}

	public  static Resources getMyResource()
	{
		return res;
	}

	public static  boolean initDisplay(Context context)
	{
		res = context.getResources();
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(dm);
		if(Build.VERSION.SDK_INT>=13) {
			Rect rect = new Rect();
			display.getRectSize(rect);
			screen_w = rect.width();
		}
		else
		{
			screen_w = display.getWidth();
		}
		return true;
	}

	public static int getScreenW()
	{
		return screen_w;
	}



	public static int pxToDp(float px)
	{
		return (int)(px*dm.density);
	}
}
