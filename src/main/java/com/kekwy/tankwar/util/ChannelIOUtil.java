package com.kekwy.tankwar.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChannelIOUtil {

	public static int readInt(SocketChannel channel, ByteBuffer buffer) throws IOException {
		buffer.clear();
		buffer.limit(4);
		do {
			channel.read(buffer);
		} while (buffer.position() < 4);
		buffer.flip();
		return buffer.getInt();
	}

	public static double readDouble(SocketChannel channel, ByteBuffer buffer) throws IOException {
		buffer.clear();
		buffer.limit(8);
		do {
			channel.read(buffer);
		} while (buffer.position() < 8);
		buffer.flip();
		return buffer.getDouble();
	}

	public static byte readByte(SocketChannel channel, ByteBuffer buffer) throws IOException {
		buffer.clear();
		buffer.limit(1);
		do {
			channel.read(buffer);
		} while (buffer.position() < 1);
		buffer.flip();
		return buffer.get();
	}

	public static String readString(SocketChannel channel, ByteBuffer buffer) throws IOException {
		int strLength = readInt(channel, buffer);
		buffer.clear();
		buffer.limit(strLength);
		do {
			channel.read(buffer);
		} while (buffer.position() < strLength);
		buffer.flip();
		byte[] bytes = new byte[strLength];
		buffer.get(bytes);
		return new String(bytes);
	}

	public static void writeString(String string, ByteBuffer buffer) throws IOException {
		byte[] byteValue = string.getBytes();
		buffer.putInt(byteValue.length);
		buffer.put(byteValue);
	}

}
