package com.yourcompanyhere.tinyhash;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
/**
 * http://en.wikipedia.org/wiki/UTF-8
 * @author oresztesz
 */
public class Utf8Iterator {
	
	private static final int MAX_STRING_SIZE = 255;
	// Can be various according to size (1-6).
	private static final int UTF8_BYTE_SIZE = 2;
	private static final int MAX_SIZE_IN_BYTES = MAX_STRING_SIZE * UTF8_BYTE_SIZE;
	private int lastByteOfCurrentChar = MAX_SIZE_IN_BYTES - 1;
	private byte[] currentText = new byte[MAX_SIZE_IN_BYTES];
	private boolean hasNext = true;
	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	
	/*
	 * TODO change API maybe:
	 * - next
	 * - getString
	 * - getBytes
	 */
	
	public String next() throws CharacterCodingException {
		if (!hasNext) {
			return null;
		}
		// Getting the first position.
		int firstPos = lastByteOfCurrentChar - getCurrentCharBytes(lastByteOfCurrentChar) + 1;
		ByteBuffer data = ByteBuffer.wrap(currentText, firstPos, MAX_SIZE_IN_BYTES - firstPos);
		CharsetDecoder decoder = UTF8_CHARSET.newDecoder();
		String result = decoder.decode(data).toString();
		increment(getLastIndexOfBuffer());
		return result;
	}
	
	// TODO hasMoreInBuffer AND JUnit tests.
	 
	
	int getLastIndexOfBuffer() {
		return currentText.length - 1;
	}
	
	private void increment(int pos) {
		if (!isMaxAtPos(pos)) {
			incrementCurrentByte(pos);
		} else {
			int charLastPos = pos;
			while (isMaxAtPos(charLastPos)) {
				int currentCharBytes = getCurrentCharBytes(charLastPos);
				// Just modify this byte range [currentCharBytes]
				handleByteRangeOverflow(charLastPos, currentCharBytes);
				// Step char size. Bite size may change on byte range overflow TODO optimize?
				charLastPos -= getCurrentCharBytes(charLastPos);
			}
		}
	}

	private void handleByteRangeOverflow(int charLastPos, int currentCharBytes) {
		// Check if we can increment at any position before the last byte of current char:
		for (int j = 1; j < currentCharBytes; j++) {
			if (!isMaxAtPos(j)) {
				incrementCurrentByte(charLastPos - j);
				// Zero out all other bytes.
				for (int k = 0; k < j; k++) {
					currentText[charLastPos - k] = (byte) 0x80;
				}
				return;
			}
		}
		// If loop ended without any modification, check if we should zero all bytes.
		if (currentCharBytes == UTF8_BYTE_SIZE) {
			// Zero out bytes according to current char size.
			zeroOutAllBytes(charLastPos, currentCharBytes);
			// Increment next character.
			lastByteOfCurrentChar = charLastPos - currentCharBytes; 
			increment(charLastPos - currentCharBytes);
		} else {
			// We should increment current char bytes by one.
			// TODO temp code write to all the byte sizes. TODO why not C0???
			currentText[charLastPos - currentCharBytes] = (byte) 0xC2;
			currentText[charLastPos] = (byte) 0x80;
		}
	}

	private void zeroOutAllBytes(int charLastPos, int currentCharBytes) {
		for (int j = 0; j < currentCharBytes; j++) {
			currentText[charLastPos - j] = 0; 
		}
	}
	
	private void incrementCurrentByte(int pos) {
		if (isOneByted(pos)) {
			currentText[pos]++;
		} else if (isMultiBytedAndNotFirst(pos)) {
			currentText[pos]++;
		} else {
			currentText[pos]++;
		}
	}
	
	private int getCurrentCharBytes(int pos) {
		int bytesInSequence = 1;
		while ((currentText[pos] & 0xC0) == 0x80) {
			bytesInSequence++;
			pos--;
		}
		return bytesInSequence;
	}
	/**
	 * 1 byte representation is in 0xxx xxxx
	 * @param pos
	 * @return
	 */
	private boolean isOneByted(int pos) {
		return (currentText[pos] & 0x80) == 0x00;
	}
	
	/**
	 * More than 1 byte representation, not first element. Format 10xx xxxx
	 * @param pos
	 * @return
	 */
	private boolean isMultiBytedAndNotFirst(int pos) {
		return (currentText[pos] & 0xC0) == 0x80;
	}
	
	private boolean isMaxAtPos(int pos) {
		if (isOneByted(pos)) {
			return currentText[pos] == 0x7F;
		} else if (isMultiBytedAndNotFirst(pos)) {
			return (currentText[pos] & 0x3F) == 0x3F;
		} else if ((currentText[pos] & 0xE0) == 0xC0) {
			// 2 byte representation, first element. Format 110x xxxx
			return (currentText[pos] & 0x1F) == 0x1F;
		} else {
			// TODO ...
			return true;
		}
	}
}
