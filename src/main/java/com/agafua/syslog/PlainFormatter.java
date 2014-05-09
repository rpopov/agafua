package com.agafua.syslog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class PlainFormatter extends Formatter {

  public synchronized String format(LogRecord record) {

    String message = formatMessage( record );
    String throwable = "";
    
    if ( record.getThrown() != null ) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter( sw );
      pw.println();
      record.getThrown().printStackTrace( pw );
      pw.close();
      throwable = sw.toString();
    }
    return String.format( "%1$s%2$s%n", message, throwable );
  }
}
