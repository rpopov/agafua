/*
 * Copyright (c) i:FAO AG 2014. All Rights Reserved.
 *
 * This SOURCE CODE FILE is provided under Eclipse Public License 1.0
 * 
 * Created on 09.05.2014
 */
package net.ifao.pci.logging.syslog;

import net.ifao.pci.logging.AsynchronousHandler;

import com.agafua.syslog.sender.Facility;
import com.agafua.syslog.sender.SyslogRfc;
import com.agafua.syslog.sender.Transport;

/**
 * Implementation of java.util.logging.Handler for syslog protocol.
 * Requires explicit initialization through the setter methods.
 * The initialization is assumed complete when publish() is called the first time
 */
public class SyslogHandlerBean extends AsynchronousHandler<SyslogConfiguration> {

  public SyslogHandlerBean() {
    super( new SyslogConfiguration() );
  }

  public void setMaxMessageSize(String maxSize) {
	  if ( maxSize != null && !maxSize.isEmpty() ) {
	    try {
	      getConfiguration().setMaxMessageSize( Integer.parseInt( maxSize.trim() ) );
	    } catch (Exception ex) {
	      throw new IllegalArgumentException("Setting maximal message size to: "+maxSize+" caused:", ex);
	    }
	  }
	}
	
	/**
   * Use bean-style initialization. Allowed values: 
   *   KERN,
   *   USER,
   *   MAIL,
   *   DAEMON,
   *   AUTH,
   *   SYSLOG,
   *   LPR,
   *   NEWS,
   *   UUCP,
   *   CRON,
   *   SECURITY,
   *   FTP,
   *   NTP,
   *   LOGAUDIT,
   *   LOGALERT,
   *   CLOCK,
   *   LOCAL0,
   *   LOCAL1,
   *   LOCAL2,
   *   LOCAL3,
   *   LOCAL4,
   *   LOCAL5,
   *   LOCAL6,
   *   LOCAL7
   *   
   * @param facilityValue
   * @see Facility
   */
  public void setFacility(String facilityValue) {
    if ( facilityValue != null && !facilityValue.isEmpty() ) {
      getConfiguration().setFacility( Facility.valueOf(facilityValue) );
    } 
  }
	
  /**
   * Use bean-style initialization. Allowed values:
   *   UDT, 
   *   TCP
   * @param transportValue
   * @see Transport
   */
  public void setTransport(String transportValue) throws IllegalArgumentException {
    if ( transportValue != null && !transportValue.isEmpty() ) {
      getConfiguration().setTransport( Transport.valueOf(transportValue) );
    }
  }
	
  /**
   * Use bean-style initialization. Allowed values:
   *   RFC3164,
   *   RFC5424
   * @param transportValue
   * @see SyslogRfc
   */
  public void setRfc(String rfcName) throws IllegalArgumentException {
    if ( rfcName != null && !rfcName.isEmpty() ) {
      getConfiguration().setSyslogRfc( SyslogRfc.valueOf(rfcName) );
    }
  }
}