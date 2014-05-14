package com.agafua.syslog.sender;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.logging.Formatter;

import net.ifao.pci.logging.internal.NetworkSender;

import com.agafua.syslog.PlainFormatter;

/**
 * Holds all configuration parameters for a handler and its connector.
 * Based on the original Configuration class by Oliver Probst 
 */
public abstract class Configuration {

  private static final String LOCALHOST = "localhost";
  private static final String DEFAULT_HOST_NAME = "0.0.0.0";
  private static final int MIN_PORT = 0;
  private static final int MAX_PORT = 65535;
  
  /**
   * Time to wait before attempt again to connect 
   */
  private static final int FAILURE_TIMEOUT = 5000;
  
  /**
   * The size of the buffer (queue) of records to log
   */
  private static final int BUFFER_SIZE = 1024;
  
  /**
   * Not null 
   */
  private static final String processId = initProcessId();
  
  /**
   * Not null 
   */
  private static final String localHostName = determineLocalHostName();
  
  
  private int port;
  
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
  private Formatter formatter = new PlainFormatter();
  
  /**
   * Construct a sender of the appropriate class for this configuration.
   * The class might be defined by the class of the configuration itself of 
   * depending on specific parameters it contains.
   * @param connector that holds the pending records and holds this specific configuration
   * @param <C> must be the same as this.class 
   * @return a non-null sender to process the records in the connector
   */
  protected abstract <C extends Configuration> NetworkSender<C> constructSender(Connector<C> connector);
  
  /**
   * @return configured 
   */
  public final int getPort() {
    return port;
  }

  /**
   * @param port the remore port to communicate to
   */
  public final void setPort(int port) {
    if ( port >= MIN_PORT && port < MAX_PORT ) {
      this.port = port;
    }
  }

  /**
   * @return not null 
   */
  public final String getApplicationId() {
    return applicationId;
  }

  /**
   * @param applicationId the non-null name of the application sending the log messages
   */
  public final void setApplicationId(String applicationId) {
    if ( applicationId != null && !applicationId.trim().isEmpty() ) {
      // The RFC allows max. 48 chars for the app id, so cut it on demand
      this.applicationId = replaceNonUsAsciiAndTrim(applicationId, 48);
    }
  }

  /**
   * @return not null 
   */
  public final String getLocalHostName() {
    return localHostName;
  }

  /**
   * @return not null 
   */
  public final String getHost() {
    return remoteHostName;
  }

  /**
   * @param remoteHostName the non-null name of the host to connect 
   */
  public final void setHost(String remoteHostName) {
    if ( remoteHostName != null && !remoteHostName.trim().isEmpty() ) {
      this.remoteHostName = replaceNonUsAsciiAndTrim(remoteHostName, 255);
    }
  }

  /**
   * @return not null 
   */
  public final Formatter getFormatter() {
    return formatter;
  }

  /**
   * @param formatter the non-null formatter to apply to all transferred records 
   */
  public final void setFormatter(Formatter formatter) {
    if ( formatter != null ) {
      this.formatter = formatter;
    }
  }

  /**
   * @return not null 
   */
  public final String getProcessId() {
  	return processId;
  }

  /**
   * @return the time in milliseconds to wait before reconnecting
   */
  public final int getSeepOnFailure() {
    return FAILURE_TIMEOUT;
  }

  /**
   * @return size of the buffer of log records to transfer
   */
  public final int getQueueSize() {
    return BUFFER_SIZE;
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
    String result;
    RuntimeMXBean bean;
    String jvmName;
    String pid;
    
    result = null;
    try {
      // This might not work on any operating system.
      // We return "-" of not successful.
      bean = ManagementFactory.getRuntimeMXBean();
  
      jvmName = bean.getName();
      pid = jvmName.split( "@" )[ 0 ];
  
      if ( pid != null && !pid.isEmpty() ) {
        result = pid;
      }
    } catch (Exception ex) {
      System.err.print("Process ID detection caused:");
      ex.printStackTrace();
    }
    return result;
  }

}