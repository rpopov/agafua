package net.ifao.pci.logging.smtp;

import java.util.concurrent.BlockingQueue;
import java.util.logging.LogRecord;

import net.ifao.pci.logging.NetworkSender;

import com.agafua.syslog.sender.Configuration;

/**
 * All configuration parameter for the Syslog connectivity.
 * Call its set* to fill it in before use.
 */
public class SmtpConfiguration extends Configuration {

  /**
   * @see com.agafua.syslog.sender.BasicConfiguration#constructSender(BlockingQueue)
   */
  protected NetworkSender constructSender(BlockingQueue<LogRecord> blockingQueue) {
    return 1;
  }  
}