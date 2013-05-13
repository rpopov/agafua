package com.agafua.syslog.sender;

public abstract class AbstractAdaptor implements Adaptor {

	private Severity severity = null;
	private String timestamp = null;
	private String messageId = null;
	private String message = null;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	protected void setSeverity(Severity severity) {
		this.severity = severity;
	}

	protected void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public AbstractAdaptor() {
		super();
	}

	public Severity getSeverity() {
		return severity;
	}

	public String getTimestamp() {
		return timestamp;
	}

}