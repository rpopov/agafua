package com.agafua.syslog.sender;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;

import com.agafua.syslog.SyslogHandler;
import com.agafua.syslog.util.SysInfo;

/**
 * All configuration parameter for the Syslog connectivity.
 */
public class Configuration {

  // Default recommendations:
  public static final String VERSION = "02";
  public static final String LOCALHOST = "localhost";
  public static final String MY_HOST_NAME = "0.0.0.0";
  public static final int DEFAULT_PORT = 514;
  public static final int MIN_PORT = 0;
  public static final int MAX_PORT = 65535;
  public static final int DEFAULT_MAX_MESSAGE_SIZE = 1024;
  public static final SyslogRfc SYSLOG_VERSION = SyslogRfc.RFC5424;
  public static final Facility FACILITY = Facility.LOCAL7;

  // Property names:
  public static final String TRANSPORT_PROPERTY = "transport";
  public static final String LOCAL_HOSTNAME_PROPERTY = "localHostname";
  public static final String REMOTE_HOSTNAME_PROPERTY = "remoteHostname";
  public static final String PORT_PROPERTY = "port";
  public static final String FACILITY_PROPERTY = "facility";
  public static final String APPLICATION_ID = "applicationId";
  public static final String MAX_MESSAGE_SIZE_PROPERTY = "maxMsgSize";
  public static final String FORMATTER = "formatter";

  // Configuration values:
  /**
   * Not null 
   */
  private final String processId;
  private int maxMessageSize = DEFAULT_MAX_MESSAGE_SIZE;
  private int port = DEFAULT_PORT;

  /**
   * @return not null 
   */
  private SyslogRfc syslogRfc = SyslogRfc.RFC5424;
  
  
  /**
   * Not null 
   */
  private String applicationId = "-";
  
  /**
   * Not null 
   */
  private String localHostName;
  
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
   * Create an empty configuration that must be filled in using its set*() methods
   */
  public Configuration() {
    localHostName = SysInfo.getInstance().determineLocalHostName();
    processId = SysInfo.getInstance().getProcessId();
  }
  
  /**
   * @param logManager TODO
   * @param handlerClass TODO
   * 
   */
  public Configuration(LogManager logManager, Class<? extends Handler> handlerClass) {
    this();
    setApplicationId( parseApplicationId(logManager, handlerClass) );
    setFacility( parseFacility(logManager, handlerClass) );
    setLocalHostName( parseLocalHostName(logManager, handlerClass) );
    setMaxMessageSize( parseMaxMessageSize(logManager, handlerClass) );
    setPort( parsePort(logManager, handlerClass) );
    setRemoteHostName( parseRemoteHostName(logManager, handlerClass) );
    setTransport( parseTransport(logManager, handlerClass) );    
    setFormatter( parseFormatter(logManager, handlerClass) );
  }


  /**
   * @param blockingQueue not null
   * @return not null thread to send the queue's content
   */
  public Thread constructWorkerThread(BlockingQueue<Message> blockingQueue) {
    return new Thread( getTransport().constructSender( this, blockingQueue ));
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


  public void setMaxMessageSize(Integer maxMessageSize) {
    this.maxMessageSize = maxMessageSize;
  }


  public Integer getPort() {
    return port;
  }


  public void setPort(Integer port) {
    this.port = port;
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
      this.applicationId = applicationId;
    }
  }


  /**
   * @return not null 
   */
  public String getLocalHostName() {
    return localHostName;
  }


  public void setLocalHostName(String localHostName) {
    if ( localHostName != null && !localHostName.trim().isEmpty() ) {
      this.localHostName  = replaceNonUsAsciiAndTrim(localHostName, 255);
    }
  }


  /**
   * @return not null 
   */
  public String getRemoteHostName() {
    return remoteHostName;
  }


  public void setRemoteHostName(String remoteHostName) {
    if ( remoteHostName != null && !remoteHostName.trim().isEmpty() ) {
      this.remoteHostName = remoteHostName;
    }
  }


  public Integer getMaxMessageSize() {
    return maxMessageSize;
  }


