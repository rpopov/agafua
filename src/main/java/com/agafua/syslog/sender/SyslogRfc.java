package com.agafua.syslog.sender;

import java.util.logging.LogRecord;

import com.agafua.syslog.utilslog.AdaptorRFC3164;
import com.agafua.syslog.utilslog.AdaptorRFC5424;

public enum SyslogRfc {

	RFC3164 {
    public AbstractAdaptor constructAdaptor(LogRecord record) {
      return new AdaptorRFC3164( record );
    }

    public Message constructMessage(AbstractAdaptor a) {
      return new MessageRFC3164( a );
    }	  
	},
	RFC5424 {
    public AbstractAdaptor constructAdaptor(LogRecord record) {
      return new AdaptorRFC5424( record );
    }
    
    public Message constructMessage(AbstractAdaptor a) {
      return new MessageRFC5424( a );
    }   
	};
	
	/**
	 * Factory method for log record adapters
	 * @param record
	 * @return non-null
	 */
	public abstract AbstractAdaptor constructAdaptor(LogRecord record);

  /**
   * Factory method for syslog messages
   * @param record
   * @return non-null
   */
  public abstract Message constructMessage(AbstractAdaptor a);
}
