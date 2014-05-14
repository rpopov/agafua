/*
 * Copyright (c) i:FAO AG 2014. All Rights Reserved.
 *
 * This SOURCE CODE FILE is provided under Eclipse Public License 1.0
 * 
 * Created on 09.05.2014
 */
package net.ifao.pci.logging.syslog;

import net.ifao.pci.logging.PciLogManager;
import net.ifao.pci.logging.internal.AsynchronousHandlerBean;

import com.agafua.syslog.SyslogHandler;
import com.agafua.syslog.sender.Facility;
import com.agafua.syslog.sender.SyslogRfc;
import com.agafua.syslog.sender.Transport;

/**
 * Implementation of java.util.logging.Handler for syslog protocol.
 * Requires explicit initialization through the setter methods.
 * Requires PciLogManager to be used instead of the standard java.util.logging.LogMandager,
 * which allows multiple handler instances configured separately, in contrast to the common 
 * configuration the standard imposes.
 * Handler configuration properties (see {@link PciLogManager}):<ul>
 * <li>class = net.ifao.pci.logging.syslog.SyslogHandlerBean
 * <li>applicationId - the unique application name that generated the logged message
 * <li>facility - the SYSLOG facility (see RFC3164, i.e. event kind/class) to report the message to
 * <li>formatter - the class name of the log record formatter to apply to the text content of the message
 * <li>maxMsgSize - the maximum size of the message to generate (RFC3164 states 1024, RFC5424 imposes no restriction)
 * <li>port - the port to connect the SYSLOG at. default: 514
 * <li>host - the host of the SYSLOG service to connect to
 * <li>rfc - states the message requisites and format when reported to SYSLOG. Values: RFC3164, RFC5424 (default)
 * <li>transport - TCP (default) or UDP protocol to connect SYSLOG service
 * </ul>
 * @see PciLogManager
 * @see SyslogHandler
 */
public class SyslogHandlerBean extends AsynchronousHandlerBean<SyslogConfiguration> {

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