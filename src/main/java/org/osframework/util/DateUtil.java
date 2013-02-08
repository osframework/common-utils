/*
 * File: DateUtil.java
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

import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Collection of utility methods for validation, conversion, and manipulation
 * of date strings and <code>Date</code> objects. This class is capable of
 * validating and parsing date strings in these formats:
 * <blockquote>
 * 	<table border="1" cellspacing="3" cellpadding="0">
 * 		<tr>
 * 			<th align="left">Format</th>
 * 			<th align="left">Pattern</th>
 * 			<th align="left">Example</th>
 * 		</tr>
 * 		<tr>
 * 			<td><code>ISO-8601</code></td>
 * 			<td>yyyy-MM-dd</code></td>
 * 			<td><code>2012-12-25</code></td>
 * 		</tr>
 * 		<tr>
 * 			<td><code>US</code></td>
 * 			<td><code>MM/dd/yyyy</code></td>
 * 			<td><code>12/25/2012</code></td>
 * 		</tr>
 * 		<tr>
 * 			<td><code>US REVERSE</code></td>
 * 			<td><code>yyyy/MM/dd</code></td>
 * 			<td><code>2012/12/25</code></td>
 * 		</tr>
 * 	</table>
 * </blockquote>
 *
 * @author <a href="mailto:dave@osframework.org">Dave Joyce</a>
 */
public final class DateUtil {

	static final String NULL_ERROR = "Date argument cannot be null";

	static final int DATE_ISO8601    = 0;
	static final int DATE_US         = 1;
	static final int DATE_US_REVERSE = 2;

	/**
	 * Regular expression for ISO-8601 date notation.
	 */
	public static final String REGEX_DATE_ISO8601 = "(\\d{4})-(\\d{2})-(\\d{2})";

	/**
	 * Regular expression for US standard date notation.
	 */
	public static final String REGEX_DATE_US = "(\\d{2})/(\\d{2})/(\\d{4})";

	/**
	 * Regular expression for US reverse date notation.
	 */
	public static final String REGEX_DATE_US_REVERSE = "(\\d{4})/(\\d{2})/(\\d{2})";

	static final Pattern PATTERN_DATE_ISO8601 = Pattern.compile(REGEX_DATE_ISO8601);
	static final Pattern PATTERN_DATE_US = Pattern.compile(REGEX_DATE_US);
	static final Pattern PATTERN_DATE_US_REVERSE = Pattern.compile(REGEX_DATE_US_REVERSE);
	static final Pattern[] PATTERN_ARRAY = { PATTERN_DATE_ISO8601, PATTERN_DATE_US, PATTERN_DATE_US_REVERSE };

	static final DateTimeFormatter FORMAT_DATE_ISO8601_INT = ISODateTimeFormat.basicDate();
	static final DateTimeFormatter FORMAT_DATE_ISO8601 = ISODateTimeFormat.date();
	static final DateTimeFormatter FORMAT_DATE_US = DateTimeFormat.forPattern("MM/dd/yyyy").withLocale(Locale.US);
	static final DateTimeFormatter FORMAT_DATE_US_REVERSE = DateTimeFormat.forPattern("yyyy/MM/dd").withLocale(Locale.US);
	static final DateTimeFormatter[] FORMAT_ARRAY = { FORMAT_DATE_ISO8601, FORMAT_DATE_US, FORMAT_DATE_US_REVERSE };

	/**
	 * Private constructor - this class cannot be instantiated.
	 */
	private DateUtil() {}

	/**
	 * Determine if the given string is a valid date representation. A valid
	 * date conforms to this policy:
	 * <ul>
	 * 	<li>All integer parts (year, month, day) are positive values</li>
	 * 	<li>Month value is in range [1,12] inclusive</li>
	 * 	<li>Day value is in range [1,31] inclusive</li>
	 * </ul>
	 * <p>This method is naiive with respect to the month: it makes no effort
	 * to adjust the valid day range by given month.</p>
	 * 
	 * @param s string to be examined
	 * @return <code>true</code> if string represents a date,
	 *         <code>false</code> otherwise
	 */
	public static boolean isDate(String s) {
		boolean valid = !StringUtils.isBlank(s);
		String trimmed = s.trim();
		int idx = -1;
		// 1. Does string match a known notation pattern?
		if (valid) {
			idx = findArrayIndex(trimmed);
			valid = (-1 != idx);
		}
		// 2. Do values fall within accepted ranges?
		if (valid) {
			Pattern p  = PATTERN_ARRAY[idx];
			Matcher m = p.matcher(trimmed);
			valid = m.lookingAt();
			if (valid) {
				int year, month, day;
				switch (idx) {
				case DATE_ISO8601:
				case DATE_US_REVERSE:
					year = Integer.parseInt(m.group(1));
					month = Integer.parseInt(m.group(2));
					day = Integer.parseInt(m.group(3));
					break;
				case DATE_US:
					year = Integer.parseInt(m.group(3));
					month = Integer.parseInt(m.group(1));
					day = Integer.parseInt(m.group(2));
					break;
				default:
					year = month = day = -1;
					break;
				}
				valid = ((0 <= year) && (1 <= month && month <= 12) && (1 <= day && day <= 31));
			}
		}
		return valid;
	}

