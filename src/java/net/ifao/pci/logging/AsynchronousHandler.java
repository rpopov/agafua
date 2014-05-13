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

package net.ifao.pci.logging;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import net.ifao.pci.logging.syslog.SyslogConfiguration;

import com.agafua.syslog.sender.Configuration;
import com.agafua.syslog.sender.Connector;
import com.agafua.syslog.sender.Message;
import com.agafua.syslog.sender.Connector;

/**
 * Implementation of java.util.logging.Handler for syslog protocol.
 * Its configuration must be filled in by the first call to publish()
 * @param <C> the type configuration 
 * @see #publish(LogRecord) 
 */
public abstract class AsynchronousHandler<C extends Configuration> extends Handler {

  private final C config;
  
	private final Connector<C> connector;

	/**
   * Provide an explicit configuration
	 * @param configuration 
   */
  protected AsynchronousHandler(C configuration) {
    this.config = configuration;
    this.connector = new Connector<C>(config);      
  }

  /**
   * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
   */
  public final void publish(LogRecord record) {
    // Lazily initialize (open) the connector to the syslog, assuming the configuration is complete
    if ( !connector.isOpen() ) { 
      connector.open();
    }
    connector.push(record);
  }

  public final void close() throws SecurityException {
    if ( connector.isOpen() ) {
      // TODO: consider sending a normal shutdown message
      connector.close();
    }
  }

  public void flush() {
  	// Does nothing because sending is asynchronous
  }

  /**
   * @see #config
   */
  protected final C getConfiguration() {
    return config;
  }

  /**
   * Use bean-style initialization 
   * @param formatterClassName
   */
  public final void setFormatter(String formatterClassName) throws IllegalArgumentException {
    Formatter formatter;
    Class<? extends Formatter> formatterClass;
    
    if ( formatterClassName != null && !formatterClassName.isEmpty()) {
      try {
        formatterClass = Class.forName( formatterClassName ).asSubclass( Formatter.class );        
        formatter = formatterClass.newInstance();
        
        getConfiguration().setFormatter( formatter );
      } catch (Exception ex) {
        throw new IllegalArgumentException("Could not initialize java.util.logging Formatter class: "+formatterClassName, ex);
      }
    }
  }

  /**
   * Use bean-style initialization
   */
  public final void setApplicationId(String  applicationId) {
    if ( applicationId != null && !applicationId.trim().isEmpty() ) {
      getConfiguration().setApplicationId( applicationId.trim() );	    
    }
  }

  public final void setPort(String port) {
    if ( port != null && !port.isEmpty()) {
      try {
        getConfiguration().setPort( Integer.parseInt( port ) );
      } catch (Exception ex) {
        throw new IllegalArgumentException("Setting port to: "+port+" caused:", ex);
      }
    }
  }

  public final void setRemoteHostName(String hostName) {
    if ( hostName != null && !hostName.isEmpty() ) {
      getConfiguration().setRemoteHostName( hostName );
    }
  }
}