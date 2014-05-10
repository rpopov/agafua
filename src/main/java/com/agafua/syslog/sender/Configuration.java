package com.agafua.syslog.sender;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * All configuration parameter for the Syslog connectivity.
 * Call its set* to fill it in before use.
 */
public class Configuration {

  // Default recommendations:
  private static final String LOCALHOST = "localhost";
  private static final String DEFAULT_HOST_NAME = "0.0.0.0";
  private static final int DEFAULT_PORT = 514;
  private static final int MIN_PORT = 0;
  private static final int MAX_PORT = 65535;
  private static final int DEFAULT_MAX_MESSAGE_SIZE = 1024;

  /**
   * Not null 
   */
  private static final String processId = initProcessId();
  
  /**
   * Not null 
   */
  private static final String localHostName = determineLocalHostName();
  
  private int maxMessageSize = DEFAULT_MAX_MESSAGE_SIZE;
  private int port = DEFAULT_PORT;

  /**
   * Not null 
   */
  private SyslogRfc syslogRfc = SyslogRfc.RFC5424;
  
  
  /**
   * Not null 
   */
  private String applicationId = "-";
  
  /**
   * Not null 
   */
  private String remoteHostName = LOCALHOST;

  /**
   * Not null 
   */
  private Transport transport = Transport.UDP;
  
  /**
   * Not null 
   */
  private Facility facility = Facility.USER;
  
  /**
   * Not null 
   */
  private Formatter formatter = new SimpleFormatter();


  /**
   * @param blockingQueue not null
   * @return not null thread to send the queue's content
   */
  public Thread constructWorkerThread(BlockingQueue<Message> blockingQueue) {
    return new Thread( getTransport().constructSender( this, blockingQueue ));
  }

  /**
   * Construct a message according to all settings held in this configuration,
   * ready to be sent to syslog  
   * @param record 
   * @param messageId the non-null ID of the message
   * @return a non-null message to send in syslog
   */
  public Message constructMessage(LogRecord record, String messageId) {
    Message message;
    
    message = getSyslogRfc().constructMessage( this, record, messageId );
    
    return message;
  }

  public Facility getFacility() {
    return facility;
  }


  /**
   * @return not null 
   */
  public void setFacility(Facility facility) {
    if ( facility != null ) {
      this.facility = facility;
    }
  }


  public Integer getPort() {
    return port;
  }


  public void setPort(int port) {
    if ( port >= MIN_PORT && port < MAX_PORT ) {
      this.port = port;
    }
  }


  /**
   * @return not null 
   */
  public SyslogRfc getSyslogRfc() {
    return syslogRfc;
  }


  public void setSyslogRfc(SyslogRfc syslogVersion) {
    if (syslogVersion != null) {
      this.syslogRfc = syslogVersion;
    }
  }

  /**
   * @return not null 
   */
  public String getApplicationId() {
    return applicationId;
  }


  public void setApplicationId(String applicationId) {
    if ( applicationId != null && !applicationId.trim().isEmpty() ) {
      // The RFC allows max. 48 chars for the app id, so cut it on demand
      this.applicationId = replaceNonUsAsciiAndTrim(applicationId, 48);
    }
  }


  /**
   * @return not null 
   */
  public String getLocalHostName() {
    return localHostName;
  }

  /**
   * @return not null 
   */
  public String getRemoteHostName() {
    return remoteHostName;
  }


  public void setRemoteHostName(String remoteHostName) {
    if ( remoteHostName != null && !remoteHostName.trim().isEmpty() ) {
      this.remoteHostName = replaceNonUsAsciiAndTrim(remoteHostName, 255);
    }
  }


  public int getMaxMessageSize() {
    return maxMessageSize;
  }


  public void setMaxMessageSize(int maxMessageSize) {
    if ( maxMessageSize > 0 && maxMessageSize <= 0xFFFF ) {
      this.maxMessageSize = maxMessageSize;
    }
  }


  /**
   * @return not null 
   */
  public Transport getTransport() {
    return transport;
  }


  public void setTransport(Transport transport) {
    if ( transport != null ) {
      this.transport = transport;
    }
  }


  /**
   * @see #formatter
   */
  public final void setFormatter(Formatter formatter) {
    if ( formatter != null ) {
      this.formatter = formatter;
    }
  }


  /**
   * @return not null 
   */
  public final Formatter getFormatter() {
    return formatter;
  }


  /**
   * @return not null 
   */
  public String getProcessId() {
  	return processId;
  }

  /** 
   * Only ASCII-7 chars are allowed. So replace all others:
   */
  private static String replaceNonUsAsciiAndTrim(String s, int maxLength) {
    s = s.substring(0, Math.min(s.length(), maxLength));

    return s.replaceAll("[\\x80-\\xFF]", ".");
  }
  
  
  /**
   * Static initializer, called only once to identify the current host name assuming this is a slow operation. 
   * @return
   */
  private static String determineLocalHostName() {
    String result;
    
    // Try to determine localhost by InetAddress
    try {
      result = java.net.InetAddress.getLocalHost().getHostName();
      
    } catch (Exception e) {
      System.err.print("Localhost lookup failed with: ");
      e.printStackTrace();
      System.err.println("Trying to use the host's address ");

      // Try to determine localhost by IPv4 InetAddress
      try {
        result = java.net.Inet4Address.getLocalHost().getHostAddress();
        
      } catch (Exception ex) {
        result = DEFAULT_HOST_NAME;
        
        System.err.print("Use default ("+DEFAULT_HOST_NAME+") local host name, because of: ");
        ex.printStackTrace();
      }
    }
    return result;
  }


  /**
   * Static initializer, called only once to identify the current process ID assuming this is a slow operation. 
   * @return
   */
  private static String initProcessId() {
    String processId;
    RuntimeMXBean bean;
    String jvmName;
    String pid;
    
    processId = "-";
    try {
      // This might not work on any operating system.
      // We return "-" of not successful.
      bean = ManagementFactory.getRuntimeMXBean();

      jvmName = bean.getName();
      pid = jvmName.split( "@" )[ 0 ];

      if ( pid != null && !pid.isEmpty() ) {
        processId = pid;
      }
    } catch (Exception ex) {
      System.err.print("Use default (-) process ID, because of: ");
      ex.printStackTrace();
    }
    return processId;
  }  
}