package com.agafua.syslog.sender;

public abstract class AbstractMessage implements Message {

	final Adaptor adaptor;

	public Severity getSeverity() {
		return adaptor.getSeverity();
	}

	public String getTimestamp() {
		return adaptor.getTimestamp();
	}

	public String getMessage() {
		return adaptor.getMessage();
	}

	public String getMessageId() {
		return adaptor.getMessageId();
	}

	public AbstractMessage(Adaptor adaptor) {
		this.adaptor = adaptor;
	}

}
