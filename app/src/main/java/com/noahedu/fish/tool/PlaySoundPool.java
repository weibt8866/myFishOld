package com.noahedu.fish.tool;

import java.util.HashMap;

import android.media.AudioManager;
import android.media.SoundPool;

public class PlaySoundPool {
	private SoundPool mSoundPool = null;
	private HashMap<Integer, Integer> mSoundMap = null;  
	
	public PlaySoundPool(int maxStreams) {
		mSoundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 5);
		mSoundMap = new HashMap<Integer, Integer>(); 
	}
	
	public void load(int[] soundIds, String[] soundPaths) {
		for(int i = 0; i < soundIds.length; i++) {
			mSoundMap.put(soundIds[i], mSoundPool.load(soundPaths[i], 1)) ;
		}
	}
	
	/**
	 * 播放短音效
	 * @param id
	 */
	public void play(int id) {
		if(null == mSoundPool) {
			return;
		}
		if(null == mSoundMap || id > mSoundMap.size()) {
			return;
		}
		try {
			mSoundPool.play(mSoundMap.get(id), 1, 1, 1, 0, 1); 
		} catch (NullPointerException e) {
		//	e.printStackTrace();
		}
	}
	
	public void release() {
		uninitSoundPool();
	}
	
	private void uninitSoundPool() {
		if(mSoundPool != null) {
			mSoundPool.release();
		}
		
		if(mSoundMap != null) {
			mSoundMap.clear();
		}
	}
}
