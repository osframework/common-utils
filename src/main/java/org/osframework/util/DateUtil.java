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
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
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
 * 	<table border="0" cellspacing="3" cellpadding="0">
 * 		<tr>
 * 			<th align="left">Format</th>
 * 			<th align="left">Pattern</th>
 * 			<th align="left">Example</th>
 * 		</tr>
 * 		<tr>
 * 			<td><code>ISO-8601</code></td>
 * 			<td>yyyy-MM-dd</td>
 * 			<td><code>2012-12-25</code></td>
 * 		</tr>
 * 		<tr>
 * 			<td><code>US</code></td>
 * 			<td>MM/dd/yyyy</td>
 * 			<td><code>12/25/2012</code></td>
 * 		</tr>
 * 		<tr>
 * 			<td><code>US REVERSE</code></td>
 * 			<td>yyyy/MM/dd</td>
 * 			<td><code>2012/12/25</code></td>
 * 		</tr>
 * 	</table>
 * </blockquote>
 *
 * @author <a href="mailto:dave@osframework.org">Dave Joyce</a>
 */
public final class DateUtil {

	static final int DATE_ISO8601    = 0;
	static final int DATE_US         = 1;
	static final int DATE_US_REVERSE = 2;

	static final String REGEX_DATE_ISO8601 = "\\d{4}-\\d{2}-\\d{2}";
	static final String REGEX_DATE_US = "\\d{2}/\\d{2}/\\d{4}";
	static final String REGEX_DATE_US_REVERSE = "\\d{4}/\\d{2}/\\d{2}";

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
	 * Determine if the given string is a valid date representation.
	 * 
	 * @param s string to be examined
	 * @return <code>true</code> if string represents a date,
	 *         <code>false</code> otherwise
	 */
	public static boolean isDate(String s) {
		boolean valid = !StringUtils.isBlank(s);
		if (valid) {
			valid = (-1 != findArrayIndex(s));
		}
		return valid;
	}

	/**
	 * Set the time parts of the specified date to 00:00:00 (midnight).
	 * 
	 * @param d date to be set to midnight
	 * @return date representing midnight of the given date
	 */
	public static Date forceMidnight(Date d) {
		return forceMidnight(new DateTime(d)).toDate();
	}

	/**
	 * Format the specified date to an <code>int</code> value.
	 * 
	 * @param d date to be converted to integer
	 * @return int value of the given date (<code>yyyyMMdd</code>)
	 */
	public static int formatDateToInt(Date d) {
		String s = FORMAT_DATE_ISO8601_INT.print(forceMidnight(new DateTime(d)));
		return Integer.parseInt(s);
	}

	/**
	 * Format the specified date to an ISO-8601 standard compliant string
	 * representation.
	 * 
	 * @param d date to be converted to string
	 * @return ISO-8601 standard date string
	 */
	public static String formatDateToISO8601(Date d) {
		return FORMAT_DATE_ISO8601.print(forceMidnight(new DateTime(d)));
	}

	/**
	 * Format the specified date to US standard convention string
	 * representation.
	 * 
	 * @param d date to be converted to string
	 * @return US standard convention date string
	 */
	public static String formatDateToUS(Date d) {
		return FORMAT_DATE_US.print(forceMidnight(new DateTime(d)));
	}

	/**
	 * Format the specified date to US reverse convention string
	 * representation.
	 * 
	 * @param d date to be converted to string
	 * @return US reverse convention date string
	 */
	public static String formatDateToUSReverse(Date d) {
		return FORMAT_DATE_US_REVERSE.print(forceMidnight(new DateTime(d)));
	}

	public static Date parseDate(String s) {
		int idx = findArrayIndex(s);
		if (-1 == idx) {
			throw new IllegalArgumentException("Invalid date string: " + s);
		}
		DateTimeFormatter dtf = FORMAT_ARRAY[idx];
		return dtf.parseDateTime(s).toDate();
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
			if (p.matcher(s).matches()) {
				idx = i;
				break;
			}
		}
		return idx;
	}

}
