/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.datacleaner.metamodel.sas;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Contains static convenience methods for low level operations (typically close
 * to IO).
 * 
 * @author Kasper Sørensen
 */
final class IO {

	private static final String CHARSET_NAME = "windows-1252";

	private IO() {
		// prevent instantiation
	}

	/**
	 * Converts an int-array to a byte-array. Makes it more convenient to use
	 * int literals in code.
	 * 
	 * @param arr
	 * @return
	 */
	public static byte[] toBytes(int... arr) {
		if (arr == null) {
			return null;
		}
		byte[] result = new byte[arr.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = (byte) arr[i];
		}
		return result;
	}

	public static String readString(byte[] buffer, int off, int len) {
		byte[] subset = readBytes(buffer, off, len);

		String str = getString(subset, CHARSET_NAME);
		return str;
	}

	private static String getString(byte[] bytes, String encoding) {
		try {
			InputStreamReader reader = new InputStreamReader(
					new ByteArrayInputStream(bytes), encoding);
			char[] chars = new char[bytes.length * 2];
			int read = reader.read(chars);
			chars = Arrays.copyOf(chars, read);
			return new String(chars);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static byte readByte(byte[] buffer, int off) {
		ByteBuffer bb = ByteBuffer.wrap(buffer);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.get(off);
	}

	public static int readInt(byte[] buffer, int off) {
		ByteBuffer bb = ByteBuffer.wrap(buffer);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt(off);
	}

	public static double readDouble(byte[] buffer, int off) {
		ByteBuffer bb = ByteBuffer.wrap(buffer);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getDouble(off);
	}

	public static byte[] readBytes(byte[] data, int off, int len) {
		if (data.length < off + len) {
			throw new SasReaderException("readBytes failed! data.length: "
					+ data.length + ", off: " + off + ", len: " + len);
		}
		byte[] subset = new byte[len];
		System.arraycopy(data, off, subset, 0, len);
		return subset;
	}

	public static short readShort(byte[] buffer, int off) {
		ByteBuffer bb = ByteBuffer.wrap(buffer);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getShort(off);
	}

	public static Number readNumber(byte[] buffer, int off, int len) {
		if (len == 1) {
			return readByte(buffer, off);
		} else if (len == 2) {
			return readShort(buffer, off);
		} else if (len == 4) {
			return readInt(buffer, off);
		} else if (len == 8) {
			return readDouble(buffer, off);
		} else {
			throw new UnsupportedOperationException(
					"Number byte-length not supported: " + len);
		}
	}

	public static byte[] concat(byte[] arr1, byte[] arr2) {
		byte[] result = new byte[arr1.length + arr2.length];
		System.arraycopy(arr1, 0, result, 0, arr1.length);
		System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
		return result;
	}
}
