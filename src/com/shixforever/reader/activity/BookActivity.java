package com.shixforever.reader.activity;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import com.shixforever.reader.R;
import com.shixforever.reader.data.BookFile;
import com.shixforever.reader.module.BookMark;
import com.shixforever.reader.utils.FusionField;
import com.shixforever.reader.view.PageWidget;
import db.DBManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

/**********************************************************
 * @文件名称：BookActivity.java
 * @文件作者：shixiang
 * @文件描述：翻页activity
 * @修改历史：2012-1-5创建初始版本
 **********************************************************/
public class BookActivity extends Activity implements OnSeekBarChangeListener,
		OnClickListener {
	/** Called when the activity is first created. */
	private PageWidget mPageWidget;
	Bitmap mCurPageBitmap, mNextPageBitmap;
	Canvas mCurPageCanvas, mNextPageCanvas;
	BookPageFactory pagefactory;
	private String filepath;
	private int width;
	private int hight;
	private static int begin = 0;// 记录的书籍开始位置
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private ProgressDialog mpDialog;
	private String filePath;
	private int light; // 亮度值
	private int size = 30; // 字体大小
	private static String word = "";// 记录当前页面的文字
	// catch路径
	private String filecatchpath = "/data/data/"
			+ FusionField.baseActivity.getPackageName() + "/";;
	private PopupWindow mPopupWindow, mToolpop, mToolpop1, mToolpop2,
			mToolpop3, mToolpop4;
	private View popupwindwow, toolpop, toolpop1, toolpop2, toolpop3, toolpop4;
	private SeekBar seekBar1, seekBar2, seekBar4;
	private Boolean show = false;// popwindow是否显示
	private TextView bookBtn1, bookBtn2, bookBtn3, bookBtn4;
	private int a = 0, b = 0;// 记录toolpop的位置
	private ImageButton imageBtn2, imageBtn3_1, imageBtn3_2;
	private ImageButton imageBtn4_1, imageBtn4_2;
	private Boolean isNight; // 亮度模式,白天和晚上
	private TextView markEdit4;
	int defaultSize = 0;
	// int readHeight; // 电子书显示高度
	private Context mContext = null;
	private DBManager mgr;
	private List<BookMark> bookmarks;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mgr = new DBManager(this);
		mContext = getBaseContext();
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		hight = dm.heightPixels;
		// hight = hight - (100 * width) / width;
		// width=480;
		// hight=800;
		// setContentView(mPageWidget);
		defaultSize = 30;
		// readHeight = hight - (50 * width) / 320;
		mPageWidget = new PageWidget(this, width, hight);
		// 当前页
		mCurPageBitmap = Bitmap.createBitmap(width, hight,
				Bitmap.Config.ARGB_8888);
		// 下一页
		mNextPageBitmap = Bitmap.createBitmap(width, hight,
				Bitmap.Config.ARGB_8888);
		// 画布
		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		setContentView(R.layout.read);
		RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.readlayout);
		rlayout.addView(mPageWidget);
		// 工厂
		pagefactory = new BookPageFactory(this, width, hight);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		editor = sp.edit();
		getSize();// 获取配置文件中的size大小
		// editor.putInt(bookPath + "begin", begin).commit();
		Bundle bundle = this.getIntent().getExtras();
		// bundle.getString("path");
		BookFile bookFile = new BookFile();
		bookFile = (BookFile) bundle.getSerializable("path");
		filepath = bookFile.name;
		// 读取记录点
		begin = sp.getInt(filepath + "begin", 0);

		pagefactory.setBgBitmap(BitmapFactory.decodeResource(
				this.getResources(), R.drawable.bg));

		try {
			if (bookFile.flag.equals("1")) {
				pagefactory.openbook(bookFile.path, begin);
			} else {
				pagefactory.openbook(filecatchpath + "catch.txt", begin);
			}
			pagefactory.setM_fontSize(size);
			pagefactory.onDraw(mCurPageCanvas);
		} catch (IOException e1) {
			e1.printStackTrace();
			Toast.makeText(this, "no find file", Toast.LENGTH_SHORT).show();
		}

		pagefactory.setBgBitmap(BitmapFactory.decodeResource(
				this.getResources(), R.drawable.bg));

		mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);

		mPageWidget.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {

				boolean ret = false;
				if (v == mPageWidget) {
					if (e.getAction() == MotionEvent.ACTION_DOWN) {
						if (e.getY() > hight) {// 超出范围了，表示单击到广告条，则不做翻页
							return false;
						}
						mPageWidget.abortAnimation();
						mPageWidget.calcCornerXY(e.getX(), e.getY());
						pagefactory.onDraw(mCurPageCanvas);
						if (mPageWidget.DragToRight()) {
							try {
								pagefactory.prePage();
								begin = pagefactory.getM_mbBufBegin();// 获取当前阅读位置
								word = pagefactory.getFirstLineText();// 获取当前阅读位置的首行文字
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							if (pagefactory.isfirstPage()) {
								Toast.makeText(mContext, "当前是第一页",
										Toast.LENGTH_SHORT).show();
								return false;
							}
							pagefactory.onDraw(mNextPageCanvas);
						} else {
							try {
								pagefactory.nextPage();
								begin = pagefactory.getM_mbBufBegin();// 获取当前阅读位置
								word = pagefactory.getFirstLineText();// 获取当前阅读位置的首行文字
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							if (pagefactory.islastPage()) {
								Toast.makeText(mContext, "已经是最后一页了",
										Toast.LENGTH_SHORT).show();
								return false;
							}
							pagefactory.onDraw(mNextPageCanvas);
						}
						mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
					}
					editor.putInt(filepath + "begin", begin).commit();
					ret = mPageWidget.doTouchEvent(e);
					return ret;
				}
				return false;
			}
		});
		setPop();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void showProgressDialog(String msg) {
		mpDialog = new ProgressDialog(BookActivity.this);
		mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
		mpDialog.setMessage(msg);
		mpDialog.setIndeterminate(false);// 设置进度条是否为不明确
		mpDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
		mpDialog.show();
	}

	public void closeProgressDialog() {
		if (mpDialog != null) {
			mpDialog.dismiss();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 保存记录点
			if (mToolpop.isShowing()) {
				popDismiss();
				return false;
			}
			if (show) {
				getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				show = false;
				mPopupWindow.dismiss();
				popDismiss();
				return false;
			}
		}
		/***
		 * menu功能由于不完善，暂不开放 2013-8-30 start again
		 */
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (show) {
				getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				show = false;
				mPopupWindow.dismiss();
				popDismiss();

			} else {

				getWindow()
						.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				show = true;

				pop();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public String getFromRaw(int fileName) {
		String res = "";
		try {
			InputStream in = getResources().openRawResource(fileName);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mgr.closeDB();
	}

	/**
	 * 读取配置文件中亮度值
	 */
	private void getLight() {
		light = sp.getInt("light", 5);
		isNight = sp.getBoolean("night", false);
	}

	/**
	 * 读取配置文件中字体大小
	 */
	private void getSize() {
		size = sp.getInt("size", defaultSize);
	}

	/**
	 * 刷新界面
	 */
	public void postInvalidateUI() {
		mPageWidget.abortAnimation();
		pagefactory.onDraw(mCurPageCanvas);
		try {
			pagefactory.currentPage();
			begin = pagefactory.getM_mbBufBegin();// 获取当前阅读位置
			word = pagefactory.getFirstLineText();// 获取当前阅读位置的首行文字
		} catch (IOException e1) {
		}

		pagefactory.onDraw(mNextPageCanvas);

		mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
		mPageWidget.postInvalidate();
	}

	/**
	 * 初始化所有POPUPWINDOW
	 */
	private void setPop() {
		popupwindwow = this.getLayoutInflater().inflate(R.layout.bookpop, null);
		toolpop = this.getLayoutInflater().inflate(R.layout.toolpop, null);
		mPopupWindow = new PopupWindow(popupwindwow, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		mToolpop = new PopupWindow(toolpop, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		toolpop1 = this.getLayoutInflater().inflate(R.layout.tool11, null);
		mToolpop1 = new PopupWindow(toolpop1, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		toolpop2 = this.getLayoutInflater().inflate(R.layout.tool22, null);
		mToolpop2 = new PopupWindow(toolpop2, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		toolpop3 = this.getLayoutInflater().inflate(R.layout.tool33, null);
		mToolpop3 = new PopupWindow(toolpop3, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		toolpop4 = this.getLayoutInflater().inflate(R.layout.tool44, null);
		mToolpop4 = new PopupWindow(toolpop4, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
	}

	/**
	 * popupwindow的弹出,工具栏
	 */
	public void pop() {

		mPopupWindow.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, 0);
		bookBtn1 = (TextView) popupwindwow.findViewById(R.id.bookBtn1);
		bookBtn2 = (TextView) popupwindwow.findViewById(R.id.bookBtn2);
		bookBtn3 = (TextView) popupwindwow.findViewById(R.id.bookBtn3);
		bookBtn4 = (TextView) popupwindwow.findViewById(R.id.bookBtn4);
		bookBtn1.setOnClickListener(this);
		bookBtn2.setOnClickListener(this);
		bookBtn3.setOnClickListener(this);
		bookBtn4.setOnClickListener(this);
	}

	/**
	 * 记录配置文件中字体大小
	 */
	// private void setSize()
	// {
	// try
	// {
	// size = seekBar1.getProgress() + 15;
	// editor.putInt("size", size);
	// editor.commit();
	// } catch (Exception e)
	// {
	// }
	// }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.seekBar1:
			pagefactory.setTextSize(seekBar1.getProgress() + 15);
			System.out.println(seekBar1.getProgress());
			pagefactory.onDraw(mCurPageCanvas);
			pagefactory.onDraw(mNextPageCanvas);
			mPageWidget.invalidate();
			editor.putInt("size", seekBar1.getProgress() + 15);
			editor.commit();
			break;
		case R.id.seekBar2:
			// // 取得当前亮度
			// int normal = Settings.System.getInt(getContentResolver(),
			// Settings.System.SCREEN_BRIGHTNESS, 255);
			// // 进度条绑定当前亮度
			// seekBar.setProgress(normal);
			// 取得当前进度
			int tmpInt = seekBar2.getProgress();
			// 当进度小于80时，设置成80，防止太黑看不见的后果。
			if (tmpInt < 80) {
				tmpInt = 80;
			}
			WindowManager.LayoutParams lp = BookActivity.this.getWindow()
					.getAttributes();
			lp.screenBrightness = Float.valueOf(tmpInt) * (1f / 255f);
			BookActivity.this.getWindow().setAttributes(lp);
			editor.putInt("light", tmpInt);
			editor.commit();
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 字体按钮
		case R.id.bookBtn1:
			a = 1;
			setToolPop(a);
			break;
		// 亮度按钮
		case R.id.bookBtn2:
			a = 2;
			setToolPop(a);
			break;
		// 书签按钮
		case R.id.bookBtn3:
			a = 3;
			setToolPop(a);
			break;
		// 跳转按钮
		case R.id.bookBtn4:
			a = 4;
			// OffersManager.getInstance(this).showOffersWallDialog(this);
			Toast.makeText(BookActivity.this, "即将完成", Toast.LENGTH_SHORT)
					.show();
			// setToolPop(a);
			break;
		case R.id.imageBtn2:
			isNight = sp.getBoolean("night", false);
			if (isNight) {
				imageBtn2.setImageResource(R.drawable.reader_switch_on);
			} else {
				imageBtn2.setImageResource(R.drawable.reader_switch_off);
			}
			break;
		case R.id.imageBtn3_1:
			BookMark mark = new BookMark();
			mark.name = filepath;
			mark.begin = begin;
			if (word.trim().equals("")) {
				mark.word = pagefactory.getSecLineText();
			} else {
				mark.word = word.trim();
			}
			mgr.addMarks(mark);
			mToolpop3.dismiss();
			mToolpop.dismiss();
			Toast.makeText(getApplication(), "添加完成", Toast.LENGTH_SHORT).show();
			break;
		case R.id.imageBtn3_2:
			bookmarks = mgr.queryMarks(filepath);
			if (bookmarks.size() <= 0) {
				Toast.makeText(BookActivity.this, "木有书签", Toast.LENGTH_SHORT)
						.show();
			} else {
				String[] items = new String[bookmarks.size()];
				for (int i = 0; i < bookmarks.size(); i++) {
					items[i] = bookmarks.get(i).word;
				}
				new AlertDialog.Builder(BookActivity.this)
						.setItems(items, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									pagefactory.nextPage();
								} catch (IOException e) {
									e.printStackTrace();
								}
								pagefactory.setM_mbBufEnd(bookmarks.get(which).begin);
								pagefactory.setM_mbBufBegin(bookmarks
										.get(which).begin);
								pagefactory.onDraw(mNextPageCanvas);
								mPageWidget.setBitmaps(mCurPageBitmap,
										mNextPageBitmap);
								mPageWidget.invalidate();
								postInvalidateUI();
								mToolpop3.dismiss();
								mToolpop.dismiss();
							}
						}).create().show();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 设置popupwindow的显示与隐藏
	 * 
	 * @param a
	 */
	public void setToolPop(int a) {
		if (a == b && a != 0) {
			if (mToolpop.isShowing()) {
				popDismiss();
			} else {
				mToolpop.showAtLocation(mPageWidget, Gravity.BOTTOM, 0,
						width * 45 / 320);
				// Font settings
				if (a == 1) {
					mToolpop1.showAtLocation(mPageWidget, Gravity.BOTTOM, 0,
							width * 45 / 320);
					seekBar1 = (SeekBar) toolpop1.findViewById(R.id.seekBar1);
					seekBar1.setMax(100);
					size = sp.getInt("size", 30);
					seekBar1.setProgress((size - 15));
					seekBar1.setOnSeekBarChangeListener(this);
				}
				// adjusting brightness
				if (a == 2) {
					mToolpop2.showAtLocation(mPageWidget, Gravity.BOTTOM, 0,
							width * 45 / 320);
					seekBar2 = (SeekBar) toolpop2.findViewById(R.id.seekBar2);
					imageBtn2 = (ImageButton) toolpop2
							.findViewById(R.id.imageBtn2);
					getLight();

					seekBar2.setProgress(light);
					if (isNight) {
						imageBtn2.setImageResource(R.drawable.reader_switch_on);
					} else {
						imageBtn2
								.setImageResource(R.drawable.reader_switch_off);
					}
					imageBtn2.setOnClickListener(this);
					seekBar2.setOnSeekBarChangeListener(this);
				}
				// Bookmarks button
				if (a == 3) {
					mToolpop3.showAtLocation(mPageWidget, Gravity.BOTTOM, 0,
							width * 45 / 320);
					imageBtn3_1 = (ImageButton) toolpop3
							.findViewById(R.id.imageBtn3_1);
					imageBtn3_2 = (ImageButton) toolpop3
							.findViewById(R.id.imageBtn3_2);
					imageBtn3_1.setOnClickListener(this);
					imageBtn3_2.setOnClickListener(this);
				}
				// 当点击跳转按钮
				if (a == 4) {
					mToolpop4.showAtLocation(mPageWidget, Gravity.BOTTOM, 0,
							width * 45 / 320);
					imageBtn4_1 = (ImageButton) toolpop4
							.findViewById(R.id.imageBtn4_1);
					imageBtn4_2 = (ImageButton) toolpop4
							.findViewById(R.id.imageBtn4_2);
					seekBar4 = (SeekBar) toolpop4.findViewById(R.id.seekBar4);
					markEdit4 = (TextView) toolpop4
							.findViewById(R.id.markEdit4);
					// begin = sp.getInt(bookPath + "begin", 1);
					float fPercent = (float) (begin * 1.0 / pagefactory
							.getM_mbBufLen());
					DecimalFormat df = new DecimalFormat("#0");
					String strPercent = df.format(fPercent * 100) + "%";
					markEdit4.setText(strPercent);
					seekBar4.setProgress(Integer.parseInt(df
							.format(fPercent * 100)));
					seekBar4.setOnSeekBarChangeListener(this);
					imageBtn4_1.setOnClickListener(this);
					imageBtn4_2.setOnClickListener(this);
				}
			}
		} else {
			if (mToolpop.isShowing()) {
				// 对数据的记录
				popDismiss();
			}
			mToolpop.showAtLocation(mPageWidget, Gravity.BOTTOM, 0,
					width * 45 / 320);
			// 点击字体按钮
			if (a == 1) {
				mToolpop1.showAtLocation(mPageWidget, Gravity.BOTTOM, 0,
						width * 45 / 320);
				seekBar1 = (SeekBar) toolpop1.findViewById(R.id.seekBar1);
				seekBar1.setMax(100);
				size = sp.getInt("size", 30);
				seekBar1.setProgress(size - 15);
				seekBar1.setOnSeekBarChangeListener(this);
			}
			// 点击亮度按钮
			if (a == 2) {
				mToolpop2.showAtLocation(mPageWidget, Gravity.BOTTOM, 0,
						width * 45 / 320);
				seekBar2 = (SeekBar) toolpop2.findViewById(R.id.seekBar2);
				seekBar2.setMax(255);
				// 取得当前亮度
				int normal = Settings.System.getInt(getContentResolver(),
						Settings.System.SCREEN_BRIGHTNESS, 255);
				// 进度条绑定当前亮度
				seekBar2.setProgress(normal);
				imageBtn2 = (ImageButton) toolpop2.findViewById(R.id.imageBtn2);
				getLight();
				seekBar2.setProgress(light);

				if (isNight) {
					pagefactory.setBgBitmap(BitmapFactory.decodeResource(
							this.getResources(), R.drawable.main_bg));
				} else {
					pagefactory.setBgBitmap(BitmapFactory.decodeResource(
							this.getResources(), R.drawable.bg));
				}

				if (isNight) {
					imageBtn2.setImageResource(R.drawable.reader_switch_on);
				} else {
					imageBtn2.setImageResource(R.drawable.reader_switch_off);
				}
				imageBtn2.setOnClickListener(this);
				seekBar2.setOnSeekBarChangeListener(this);
			}
			// 点击书签按钮
			if (a == 3) {
				mToolpop3.showAtLocation(mPageWidget, Gravity.BOTTOM, 0,
						width * 45 / 320);
				imageBtn3_1 = (ImageButton) toolpop3
						.findViewById(R.id.imageBtn3_1);
				imageBtn3_2 = (ImageButton) toolpop3
						.findViewById(R.id.imageBtn3_2);
				imageBtn3_1.setOnClickListener(this);
				imageBtn3_2.setOnClickListener(this);
			}
			// 点击跳转按钮
			if (a == 4) {
				mToolpop4.showAtLocation(mPageWidget, Gravity.BOTTOM, 0,
						width * 45 / 320);
				imageBtn4_1 = (ImageButton) toolpop4
						.findViewById(R.id.imageBtn4_1);
				imageBtn4_2 = (ImageButton) toolpop4
						.findViewById(R.id.imageBtn4_2);
				seekBar4 = (SeekBar) toolpop4.findViewById(R.id.seekBar4);
				markEdit4 = (TextView) toolpop4.findViewById(R.id.markEdit4);
				// jumpPage = sp.getInt(bookPath + "jumpPage", 1);
				float fPercent = (float) (begin * 1.0 / pagefactory
						.getM_mbBufLen());
				DecimalFormat df = new DecimalFormat("#0");
				String strPercent = df.format(fPercent * 100) + "%";
				markEdit4.setText(strPercent);
				seekBar4.setProgress(Integer.parseInt(df.format(fPercent * 100)));
				seekBar4.setOnSeekBarChangeListener(this);
				imageBtn4_1.setOnClickListener(this);
				imageBtn4_2.setOnClickListener(this);
			}
		}
		// 记录上次点击的是哪一个
		b = a;
	}

	/**
	 * 关闭55个弹出pop
	 */
	public void popDismiss() {
		mToolpop.dismiss();
		mToolpop1.dismiss();
		mToolpop2.dismiss();
		mToolpop3.dismiss();
		mToolpop4.dismiss();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

}