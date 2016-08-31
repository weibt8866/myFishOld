package com.noahedu.fish; 

import com.count.android.api.StatWrapper;

import android.app.Activity;
import android.util.Log;

/**统计APP时间
 * @author ZhangJuna
 * @date 2014-12-5
 */
public class BaseActivity extends Activity{
	
	@Override
	protected void onResume() {
		StatWrapper.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		StatWrapper.onPause(this);
		super.onPause();
	}
}
 