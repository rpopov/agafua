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

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import com.agafua.syslog.sender.Configuration;
import com.agafua.syslog.sender.Connector;
import com.agafua.syslog.sender.Message;
import com.agafua.syslog.sender.SyslogConnector;

/**
 * Implementation of java.util.logging.Handler for syslog protocol.
 * Its configuration must be filled in by the first call to publish()
 * @see #publish(LogRecord) 
 */
abstract class BaseSyslogHandler extends Handler {

  private final Configuration config;
  
	private final Connector connector;

	/**
   * Provide an explicit configuration
   */
  protected BaseSyslogHandler() {
    this.config = new Configuration();
    this.connector = new SyslogConnector(config);      
  }

  /**
   * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
   */
  public void publish(LogRecord record) {
    Message message;
    
    // Lazily initialize (open) the connector to the syslog, assuming the configuration is complete
    if ( !connector.isOpen() ) { 
      connector.open();
    }
    message = config.constructMessage( record, "-" );  
    connector.publish(message);
  }

  public void close() throws SecurityException {
    if ( connector.isOpen() ) { 
      connector.close();
    }
  }

  public void flush() {
  	// Does nothing because sending is asynchronous
  }

  /**
   * @see #config
   */
  protected final Configuration getConfig() {
    return config;
  }
}