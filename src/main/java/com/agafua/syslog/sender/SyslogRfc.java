package com.agafua.syslog.sender;

import java.util.logging.LogRecord;

public enum SyslogRfc {

	RFC3164 {
    public Message constructMessage(Configuration config, LogRecord record, String messageId) {
      return new MessageRFC3164( config, record, messageId );      
    }
	},
	RFC5424 {
    public Message constructMessage(Configuration config, LogRecord record, String messageId) {
      return new MessageRFC5424( config, record, messageId );
    }   
	};
	
	/**
   * Factory method for syslog messages
   * @return non-null message compatible and initialized as of the RFC
   */
  public abstract Message constructMessage(Configuration config, LogRecord record, String messageId);
}
