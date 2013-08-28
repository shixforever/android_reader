package com.shixforever.reader.manager;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
/**********************************************************
 * @文件名称：SysManager.java
 * @文件作者：shixiang
 * @文件描述：SysManager
 * @修改历史：2012-3创建初始版本
 **********************************************************/
public class SysManager
{
	private static SysManager sysManager;
	public static SysManager getInstance()
	{
		if (sysManager == null)
		{
			sysManager= new SysManager();
		}
		return sysManager;
	}
	private SysManager(){
	}
	
	/***
	 * when you call this method, it returns screen parameter data
	 * 
	 * @return DisplayMetrics
	 */
	public static DisplayMetrics getDisplayMetrics(Context context)
	{
		DisplayMetrics dm= new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return dm;
	}
}
