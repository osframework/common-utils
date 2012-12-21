package org.osframework.util;

import static org.testng.Assert.*;

import java.lang.reflect.Method;
import java.util.Date;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

public class HashCodeUtilTest {

	@Test(groups = "general", dataProvider = "dp")
	public void testHashBoolean(boolean b1, boolean b2, boolean check) {
		final int seed = HashCodeUtil.SEED;
		int h1 = HashCodeUtil.hash(seed, b1);
		int h2 = HashCodeUtil.hash(seed, b2);
		assertEquals((h1 == h2), check);
	}

	@Test(groups = "general", dataProvider = "dp")
	public void testHashChar(char c1, char c2, boolean check) {
		final int seed = HashCodeUtil.SEED;
		int h1 = HashCodeUtil.hash(seed, c1);
		int h2 = HashCodeUtil.hash(seed, c2);
		assertEquals((h1 == h2), check);
	}

	@Test(groups = "general", dataProvider = "dp")
	public void testHashDouble(double d1, double d2, boolean check) {
		final int seed = HashCodeUtil.SEED;
		int h1 = HashCodeUtil.hash(seed, d1);
		int h2 = HashCodeUtil.hash(seed, d2);
		assertEquals((h1 == h2), check);
	}

	@Test(groups = "general", dataProvider = "dp")
	public void testHashLong(long l1, long l2, boolean check) {
		final int seed = HashCodeUtil.SEED;
		int h1 = HashCodeUtil.hash(seed, l1);
		int h2 = HashCodeUtil.hash(seed, l2);
		assertEquals((h1 == h2), check);
	}

	@Test(groups = "general", dataProvider = "dp")
	public void testHashObject(Object o1, Object o2, boolean check) {
		final int seed = HashCodeUtil.SEED;
		int h1 = HashCodeUtil.hash(seed, o1);
		int h2 = HashCodeUtil.hash(seed, o2);
		assertEquals((h1 == h2), check);
	}

	@DataProvider
	public Object[][] dp(Method method) {
		Object[] set1 = null, set2 = null, set3 = null, set4 = null;
		if ("testHashBoolean".equals(method.getName())) {
			set1 = new Object[] { true, true, true };
			set2 = new Object[] { false, false, true};
			set3 = new Object[] { true, false, false };
			set4 = new Object[] { false, true, false};
		} else if ("testHashChar".equals(method.getName())) {
			set1 = new Object[] { '\0', '\0', true };
			set2 = new Object[] { ' ', ' ', true };
			set3 = new Object[] { 'a', 'b', false };
			set4 = new Object[] { 'a', 'A', false };
		} else if ("testHashDouble".equals(method.getName())) {
			set1 = new Object[] { Math.PI, Math.PI, true };
			set2 = new Object[] { Double.MAX_VALUE, Double.MAX_VALUE, true };
			set3 = new Object[] { 19.875D, 19.8750D, true };
			set4 = new Object[] { 19.875D, 19.874999D, false };
		} else if ("testHashLong".equals(method.getName())) {
			set1 = new Object[] { Long.MAX_VALUE, Long.MAX_VALUE, true };
			set2 = new Object[] { Long.MIN_VALUE, Long.MIN_VALUE, true };
			set3 = new Object[] { 1000000000L, 1000000001L, false };
			set4 = new Object[] { 1000000001L, 1000000000L, false };
		} else if ("testHashObject".equals(method.getName())) {
			long curTime = System.currentTimeMillis();
			Date d1 = new Date(curTime), d2 = new Date(curTime);
			set1 = new Object[] { d1, d2, true };
			
			set2 = new Object[] { null, null, true };
			
			Date[] arr1 = new Date[] { d1, d2 }, arr2 = new Date[] { d1, d2 };
			set3 = new Object[] { arr1, arr2, true };
			
			// Order of elements doesn't matter
			arr1 = new Date[] { d1, d2 };
			arr2 = new Date[] { d2, d1 };
			set4 = new Object[] { arr1, arr2, true };
		}
		
		return new Object[][] {
			set1, set2, set3, set4
		};
	}

}
