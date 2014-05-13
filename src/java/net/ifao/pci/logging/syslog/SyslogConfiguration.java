package net.ifao.pci.logging.syslog;

import java.util.concurrent.BlockingQueue;
import java.util.logging.LogRecord;

import net.ifao.pci.logging.NetworkSender;

import com.agafua.syslog.sender.Configuration;
import com.agafua.syslog.sender.Facility;
import com.agafua.syslog.sender.Message;
import com.agafua.syslog.sender.SyslogRfc;
import com.agafua.syslog.sender.Transport;

/**
 * All configuration parameter for the Syslog connectivity.
 * Call its set* to fill it in before use.
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
  private Transport transport = Transport.UDP;
  
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
   * @see com.agafua.syslog.sender.Configuration#constructSender(BlockingQueue)
   */
  protected NetworkSender constructSender(BlockingQueue<LogRecord> blockingQueue) {
    return transport.constructSender( remoteHostName, remotePort, blockingQueue );
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