package com.yourcompanyhere.tinyhash.test;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.yourcompanyhere.tinyhash.SimpleUtf8Iterator;

public class SimpleUtf8IteratorTest extends TestCase {
	
	public void testIterationWithFirstCharacter() throws CharacterCodingException {
		// 0x21 is the character !.
		SimpleUtf8Iterator it = new SimpleUtf8Iterator();
		String value = "";
		for(int iterations = 0; iterations != 0x22; iterations++) {
			value = it.next();
		}
		Assert.assertEquals("!", value);
	}
	
	public void testIterationWithOtherChar() throws CharacterCodingException {
		// 0x40 is the character @.
		SimpleUtf8Iterator it = new SimpleUtf8Iterator();
		String value = "";
		for(int iterations = 0; iterations != 0x41; iterations++) {
			value = it.next();
		}
		Assert.assertEquals("@", value);
	}
	
	public void testIterationWithNotAscii() throws CharacterCodingException {
		SimpleUtf8Iterator it = new SimpleUtf8Iterator();
		String expected = "á";
		String value = "";
		int iterations = 0;
		for(; iterations != 65535 && !expected.equals(value); iterations++) {
			value = it.next();
		}
		Assert.assertEquals(expected, value);
	}
	
	public void testIterationWithTwoLetters() throws CharacterCodingException {
		SimpleUtf8Iterator it = new SimpleUtf8Iterator();
		String expected = "da";
		String value = "";
		int iterations = 0;
		for(; iterations != 65535 && !expected.equals(value); iterations++) {
			value = it.next();
		}
		Assert.assertEquals(expected, value);
	}
	
	public void testIterationWithSimpleWords() throws Exception {
		SimpleUtf8Iterator it = new SimpleUtf8Iterator();
		String expected = "dog";
		byte[] expectedValue = expected.getBytes("UTF8");
		String value = "";
		int iterations = 0;
		for(; iterations != Integer.MAX_VALUE && !expected.equals(value); iterations++) {
			value = it.next();
		}
		Assert.assertEquals(expected, value);
	}
	
	public void testImplementation() throws Exception {
		String expected = "oé";
		byte[] gotValue = expected.getBytes("UTF8");
		byte[] expectedValue = {0, 0, 111, -23};
		ByteBuffer data = ByteBuffer.wrap(expectedValue, 2, 2);
		String result = Charset.forName("UTF-8").decode(data).toString();
		String result2 = new String(gotValue, "UTF8");
		Assert.assertEquals(expected, result2);
		Assert.assertEquals(expected, result);
	}
}
