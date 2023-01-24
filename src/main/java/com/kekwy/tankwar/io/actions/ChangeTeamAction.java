package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChangeTeamAction extends GameAction {

	/**
	 * 状态码
	 * 1 - 请求 <br/>
	 * 0 - 成功 <br/>
	 * -1 - 失败 <br/>
	 */
	public int stateCode = 1;
	public String name;
	public int team;
	public int oldTeam;

	public ChangeTeamAction(String name, int team, int oldTeam) {
		this.name = name;
		this.team = team;
		this.oldTeam = oldTeam;
	}

	public ChangeTeamAction(SocketChannel channel, ByteBuffer buffer) {
		try {
			stateCode = ChannelIOUtil.readInt(channel, buffer);
			if (stateCode != -1) {
				name = ChannelIOUtil.readString(channel, buffer);
				team = ChannelIOUtil.readInt(channel, buffer);
				oldTeam = ChannelIOUtil.readInt(channel, buffer);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		try {
			buffer.clear();
			buffer.putInt(CHANGE_TEAM_CODE);
			buffer.putInt(stateCode);
			if (stateCode != -1) {
				ChannelIOUtil.writeString(name, buffer);
				buffer.putInt(team);
				buffer.putInt(oldTeam);
			}
			buffer.flip();
			channel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
