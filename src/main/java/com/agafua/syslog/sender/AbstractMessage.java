package com.agafua.syslog.sender;

public abstract class AbstractMessage implements Message {

  private static final byte NON_ASCII_SYMBOL = (byte) '.';
  private static final byte LF_SYMBOL = (byte) '\\';
  
  private final Adaptor adaptor;  
  private final byte[] value;
  private int pos = 0;

	public AbstractMessage(Adaptor adaptor, int size) {
  	this.adaptor = adaptor;
  	
  	value = new byte[size];
  }

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

  public int getLength() {
  	return pos;
  }

  public byte[] getBytes() {
  	return value;
  }

  public void print(String s) {
    char c;
    
  	for (int i = 0; i < s.length() && pos < value.length; i++) {
  		c = s.charAt(i);
  		
  		if (c >= 32 && c <= 126) {
  			value[pos] = (byte) c;
  		} else if (c == 10) {
  			value[pos] = LF_SYMBOL;// LF_SYMBOL;
  		} else {
  			value[pos] = NON_ASCII_SYMBOL;
  		}
  		pos++;
  	}
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
  	return new String(value,0, pos);
  }
}
