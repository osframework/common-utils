package org.osframework.util;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class EqualsUtilTest {

	@Test(groups = "general", dataProvider = "equalityPairs")
	public void testAreEqualBoolean(final boolean boolean1, final boolean boolean2, final boolean check) {
		assertEquals(EqualsUtil.areEqual(boolean1, boolean2), check);
	}

	@Test(groups = "general", dataProvider = "equalityPairs")
	public void testAreEqualChar(final char char1, final char char2, final boolean check) {
		assertEquals(EqualsUtil.areEqual(char1, char2), check);
	}

	@Test(groups = "general", dataProvider = "equalityPairs")
	public void testAreEqualLong(final long long1, final long long2, final boolean check) {
		assertEquals(EqualsUtil.areEqual(long1, long2), check);
	}

	@DataProvider
	public Object[][] equalityPairs(final Method method) {
		Object[] set1 = null, set2 = null, set3 = null, set4 = null;
		if ("testAreEqualBoolean".equals(method.getName())) {
			set1 = new Object[] { true, true, true };
			set2 = new Object[] { false, false, true};
			set3 = new Object[] { true, false, false };
			set4 = new Object[] { false, true, false };
		} else if ("testAreEqualChar".equals(method.getName())) {
			set1 = new Object[] { 'a', 'a', true };
			set2 = new Object[] { 'b', 'b', true };
			set3 = new Object[] { 'A', 'a', false };
			set4 = new Object[] { 'b', 'B', false };
		} else if ("testAreEqualLong".equals(method.getName())) {
			set1 = new Object[] { Long.MAX_VALUE, Long.MAX_VALUE, true };
			set2 = new Object[] { 0, 0, true };
			set3 = new Object[] { 2, -2, false };
			set4 = new Object[] { -2, 2, false };
		}
		
		return new Object[][] {
			set1, set2, set3, set4,
		};
	}

}
