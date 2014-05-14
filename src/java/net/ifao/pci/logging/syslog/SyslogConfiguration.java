/*
 * Copyright (c) i:FAO AG 2014. All Rights Reserved.
 *
 * This SOURCE CODE FILE is provided under Eclipse Public License 1.0
 * 
 * Created on 09.05.2014
 */
package net.ifao.pci.logging.syslog;

import java.util.logging.LogRecord;

import net.ifao.pci.logging.internal.NetworkSender;

import com.agafua.syslog.sender.Configuration;
import com.agafua.syslog.sender.Connector;
import com.agafua.syslog.sender.Facility;
import com.agafua.syslog.sender.Message;
import com.agafua.syslog.sender.SyslogRfc;
import com.agafua.syslog.sender.Transport;

/**
 * All configuration parameter for the Syslog connectivity.
 * Call its set* to fill it in before use.
 * Based on the original configuration class by Oliver Probst
 */
public class SyslogConfiguration extends Configuration {

  private static final int DEFAULT_MAX_MESSAGE_SIZE = 1024;
  private static final int DEFAULT_PORT = 514;

  private int maxMessageSize = DEFAULT_MAX_MESSAGE_SIZE;
  /**
   * Not null 
   */
  private SyslogRfc syslogRfc = SyslogRfc.RFC5424;
  
  
  /**
   * Not null 
   */
  private Transport transport = Transport.TCP;
  
  /**
   * Not null 
   */
  private Facility facility = Facility.USER;

  /**
   * 
   */
  public SyslogConfiguration() {
    setPort( DEFAULT_PORT );
  }

  /**
   * @see com.agafua.syslog.sender.Configuration#constructSender(Connector<C>)
   */
  protected <C extends Configuration> NetworkSender<C> constructSender(Connector<C> connector) {
    return (NetworkSender<C>) transport.constructSender( (Connector<SyslogConfiguration>) connector );
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
}