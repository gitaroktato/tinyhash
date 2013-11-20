package com.yourcompanyhere.tinyhash.test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.MalformedInputException;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.yourcompanyhere.tinyhash.Utf8Iterator;

public class Utf8IteratorTest extends TestCase {
	
	
	public void testIterationWithAscii() throws CharacterCodingException, UnsupportedEncodingException {
		Utf8Iterator it = new Utf8Iterator();
		String value = "";
		
		for(int iterations = 0; iterations != 0x0040 + 1; iterations++) {
			value = it.next();
		}
		Assert.assertEquals("@", value);
	}
	
	public void testIterationWithTwoAscii() throws CharacterCodingException, UnsupportedEncodingException {
		Utf8Iterator it = new Utf8Iterator();
		String value = "";
		String expected = "da";
		int iterations = 0;
		while(!value.equals(expected) && iterations < 16000) {
			value = it.next();
			iterations++;
		}
		Assert.assertEquals("Got wrong value after end of iteration", expected, value);
	}
	
	public void testIterationWithOneCharOverAscii() throws CharacterCodingException {
		Utf8Iterator it = new Utf8Iterator();
		String value = "";
		String expected = "ú";
		int iterations = 0;
		try {
			while(!value.equals(expected) && iterations < 128000) {
				value = it.next();
				iterations++;
			}
		} catch (CharacterCodingException ex) {
			throw new RuntimeException("" + iterations, ex);
		}
		Assert.assertEquals(expected, value);
	}
	
	public void testIterationWithOneChar2() throws CharacterCodingException {
		Utf8Iterator it = new Utf8Iterator();
		String value = "";
		int iterations = 0;
		try {
			for(; iterations != 0x019F + 1; iterations++) {
				value = it.next();
			}
		} catch(MalformedInputException ex) {
			throw new RuntimeException("Exception at: " + iterations, ex);
		}
		Assert.assertEquals("\u019F", value);
	}
}
