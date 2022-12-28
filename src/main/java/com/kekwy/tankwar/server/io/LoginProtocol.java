package com.kekwy.tankwar.server.io;

public class LoginProtocol extends Protocol {
	private String name;
	private String passwd;

	public LoginProtocol() {
	}

	public LoginProtocol(String name, String passwd) {
		this.name = name;
		this.passwd = passwd;
	}

	public void init(String name, String passwd) {
		this.name = name;
		this.passwd = passwd;
	}

	public String getName() {
		return name;
	}

	public String getPasswd() {
		return passwd;
	}
}
