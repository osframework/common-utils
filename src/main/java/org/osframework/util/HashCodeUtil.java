/*
 * File: HashCodeUtil.java
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

import java.lang.reflect.Array;

/**
 * Collected methods which allow easy implementation of <code>hashCode</code>.
 * <p>Example use case:</p>
 * 
 * <pre>
 * public int hashCode() {
 * 	int result = HashCodeUtil.SEED;
 * 	// collect the contributions of various fields
 * 	result = HashCodeUtil.hash(result, fPrimitive);
 * 	result = HashCodeUtil.hash(result, fObject);
 * 	result = HashCodeUtil.hash(result, fArray);
 * 	return result;
 * }
 * </pre>
 * 
 * @author <a href="http://www.javapractices.com/">Hirondelle Systems</a>
 * @author <a href="mailto:dave@osframework.org">Dave Joyce</a>
 */
public final class HashCodeUtil {

	/**
	 * An initial value for a <code>hashCode</code>, to which is added
	 * contributions from fields. Using a non-zero value decreases collisons of
	 * <code>hashCode</code> values.
	 */
	public static final int SEED = 23;

	/**
	 * Calculate a hash value for a <code>boolean</code> field.
	 * 
	 * @param seed
	 * @param boolField
	 * @return hash value
	 */
	public static int hash(int seed, boolean boolField) {
		return firstTerm(seed) + (boolField ? 1 : 0);
	}

	/**
	 * Calculate a hash value for a <code>char</code> field.
	 * 
	 * @param seed
	 * @param charField
	 * @return hash value
	 */
	public static int hash(int seed, char charField) {
		return firstTerm(seed) + (int)charField;
	}

	/**
	 * Calculate a hash value for a <code>int</code> field. Note that
	 * <code>byte</code> and <code>short</code> are handled by this method,
	 * through implicit conversion.
	 * 
	 * @param seed
	 * @param intField
	 * @return hash value
	 */
	public static int hash(int seed, int intField) {
		return firstTerm(seed) + intField;
	}

	/**
	 * Calculate a hash value for a <code>long</code> field.
	 * 
	 * @param seed
	 * @param longField
	 * @return hash value
	 */
	public static int hash(int seed, long longField) {
		return firstTerm(seed) + (int)(longField ^ (longField >>> 32));
	}

	/**
	 * Calculate a hash value for a <code>float</code> field.
	 * 
	 * @param seed
	 * @param floatField
	 * @return hash value
	 */
	public static int hash(int seed, float floatField) {
		return hash(seed, Float.floatToIntBits(floatField));
	}

	/**
	 * Calculate a hash value for a <code>double</code> field.
	 * 
	 * @param seed
	 * @param doubleField
	 * @return hash value
	 */
	public static int hash(int seed, double doubleField) {
		return hash(seed, Double.doubleToLongBits(doubleField));
	}

	/**
	 * Calculate a hash value for an object field, which may be
	 * <code>null</code> or an array.
	 * 
	 * @param seed seed value for hash calculation
	 * @param obj object for which hash value is calculated
	 * @return hash value
	 */
	public static int hash(int seed, Object obj) {
		int result = seed;
		if (null == obj) {
			result = hash(result, 0);
		} else if (isArray(obj)) {
			int length = Array.getLength(obj);
			for (int i = 0; i < length; i++) {
				Object item = Array.get(obj, i);
				// Recurse
				result = hash(result, item);
			}
		} else {
			result = hash(result, obj.hashCode());
		}
		return result;
	}

	private static final int ODD_PRIME_NUMBER = 37;

	/**
	 * Private constructor - this class cannot be instantiated.
	 *
	 */
	private HashCodeUtil() {}

	private static int firstTerm(int seed) {
		return (ODD_PRIME_NUMBER * seed);
	}

	private static boolean isArray(Object obj) {
		return (null == obj) ? false : obj.getClass().isArray();
	}

}
