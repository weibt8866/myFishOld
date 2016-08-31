package com.noahedu.fish;

import java.io.File;
import java.io.IOException;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;

public class BGService extends Service {
	public static String filepath;
	private static MediaPlayer mediaBg = new MediaPlayer();

	public static void setResFilePath(String filepath) {
		BGService.filepath = filepath;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {

	}

	@Override
	public void onStart(Intent intent, int startid) {
		playMediaBg();
	}

	@Override
	public void onDestroy() {
		releaseMedia();
	}

	private void playMediaBg() {
		if (filepath == null)
			return;
		if (mediaBg == null) {
			mediaBg = new MediaPlayer();
		}
		File f = new File(filepath);
		if (f != null && f.exists() && mediaBg != null) {
			try {
				mediaBg.reset();
				mediaBg.setDataSource(filepath);
				mediaBg.prepare();
				mediaBg.start();
				mediaBg.setVolume((float) 1.0, (float) 1.0);
				mediaBg.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						playMediaBg();
					}
				});
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void setBgVolume(float leftV, float rightV) {
		if (mediaBg != null) {
			mediaBg.setVolume(leftV, rightV);
		}
	}

	private void releaseMedia() {
		if (mediaBg != null) {
			if (mediaBg.isPlaying()) {
				mediaBg.stop();
			}
			mediaBg.reset();
			mediaBg.release();
			mediaBg = null;
		}
	}
}
