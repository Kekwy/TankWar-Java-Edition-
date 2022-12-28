package com.kekwy.jw.tankwar.effect;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class BgmPlayer {

	private final MediaPlayer intro;
	private final MediaPlayer loop;

	public BgmPlayer(Media intro, Media loop) {
		this.intro = new MediaPlayer(intro);
		this.loop = new MediaPlayer(loop);
		this.intro.setOnEndOfMedia(() -> {
			this.loop.play();
			this.intro.stop();
		});
		this.loop.setOnEndOfMedia(() -> {
			this.loop.stop();
			this.loop.play();
		});
	}

	public void play() {
		intro.play();
	}

	public void stop() {
		this.intro.stop();
		this.loop.stop();
	}

}
