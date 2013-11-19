package com.yourcompanyhere.tinyhash.test;

import java.nio.charset.CharacterCodingException;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.yourcompanyhere.tinyhash.Utf8Iterator;

public class Utf8IteratorTest extends TestCase {
	
	public void testIteration() throws CharacterCodingException {
		// 0x21 is the character !.
		Utf8Iterator it = new Utf8Iterator();
		String value = "";
		for(int iterations = 0; iterations != 0x22; iterations++) {
			value = it.next();
		}
		Assert.assertEquals("!", value);
	}
}
