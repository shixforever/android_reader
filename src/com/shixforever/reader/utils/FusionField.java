package com.shixforever.reader.utils;

import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;

/**********************************************************
 * @文件名称：FusionField.java
 * @文件作者：shixiang
 * @文件描述：
 * @修改历史：2012-3创建初始版本
 **********************************************************/
public class FusionField
{
	public static Activity baseActivity = null;
	public static float widthScale = 0;
	public static float HeightScale = 0;
	public static float density = 0;
	public static HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>();
}
