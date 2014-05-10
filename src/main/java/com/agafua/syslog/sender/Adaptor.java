package com.agafua.syslog.sender;

/**
 * The adaptor is responsible for the mapping between the logging framework and
 * the syslog dependent message.
 */
public interface Adaptor {

	Severity getSeverity();

	String getTimestamp();

	String getMessage();

	String getMessageId();
}
