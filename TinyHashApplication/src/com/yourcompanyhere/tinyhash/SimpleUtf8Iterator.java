package com.yourcompanyhere.tinyhash;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;

public class SimpleUtf8Iterator {
	
	private static final int MAX_STRING_SIZE = 6;
	private static final int CHAR_SIZE = 4;
	private static final int MAX_SIZE_IN_BYTES = MAX_STRING_SIZE * CHAR_SIZE;
	private int currentPosition = MAX_SIZE_IN_BYTES - 1;
	private byte[] currentText = new byte[MAX_SIZE_IN_BYTES];
	private boolean hasNext = true;
	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	private CharsetDecoder decoder = UTF8_CHARSET.newDecoder();
	
	public String next() throws CharacterCodingException {
		if (!hasNext) {
			return null;
		}
		String result = null;
		ByteBuffer data;
		while (result == null && hasNext) {
			try {
				data = ByteBuffer.wrap(currentText, currentPosition, MAX_SIZE_IN_BYTES - currentPosition);
				result = decoder.decode(data).toString();
			} catch (MalformedInputException ex) {
				// We try to increment until decoder finds a valid value.
				// Really slow!! Exception creation is very expensive.
			}
			increment();
		}
		return result;
	}
	
	private void increment() {
		if (currentText[MAX_SIZE_IN_BYTES - 1] != (byte)0x7F) {
			currentText[MAX_SIZE_IN_BYTES - 1]++;
		} else {
			int i = MAX_SIZE_IN_BYTES - 1;
			while (currentText[i] == (byte)0x7F) {
				currentText[i] = 0;
				if (--i < 0) {
					hasNext = false;
				} else {
					currentText[i]++;
				}
			}
			if (hasNext && i < currentPosition) {
				currentPosition = i; 
			}
		}
	}

}
