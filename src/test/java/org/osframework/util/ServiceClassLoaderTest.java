package org.osframework.util;

import static org.testng.Assert.*;

import java.util.Iterator;

import org.osframework.util.impl.DummyServiceDefaultImpl;
import org.testng.annotations.Test;

public class ServiceClassLoaderTest {

	@Test
	public void testLoad() {
		ServiceClassLoader<DummyService> scl = ServiceClassLoader.load(DummyService.class);
		Iterator<Class<? extends DummyService>> it = scl.iterator();
		assertTrue(it.hasNext());
		Class<? extends DummyService> cls = it.next();
		assertEquals(cls, DummyServiceDefaultImpl.class);
	}

}
