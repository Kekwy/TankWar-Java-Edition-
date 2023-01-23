package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class LoginAction extends GameAction {

	/**
	 * -1 - 登录请求;
	 * 0 - 登录成功;
	 * 1 - 登录失败;
	 */
	public int stateCode = -1;
	public String name;
	public String passwd;
	public String userUuid;

	public LoginAction(String name, String passwd) {
		this.name = name;
		this.passwd = passwd;
	}

	public LoginAction(SocketChannel channel, ByteBuffer buffer) {
		try {
			stateCode = ChannelIOUtil.readInt(channel, buffer);
			if (stateCode == -1) {
				name = ChannelIOUtil.readString(channel, buffer);
				passwd = ChannelIOUtil.readString(channel, buffer);
			} else if (stateCode == 0) {
				userUuid = ChannelIOUtil.readString(channel, buffer);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		buffer.clear();
		buffer.putInt(LOGIN_CODE);
		buffer.putInt(stateCode);
		try {
			if (stateCode == -1) {
				ChannelIOUtil.writeString(name, buffer);
				ChannelIOUtil.writeString(passwd, buffer);
			} else if (stateCode == 0) {
				ChannelIOUtil.writeString(userUuid, buffer);
			} else if (stateCode != 1) {
				throw new RuntimeException("???");
			}
			buffer.flip();
			channel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
