package org.osframework.util;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Date;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = "general")
public class HashCodeUtilTest {

	@Test(dataProvider = "hashPairs")
	public void testHashBoolean(final boolean boolean1, final boolean boolean2, final boolean check) {
		final int seed = HashCodeUtil.SEED;
		final int hash1 = HashCodeUtil.hash(seed, boolean1);
		final int hash2 = HashCodeUtil.hash(seed, boolean2);
		assertEquals((hash1 == hash2), check);
	}

	@Test(dataProvider = "hashPairs")
	public void testHashChar(final char char1, final char char2, final boolean check) {
		final int seed = HashCodeUtil.SEED;
		final int hash1 = HashCodeUtil.hash(seed, char1);
		final int hash2 = HashCodeUtil.hash(seed, char2);
		assertEquals((hash1 == hash2), check);
	}

	@Test(dataProvider = "hashPairs")
	public void testHashDouble(final double double1, final double double2, final boolean check) {
		final int seed = HashCodeUtil.SEED;
		final int hash1 = HashCodeUtil.hash(seed, double1);
		final int hash2 = HashCodeUtil.hash(seed, double2);
		assertEquals((hash1 == hash2), check);
	}

	@Test(dataProvider = "hashPairs")
	public void testHashLong(final long long1, final long long2, final boolean check) {
		final int seed = HashCodeUtil.SEED;
		final int hash1 = HashCodeUtil.hash(seed, long1);
		final int hash2 = HashCodeUtil.hash(seed, long2);
		assertEquals((hash1 == hash2), check);
	}

	@Test(dataProvider = "hashPairs")
	public void testHashObject(final Object object1, final Object object2, final boolean check) {
		final int seed = HashCodeUtil.SEED;
		final int hash1 = HashCodeUtil.hash(seed, object1);
		final int hash2 = HashCodeUtil.hash(seed, object2);
		assertEquals((hash1 == hash2), check);
	}

	@DataProvider
	public Object[][] hashPairs(final Method method) {
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
			final long curTime = System.currentTimeMillis();
			final Date date1 = new Date(curTime), date2 = new Date(curTime);
			set1 = new Object[] { date1, date2, true };
			
			set2 = new Object[] { null, null, true };
			
			Date[] arr1 = new Date[] { date1, date2 }, arr2 = new Date[] { date1, date2 };
			set3 = new Object[] { arr1, arr2, true };
			
			// Order of elements doesn't matter
			arr1 = new Date[] { date1, date2 };
			arr2 = new Date[] { date2, date1 };
			set4 = new Object[] { arr1, arr2, true };
		}
		
		return new Object[][] {
			set1, set2, set3, set4
		};
	}

}
