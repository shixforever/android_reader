package com.shixforever.reader.utils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/**********************************************************
 * @文件名称：FileTools.java
 * @文件作者：shixiang
 * @文件描述：file工具类
 * @修改历史：2012-3创建初始版本
 **********************************************************/
public class FileTools
{
	/**
	 * 从assets中取文件流
	 * @param context
	 * @param name
	 * @return
	 */
	public static InputStream getStreamFromAssets(Context context, String name)
	{
		InputStream is= null;
		try
		{
			is= context.getResources().getAssets().open(name);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return is;
	}
	/**
	 * 根据名称在资源文件中取出
	 * @param context
	 * @param name
	 * @return
	 */
	public static Bitmap getBitmapFromResource(Context context, String name)
	{
		int id= getResource(context, name);
		return BitmapFactory.decodeResource(context.getResources(), id);
	}
	public static int getResource(Context context, String name)
	{
		return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
	}
	/**
	 * 保存文件
	 * @param b
	 * @param savePath
	 * @param outputFile
	 * @return
	 */
	public static String saveFileFromBytes(byte[] b, String savePath, String outputFile)
	{
		File dfile= new File(savePath);
		if (!dfile.exists())
		{
			dfile.mkdirs();
		}
		File ret= null;
		BufferedOutputStream stream= null;
		try
		{
			ret= new File(outputFile);
			FileOutputStream fstream= new FileOutputStream(ret);
			stream= new BufferedOutputStream(fstream);
			stream.write(b);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return outputFile;
	}
	
	/**
	 * 保存图片
	 * @param bm
	 * @param directory
	 * @param fileName
	 * @return
	 */
	public static File saveFileFromBitmap(Bitmap bm, String directory, String fileName)
	{
		File dfile= new File(directory);
		if (!dfile.exists())
		{
			dfile.mkdirs();
		}
		File ret= null;
		BufferedOutputStream stream= null;
		try
		{
			ret= new File(directory + fileName);
			if (ret.exists())
			{
				ret.delete();
			}
			stream= new BufferedOutputStream(new FileOutputStream(ret));
			bm.compress(Bitmap.CompressFormat.JPEG, 80, stream);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
	public static File saveFileFromBitmap(Bitmap bm, String fileName)
	{
		File ret= null;
		BufferedOutputStream stream= null;
		try
		{
			ret= new File(fileName);
			if (ret.exists())
			{
				ret.delete();
			}
			stream= new BufferedOutputStream(new FileOutputStream(ret));
			bm.compress(Bitmap.CompressFormat.JPEG, 80, stream);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
}
