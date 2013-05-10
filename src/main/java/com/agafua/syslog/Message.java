package com.agafua.syslog;

public interface Message {

	void print(String s);

	int getLength();

	byte[] getBytes();
}