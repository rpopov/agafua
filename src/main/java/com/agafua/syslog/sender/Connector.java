package com.agafua.syslog.sender;



public interface Connector {

	void publish(Message record);

	void close() throws SecurityException;

}