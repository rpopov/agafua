/*
 * Copyright (c) i:FAO AG 2014. All Rights Reserved.
 *
 * This SOURCE CODE FILE is provided under Eclipse Public License 1.0
 * 
 * Created on 11.05.2014
 */
package net.ifao.pci.logging;

import java.io.IOException;
import java.util.logging.LogRecord;

import com.agafua.syslog.sender.Configuration;
import com.agafua.syslog.sender.Connector;

/**
 * Extracted the common logic of any delivery of log records from the connector, 
 * leaving the specific protocols in the subclasses. 
 * Applies Template Method design pattern for the communication logic. 
 * @author rpopov
 */
public abstract class NetworkSender<C extends Configuration> implements Runnable {

  private final Connector<C> connector;

  /**
   * @param connector the non-null configuration that defines the sending details   
   */
  public NetworkSender(Connector<C> connector) {
    this.connector = connector;
  }

  /**
   * @see java.lang.Runnable#run()
   */
  public final void run() {
    LogRecord record;

    record = null;    
    try {
      while (true) {
        // invariant: record == null meaning successful delivery of the last log record
        
        record = connector.take();
        
        // invariant:
        //  record != null holds the log record to deliver        
        deliver( record );
        
        // the record delivery completed successfully 
        record = null;
      }
    } catch (Throwable ex) { // waiting for record or delivery interrupted, leaving the worker thread
      if ( record != null ) { // the last record was not delivered, there might be more record waiting
        
        System.err.println("Interruped delivery.\nThe following log records will not be delivered:");
        do {
          System.err.println( record );
          record = connector.poll();
        } while ( record != null);
      }
    } finally {
      releaseResources();
    }
  }

  /**
   * Do whatever is takes to deliver the log record
   * @param record not null
   * @throws InterruptedException when the thread is interrupted explicitly, considering the log record 
   *         as not delivered
   */
  private void deliver(LogRecord record) throws InterruptedException {
    do {
      try {
        establishConnection();
        sendMessage( record );
        
        // successfully delivered
        record = null;
      } catch (IOException ex) {
        System.err.print("Network communication "+describeConnection()+" caused:");
        ex.printStackTrace();
        
        releaseResources();
        
        Thread.sleep( getConfiguration().getSeepOnFailure() );
      }
    } while ( record != null );
  }

  /**
   * @return the non-null configuration stating the details of the records' delivery. To be used in subclasses. 
   */
  protected final C getConfiguration() {
    return connector.getConfiguration();  
  }
  
  /**
   * @return a non-empty description of the communication channel to establish/already established
   */
  protected  abstract String describeConnection();

  /**
   * Establishes all means to communicate. It makes sure sendMessage() is safe to call. 
   * @throws IOException when the communication is not possible (so far)
   */
  protected abstract void establishConnection() throws IOException;

  /**
   * Send the specific record
   * pre-condition:
   *   establishConnection() succeeded
   * @param record non-null log record to deliver 
   * @throws IOException when sending the record failed
   */
  protected abstract void sendMessage(LogRecord record) throws IOException;

  /**
   * Release any communication resources, if any were allocated. 
   * In general it is possible to call this method even before any resources are allocated.
   * Also, the resources allocation method (establishConnection) might have failed at any of its
   * steps, so not all communication resources might have been allocated.
   * Thus, this method should handle none, partial and complete allocation of the resources. 
   */
  protected abstract void releaseResources();
}