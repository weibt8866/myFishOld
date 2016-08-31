package com.noahedu.fish.engine;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 简单的GIF控件。（每秒播放一张图片）
 * 
 * @author Administrator
 * 
 */
public class GifView extends View implements Runnable {
	private Context mContext = null;

	private GifFrame mGifFrame = null;
	private Thread mGameViewThread = null;
	private boolean mBrushStoped = true;

	private static final String tag = "GifView";

	public GifView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(mContext);
	}

	public GifView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(mContext);
	}

	public GifView(Context context) {
		super(context);
		init(mContext);
	}

	private void init(Context context) {
		mContext = context;
		setClickable(false);
		setFocusable(false);
		setLongClickable(false);
	}

	public void setGifData(byte[] pictureData) {
		stopThread();
		mGifFrame = GifFrame.CreateGifImage(pictureData);
		beginThread();
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Bitmap b = mGifFrame.getImage();

		// Log.i(tag, "b.getWidth() = " + b.getWidth());
		// Log.i(tag, "b.getHeight() = " + b.getHeight());
		if (mWidth == 0) {
			mWidth = b.getWidth();
		}
		if (mHeight == 0) {
			mHeight = b.getHeight();
		}

		if (b != null) {
			canvas.drawBitmap(b, 10, 10, null);
		}
	}

	private int mWidth = 0;
	private int mHeight = 0;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		//if (mWidth != 0 && mHeight != 0) {
		//	setMeasuredDimension(mWidth, mHeight);
		//} else {
			setMeasuredDimension(1280, 752);
		//}
	}

	public void run() {
		while (!mBrushStoped || !Thread.currentThread().isInterrupted()) {
			try {
				mGifFrame.nextFrame();
				int i = 0;
				while (i < 1500) {
					if (mBrushStoped)
						return;
					Thread.sleep(50);
					i += 50;
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				postInvalidate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public byte[] fileConnect(InputStream is) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int ch = 0;
			while ((ch = is.read()) != -1) {
				baos.write(ch);
			}
			byte[] datas = baos.toByteArray();
			baos.close();
			baos = null;
			is.close();
			is = null;
			return datas;
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * 开始刷新线程
	 */
	private void beginThread() {
		mGameViewThread = new Thread(this);
		mGameViewThread.start();
		mBrushStoped = false;
	}

	/*
	 * 停止刷新线程并销毁bitmap资源
	 */
	public void stopThread() {
		mBrushStoped = true;
		// if (mGameViewThread != null) {
		// try {
		// mGameViewThread.join();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		destoryBitmap();
	}

	public void destoryBitmap() {
		if (mGifFrame != null && mGifFrame.size() > 0) {
			mGifFrame.destoryAllBitmap();
		}
	}
}