  public void setMaxMessageSize(int maxMessageSize) {
    this.maxMessageSize = maxMessageSize;
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

  private static Formatter parseFormatter(LogManager logManager, Class<? extends Handler> handlerClass) throws IllegalArgumentException {
    Formatter formatter = null;
    Class<? extends Formatter> c2;
    
    String formatterClassName = logManager.getProperty( handlerClass.getName() + "." + FORMATTER );
    if ( formatterClassName != null ) {
      try {
        c2 = Class.forName( formatterClassName ).asSubclass( Formatter.class );
        formatter = c2.newInstance();
      } catch (  ClassNotFoundException 
               | InstantiationException 
               | IllegalAccessException ex) {
        throw new IllegalArgumentException("Could not initialize java.util.logging Formatter class: "+formatterClassName, ex);
      }
    }
    return formatter;
  }


  private static Transport parseTransport(LogManager logManager, Class<? extends Handler> handlerClass) {
    Transport result = null;
    String transportValue = logManager.getProperty( handlerClass.getName() + "." + TRANSPORT_PROPERTY );
    
    if ( transportValue != null ) {
      result = Transport.valueOf(transportValue);
    }
    return result;
  }


  private String parseApplicationId(LogManager logManager, Class<? extends Handler> handlerClass) {
    String appIdValue = logManager.getProperty( handlerClass.getName() + "." + APPLICATION_ID );
    if ( appIdValue != null && appIdValue.length() > 0 ) {
      // The RFC allows max. 48 chars for the app id, so cut it on demand
      appIdValue = replaceNonUsAsciiAndTrim(appIdValue, 48);
      return appIdValue;
    }

    return "-";
  }


  private String parseRemoteHostName(LogManager logManager, Class<? extends Handler> handlerClass) {
    String hostNameValue = logManager.getProperty( handlerClass.getName() + "." + REMOTE_HOSTNAME_PROPERTY );
    if ( hostNameValue != null && hostNameValue.length() > 0 ) {
      hostNameValue = replaceNonUsAsciiAndTrim(hostNameValue, 255);
      return hostNameValue;
    }
    return LOCALHOST;
  }


  private int parseMaxMessageSize(LogManager logManager, Class<? extends Handler> handlerClass) {
    String maxMsgSize = logManager.getProperty( handlerClass.getName() + "." + MAX_MESSAGE_SIZE_PROPERTY );
    if ( maxMsgSize != null ) {
      Integer p = null;
      try {
        p = Integer.parseInt( SyslogHandler.class.getName() + "." + MAX_MESSAGE_SIZE_PROPERTY );
      } catch (NumberFormatException e) {

      }
      if ( p != null ) {
        return p;
      }
    }
    return DEFAULT_MAX_MESSAGE_SIZE;
  }


  private static String parseLocalHostName(LogManager logManager, Class<? extends Handler> handlerClass) {
    return logManager.getProperty( handlerClass.getName() + "." + LOCAL_HOSTNAME_PROPERTY );
  }


  private int parsePort(LogManager logManager, Class<? extends Handler> handlerClass) {
    String portValue = logManager.getProperty( handlerClass.getName() + "." + PORT_PROPERTY );
    if ( portValue != null ) {
      Integer p = null;
      try {
        p = Integer.parseInt( portValue );
      } catch (NumberFormatException e) {

      }
      if ( p != null && p >= MIN_PORT && p < MAX_PORT ) {
        return p;
      }
    }
    return DEFAULT_PORT;
  }


  private static Facility parseFacility(LogManager logManager, Class<? extends Handler> handlerClass) {
    Facility result = null;
    String facilityValue = logManager.getProperty( handlerClass.getName() + "." + FACILITY_PROPERTY );
    
    if ( facilityValue != null ) {
      result = Facility.valueOf(facilityValue);
    } 
    return result;
  }

  private static String replaceNonUsAsciiAndTrim(String s, int maxLength) {
    s = s.substring(0, Math.min(s.length(), maxLength));

    // Only ASCII-7 chars are allowed. So replace all others:
    return s.replaceAll("[\\x80-\\xFF]", ".");
  }
}