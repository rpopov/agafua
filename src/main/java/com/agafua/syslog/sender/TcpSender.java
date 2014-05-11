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
import java.util.concurrent.BlockingQueue;

/*
 *  Worker for sending syslog messages with TCP transport
 */
class TcpSender extends NetworkSender implements Runnable {

  private final String hostName;

  private final int port;


  private OutputStream os;
  private Socket socket;


  public TcpSender(String hostName, int port, BlockingQueue<Message> blockingQueue) {
    super( blockingQueue );
    this.hostName = hostName;
    this.port = port; 
  }


  /**
   * @see com.agafua.syslog.sender.NetworkSender#establishConnection()
   */
  protected void establishConnection() throws IOException {
    if ( os == null ) {
      socket = new Socket( hostName, port );
      os = socket.getOutputStream();
    } 
  }


  /**
   * @see com.agafua.syslog.sender.NetworkSender#sendMessage(com.agafua.syslog.sender.Message)
   */
  protected void sendMessage(Message message) throws IOException {
    os.write( message.getBytes(), 0, message.getLength() );
    os.flush();
  }


  /**
   * @see com.agafua.syslog.sender.NetworkSender#releaseResources()
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
}
