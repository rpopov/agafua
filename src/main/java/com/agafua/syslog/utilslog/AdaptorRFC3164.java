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

package com.agafua.syslog.utilslog;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.agafua.syslog.sender.AbstractAdaptor;
import com.agafua.syslog.sender.Adaptor;
import com.agafua.syslog.sender.Severity;

/**
 * Auxiliary class that adapts fields of java.util.logging.LogRecord for syslog
 * format RFC 3164.
 */
public class AdaptorRFC3164 extends AbstractAdaptor {

	private final LogRecord logRecord;

	private static final String[] MONTH_NAMES = { "Jan", "Feb", "Mar", "Apr",
	                                              "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

  public AdaptorRFC3164(LogRecord record) {
		logRecord = record;
		
    adaptSeverity();
    adaptTimeStamp();		
	}

	protected final LogRecord getLogRecord() {
  	return logRecord;
  }

  protected Severity adaptSeverity() {
		Level level = logRecord.getLevel();
		if (level.intValue() >= Level.SEVERE.intValue()) {
			setSeverity(Severity.ERROR);
		} else if (level.intValue() >= Level.WARNING.intValue()) {
			setSeverity(Severity.WARNING);
		} else if (level.intValue() >= Level.INFO.intValue()) {
			setSeverity(Severity.INFO);
		} else {
			setSeverity(Severity.DEBUG);
		}
		return getSeverity();
	}

	protected String adaptTimeStamp() {
		long millis = logRecord.getMillis();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(millis);
		int month = limit(calendar.get(Calendar.MONTH), 0, 11);
		String mmm = MONTH_NAMES[month];
		int day = limit(calendar.get(Calendar.DAY_OF_MONTH), 1, 31);
		String dd = indent("" + day, 2, ' ');
		int hour = limit(calendar.get(Calendar.HOUR_OF_DAY), 0, 23);
		String hh = indent("" + hour, 2, '0');
		int minute = limit(calendar.get(Calendar.MINUTE), 0, 59);
		String mm = indent("" + minute, 2, '0');
		int second = limit(calendar.get(Calendar.SECOND), 0, 59);
		String ss = indent("" + second, 2, '0');
		return String.format("%s %s %s:%s:%s", mmm, dd, hh, mm, ss);
	}

	private static String indent(String s, int requiredLength, char identChar) {
		while (s.length() < requiredLength) {
			s = identChar + s;
		}
		return s;
	}

	private static int limit(int value, int min, int max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}
}