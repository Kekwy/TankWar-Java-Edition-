package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NewTankAction extends NewObjectAction {

	/**
	 * 0 - playerTank<br/>
	 * 1 - enemyTank<br/>
	 */
	public int typeCode;
	public int direction;
	public String name;
	public int group;
	public double r, g, b;

	public NewTankAction(String identity, String className, int typeCode, double x, double y,
	                     int direction, String name, int group, double r, double g, double b) {
		super(identity, className, x, y);
		this.typeCode = typeCode;
		this.direction = direction;
		this.name = name;
		this.group = group;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public NewTankAction(SocketChannel channel, ByteBuffer buffer) {
		super(channel, buffer);
		try {
			typeCode = ChannelIOUtil.readInt(channel, buffer);
			direction = ChannelIOUtil.readInt(channel, buffer);
			name = ChannelIOUtil.readString(channel, buffer);
			group = ChannelIOUtil.readInt(channel, buffer);
			r = ChannelIOUtil.readDouble(channel, buffer);
			g = ChannelIOUtil.readDouble(channel, buffer);
			b = ChannelIOUtil.readDouble(channel, buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
//		identity
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		super.send(NEW_Tank_CODE, buffer);
		try {
			buffer.putInt(typeCode);
			buffer.putInt(direction);

			ChannelIOUtil.writeString(name, buffer);

			buffer.putInt(group);
			buffer.putDouble(r);
			buffer.putDouble(g);
			buffer.putDouble(b);

			buffer.flip();
			channel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
