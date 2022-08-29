package com.kekwy.tankwar.effect;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;


public class AudioPlayer extends Thread {


	Player player;
	String music;

	public AudioPlayer(String file) {
		this.music = file;
	}

	public void run() {
		try {
			play();
		} catch (FileNotFoundException | JavaLayerException e) {
			e.printStackTrace();
		}
	}

	public void play() throws FileNotFoundException, JavaLayerException {
		// File file = null;//new File(this.getClass().getResource("/gameBGM.wav"));
		BufferedInputStream buffer = new BufferedInputStream(Objects.requireNonNull(AudioPlayer.class.getResourceAsStream("/gameBGM.wav")));
		player = new Player(buffer);
		player.play();
	}
}