	/**
	 * Set the time parts of the specified date to 00:00:00 (midnight).
	 * 
	 * @param d date to be set to midnight
	 * @return date representing midnight of the given date
	 * @throws IllegalArgumentException if argument is null
	 */
	public static Date forceMidnight(Date d) {
		Validate.notNull(d, NULL_ERROR);
		return forceMidnight(new DateTime(d)).toDate();
	}

	/**
	 * Format the specified date to an <code>int</code> value.
	 * 
	 * @param d date to be converted to integer
	 * @return int value of the given date (<code>yyyyMMdd</code>)
	 * @throws IllegalArgumentException if argument is null
	 */
	public static int formatDateToInt(Date d) {
		Validate.notNull(d, NULL_ERROR);
		String s = FORMAT_DATE_ISO8601_INT.print(forceMidnight(new DateTime(d)));
		return Integer.parseInt(s);
	}

	/**
	 * Format the specified date to an ISO-8601 standard compliant string
	 * representation.
	 * 
	 * @param d date to be converted to string
	 * @return ISO-8601 standard date string
	 * @throws IllegalArgumentException if argument is null
	 */
	public static String formatDateToISO8601(Date d) {
		Validate.notNull(d, NULL_ERROR);
		return FORMAT_DATE_ISO8601.print(forceMidnight(new DateTime(d)));
	}

	/**
	 * Format the specified date to US standard convention string
	 * representation.
	 * 
	 * @param d date to be converted to string
	 * @return US standard convention date string
	 * @throws IllegalArgumentException if argument is null
	 */
	public static String formatDateToUS(Date d) {
		Validate.notNull(d, NULL_ERROR);
		return FORMAT_DATE_US.print(forceMidnight(new DateTime(d)));
	}

	/**
	 * Format the specified date to US reverse convention string
	 * representation.
	 * 
	 * @param d date to be converted to string
	 * @return US reverse convention date string
	 * @throws IllegalArgumentException if argument is null
	 */
	public static String formatDateToUSReverse(Date d) {
		Validate.notNull(d, NULL_ERROR);
		return FORMAT_DATE_US_REVERSE.print(forceMidnight(new DateTime(d)));
	}

	/**
	 * Parse the date represented by the specified string. This method handles
	 * dates in these formats:
	 * <ul>
	 * 	<li>yyyyMMdd</li>
	 * 	<li>yyyy-MM-dd</li>
	 * 	<li>yyyy/MM/dd</li>
	 * 	<li>MM/dd/yyyy</li>
	 * </ul>
	 * 
	 * @param s date string to be parsed
	 * @return represented date
	 * @throws IllegalArgumentException if specified string is empty
	 */
	public static Date parseDate(String s) {
		if (StringUtils.isBlank(s)) {
			throw new IllegalArgumentException("Invalid date string: " + s);
		}
		String trimmed = s.trim();
		Date d = null;
		if (isNumber(trimmed)) {
			d = FORMAT_DATE_ISO8601_INT.parseDateTime(trimmed).toDate();
		} else {
			int idx = findArrayIndex(trimmed);
			if (-1 == idx) {
				throw new IllegalArgumentException("Invalid date string: " + s);
			}
			DateTimeFormatter dtf = FORMAT_ARRAY[idx];
			d = dtf.parseDateTime(trimmed).toDate();
		}
		return d;
	}

	static DateTime forceMidnight(DateTime dt) {
		MutableDateTime mdt = dt.toMutableDateTime();
		mdt.setHourOfDay(0);
		mdt.setMinuteOfHour(0);
		mdt.setSecondOfMinute(0);
		return mdt.toDateTime();
	}

	private static int findArrayIndex(String s) {
		int idx = -1;
		for (int i = 0; i < PATTERN_ARRAY.length; i++) {
			Pattern p = PATTERN_ARRAY[i];
			if (p.matcher(s).lookingAt()) {
				idx = i;
				break;
			}
		}
		return idx;
	}

	private static boolean isNumber(String s) {
		char[] chars = s.toCharArray();
		boolean number = true;
		for (char c : chars) {
			if (!Character.isDigit(c)) {
				number = false;
				break;
			}
		}
		return number;
	}

}
