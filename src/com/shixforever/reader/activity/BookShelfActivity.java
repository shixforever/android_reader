package com.shixforever.reader.activity;

import java.io.File;
import java.util.List;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.PointsChangeNotify;
import net.youmi.android.offers.PointsManager;
import net.youmi.android.smart.SmartBannerManager;
import net.youmi.android.spot.SpotDialogLinstener;
import net.youmi.android.spot.SpotManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.test.RenamingDelegatingContext;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.shixforever.reader.R;
import com.shixforever.reader.adapter.AlbumShelfAdapter;
import com.shixforever.reader.data.BookFile;
import com.shixforever.reader.manager.FileManager;
import com.shixforever.reader.utils.FusionField;
import com.shixforever.reader.utils.Tools;

import db.DBManager;

/**********************************************************
 * @文件名称：BookShelfActivity.java
 * @文件作者：shixiang
 * @文件描述：书架
 * @修改历史：2012-1-5创建初始版本
 **********************************************************/
public class BookShelfActivity extends BaseActivity implements
		PointsChangeNotify {
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
	/**
	 * 积分墙
	 */
	private TextView mTextViewPoints;
	private Button bt_jfq;
	private boolean nhFlag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		isConnected = isWifiConnect();
		AdManager.getInstance(this).init("73057fb81878f2ba",
				"f2707618edb7c2a4", false);
		// 如果使用积分广告，请务必调用积分广告的初始化接口:
		// OffersManager.getInstance(this).onAppLaunch();
		// SpotManager.getInstance(this).loadSpotAds();

		// 预加载
		initView();
		mgr = new DBManager(this);
		initData();
		mTextViewPoints = (TextView) findViewById(R.id.tv_points);
		bt_jfq = (Button) findViewById(R.id.bt_jfq);
		bt_jfq.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				OffersManager.getInstance(BookShelfActivity.this)
						.showOffersWallDialog(BookShelfActivity.this);
				// PointsManager.getInstance(BookShelfActivity.this).awardPoints(
				// 10);
			}
		});
		int pointsBalance = PointsManager.getInstance(this).queryPoints();// 查询积分余额
		mTextViewPoints.setText("积分余额:" + pointsBalance);
		// (可选)注册积分监听-随时随地获得积分的变动情况
		PointsManager.getInstance(this).registerNotify(this);
		SpotManager.getInstance(this).showSpotAds(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onRestart() {
		this.notifyDataChange();
		super.onRestart();
	}

	private void initView() {
		bookShelf = (GridView) findViewById(R.id.bookShelf);
	}

	private void notifyDataChange() {
	}

	private void initData() {
		// initialize DB
		books = mgr.queryPro();
		if (!nhFlag) {
			for (int i = 0; i < books.size(); i++) {
				if (books.get(i).flag.equals("3"))
					books.remove(i);
			}
		}
		File file = new File(FileManager.FILE_SDCARD_PATH);
		if (file.isDirectory()) {
			File[] fileArray = file.listFiles();
			if (null != fileArray && 0 != fileArray.length) {
				for (int i = 0; i < fileArray.length; i++) {
					BookFile bookFile = new BookFile();
					bookFile.path = fileArray[i].getPath();
					bookFile.name = fileArray[i]
							.getPath()
							.trim()
							.substring(
									fileArray[i].getPath().trim()
											.lastIndexOf("/") + 1);
					bookFile.flag = "1";
					books.add(bookFile);
				}

			}
		}
		albumShelfAdapter = new AlbumShelfAdapter(books, this);
		bookShelf.setAdapter(albumShelfAdapter);
		bookShelf.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BookFile albumitem = (BookFile) parent
						.getItemAtPosition(position);
				// 传递file会崩溃
				BookFile album = new BookFile();
				album.id = albumitem.id;
				album.name = albumitem.name;
				album.cover = albumitem.cover;
				album.path = albumitem.path;
				album.flag = albumitem.flag;
				System.out.println(filecatchpath);
				if (albumitem.flag.equals("0")) {
					filecatch = Tools.getFile(albumitem.file, filecatchpath,
							"catch.txt");
					if (filecatch == null) {
						System.err.println("null");
					}
				}
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("path", album);
				intent.putExtras(bundle);
				intent.setClass(BookShelfActivity.this, BookActivity.class);
				startActivity(intent);
			}
		});
		bookShelf.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(final AdapterView<?> parent,
					View view, final int position, long id) {
				AlertDialog.Builder builder = new Builder(
						BookShelfActivity.this);
				builder.setMessage("删除");
				builder.setTitle("确认删除？");
				builder.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								BookFile bookFile = (BookFile) parent
										.getItemAtPosition(position);

								if (bookFile.flag.equals("0")) {
									// 删除
									mgr.deleteBook((BookFile) parent
											.getItemAtPosition(position));
								} else {
									File file = new File(
											FileManager.FILE_SDCARD_PATH
													+ bookFile.name);
									if (file.isFile()) {
										file.delete();
									}
								}
								// 刷新
								albumShelfAdapter.change(refresh());
								Toast.makeText(BookShelfActivity.this, "删除完成",
										Toast.LENGTH_SHORT).show();
							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				builder.create().show();
				return true;
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mgr.closeDB();
		// 注销积分监听-如果在onCreate注册了，那这里必须得注销
		PointsManager.getInstance(this).unRegisterNotify(this);
	}

	/**
	 * 判断wifi是否连接
	 * 
	 * @return
	 */
	public boolean isWifiConnect() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}

	@Override
	public void onPointBalanceChange(int pointsBalance) {
		// 积分SDK是在UI线程上回调该函数的，因此可直接操作UI，但切勿进行其他的长时间操作
		mTextViewPoints.setText("积分余额:" + pointsBalance);
		if (pointsBalance > 100) {
			AlertDialog.Builder builder = new Builder(BookShelfActivity.this);
			builder.setMessage("surprise");
			builder.setTitle("surprise~内涵模式开启!!!!");
			builder.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 开启隐藏
							nhFlag = true;
						}
					});
			builder.create().show();
		}
	}

	private List<BookFile> refresh() {
		List<BookFile> booksdata = mgr.queryPro();
		if (!nhFlag) {
			for (int i = 0; i < booksdata.size(); i++) {
				if (booksdata.get(i).flag.equals("3"))
					booksdata.remove(i);
			}
		}
		File file = new File(FileManager.FILE_SDCARD_PATH);
		if (file.isDirectory()) {
			File[] fileArray = file.listFiles();
			if (null != fileArray && 0 != fileArray.length) {
				for (int i = 0; i < fileArray.length; i++) {
					BookFile bookFile = new BookFile();
					bookFile.path = fileArray[i].getPath();
					bookFile.name = fileArray[i]
							.getPath()
							.trim()
							.substring(
									fileArray[i].getPath().trim()
											.lastIndexOf("/") + 1);
					bookFile.flag = "1";
					booksdata.add(bookFile);
				}

			}
		}
		return booksdata;
	}
}