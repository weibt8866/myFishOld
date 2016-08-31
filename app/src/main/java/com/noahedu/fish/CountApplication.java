package com.noahedu.fish;

import com.count.android.api.StatWrapper;
import com.noahedu.fish.tool.Utils;

import android.app.Application;
import android.util.Log;

/**
 * @author Zhangjuna
 * @date 2014-12-5
 */
public class CountApplication extends Application {
	
	@Override
	public void onCreate() {
		Utils.initDisplay(this);

		StatWrapper.init(this);
		// 是否打开log开关
		StatWrapper.setLoggingEnabled(true);
		// 打开异常统计开关
		StatWrapper.setCatchUncaughtExceptions(true);
		super.onCreate();
	}

}
