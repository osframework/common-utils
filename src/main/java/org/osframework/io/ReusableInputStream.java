/*
 * File: ReusableInputStream.java
 * 
 * Copyright 2013 OSFramework Project.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osframework.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * InputStream which can be read multiple times.
 *
 * @author <a href="mailto:dave@osframework.org">Dave Joyce</a>
 */
public class ReusableInputStream extends InputStream {

	private InputStream input;
	private ByteArrayOutputStream output;
	private ByteBuffer buffer;

	/**
	 * Constructor.
	 *
	 * @param input input stream to be made reusable
	 * @throws IllegalArgumentException if <code>input</code> is null
	 * @throws IOException if an I/O error occurs
	 */
	public ReusableInputStream(InputStream input) throws IOException {
		if (null == input) {
			throw new IllegalArgumentException("input stream argument cannot be null");
		}
		this.input = input;
		this.output = new ByteArrayOutputStream(input.available());
	}

	@Override
	public int available() throws IOException {
		int avail;
		if (null != input) {
			avail = input.available();
		} else if (null != buffer) {
			avail = buffer.remaining();
		} else {
			avail = 0;
		}
		return avail;
	}

	@Override
	public int read() throws IOException {
		return read(new byte[1], 0, 1);
	}

	@Override
	public int read(byte[] bytes) throws IOException {
		return read(bytes, 0, bytes.length);
	}

	/**
	 * Reads up to length bytes of data from the input stream into an array of
	 * bytes. An attempt is made to read as many as length bytes, but a smaller
	 * number may be read. The number of bytes actually read is returned as an
	 * integer.
	 * <p>
	 * This implementation delegates to the underlying input stream for the
	 * first complete read of all data, after which it is closed. Subsequent
	 * reads access this object's internal buffer.
	 * </p>
	 */
	@Override
	public int read(byte[] bytes, int offset, int length) throws IOException {
		int read;
		if (null == buffer) {
			read = input.read(bytes, offset, length);
			if (0 > read) {
				input.close();
				input = null;
				buffer = ByteBuffer.wrap(output.toByteArray());
				output = null;
				read = -1;
			} else {
				output.write(bytes, offset, length);
			}
		} else {
			read = Math.min(length, buffer.remaining());
			if (0 >= read) {
				read = -1;
			} else {
				buffer.get(bytes, offset, read);
			}
		}
		return read;
	}

	@Override
	public synchronized void reset() throws IOException {
		if (null == buffer) {
			throw new IOException("Input stream not ready for reset");
		} else {
			buffer.flip();
		}
	}

	@Override
	public void close() throws IOException {
		if (null != input) {
			input.close();
		}
	}

}
