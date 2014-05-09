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
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import com.agafua.syslog.sender.Configuration;
import com.agafua.syslog.sender.Connector;
import com.agafua.syslog.sender.Facility;
import com.agafua.syslog.sender.MessageRFC5424;
import com.agafua.syslog.sender.SyslogConnector;
import com.agafua.syslog.sender.Transport;
import com.agafua.syslog.utilslog.AdaptorRFC5424;

/**
 * Implementation of java.util.logging.Handler for syslog protocol RFC 3164.
 */
public class SyslogHandlerBean extends Handler {

  private final Configuration config;
  
	private final Connector connect;

	private boolean closed;

	public SyslogHandlerBean() {
		config = new Configuration(LogManager.getLogManager(), SyslogHandlerBean.class);
		connect = new SyslogConnector(config);
	}

	public void publish(LogRecord record) {
		if (closed) {
			return;
		}
		String msg = config.getFormatter().format(record);
		AdaptorRFC5424 a = new AdaptorRFC5424(record);
		a.setMessage(msg);
		a.adaptSeverity();
		a.adaptTimeStamp();
		a.setMessageId(getMessageId());
		MessageRFC5424 m = new MessageRFC5424(a);

		connect.publish(m);
	}

	private String getMessageId() {
		return "-";
	}

	public void flush() {
		// Does nothing because sending is asynchronous
	}

	public void close() throws SecurityException {
		connect.close();
		closed = true;
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