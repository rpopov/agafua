/*
 * Copyright (c) i:FAO AG 2014. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by i:FAO AG as part
 * of a product of i:FAO AG for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information.
 * 
 * Created on 11.05.2014
 */
package com.agafua.syslog.sender;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Extracted the common logic of a messages read from a queue  
 * Applies Template Method design pattern for the communication logic. 
 * @author rpopov
 */
abstract class NetworkSender implements Runnable {

  private static final int FAILURE_TIMEOUT = 5000;
  
  private final BlockingQueue<Message> blockingQueue;

  /**
   * @param hostName
   * @param port
   * @param blockingQueue
   */
  public NetworkSender(BlockingQueue<Message> blockingQueue) {
    this.blockingQueue = blockingQueue;
  }

  /**
   * @see java.lang.Runnable#run()
   */
  public final void run() {
    Message message;

    message = null;    
    try {
      while (true) {
        // invariant: message == null meaning successful delivery of the last message
        
        message = blockingQueue.take();
        
        // invariant:
        //  message != null holds the message to deliver
        
        deliver( message );
        
        // the message delivery completed successfully 
        message = null;
      }
    } catch (Throwable ex) { // waiting for messages or delivery interrupted, leaving the worker thread
      if ( message != null ) { // the last message was not delivered, there might be more messages waiting
        
        System.err.println("Interruped syslog messages delivery.\nThe following messages will not be delivered");
        do {
          System.err.println( message );
          message = blockingQueue.poll();
        } while ( message != null);
      }
    } finally {
      releaseResources();
    }
  }

  /**
   * Do whatever is takes to deliver the message
   * @param message not null
   * @throws InterruptedException when the thread is interrupted explicitly, considering the message as not delivered
   */
  private void deliver(Message message) throws InterruptedException {
    do {
      try {
        establishConnection();
        sendMessage( message );
        
        // successfully delivered
        message = null;
      } catch (IOException ex) {
        ex.printStackTrace();
        
        releaseResources();
        
        Thread.sleep( FAILURE_TIMEOUT );
      }
    } while ( message != null );
  }

  /**
   * Establishes all means to communicate. It makes sure sendMessage() is safe to call. 
   * @throws IOException when the communication is not possible (so far)
   */
  protected abstract void establishConnection() throws IOException;

  /**
   * Send the specific message
   * pre-condition:
   *   establishConnection() succeeded
   * @param message
   * @throws IOException when sending the message failed
   */
  protected abstract void sendMessage(Message message) throws IOException;

  /**
   * Release any communication resources, if any were allocated. 
   * In general it is possible to call this method even before any resources are allocated.
   * Also, the resources allocation method (establishConnection) might have failed at any of its
   * steps, so not all communication resources might have been allocated.
   * Thus, this method should handle none, partial and complete allocation of the resources. 
   */
  protected abstract void releaseResources();
}