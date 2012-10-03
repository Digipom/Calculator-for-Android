//   Copyright 2012 Digipom Inc.
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.digipom.android.library.util;

import junit.framework.TestCase;

public class TestObjectUtils extends TestCase {
	private Boolean returnNull() { return null; }
	
	public void testReturnDefaultIfNull() {
		String shouldbeHello = ObjectUtils.returnDefaultIfNull("Hello", "Goodbye");
		
		assertEquals("Hello", shouldbeHello);
		
		boolean shouldBeTrue = ObjectUtils.returnDefaultIfNull(returnNull(), true);
		boolean shouldBeFalse = ObjectUtils.returnDefaultIfNull(returnNull(), false);
		
		assertEquals(true, shouldBeTrue);
		assertEquals(false, shouldBeFalse);
		
		// Nonsensical, but should still work.
		Boolean shouldBeNull = ObjectUtils.returnDefaultIfNull(returnNull(), null);
		assertNull(shouldBeNull);				
	}
}
