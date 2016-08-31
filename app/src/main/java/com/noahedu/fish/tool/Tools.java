package com.noahedu.fish.tool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class Tools {

	/**
	 * Bitmap资源回收
	 */
	public static void safeRecycle(Bitmap bmp) {
		if (bmp != null && !bmp.isRecycled()) {
			bmp.recycle();
			bmp = null;
			Log.d("Fish", "#################safeRecycle#####################");
		}
	}
	
	/**
	 * 实现图片的翻转
	 * 
	 * @param bmp
	 * @return 翻转后的图片
	 */
	public static Bitmap rotateBitmap(Bitmap bmp, float degree) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		return Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
	}

	/**
	 * 获取图片的镜像
	 * 
	 * @param 原图片
	 * @return 图片的镜像
	 */
	public static Bitmap convertBitmap(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		// 创建一个新的和SRC长度宽度一样的位图
		Bitmap convertBmp = Bitmap
				.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(convertBmp);
		Matrix matrix = new Matrix();
		matrix.postScale(-1, 1);
		Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix,
				true);
		canvas.drawBitmap(newBmp,
				new Rect(0, 0, newBmp.getWidth(), newBmp.getHeight()),
				new Rect(0, 0, width, height), null);
		return convertBmp;
	}

	/**
	 * 
	 * 矩形碰撞的函数，判断两个矩形区域是否有重叠
	 * 
	 * @param x1
	 *            第一个矩形的X坐标
	 * 
	 * @param y1
	 *            第一个矩形的Y坐标
	 * 
	 * @param w1
	 *            第一个矩形的宽
	 * 
	 * @param h1
	 *            第一个矩形的高
	 * 
	 * @param x2
	 *            第二个矩形的X坐标
	 * 
	 * @param y2
	 *            第二个矩形的Y坐标
	 * 
	 * @param w2
	 *            第二个矩形的宽
	 * 
	 * @param h2
	 *            第二个矩形的高
	 */

	public static boolean isCollsionWithRect(int x1, int y1, int w1, int h1,
			int x2, int y2, int w2, int h2) {

		// 当矩形1位于矩形2的左侧

		if (x1 >= x2 && x1 >= x2 + w2) {

			return false;

			// 当矩形1位于矩形2的右侧

		} else if (x1 <= x2 && x1 + w1 <= x2) {

			return false;

			// 当矩形1位于矩形2的上方

		} else if (y1 >= y2 && y1 >= y2 + h2) {

			return false;

		} else if (y1 <= y2 && y1 + h1 <= y2) {

			return false;

		}

		// 所有不会发生碰撞都不满足时，肯定就是碰撞了

		return true;

	}
}
