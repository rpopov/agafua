package com.agafua.syslog;

import java.util.ArrayList;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class SyslogAppender extends AppenderSkeleton {
	
	@Override
	protected void append(LoggingEvent event) {
		
	}

	public void close() {
	}

	public boolean requiresLayout() {
		return false;
	}

}