package com.shixforever.reader.utils;

import com.shixforever.reader.manager.SysManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

/**********************************************************
 * @文件名称：BitmapUtil.java
 * @文件作者：shixiang
 * @文件描述：图片工具类
 * @修改历史：2012-3创建初始版本
 **********************************************************/
public class BitmapUtil
{
	/**
	 * 图片旋转
	 * 
	 * @param bitmap
	 * @param degrees
	 * @return
	 */
	public static Bitmap rotate(Bitmap bitmap, int degrees)
	{
		Bitmap bm = null;
		Matrix matrix = new Matrix();
		matrix.postScale(0.5f, 0.5f);
		matrix.setRotate(degrees);
		bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return bm;
	}

	public static Bitmap scale(Bitmap bitmap, float scale)
	{
		Bitmap bm = null;
		int bmpWidth = bitmap.getWidth();
		int bmpHeight = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		bm = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix,
				true);
		return bm;
	}

	public static float calculateScale(float w0, float h0, float w, float h)
	{
		float scaleW = w / w0;
		float scaleH = h / h0;
		if (scaleW >= scaleH)
		{
			return scaleW;
		} else
		{
			return scaleH;
		}
	}

	public static Bitmap ModifyAsWH(Bitmap obm, int w, int h)
	{
		/*
		 * int width= obm.getWidth(); int height= obm.getHeight(); Matrix
		 * matrix= new Matrix(); float scaleWidth= ((float) w / width); float
		 * scaleHeight= ((float) h / height); matrix.postScale(scaleWidth,
		 * scaleHeight); Bitmap nbm= Bitmap.createBitmap(obm, 0, 0, width,
		 * height, matrix, true); return nbm;
		 */
		float w0 = obm.getWidth();
		float h0 = obm.getHeight();
		float scale = calculateScale(w0, h0, w, h);
		Bitmap bm = scale(obm, scale);
		Bitmap nbm = Bitmap.createBitmap(bm, Math.abs(w - bm.getWidth()) / 2,
				Math.abs(h - bm.getHeight()) / 2, w, h);
		return nbm;
	}

	public static Bitmap ifNeedRoate(Bitmap bm)
	{
		int w = bm.getWidth();
		int h = bm.getHeight();
		if (w > h)
		{
			bm = BitmapUtil.rotate(bm, 90);
		}
		return bm;
	}

	public static Bitmap scallAsFullSreen(Bitmap bm, Context context)
	{
		int screenW = SysManager.getDisplayMetrics(context).widthPixels;
		if (bm.getWidth() >= screenW)
		{
			return bm;
		} else
		{
			float scale = (float) screenW / (float) bm.getWidth();
			return scale(bm, scale);
		}
	}
}
