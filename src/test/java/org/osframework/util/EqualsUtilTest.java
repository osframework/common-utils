package org.osframework.util;

import static org.testng.Assert.*;

import java.lang.reflect.Method;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

public class EqualsUtilTest {

	@Test(dataProvider = "dp")
	public void testAreEqualBoolean(boolean b1, boolean b2, boolean check) {
		assertEquals(EqualsUtil.areEqual(b1, b2), check);
	}

	@Test(dataProvider = "dp")
	public void testAreEqualChar(char c1, char c2, boolean check) {
		assertEquals(EqualsUtil.areEqual(c1, c2), check);
	}

	@Test(dataProvider = "dp")
	public void testAreEqualLong(long l1, long l2, boolean check) {
		assertEquals(EqualsUtil.areEqual(l1, l2), check);
	}

	@DataProvider
	public Object[][] dp(Method method) {
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
