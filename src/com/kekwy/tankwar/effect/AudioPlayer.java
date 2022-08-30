package com.kekwy.tankwar.effect;

import javafx.scene.media.AudioClip;

public class AudioPlayer extends Thread {
	AudioClip audioClip;

	public AudioPlayer(String s) {
		audioClip = new AudioClip(s);
	}



}
