package com.agafua.syslog.sender;

public abstract class AbstractAdaptor implements Adaptor {

	private Severity severity;
	private String timestamp;
	private String messageId;
	private String message;

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

	public Severity getSeverity() {
  	return severity;
  }

  protected void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public String getTimestamp() {
  	return timestamp;
  }

  protected void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}