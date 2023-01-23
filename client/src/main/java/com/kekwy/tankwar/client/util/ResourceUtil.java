package com.kekwy.tankwar.client.util;

import java.util.Objects;

public class ResourceUtil {
	public static String getAsPath(String s) {
		return Objects.requireNonNull(ResourceUtil.class.getResource(s)).toString();
	}
}
