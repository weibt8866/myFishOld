package com.noahedu.fish.tool;

import java.io.File;
import java.io.IOException;
import android.media.MediaPlayer;
import com.noahedu.SoundPlayer.SoundPlayer;
import com.noahedu.SoundPlayer.SoundPlayerParam;

public class PlaySound {

	public static void playSound(MediaPlayer mediaPlayer, String path,
			boolean looping) {
		if (path == null) {
			return;
		}
		File f = new File(path);
		if (f != null && f.exists() && mediaPlayer != null) {
			try {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(path);
				mediaPlayer.setLooping(looping);
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void stopSound(MediaPlayer mediaPlayer) {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}
	}

	public static void releaseSound(MediaPlayer mediaPlayer) {
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	public static void playSound(SoundPlayer soundPlayer,
			SoundPlayerParam param, byte[] data) {
		if (data != null && data.length > 1 && soundPlayer != null
				&& param != null) {
			param.setBuffer(data, 0, data.length);
			try {
				soundPlayer.prepare(param);
				soundPlayer.play();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void playSound(SoundPlayer soundPlayer, byte[] data) {
		if (data != null && data.length > 1 && soundPlayer != null) {
			SoundPlayerParam param = new SoundPlayerParam();
			param.setBuffer(data, 0, data.length);
			try {
				soundPlayer.prepare(param);
				soundPlayer.play();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void playSound(SoundPlayer soundPlayer, byte[] data,
			SoundPlayerParam.onStatusListener cbFun) {
		if (data != null && data.length > 1 && soundPlayer != null) {
			SoundPlayerParam param = new SoundPlayerParam();
			param.setBuffer(data, 0, data.length);
			param.setOnStatusListener(cbFun);
			try {
				soundPlayer.prepare(param);
				soundPlayer.play();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void playSound(SoundPlayer soundPlayer, String filePath,
			SoundPlayerParam.onStatusListener cbFun) {
		File f = new File(filePath);
		if (f != null && f.exists() && soundPlayer != null) {
			SoundPlayerParam param = new SoundPlayerParam();
			param.setFilePath(filePath, 0, 0);
			param.setOnStatusListener(cbFun);
			try {
				soundPlayer.prepare(param);
				soundPlayer.play();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void playSound(SoundPlayer soundPlayer, String filePath) {
		File f = new File(filePath);
		if (f != null && f.exists() && soundPlayer != null) {
			SoundPlayerParam param = new SoundPlayerParam();
			param.setFilePath(filePath, 0, 0);
			try {
				soundPlayer.prepare(param);
				soundPlayer.play();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void stopSound(SoundPlayer soundPlayer) {
		if (soundPlayer != null) {
			try {
				soundPlayer.pause();
				soundPlayer.stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void releaseSound(SoundPlayer soundPlayer) {
		if (soundPlayer != null) {
			try {
				soundPlayer.pause();
				soundPlayer.stop();
				soundPlayer.recycle();
				soundPlayer = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
