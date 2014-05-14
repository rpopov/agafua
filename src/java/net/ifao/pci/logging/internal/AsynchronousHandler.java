/*
 * Copyright (c) i:FAO AG 2014. All Rights Reserved.
 *
 * This SOURCE CODE FILE is provided under Eclipse Public License 1.0
 * 
 * Created on 09.05.2014
 */
package net.ifao.pci.logging.internal;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import com.agafua.syslog.sender.Configuration;
import com.agafua.syslog.sender.Connector;

/**
 * Implementation of java.util.logging.Handler for syslog protocol.
 * Its configuration must be filled in by the first call to publish()
 * @param <C> the type configuration 
 * @see #publish(LogRecord) 
 */
public abstract class AsynchronousHandler<C extends Configuration> extends Handler {

	private final Connector<C> connector;

	/**
   * Provide an explicit configuration
	 * @param configuration 
   */
  protected AsynchronousHandler(C configuration) {
    this.connector = new Connector<C>( configuration );      
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
    return connector.getConfiguration();
  }
}