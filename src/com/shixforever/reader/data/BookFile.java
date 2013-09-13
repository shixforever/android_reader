package com.shixforever.reader.data;

import java.io.Serializable;

/**********************************************************
 * @文件名称：BookFile.java
 * @文件作者：shixiang
 * @文件描述：book对象
 * @修改历史：2012-3-1创建初始版本
 **********************************************************/
public class BookFile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/***
	 * 
	 */
	public int id;
	/***
	 * 书籍名
	 */
	public String name;
	/***
	 * 文件流
	 */
	public byte[] file;
	/***
	 * 封面
	 */
	public String cover;
	/** 
	* @Fields path : 本地路径 
	*/ 
	public String path;
	/** 
	* @Fields flag : 类别 1 为SD卡
	*/ 
	public String flag="0";
	public BookFile() {
	}

}
