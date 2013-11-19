package com.yourcompanyhere.tinyhash;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class Utf8Iterator {
	
	private static final int MAX_STRING_SIZE = 255;
	private static final int CHAR_SIZE = 4;
	private static final int MAX_SIZE_IN_BYTES = MAX_STRING_SIZE * CHAR_SIZE;
	private int currentPosition = MAX_SIZE_IN_BYTES - 1;
	private byte[] currentText = new byte[MAX_SIZE_IN_BYTES];
	private boolean hasNext = true;
	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	
	public String next() throws CharacterCodingException {
		if (!hasNext) {
			return null;
		}
		ByteBuffer data = ByteBuffer.wrap(currentText, currentPosition, MAX_SIZE_IN_BYTES - currentPosition);
		CharsetDecoder decoder = UTF8_CHARSET.newDecoder();
		String result = decoder.decode(data).toString();
		increment();
		return result;
	}
	
	private void increment() {
		if (currentText[currentPosition] < 0xFF) {
			currentText[currentPosition]++;
		} else {
			int i = currentPosition;
			while (currentText[i] == 0xFF) {
				currentText[i] = 0;
				if (--i < 0) {
					hasNext = false;
				} else {
					currentText[i]++;
				}
			}
			if (hasNext) {
				currentPosition = i; 
			}
		}
	}

}
