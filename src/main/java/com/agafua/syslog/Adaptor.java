package com.agafua.syslog;

import java.util.logging.LogRecord;

/**
 * Auxiliary class that adapts fields of java.util.logging.LogRecord for syslog
 * format.
 */
interface Adaptor {

	public String adaptPriority(LogRecord logRecord, Facility facility);

	public int adaptSeverity(LogRecord logRecord);

	public String adaptTimeStamp(LogRecord logRecord);
}
