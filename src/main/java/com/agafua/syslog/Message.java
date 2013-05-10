package com.agafua.syslog;

public abstract class Message {

	private byte[] value = null;

	protected byte[] getValue() {
		if (value == null) {
			value = new byte[getMaxMsgSize()];
		}
		return value;
	}

	protected int pos = 0;

	public Message() {
		super();

	}

	public int getLength() {
		return pos;
	}

	public byte[] getBytes() {
		return value;
	}

	@Override
	public String toString() {
		byte[] b = new byte[pos];
		System.arraycopy(value, 0, b, 0, pos);
		return new String(b);
	}

	protected abstract int getMaxMsgSize();

	protected abstract void print(String s);

}