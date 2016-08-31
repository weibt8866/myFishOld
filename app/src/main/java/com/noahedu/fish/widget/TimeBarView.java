package com.noahedu.fish.widget;

import com.noahedu.fish.R;
import com.noahedu.fish.tool.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * 显示一张图片的某一部分
 */
public class TimeBarView extends View {

	public TimeBarView(Context context, int width) {
		super(context);
		this.width = width;
		init();
	}

	Bitmap bmpTimeBar;
	int width = 0;

	void init() {
		bmpTimeBar = BitmapFactory.decodeResource(getResources(),
				R.drawable.time_bar1);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		canvas.save(); // 记录原来的canvas状态
		canvas.clipRect(0, 0, Utils.pxToDp(width), this.getContext().getResources().getDimension(R.dimen.timer_h)); // 显示从(0,0)到(width,44)的区域
		canvas.drawBitmap(bmpTimeBar, 0, 0, paint);
		canvas.restore(); // 恢复canvas状态
		super.onDraw(canvas);
	}
}