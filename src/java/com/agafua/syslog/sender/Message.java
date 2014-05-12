package com.agafua.syslog.sender;

/**
 * This is the message object handled by the syslog connector. It is independent
 * from the logging framework.
 */
public interface Message {

	int getLength();

	byte[] getBytes();
}