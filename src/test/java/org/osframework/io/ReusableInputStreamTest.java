package org.osframework.io;

import static org.testng.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

public class ReusableInputStreamTest {

	private InputStream fileIn = null;

	@BeforeMethod
	public void initResource() {
		fileIn = this.getClass().getResourceAsStream("loremipsum.txt");
	}

	@Test
	public void testRepeatedRead() throws IOException {
		String s1 = null, s2 = null;
		
		ReusableInputStream ris = new ReusableInputStream(fileIn);
		BufferedReader reader = new BufferedReader(new InputStreamReader(ris));
		StringBuilder buf = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			buf.append(line);
		}
		reader.close();
		s1 = buf.toString();
		
		ris.reset();
		reader = null;
		buf.setLength(0);
		line = null;
		
		reader = new BufferedReader(new InputStreamReader(ris));
		while ((line = reader.readLine()) != null) {
			buf.append(line);
		}
		reader.close();
		s2 = buf.toString();
		
		assertEquals(s2, s1);
	}

}
