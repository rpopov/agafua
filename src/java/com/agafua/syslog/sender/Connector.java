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
import java.util.logging.LogRecord;

/**
 * The medium between a Handler and a Sender thread 
 */
public final class Connector<C extends Configuration> {

	private final BlockingQueue<LogRecord> blockingQueue;
	
  private final C configuration;

	/**
	 * Invariant:
	 *   open <=> worker is not null
	 */
	private Thread worker;

	public Connector(C config) {
		this.configuration = config;
		this.blockingQueue = new ArrayBlockingQueue<LogRecord>(config.getQueueSize());
	}

  /**
	 * @see com.agafua.syslog.sender.Connector#isOpen()
	 */
  public boolean isOpen() {
    return worker != null;
  }

  /**
   * pre-condition: !isOpen()
   * post-condition: isOpen()
   */
  public void open() {
    if ( !isOpen() ) {
      worker = new Thread( configuration.constructSender( blockingQueue ));
      worker.start(); // isOpen
    } else {
      throw new IllegalStateException("Expected a not open connector");
    }
  }

  /**
   * pre-condition: isOpen()
   * post-condition: isOpen()
   */
	public void push(LogRecord message) {
    if ( isOpen() ) {
      try {
        blockingQueue.put(message);
      } catch (Exception ex) {
        ex.printStackTrace();
        
        System.err.println("The following message will not be delivered:\n"+message);
      }
    } else {
      throw new IllegalStateException("Expected an open connector");
    }
	}

  /**
   * Retrieve the earliest log record pending for transfer, waiting a for a record if none exist  
   * pre-condition: isOpen()
   * post-condition: isOpen()
   * @return not null record
   */
  public LogRecord take() throws InterruptedException {
    return blockingQueue.take();
  }
	
  /**
   * Retrieve the earliest log record pending for transfer, not waiting if none exists
   * pre-condition: isOpen()
   * post-condition: isOpen()
   * @return a record found or null, if there are none
   */
  public LogRecord poll() {
    return blockingQueue.poll();
  }
  
	
	/**
   * pre-condition: isOpen()
   * post-condition: !isOpen()
	 */
	public final void close() throws SecurityException {
	  if ( isOpen() ) {
      worker.interrupt();
      
      // wait the worker to complete normally and dump the pending messages. Otherwise they may not be printed due to JVM shutdown.
      try {
        worker.join();
      } catch (InterruptedException ex) {
        // suppress
      }
  		worker = null;  // !isOpen
    } else {
      throw new IllegalStateException("Expected an open connector");
    }
	}

  /**
   * @see #configuration
   */
  public final C getConfiguration() {
    return configuration;
  }
}