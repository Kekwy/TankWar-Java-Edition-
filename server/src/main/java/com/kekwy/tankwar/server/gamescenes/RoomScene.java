package com.kekwy.tankwar.server.gamescenes;

import com.kekwy.tankwar.server.GameScene;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RoomScene extends GameScene {

	private static final int SCENE_WIDTH = 1030, SCENE_HEIGHT = 840;

	public RoomScene() {
		super(SCENE_WIDTH, SCENE_HEIGHT);
	}

	private final List<List<String>> playerList = new ArrayList<>() {{
		add(new LinkedList<>());
		add(new LinkedList<>());
		add(new LinkedList<>());
		add(new LinkedList<>());
	}};

	public boolean changeTeam(String name, int team, int oldTeam) {
		synchronized (playerList) {
			List<String> list = playerList.get(team);
			if (list.size() >= 2) return false;
			else {
				playerList.get(oldTeam).remove(name);
				list.add(name);
				return true;
			}
		}
	}

	public void joinATeam(String name) {
		int team = 0, minCount = playerList.get(0).size();
		synchronized (playerList) {
			for (int i = 1; i < playerList.size(); i++) {
				if (playerList.get(i).size() < minCount) {
					team = i;
					minCount = playerList.get(i).size();
				}
			}
			if (!playerList.get(team).contains(name))
				playerList.get(team).add(name);
		}
	}

}
