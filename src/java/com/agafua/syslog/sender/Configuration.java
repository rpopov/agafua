/*
 * Copyright (c) i:FAO AG 2014. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by i:FAO AG as part
 * of a product of i:FAO AG for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information.
 * 
 * Created on 13.05.2014
 */
package com.agafua.syslog.sender;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import com.agafua.syslog.PlainFormatter;

import net.ifao.pci.logging.NetworkSender;

public abstract class Configuration {

  private static final String LOCALHOST = "localhost";
  private static final String DEFAULT_HOST_NAME = "0.0.0.0";
  private static final int MIN_PORT = 0;
  private static final int MAX_PORT = 65535;
  
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
  static final int LOG_QUEUE_SIZE = 1024;

  /**
   * Construct a sender of the appropriate class for this configuration.
   * The class might be defined by the class of the configuration itself of 
   * depending on specific parameters it contains.
   * @param blockingQueue
   * @return
   */
  protected abstract NetworkSender constructSender(BlockingQueue<LogRecord> blockingQueue);
  
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
  public final String getRemoteHostName() {
    return remoteHostName;
  }

  public final void setRemoteHostName(String remoteHostName) {
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
  public final String getProcessId() {
  	return processId;
  }

  /**
   * TODO State the purpose of this method in one statement
   * TODO Describe the method's purpose
   * TODO Describe any requirements and pre-conditions
   * TODO Mention any concurrency considerations
   * TODO Describe the non-local objects modified in this method and any side effects
   * TODO Describe the method's effects and the post-condition state
   * TODO Describe the method’s usage. Provide examples if appropriate.
   * 
   * @return
   * 
   * NOTES: 
   */
  public static int getQueueSize() {
    return LOG_QUEUE_SIZE;
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