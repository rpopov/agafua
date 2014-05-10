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

    public void fillIn(Configuration config, Message message) {
      // TODO: IMPLEMENT
    }	  
	},
	RFC5424 {
    public AbstractAdaptor constructAdaptor(LogRecord record) {
      return new AdaptorRFC5424( record );
    }
    
    public Message constructMessage(AbstractAdaptor a) {
      return new MessageRFC5424( a );
    }

    public void fillIn(Configuration config, Message message) {
      String pri = calculatePriority(message, config);

      message.print(pri); // ABNF RFC5424: PRI
      message.print("2"); // TODO ABNF RFC5424: VERSION
      message.print(" "); // ABNF RFC5424: SP
      
      message.print(message.getTimestamp()); // ABNF RFC5424: TIMESTAMP
      
      message.print(" "); // ABNF RFC5424: SP
      
      message.print(config.getLocalHostName()); // ABNF RFC5424: HOSTNAME
      
      message.print(" "); // ABNF RFC5424: SP
      
      message.print(config.getApplicationId()); // ABNF RFC5424: APP-NAME
      
      message.print(" "); // ABNF RFC5424: SP
      
      message.print(config.getProcessId()); // ABNF RFC5424: PROCID
      
      message.print(" "); // ABNF RFC5424: SP
      
      message.print(message.getMessageId()); // ABNF RFC5424: MSGID
      
      message.print(" "); // ABNF RFC5424: SP
      
      message.print(message.getMessage());
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

  /**
   * Fill in the message the details from the configration, as of the implemented RFC
   * @param config
   * @param message
   */
  public abstract void fillIn(Configuration config, Message message);

  protected String calculatePriority(Message message, Configuration config) {
  	int code = (config.getFacility().getId() << 3) + message.getSeverity().getLevel();
  	return String.format("<%d>", code);
  
  }
}
