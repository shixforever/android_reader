package com.shixforever.reader.activity;

import java.io.File;
import java.util.List;
import net.youmi.android.YoumiAdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import net.youmi.android.spot.SpotManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.shixforever.reader.R;
import com.shixforever.reader.adapter.AlbumShelfAdapter;
import com.shixforever.reader.data.BookFile;
import com.shixforever.reader.utils.FusionField;
import com.shixforever.reader.utils.Tools;

import db.DBManager;

/**********************************************************
 * @文件名称：BookShelfActivity.java
 * @文件作者：shixiang
 * @文件描述：书架
 * @修改历史：2012-1-5创建初始版本
 **********************************************************/
public class BookShelfActivity extends BaseActivity
{
	private GridView bookShelf;
	private AlbumShelfAdapter albumShelfAdapter;
	private DBManager mgr;
	private List<BookFile> books;
	/**
	 * book File cache
	 */
	private File filecatch;
	private String filecatchpath = "/data/data/"
			+ FusionField.baseActivity.getPackageName() + "/";
	private boolean isConnected;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		isConnected = isWifiConnect();

//		YoumiAdManager.getInstance(this).init("37d2d108a3c1e124",
//				"8da5d0b804d89db9", false);
		LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
		AdView adView = new AdView(this, AdSize.SIZE_320x50);
		adLayout.addView(adView);
		// 预加载
		SpotManager.getInstance(this).loadSpotAds();
		SpotManager.getInstance(BookShelfActivity.this).showSpotAds(
				BookShelfActivity.this);
		initView();
		initData();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onRestart()
	{
		this.notifyDataChange();
		super.onRestart();
	}

	private void initView()
	{
		bookShelf = (GridView) findViewById(R.id.bookShelf);
	}

	private void notifyDataChange()
	{
	}

	private void initData()
	{
		// initialize DB
		mgr = new DBManager(this);
		books = mgr.queryPro();
		albumShelfAdapter = new AlbumShelfAdapter(books, this);
		bookShelf.setAdapter(albumShelfAdapter);
		bookShelf.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				BookFile album = (BookFile) parent.getItemAtPosition(position);
				System.out.println(filecatchpath);
				filecatch = Tools.getFile(album.file, filecatchpath,
						"catch.txt");
				if (filecatch == null)
				{
					System.err.println("null");
				}
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("path", album.name);
				intent.putExtras(bundle);
				intent.setClass(BookShelfActivity.this, BookActivity.class);
				startActivity(intent);
			}
		});
		bookShelf.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				return false;
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mgr.closeDB();
	}

	/**
	 * 判断wifi是否连接
	 * 
	 * @return
	 */
	public boolean isWifiConnect()
	{
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}
}