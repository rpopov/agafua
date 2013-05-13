package com.agafua.syslog.log4j;

import org.apache.log4j.pattern.LogEvent;

import com.agafua.syslog.sender.Adaptor;
import com.agafua.syslog.sender.Facility;

public interface AdaptorLog4J extends Adaptor {

	public String adaptPriority(LogEvent logRecord, Facility facility);

	public int adaptSeverity(LogEvent logRecord);

	public String adaptTimeStamp(LogEvent logRecord);

}
