package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NewBulletAction extends NewObjectAction {

	public String identity;
	public String fromIdentity;
	public double x, y;
	public double r, g, b;
	public int atk;
	public int direction;

	public NewBulletAction(String identity, double x, double y, int atk,
	                     int direction, String fromIdentity, double r, double g, double b) {
		super(identity, "className");
		this.identity = identity;
		this.fromIdentity = fromIdentity;
		this.x = x;
		this.y = y;
		this.r = r;
		this.g = g;
		this.b = b;
		this.atk = atk;
		this.direction = direction;
	}

	public NewBulletAction(SocketChannel channel, ByteBuffer buffer) {
		super(channel, buffer);
		try {
			identity = ChannelIOUtil.readString(channel, buffer);
			fromIdentity = ChannelIOUtil.readString(channel, buffer);
			x = ChannelIOUtil.readDouble(channel, buffer);
			y = ChannelIOUtil.readDouble(channel, buffer);
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
		super.send(NEW_BULLET_CODE, channel, buffer);
		try {
			ChannelIOUtil.writeString(identity, buffer);
			ChannelIOUtil.writeString(fromIdentity, buffer);

			buffer.putDouble(x);
			buffer.putDouble(y);

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
