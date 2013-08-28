package com.shixforever.reader.activity;
import com.shixforever.reader.R;
import com.shixforever.reader.utils.FusionField;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
/**********************************************************
 * @文件名称：BaseActivity.java
 * @文件作者：shixiang
 * @文件描述：baseactivity
 * @修改历史：2012-1-5创建初始版本
 **********************************************************/
public class BaseActivity extends Activity
{	
	protected TextView tvHead;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		FusionField.baseActivity = this;
	}
	protected TextView getHeadTextView(){
		if(tvHead==null){
			tvHead = (TextView) findViewById(R.id.tv_head);
		}
		return tvHead;
	}
}
