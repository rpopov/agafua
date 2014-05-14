package com.agafua.syslog.sender;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.ifao.pci.logging.syslog.SyslogConfiguration;

abstract class AbstractMessage implements Message {

  private final LogRecord logRecord;
  
  private static final byte NON_ASCII_SYMBOL = (byte) '.';
  private static final byte LF_SYMBOL = (byte) ' ';
  
  private final byte[] value;
  private int pos = 0;
  private String messageId;
  private String message;

	public AbstractMessage(SyslogConfiguration configuration, LogRecord record, String messageId) {
	  this.logRecord = record;
  	
  	this.value = new byte[ configuration.getMaxMessageSize() ];
  	
  	this.message = configuration.getFormatter().format(record);
  	this.messageId = messageId;
  }

  public int getLength() {
  	return pos;
  }

  public byte[] getBytes() {
  	return value;
  }

  protected String getMessage() {
    return message;
  }

  protected void setMessage(String message) {
    this.message = message;
  }

  protected String getMessageId() {
    return messageId;
  }

  protected void setMessageId(String messageId) {
    this.messageId = messageId;
  }
		
  protected Severity getSeverity() {
    Severity result; 
    Level level = logRecord.getLevel();
    
    if (level.intValue() >= Level.SEVERE.intValue()) {
      result = Severity.ERROR;
    } else if (level.intValue() >= Level.WARNING.intValue()) {
      result = Severity.WARNING;
    } else if (level.intValue() >= Level.INFO.intValue()) {
      result = Severity.INFO;
    } else {
      result = Severity.DEBUG;
    }
    return result;
  }


  protected final LogRecord getLogRecord() {
    return logRecord;
  }

  protected void print(String s) {
    char c;
    boolean lineSeparator;
    
    lineSeparator = false;
  	for (int i = 0; i < s.length() && pos < value.length; i++) {
  		c = s.charAt(i);
  		
  		if (c >= 32 && c <= 126) {
  			value[pos++] = (byte) c;
  			lineSeparator = false;
  		} else if (c == 10 || c == 13) {
  		  if ( !lineSeparator ) {
          value[pos++] = LF_SYMBOL;  		    
  		  }
  		  lineSeparator = true;
  		} else {
  			value[pos++] = NON_ASCII_SYMBOL;
        lineSeparator = false;
  		}
  	}
  }

  /**
   * Print only alpha-numeric characters up to max number 
   * @param s
   * @param max
   */
  protected void printAlphaNum(String s, final int max) {
    char c;
    final int start = pos;
    
    for (int i = 0; i < s.length() && pos < value.length && pos < start + max; i++) {
      c = s.charAt(i);
      
      if (    c>='a' && c<='z'
           || c>='A' && c<='Z'
           || c>='0' && c<='9') {
        value[pos++] = (byte) c;
      }
    }
  }
  
  
  protected static String indent(String s, int requiredLength, char identChar) {
    while (s.length() < requiredLength) {
      s = identChar + s;
    }
    return s;
  }

  protected static int limit(int value, int min, int max) {
    if (value < min) {
      return min;
    }
    if (value > max) {
      return max;
    }
    return value;
  }
  
  
  protected String calculatePriority(SyslogConfiguration config) {
    int code = (config.getFacility().getId() << 3) + getSeverity().getLevel();
    return String.format("<%d>", code);
  
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
  	return new String(value,0, pos);
  }
}