/*
 * File: EqualsUtil.java
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

/**
 * Collected methods which allow easy implementation of <code>equals</code>.
 * 
 * Example use case in a class called Car:
 * 
 * <pre>
 * public boolean equals(Object aThat) {
 * 	if (this == aThat)
 * 		return true;
 * 	if (!(aThat instanceof Car))
 * 		return false;
 * 	Car that = (Car) aThat;
 * 	return EqualsUtil.areEqual(this.fName, that.fName)
 * 			&amp;&amp; EqualsUtil.areEqual(this.fNumDoors, that.fNumDoors)
 * 			&amp;&amp; EqualsUtil.areEqual(this.fGasMileage, that.fGasMileage)
 * 			&amp;&amp; EqualsUtil.areEqual(this.fColor, that.fColor)
 * 			&amp;&amp; Arrays.equals(this.fMaintenanceChecks, that.fMaintenanceChecks); // array!
 * }
 * </pre>
 * 
 * <em>Arrays are not handled by this class</em>. This is because the
 * <code>Arrays.equals</code> methods should be used for array fields.
 * 
 * @author <a href="mailto:dave@osframework.org">Dave Joyce</a>
 */
public final class EqualsUtil {

	private EqualsUtil() {
	}

	public static boolean areEqual(final boolean aThis, final boolean aThat) {
		return (aThis == aThat);
	}

	public static boolean areEqual(final char aThis, final char aThat) {
		return (aThis == aThat);
	}

	public static boolean areEqual(final int aThis, final int aThat) {
		return (aThis == aThat);
	}

	public static boolean areEqual(final long aThis, final long aThat) {
		return (aThis == aThat);
	}

	public static boolean areEqual(final float aThis, final float aThat) {
		return (Float.floatToIntBits(aThis) == Float.floatToIntBits(aThat));
	}

	public static boolean areEqual(final double aThis, final double aThat) {
		return (Double.doubleToLongBits(aThis) == Double.doubleToLongBits(aThat));
	}

	/**
	 * Determine if (possibly null) object fields are equal to each other. This
	 * method handles type-safe enumerations and collections, but does not
	 * handle arrays. See class comment.
	 * 
	 * @param aThis
	 * @param aThat
	 * @return <code>true</code> if the given objects are equal,
	 *         <code>false</code> otherwise
	 */
	public static boolean areEqual(final Object aThis, final Object aThat) {
		return (null == aThis) ? (null == aThat) : aThis.equals(aThat);
	}

}
