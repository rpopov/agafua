package com.agafua.syslog.sender;


/**
 * The Connector can be <b>open</b> or not.
 */
public interface Connector {

  boolean isOpen();
  
  /**
   * pre-condition: !isOpen()
   * post-condition: isOpen()
   */
  void open();
  
  /**
   * pre-condition: isOpen()
   * post-condition: isOpen()
   */
	void publish(Message record);

  /**
   * pre-condition: isOpen()
   * post-condition: !isOpen()
   */
	void close() throws SecurityException;
}