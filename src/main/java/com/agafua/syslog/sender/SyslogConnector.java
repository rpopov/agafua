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

package com.agafua.syslog.sender;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Implementation of java.util.logging.Handler for syslog protocol RFC 3164 and
 * RFC 5424
 */
public class SyslogConnector implements Connector {

	private static final int LOG_QUEUE_SIZE = 1024;

	private final BlockingQueue<Message> blockingQueue = new ArrayBlockingQueue<Message>(LOG_QUEUE_SIZE);
  private final Configuration config;

	/**
	 * Invariant:
	 *   open <=> worker is not null
	 */
	private Thread worker;

	public SyslogConnector(Configuration config) {
		this.config = config;
	}

	/**
	 * @see com.agafua.syslog.sender.Connector#isOpen()
	 */
  public boolean isOpen() {
    return worker != null;
  }

  /**
   * @see com.agafua.syslog.sender.Connector#open()
   */
  public void open() {
    if ( !isOpen() ) {
      worker = config.startNewWorker( blockingQueue );  	// isOpen
    } else {
      throw new IllegalStateException("Expected a not open connector");
    }
  }

  /**
	 * @see com.agafua.syslog.SyslogConnect#publish(java.util.logging.LogRecord)
	 */
	public void publish(Message message) {
    if ( isOpen() ) {
      blockingQueue.offer(message);
    } else {
      throw new IllegalStateException("Expected an open connector");
    }
	}

	/**
	 * @see com.agafua.syslog.SyslogConnect#close()
	 */
	public void close() throws SecurityException {
	  if ( isOpen() ) {
      worker.interrupt();  		
  		worker = null;  // !isOpen
    } else {
      throw new IllegalStateException("Expected an open connector");
    }
	}
}