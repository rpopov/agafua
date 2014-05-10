/*
Copyright (c) 2012 Vitaly Russinkovsky

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package com.agafua.syslog;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import com.agafua.syslog.sender.Configuration;
import com.agafua.syslog.sender.Connector;
import com.agafua.syslog.sender.Facility;
import com.agafua.syslog.sender.Message;
import com.agafua.syslog.sender.SyslogConnector;
import com.agafua.syslog.sender.Transport;

/**
 * Implementation of java.util.logging.Handler for syslog protocol RFC 3164.
 * Requires explicit initialization through the setter methods.
 * The initialization is assumed complete when publish() is called the first time
 */
public class SyslogHandlerBean extends Handler {

  private final Configuration config;
  
	private Connector connector;

	private boolean closed;

	/**
	 * Requires explicit initialization through the setter methods
	 */
	public SyslogHandlerBean() {
		config = new Configuration();
	}

	/**
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
	public void publish(LogRecord record) {
	  Message message;
	  
		if ( !closed ) {
  		message = config.constructMessage( record, "-" );
  
  		getConnector().publish(message);
		}
	}

	public void flush() {
		// Does nothing because sending is asynchronous
	}

	public void close() throws SecurityException {
	  if ( !closed ) {
  		getConnector().close();
  		closed = true;
	  }
	}
	
  /**
   * Lazily initialize the connector to the syslog, assuming the configuration phase completed  
   * @see #connector
   */
  public final Connector getConnector() {
    if ( connector == null ) {
      connector = new SyslogConnector(config);      
    }
    return connector;
  }

  /**
   * Use bean-style initialization
   */ 
	public void setApplicationId(String  applicationId) {
	  if ( applicationId != null && !applicationId.trim().isEmpty() ) {
	    config.setApplicationId( applicationId.trim() );	    
	  }
	}
	
	public void setMaxMessageSize(String maxSize) {
	  if ( maxSize != null && !maxSize.isEmpty() ) {
	    try {
	      config.setMaxMessageSize( Integer.parseInt( maxSize.trim() ) );
	    } catch (Exception ex) {
	      throw new IllegalArgumentException("Setting maximal message size to: "+maxSize+" caused:", ex);
	    }
	  }
	}
	
	public void setPort(String port) {
	  if ( port != null && !port.isEmpty()) {
	    try {
	      config.setPort( Integer.parseInt( port ) );
	    } catch (Exception ex) {
        throw new IllegalArgumentException("Setting port to: "+port+" caused:", ex);
	    }
	  }
	}
	
	public void setRemoteHostName(String hostName) {
	  if ( hostName != null && !hostName.isEmpty() ) {
	    config.setRemoteHostName( hostName );
	  }
	}
	
  /**
   * Use bean-style initialization 
   * @param facilityValue
   */
  public void setFacility(String facilityValue) {
    if ( facilityValue != null && !facilityValue.isEmpty() ) {
      config.setFacility( Facility.valueOf(facilityValue) );
    } 
  }
	
  /**
   * Use bean-style initialization 
   * @param transportValue
   */
  public void setTransport(String transportValue) throws IllegalArgumentException {
    if ( transportValue != null && !transportValue.isEmpty() ) {
      config.setTransport( Transport.valueOf(transportValue) );
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
        
        config.setFormatter( formatter );
      } catch (Exception ex) {
        throw new IllegalArgumentException("Could not initialize java.util.logging Formatter class: "+formatterClassName, ex);
      }
    }
  }
}