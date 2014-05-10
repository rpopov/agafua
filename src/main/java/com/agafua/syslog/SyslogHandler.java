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
import java.util.logging.LogManager;

import com.agafua.syslog.sender.Facility;
import com.agafua.syslog.sender.Transport;

/**
 * Implementation of java.util.logging.Handler for syslog protocol.
 * Uses the standard convention of java.util.logging for initialization through the LogManager
 */
public class SyslogHandler extends BaseSyslogHandler {

	// Property names:
  private static final String TRANSPORT_PROPERTY = "transport";
  private static final String REMOTE_HOSTNAME_PROPERTY = "remoteHostname";
  private static final String PORT_PROPERTY = "port";
  private static final String FACILITY_PROPERTY = "facility";
  private static final String APPLICATION_ID = "applicationId";
  private static final String MAX_MESSAGE_SIZE_PROPERTY = "maxMsgSize";
  private static final String FORMATTER_PROPERTY = "formatter";

  /**
	 * The default constructor.
	 * Read the handler configuration from the standard LogManager
	 */
	public SyslogHandler() {
	  LogManager logManager;
	  
	  logManager = LogManager.getLogManager();
	  
    try {
      parseApplicationId(logManager);
      parseFacility(logManager);
      
      parseMaxMessageSize(logManager);      
      parsePort(logManager);
      
      parseRemoteHostName(logManager);
      parseTransport(logManager);    
      parseFormatter(logManager);
      
    } catch (IllegalArgumentException ex) {
      System.err.print("Initialization of the syslog logging mechanism failed: ");
      ex.printStackTrace();
    }	  
	}

  private void parseApplicationId(LogManager logManager) {
    getConfig().setApplicationId( logManager.getProperty( this.getClass().getName() + "." + APPLICATION_ID ) );    
  }

  private void parseRemoteHostName(LogManager logManager) {
    getConfig().setRemoteHostName( logManager.getProperty( this.getClass().getName() + "." + REMOTE_HOSTNAME_PROPERTY ));
  }

  private void parseFormatter(LogManager logManager) throws IllegalArgumentException {
    Formatter formatter;
    Class<? extends Formatter> c2;
    
    String formatterClassName = logManager.getProperty( this.getClass().getName() + "." + FORMATTER_PROPERTY );
    if ( formatterClassName != null ) {
      try {
        c2 = Class.forName( formatterClassName ).asSubclass( Formatter.class );
        formatter = c2.newInstance();
        
        getConfig().setFormatter( formatter );
      } catch (  ClassNotFoundException 
               | InstantiationException 
               | IllegalAccessException ex) {
        throw new IllegalArgumentException("Could not initialize java.util.logging Formatter class: "+formatterClassName, ex);
      }
    }
  }

  private void parseTransport(LogManager logManager) {
    Transport result;
    String transportValue = logManager.getProperty( this.getClass().getName() + "." + TRANSPORT_PROPERTY );
    
    if ( transportValue != null ) {
      result = Transport.valueOf(transportValue);
      
      getConfig().setTransport( result );
    }
  }

  private void parseMaxMessageSize(LogManager logManager) throws IllegalArgumentException {
    int p;
    String maxMsgSize = logManager.getProperty( this.getClass().getName() + "." + MAX_MESSAGE_SIZE_PROPERTY );
    
    if ( maxMsgSize != null ) {
      try {
        p = Integer.parseInt( maxMsgSize );        
        getConfig().setMaxMessageSize( p );
        
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("Parsing max message size: "+maxMsgSize+" for "+this.getClass().getName()+"caused:", ex);
      }
    }
  }

  private void parsePort(LogManager logManager) throws IllegalArgumentException {
    int p;
    String portValue = logManager.getProperty( this.getClass().getName() + "." + PORT_PROPERTY );
    
    if ( portValue != null ) {
      try {
        p = Integer.parseInt( portValue );
        
        getConfig().setPort( p );
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("Parsing port: "+portValue+" for "+this.getClass().getName()+"caused:", ex);
      }
    }
  }

  private void parseFacility(LogManager logManager) {
    Facility result;
    String facilityValue = logManager.getProperty( this.getClass().getName() + "." + FACILITY_PROPERTY );
    
    if ( facilityValue != null ) {
      result = Facility.valueOf(facilityValue);
      
      getConfig().setFacility( result );
    } 
  }
}