package com.kekwy.tankwar.effect;
import javafx.scene.media.AudioClip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Player {

	private final List<AudioClip> clips = new ArrayList<>();
	int currentClip;
	boolean active = true;
	boolean playing = false;
	Thread playThread = new Thread(this::playCycle);

	public void load(List<AudioClip> clips) {
		this.clips.clear();
		this.clips.addAll(clips);
		currentClip = 0;
	}

	public void play() {
		playing = true;
		if (!playThread.isAlive()) {
			playThread.start();
		} else {
			synchronized (clips) {
				clips.notify();
			}
		}
	}

	public void stop() {
		playing = false;
		clips.get(currentClip).stop();
	}

	public void destroy() {
		if(!clips.isEmpty()){
			stop();
		}
		active = false;
		while(playThread.isAlive()) {
			synchronized (clips) {
				clips.notify();
			}
		}
	}

	@SuppressWarnings("BusyWait")
	private void playCycle() {
		AudioClip audioClip;
		while (active) {
			while (playing) {
				audioClip = clips.get(currentClip);
				audioClip.play(0.5);
				while (audioClip.isPlaying()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
				currentClip = (currentClip + 1) % clips.size();
			}
			synchronized (clips) {
				try {
					clips.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

}
