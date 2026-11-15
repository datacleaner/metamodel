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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.datacleaner.metamodel.sas.IO;
import org.datacleaner.metamodel.sas.SasReaderException;

import junit.framework.TestCase;

public class IOTest extends TestCase {

	public void testReadIntAndShortAndDouble() throws Exception {
		int size = 4 + 2 + 8;
		ByteBuffer bb = ByteBuffer.allocate(size);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		bb.putInt(12);
		bb.putShort((short) 3);
		bb.putDouble(562.3);

		byte[] bytes = bb.array();
		assertEquals(size, bytes.length);
		assertEquals(12, IO.readInt(bytes, 0));
		assertEquals(3, IO.readShort(bytes, 4));
		assertEquals(562.3, IO.readDouble(bytes, 6));
	}

	public void testGetBytes() throws Exception {
		byte[] bytes = new byte[] { 0, 2, 4, 6, 8, 10, 12 };

		byte[] result = IO.readBytes(bytes, 2, 3);
		assertTrue(Arrays.equals(new byte[] { 4, 6, 8 }, result));

		assertEquals(0, IO.readByte(bytes, 0));
		assertEquals(2, IO.readByte(bytes, 1));
	}

	public void testConcat() throws Exception {
		byte[] a1 = new byte[] { 0, 1, 2 };
		byte[] a2 = new byte[] { 3, 4 };
		byte[] res = IO.concat(a1, a2);

		assertEquals("[0, 1, 2, 3, 4]", Arrays.toString(res));
	}

	public void testReadInvalidBytes() throws Exception {
		try {
			IO.readBytes(IO.toBytes(1, 2, 3, 4), 3, 3);
			fail("Exception expected");
		} catch (SasReaderException e) {
			assertEquals("readBytes failed! data.length: 4, off: 3, len: 3",
					e.getMessage());
		}
	}
}
