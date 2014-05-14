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

package com.agafua.syslog.sender;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.LogRecord;

import net.ifao.pci.logging.internal.NetworkSender;
import net.ifao.pci.logging.syslog.SyslogConfiguration;

/*
 *  Worker for sending syslog messages with TCP transport
 */
class TcpSender extends NetworkSender<SyslogConfiguration> implements Runnable {

  private OutputStream os;
  private Socket socket;


  public TcpSender(Connector<SyslogConfiguration> connector) {
    super( connector );
  }


  /**
   * @see net.ifao.pci.logging.internal.NetworkSender#establishConnection()
   */
  protected void establishConnection() throws IOException {
    if ( os == null ) {
      socket = new Socket( getConfiguration().getHost(), 
                           getConfiguration().getPort() );
      os = socket.getOutputStream();
    } 
  }


  /**
   * @see net.ifao.pci.logging.internal.NetworkSender#sendMessage(LogRecord)
   */
  protected void sendMessage(LogRecord record) throws IOException {
    Message message;
    
    message = constructMessage( record );  

    os.write( message.getBytes(), 0, message.getLength() );
    os.flush();
  }


  /**
   * @see net.ifao.pci.logging.internal.NetworkSender#releaseResources()
   */
  protected void releaseResources() {
    if ( os != null ) {
      try {
        os.close();
      } catch (Throwable t) {
      }
      os = null;
    }
    if ( socket != null ) {
      try {
        socket.close();
      } catch (Throwable t) {
      }
      socket = null;
    }
  }


  /**
   * @see net.ifao.pci.logging.internal.NetworkSender#describeConnection()
   */
  protected String describeConnection() {
    return "TCP connection to host:"+getConfiguration().getHost()+" and port:"+ getConfiguration().getPort();
  }

  /**
   * @param record
   * @return
   */
  private Message constructMessage(LogRecord record) {
    return getConfiguration().constructMessage( record, "-" );
  }


  /**
   * @see net.ifao.pci.logging.internal.NetworkSender#formatPrintable(java.util.logging.LogRecord)
   */
  protected String formatPrintable(LogRecord record) {
    return constructMessage( record ).toString();
  }
}
