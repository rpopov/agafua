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
  public static final int DEFAULT_MAX_MESSAGE_SIZE = 65535;
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
  private SysInfo facts = SysInfo.getInstance();
  private Integer port;
  private SyslogRfc syslogRfc;
  private String processId;
  private String applicationId;
  private String localHostName;
  private String remoteHostName;
  private Integer maxMessageSize;
  private Transport transport;
  private Facility facility;
  private Formatter formatter;


  /**
   * 
   */
  public Configuration() {
    setApplicationId( parseApplicationId() );
    setFacility( parseFacility() );
    setLocalHostName( parseLocalHostName() );
    setMaxMessageSize( parseMaxMessageSize() );
    setPort( parsePort() );
    setProcessId( SysInfo.getInstance().getProcessId() );
    setRemoteHostName( parseRemoteHostName() );
    setSyslogRfc( SyslogRfc.RFC5424 );
    setTransport( parseTransport() );
    
    formatter = parseFormatter();
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
    this.localHostName = localHostName;
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
    this.formatter = formatter;
  }


  /**
   * @see #formatter
   */
  public final Formatter getFormatter() {
    return formatter;
  }


  private Formatter parseFormatter() {

    Formatter formatter = new SimpleFormatter();
    String formatterProperty = SyslogHandler.class.getName() + "." + FORMATTER;
    String formatterValue = LogManager.getLogManager().getProperty( formatterProperty );
    if ( formatterValue != null ) {
      try {
        Class<? extends Formatter> c2 = Class.forName( formatterValue ).asSubclass( Formatter.class );
        formatter = c2.newInstance();
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        // TODO Log ERROR
        System.err.println( "Could not initialize java.util.logging Formatter class." );
        e.printStackTrace();
      }
    }

    return formatter;
  }


  private Transport parseTransport() {
    String transportProperty = SyslogHandler.class.getName() + "." + TRANSPORT_PROPERTY;
    String transportValue = LogManager.getLogManager().getProperty( transportProperty );
    for (Transport t : Transport.values()) {
      if ( t.name().equalsIgnoreCase( transportValue ) ) {
        return t;
      }
    }
    return Transport.UDP;
  }


  private String parseApplicationId() {
    String appIdProperty = SyslogHandler.class.getName() + "." + APPLICATION_ID;
    String appIdValue = LogManager.getLogManager().getProperty( appIdProperty );
    if ( appIdValue != null && appIdValue.length() > 0 ) {
      // The RFC allows max. 48 chars for the app id, so cut it on demand
      appIdValue = Validator.replaceNonUsAsciiAndTrim( appIdValue, 48 );
      return appIdValue;
    }

    return "-";
  }


  private String parseRemoteHostName() {
    String hostNameProperty = SyslogHandler.class.getName() + "." + REMOTE_HOSTNAME_PROPERTY;
    String hostNameValue = LogManager.getLogManager().getProperty( hostNameProperty );
    if ( hostNameValue != null && hostNameValue.length() > 0 ) {
      hostNameValue = Validator.replaceNonUsAsciiAndTrim( hostNameValue, 255 );
      return hostNameValue;
    }
    return LOCALHOST;
  }


  private int parseMaxMessageSize() {
    String maxMsgSizeProperty = SyslogHandler.class.getName() + "." + MAX_MESSAGE_SIZE_PROPERTY;
    String maxMsgSize = LogManager.getLogManager().getProperty( maxMsgSizeProperty );
    if ( maxMsgSize != null ) {
      Integer p = null;
      try {
        p = Integer.parseInt( maxMsgSizeProperty );
      } catch (NumberFormatException e) {

      }
      if ( p != null ) {
        return p;
      }
    }
    return DEFAULT_MAX_MESSAGE_SIZE;
  }


  private String parseLocalHostName() {

    String localHostProperty = SyslogHandler.class.getName() + "." + FACILITY_PROPERTY;
    String localHostValue = LogManager.getLogManager().getProperty( localHostProperty );

    if ( localHostValue != null && localHostValue.length() > 0 ) {
      localHostValue = Validator.replaceNonUsAsciiAndTrim( localHostValue, 255 );
      return localHostValue;
    } else {
      return facts.determineLocalHostName();
    }
  }


  private int parsePort() {
    String portProperty = SyslogHandler.class.getName() + "." + PORT_PROPERTY;
    String portValue = LogManager.getLogManager().getProperty( portProperty );
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


  private Facility parseFacility() {
    String facilityProperty = SyslogHandler.class.getName() + "." + FACILITY_PROPERTY;
    String facilityValue = LogManager.getLogManager().getProperty( facilityProperty );
    for (Facility f : Facility.values()) {
      if ( f.name().equalsIgnoreCase( facilityValue ) ) {
        return f;
      }
    }
    return Facility.USER;
  }
}