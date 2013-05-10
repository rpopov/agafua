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

package com.agafua.syslog;

/**
 * Message for sending by worker implementation.
 */
class MessageRFC5424 implements Message {
	private final int maxMsgSize;

	protected int getMaxMsgSize() {
		return maxMsgSize;
	}

	protected MessageRFC5424(int maxMessageSize) {
		this.maxMsgSize = maxMessageSize;
	}

	StringBuffer sb = new StringBuffer();

	String result = null;

	public void print(String s) {
		result = null;
		sb.append(s);
	}

	@Override
	public String toString() {
		if (result == null) {
			String output = sb.toString();
			output = output.substring(0, Math.min(maxMsgSize, output.length()));
			result = output;
		}
		return result;
	}

	@Override
	public byte[] getBytes() {
		return toString().getBytes();
	}

	@Override
	public int getLength() {
		return toString().length();
	}
}
