package com.agafua.syslog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Auxiliary class that adapts fields of java.util.logging.LogRecord for syslog
 * format RFC 5424.
 */
class AdaptorRFC5424 implements Adaptor {

	public String adaptPriority(LogRecord logRecord, Facility facility) {
		int code = (facility.getId() << 3) + adaptSeverity(logRecord);
		return String.format("<%d>", code);
	}

	public int adaptSeverity(LogRecord logRecord) {
		Level level = logRecord.getLevel();
		if (level.intValue() >= Level.SEVERE.intValue()) {
			return Severity.ERROR.getLevel();
		} else if (level.intValue() >= Level.WARNING.intValue()) {
			return Severity.WARNING.getLevel();
		} else if (level.intValue() >= Level.INFO.intValue()) {
			return Severity.INFO.getLevel();
		} else {
			return Severity.DEBUG.getLevel();
		}
	}

	public String adaptTimeStamp(LogRecord logRecord) {
		long millis = logRecord.getMillis();
		Date logDate = new Date(millis);

		String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
				.format(logDate);
		timestamp = timestamp.replaceAll("(\\d\\d)(\\d\\d)$", "$1:$2");
		return timestamp;
	}

}
