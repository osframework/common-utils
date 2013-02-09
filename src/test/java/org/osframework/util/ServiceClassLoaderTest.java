/*
 * File: ServiceClassLoaderTest.java
 * 
 * Copyright 2012 OSFramework Project.
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
package org.osframework.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;

import org.osframework.util.impl.DummyServiceDefaultImpl;
import org.testng.annotations.Test;

/**
 * Unit tests for <code>ServiceClassLoader</code>.
 *
 * @author <a href="mailto:dave@osframework.org">Dave Joyce</a>
 */
public class ServiceClassLoaderTest {

	@Test
	public void testLoad() {
		final ServiceClassLoader<DummyService> scl = ServiceClassLoader.load(DummyService.class);
		final Iterator<Class<? extends DummyService>> sclIt = scl.iterator();
		assertTrue(sclIt.hasNext(), "Expected iterator to contain next value");
		final Class<? extends DummyService> cls = sclIt.next();
		assertEquals(cls, DummyServiceDefaultImpl.class, ("Expected " + DummyServiceDefaultImpl.class.getName()));
	}

}
