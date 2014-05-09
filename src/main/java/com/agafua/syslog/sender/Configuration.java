package com.agafua.syslog.sender;

import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;

import com.agafua.syslog.SyslogHandler;
import com.agafua.syslog.util.SysInfo;
import com.agafua.syslog.util.Validator;

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
  private Integer port = DEFAULT_PORT;
  private SyslogRfc syslogRfc;
  private String processId;
  private String applicationId = "-";
  private String localHostName;
  private String remoteHostName = LOCALHOST;
  private Integer maxMessageSize = DEFAULT_MAX_MESSAGE_SIZE;
  private Transport transport = Transport.UDP;
  private Facility facility = Facility.USER;
  private Formatter formatter = new SimpleFormatter();


  /**
   * @param logManager TODO
   * 
   */
  private Configuration() {
    localHostName = SysInfo.getInstance().determineLocalHostName();    
  }
  
  /**
   * @param logManager TODO
   * @param handlerClass TODO
   * 
   */
  public Configuration(LogManager logManager, Class<SyslogHandler> handlerClass) {
    setApplicationId( parseApplicationId(logManager, handlerClass) );
    setFacility( parseFacility(logManager, handlerClass) );
    setLocalHostName( parseLocalHostName(logManager, handlerClass) );
    setMaxMessageSize( parseMaxMessageSize(logManager, handlerClass) );
    setPort( parsePort(logManager, handlerClass) );
    setProcessId( SysInfo.getInstance().getProcessId() );
    setRemoteHostName( parseRemoteHostName(logManager, handlerClass) );
    setSyslogRfc( SyslogRfc.RFC5424 );
    setTransport( parseTransport(logManager, handlerClass) );
    
    formatter = parseFormatter(logManager, handlerClass);
  }


  public Facility getFacility() {
    return facility;
  }


  public void setFacility(Facility facility) {
    this.facility = facility;
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


  public SyslogRfc getSyslogRfc() {
    return syslogRfc;
  }


  public void setSyslogRfc(SyslogRfc syslogVersion) {
    this.syslogRfc = syslogVersion;
  }


  public String getProcessId() {
    return processId;
  }


  public void setProcessId(String processId) {
    this.processId = processId;
  }


  public String getApplicationId() {
    return applicationId;
  }


  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }


  public String getLocalHostName() {
    return localHostName;
  }


  public void setLocalHostName(String localHostName) {
    if ( localHostName != null 
         && localHostName.length() > 0 ) {
      this.localHostName  = Validator.replaceNonUsAsciiAndTrim( localHostName, 255 );
    }
  }


  public String getRemoteHostName() {
    return remoteHostName;
  }


  public void setRemoteHostName(String remoteHostName) {
    this.remoteHostName = remoteHostName;
  }


  public Integer getMaxMessageSize() {
    return maxMessageSize;
  }


  public void setMaxMessageSize(int maxMessageSize) {
    this.maxMessageSize = maxMessageSize;
  }


  public Transport getTransport() {
    return transport;
  }


  public void setTransport(Transport transport) {
    this.transport = transport;
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
   * @see #formatter
   */
  public final Formatter getFormatter() {
    return formatter;
  }


  private Formatter parseFormatter(LogManager logManager, Class<SyslogHandler> handlerClass) throws IllegalArgumentException {
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


  // TODO: handle null
  private Transport parseTransport(LogManager logManager, Class<SyslogHandler> handlerClass) {
    String transportValue = logManager.getProperty( handlerClass.getName() + "." + TRANSPORT_PROPERTY );
    return Transport.valueOf(transportValue);
  }


  private String parseApplicationId(LogManager logManager, Class<SyslogHandler> handlerClass) {
    String appIdValue = logManager.getProperty( handlerClass.getName() + "." + APPLICATION_ID );
    if ( appIdValue != null && appIdValue.length() > 0 ) {
      // The RFC allows max. 48 chars for the app id, so cut it on demand
      appIdValue = Validator.replaceNonUsAsciiAndTrim( appIdValue, 48 );
      return appIdValue;
    }

    return "-";
  }


  private String parseRemoteHostName(LogManager logManager, Class<SyslogHandler> handlerClass) {
    String hostNameValue = logManager.getProperty( handlerClass.getName() + "." + REMOTE_HOSTNAME_PROPERTY );
    if ( hostNameValue != null && hostNameValue.length() > 0 ) {
      hostNameValue = Validator.replaceNonUsAsciiAndTrim( hostNameValue, 255 );
      return hostNameValue;
    }
    return LOCALHOST;
  }


  private int parseMaxMessageSize(LogManager logManager, Class<SyslogHandler> handlerClass) {
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


  // TODO: FIX THE PROPERTY NAME!
  private static String parseLocalHostName(LogManager logManager, Class<SyslogHandler> handlerClass) {
    return logManager.getProperty( handlerClass.getName() + "." + FACILITY_PROPERTY );
  }


  private int parsePort(LogManager logManager, Class<SyslogHandler> handlerClass) {
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


  // TODO: handle null
  private Facility parseFacility(LogManager logManager, Class<SyslogHandler> handlerClass) {
    String facilityValue = logManager.getProperty( handlerClass.getName() + "." + FACILITY_PROPERTY );
    return Facility.valueOf(facilityValue);
  }
}