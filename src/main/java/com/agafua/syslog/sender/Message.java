package com.agafua.syslog.sender;

/**
 * This is the message object handled by the syslog connector. It is independent
 * from the logging framework.
 */
public interface Message {

	void print(String s);

	int getLength();

	byte[] getBytes();

	String getTimestamp();

	String getMessageId();

	String getMessage();

	Severity getSeverity();
}