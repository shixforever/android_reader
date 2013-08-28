package com.shixforever.reader.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;

/**********************************************************
 * @文件名称：UnzipAssets.java
 * @文件作者：shixiang
 * @文件描述：zip工具方法
 * @修改历史：2012-1创建初始版本
 **********************************************************/
public class UnzipAssets {
	/**
	 * 解压assets下zip到sd卡中
	 * @param context
	 * @param assetName
	 * @param outputDirectory
	 * @throws IOException
	 */
	public static void unZip(Context context, String assetName,
			String outputDirectory) throws IOException {
		File file = new File(outputDirectory);
		if (!file.exists()) {
			file.mkdirs();
		}
		InputStream inputStream = null;
		inputStream = context.getAssets().open(assetName);
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		byte[] buffer = new byte[1024 * 1024];
		int count = 0;
		while (zipEntry != null) {
			if (zipEntry.isDirectory()) {
				// String name = zipEntry.getName();
				// name = name.substring(0, name.length() - 1);
				file = new File(outputDirectory + File.separator
						+ zipEntry.getName());
				file.mkdir();
			} else {
				file = new File(outputDirectory + File.separator
						+ zipEntry.getName());
				file.createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				while ((count = zipInputStream.read(buffer)) > 0) {
					fileOutputStream.write(buffer, 0, count);
				}
				fileOutputStream.close();
			}
			zipEntry = zipInputStream.getNextEntry();
		}
		zipInputStream.close();
	}
}