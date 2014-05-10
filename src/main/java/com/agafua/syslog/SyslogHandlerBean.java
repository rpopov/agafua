package com.agafua.syslog;

import java.util.logging.Formatter;

import com.agafua.syslog.sender.Facility;
import com.agafua.syslog.sender.Transport;

/**
 * Implementation of java.util.logging.Handler for syslog protocol.
 * Requires explicit initialization through the setter methods.
 * The initialization is assumed complete when publish() is called the first time
 */
public class SyslogHandlerBean extends BaseSyslogHandler {

  /**
   * Use bean-style initialization
   */ 
	public void setApplicationId(String  applicationId) {
	  if ( applicationId != null && !applicationId.trim().isEmpty() ) {
	    getConfig().setApplicationId( applicationId.trim() );	    
	  }
	}
	
	public void setMaxMessageSize(String maxSize) {
	  if ( maxSize != null && !maxSize.isEmpty() ) {
	    try {
	      getConfig().setMaxMessageSize( Integer.parseInt( maxSize.trim() ) );
	    } catch (Exception ex) {
	      throw new IllegalArgumentException("Setting maximal message size to: "+maxSize+" caused:", ex);
	    }
	  }
	}
	
	public void setPort(String port) {
	  if ( port != null && !port.isEmpty()) {
	    try {
	      getConfig().setPort( Integer.parseInt( port ) );
	    } catch (Exception ex) {
        throw new IllegalArgumentException("Setting port to: "+port+" caused:", ex);
	    }
	  }
	}
	
	public void setRemoteHostName(String hostName) {
	  if ( hostName != null && !hostName.isEmpty() ) {
	    getConfig().setRemoteHostName( hostName );
	  }
	}
	
  /**
   * Use bean-style initialization 
   * @param facilityValue
   */
  public void setFacility(String facilityValue) {
    if ( facilityValue != null && !facilityValue.isEmpty() ) {
      getConfig().setFacility( Facility.valueOf(facilityValue) );
    } 
  }
	
  /**
   * Use bean-style initialization 
   * @param transportValue
   */
  public void setTransport(String transportValue) throws IllegalArgumentException {
    if ( transportValue != null && !transportValue.isEmpty() ) {
      getConfig().setTransport( Transport.valueOf(transportValue) );
    }
  }
	
	/**
	 * Use bean-style initialization 
	 * @param formatterClassName
	 */
  public void setFormatter(String formatterClassName) throws IllegalArgumentException {
    Formatter formatter;
    Class<? extends Formatter> formatterClass;
    
    if ( formatterClassName != null && !formatterClassName.isEmpty()) {
      try {
        formatterClass = Class.forName( formatterClassName ).asSubclass( Formatter.class );        
        formatter = formatterClass.newInstance();
        
        getConfig().setFormatter( formatter );
      } catch (Exception ex) {
        throw new IllegalArgumentException("Could not initialize java.util.logging Formatter class: "+formatterClassName, ex);
      }
    }
  }
}