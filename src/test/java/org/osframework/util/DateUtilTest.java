package org.osframework.util;

import static org.testng.Assert.*;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

public class DateUtilTest {

	@Test(dataProvider = "isDateData")
	public void testIsDate(String input, boolean expected) {
		boolean actual = DateUtil.isDate(input);
		assertEquals(actual, expected);
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testForceMidnightNullArg() {
		DateUtil.forceMidnight((Date)null);
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testFormatDateToIntNullArg() {
		DateUtil.formatDateToInt(null);
	}

	@Test(dataProvider = "formatDateData")
	public void testFormatDateToInt(Date input, int expected) {
		int actual = DateUtil.formatDateToInt(input);
		assertEquals(actual, expected);
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testFormatDateToISO8601NullArg() {
		DateUtil.formatDateToISO8601(null);
	}

	@Test(dataProvider = "formatDateData")
	public void testFormatDateToISO8601(Date input, String expected) {
		String actual = DateUtil.formatDateToISO8601(input);
		assertEquals(actual, expected);
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testFormatDateToUSNullArg() {
		DateUtil.formatDateToUS(null);
	}

	@Test(dataProvider = "formatDateData")
	public void testFormatDateToUS(Date input, String expected) {
		String actual = DateUtil.formatDateToUS(input);
		assertEquals(actual, expected);
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testFormatDateToUSReverseNullArg() {
		DateUtil.formatDateToUSReverse(null);
	}

	@Test(dataProvider = "formatDateData")
	public void testFormatDateToUSReverse(Date input, String expected) {
		String actual = DateUtil.formatDateToUSReverse(input);
		assertEquals(actual, expected);
	}

	@DataProvider
	public Object[][] isDateData() {
		Object[] set1 = new Object[] { "2012-12-25", true };
		Object[] set2 = new Object[] { "12/25/2012", true };
		Object[] set3 = new Object[] { "2012/12/25", true };
		Object[] set4 = new Object[] { "25.12.2012", false };
		Object[] set5 = new Object[] { null, false };
		Object[] set6 = new Object[] { "", false };
		Object[] set7 = new Object[] { "  ", false };
		Object[] set8 = new Object[] { "2012-02-29", true };
		Object[] set9 = new Object[] { "2011-02-29", false };
		Object[] set10 = new Object[] { "2012-11-31", false };
		
		return new Object[][] {
			set1, set2, set3, set4, set5,
			set6, set7, set8, set9, set10
		};
	}

	@DataProvider
	public Object[][] formatDateData(Method method) {
		Calendar c1, c2, c3, c4;
		c1 = Calendar.getInstance();
		c2 = (Calendar)c1.clone();
		c3 = (Calendar)c1.clone();
		c4 = (Calendar)c1.clone();
		c1.set(2012, Calendar.DECEMBER, 25);
		c2.set(2012, Calendar.DECEMBER, 26);
		c3.set(2013, Calendar.JANUARY, 1);
		c4.set(2013, Calendar.NOVEMBER, 13);
		
		Object[] set1, set2, set3, set4;
		set1 = set2 = set3 = set4 = null;
		
		if ("testFormatDateToInt".equals(method.getName())) {
			set1 = new Object[] { c1.getTime(), 20121225 };
			set2 = new Object[] { c2.getTime(), 20121226 };
			set3 = new Object[] { c3.getTime(), 20130101 };
			set4 = new Object[] { c4.getTime(), 20131113 };
		} else if ("testFormatDateToISO8601".equals(method.getName())) {
			set1 = new Object[] { c1.getTime(), "2012-12-25" };
			set2 = new Object[] { c2.getTime(), "2012-12-26" };
			set3 = new Object[] { c3.getTime(), "2013-01-01" };
			set4 = new Object[] { c4.getTime(), "2013-11-13" };
		} else if ("testFormatDateToUS".equals(method.getName())) {
			set1 = new Object[] { c1.getTime(), "12/25/2012" };
			set2 = new Object[] { c2.getTime(), "12/26/2012" };
			set3 = new Object[] { c3.getTime(), "01/01/2013" };
			set4 = new Object[] { c4.getTime(), "11/13/2013" };
		} else if ("testFormatDateToUSReverse".equals(method.getName())) {
			set1 = new Object[] { c1.getTime(), "2012/12/25" };
			set2 = new Object[] { c2.getTime(), "2012/12/26" };
			set3 = new Object[] { c3.getTime(), "2013/01/01" };
			set4 = new Object[] { c4.getTime(), "2013/11/13" };
		}
		
		return new Object[][] {
			set1, set2, set3, set4,
		};
	}

}
