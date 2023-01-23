package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NewBulletAction extends NewObjectAction {

	public String fromIdentity;
	public double r, g, b;
	public int atk;
	public int direction;

	public NewBulletAction(String identity, double x, double y, int atk,
	                     int direction, String fromIdentity, double r, double g, double b) {
		super(identity, "className", x, y);
		this.fromIdentity = fromIdentity;
		this.r = r;
		this.g = g;
		this.b = b;
		this.atk = atk;
		this.direction = direction;
	}

	public NewBulletAction(SocketChannel channel, ByteBuffer buffer) {
		super(channel, buffer);
		try {
			fromIdentity = ChannelIOUtil.readString(channel, buffer);
			r = ChannelIOUtil.readDouble(channel, buffer);
			g = ChannelIOUtil.readDouble(channel, buffer);
			b = ChannelIOUtil.readDouble(channel, buffer);
			atk = ChannelIOUtil.readInt(channel, buffer);
			direction = ChannelIOUtil.readInt(channel, buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		super.send(NEW_BULLET_CODE, buffer);
		try {
			ChannelIOUtil.writeString(fromIdentity, buffer);

			buffer.putDouble(r);
			buffer.putDouble(g);
			buffer.putDouble(b);

			buffer.putInt(atk);
			buffer.putInt(direction);

			buffer.flip();
			channel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
