package com.kekwy.tankwar.client.effect;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayer {
	private static final double PLAY_VOLUME = 0.3;
	private final List<MediaPlayer> clips = new ArrayList<>();
	int currentClip;
	boolean active = true;

	public void load(List<Media> medias) {
		this.clips.clear();
		for (Media media : medias) {
			MediaPlayer player = new MediaPlayer(media);
			player.setOnEndOfMedia(() -> {
				synchronized (clips) {
					if (active) {
						clips.get(currentClip).stop();
						currentClip = (currentClip + 1) % clips.size();
						clips.get(currentClip).play();
					}
				}
			});
			player.setVolume(PLAY_VOLUME);
			this.clips.add(player);
		}
		currentClip = 0;
	}

	public void play() {
		active = true;
		synchronized (clips) {
			if (active) {
				clips.get(currentClip).play();
			}
		}
	}

	public void stop() {
		active = false;
		synchronized (clips) {
			clips.get(currentClip).stop();
		}
	}
}
