package org.wololo.viper2;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

public class ViperSound {
	AudioManager audioManager;
	SoundPool soundPool = null;

	static int SOUND_BACKGROUND = 0;
	static int SOUND_BOUNCE = 1;
	static int SOUND_DOH1 = 2;
	static int SOUND_DOH2 = 3;
	static int SOUND_DOH3 = 4;
	static int SOUND_DOH4 = 5;
	static int SOUND_DOH5 = 6;
	static int SOUND_DOH6 = 7;
	static int SOUND_GAMEOVER = 8;
	static int SOUND_LAUGH = 9;
	static int SOUND_LOAD = 10;
	static int SOUND_START = 11;
	static int SOUND_THREAD = 12;
	static int SOUND_WOHOO = 13;
	SparseIntArray soundPoolMap = new SparseIntArray(14);
	
	void play(int id) {
		float streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = streamVolume / streamMaxVolume;
		soundPool.play(soundPoolMap.get(id), volume, volume, 0, 0, 1);
	}
	
	int randomDoh() {
		int nr = (int) (Math.random() * 5);
		int id = ViperSound.SOUND_DOH1;

		switch (nr) {
		case 0:
			id = ViperSound.SOUND_DOH1;
			break;
		case 1:
			id = ViperSound.SOUND_DOH2;
			break;
		case 2:
			id = ViperSound.SOUND_DOH3;
			break;
		case 3:
			id = ViperSound.SOUND_DOH4;
			break;
		case 4:
			id = ViperSound.SOUND_DOH5;
			break;
		case 5:
			id = ViperSound.SOUND_DOH6;
			break;
		}
		
		return id;
	}
	
	ViperSound(Context context) {
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 1);
		soundPoolMap.put(SOUND_BOUNCE, soundPool.load(context, R.raw.bounce, 1));
		soundPoolMap.put(SOUND_DOH1, soundPool.load(context, R.raw.doh1, 1));
		soundPoolMap.put(SOUND_DOH2, soundPool.load(context, R.raw.doh2, 1));
		soundPoolMap.put(SOUND_DOH3, soundPool.load(context, R.raw.doh3, 1));
		soundPoolMap.put(SOUND_DOH4, soundPool.load(context, R.raw.doh4, 1));
		soundPoolMap.put(SOUND_DOH5, soundPool.load(context, R.raw.doh5, 1));
		soundPoolMap.put(SOUND_DOH6, soundPool.load(context, R.raw.doh6, 1));
		soundPoolMap.put(SOUND_GAMEOVER, soundPool.load(context, R.raw.gameover, 1));
		soundPoolMap.put(SOUND_LAUGH, soundPool.load(context, R.raw.laugh, 1));
		soundPoolMap.put(SOUND_LOAD, soundPool.load(context, R.raw.load, 1));
		soundPoolMap.put(SOUND_START, soundPool.load(context, R.raw.start, 1));
		soundPoolMap.put(SOUND_THREAD, soundPool.load(context, R.raw.thread, 1));
		soundPoolMap.put(SOUND_WOHOO, soundPool.load(context, R.raw.wohoo, 1));
	}
}
