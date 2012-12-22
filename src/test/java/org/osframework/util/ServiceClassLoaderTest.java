package org.osframework.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;

import org.osframework.util.impl.DummyServiceDefaultImpl;
import org.testng.annotations.Test;

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
