/*
 * File: ServiceClassLoader.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;

/**
 * A simple service-provider loading facility. Replicates the behavior of
 * {@linkplain java.util.ServiceLoader} in every way, except this class does not
 * instantiate the located provider classes. Deferred instantiation is of value
 * in situations where the client needs to perform additional inspection or
 * reflection of the loaded provider class prior to instantiation, or where the
 * provider class has no default constructor.
 *
 * @author <a href="mailto:dave@osframework.org">Dave Joyce</a>
 */
public final class ServiceClassLoader<S> implements Iterable<Class<? extends S>> {

	private static final String PREFIX = "META-INF/services/";

	private Class<S> serviceClass;
	private ClassLoader loader;
	private LinkedHashMap<String, Class<? extends S>> providerClasses = new LinkedHashMap<String, Class<? extends S>>();

	// The current lazy-lookup iterator
    private LazyIterator lookupIterator;

	public static <S> ServiceClassLoader<S> load(Class<S> serviceClass, ClassLoader loader) {
		return new ServiceClassLoader<S>(serviceClass, loader);
	}

	public static <S> ServiceClassLoader<S> load(Class<S> serviceClass) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		return ServiceClassLoader.load(serviceClass, cl);
	}

	public static <S> ServiceClassLoader<S> loadInstalled(Class<S> serviceClass) {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		ClassLoader prev = null;
		while (null != cl) {
			prev = cl;
			cl = cl.getParent();
		}
		return ServiceClassLoader.load(serviceClass, prev);
	}

	public void reload() {
		providerClasses.clear();
		lookupIterator = new LazyIterator(serviceClass, loader);
	}

	public Iterator<Class<? extends S>> iterator() {
		// TODO Auto-generated method stub
		return new Iterator<Class<? extends S>>() {
			Iterator<Map.Entry<String, Class<? extends S>>> knownProviderClasses = providerClasses.entrySet().iterator();
		
			public boolean hasNext() {
				if (knownProviderClasses.hasNext()) {
					return true;
				}
				return lookupIterator.hasNext();
			}
		
			public Class<? extends S> next() {
				if (knownProviderClasses.hasNext()) {
					return knownProviderClasses.next().getValue();
				}
				return lookupIterator.next();
			}
		
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder("org.osframework.util.ServiceClassLoader[")
		                        .append(serviceClass.getName())
		                        .append("]");
		return buf.toString();
	}

    private static void fail(Class<?> service, String msg, Throwable cause)
    	throws ServiceConfigurationError {
    	throw new ServiceConfigurationError(service.getName() + ": " + msg, cause);
    }

    private static void fail(Class<?> service, String msg)
    	throws ServiceConfigurationError {
    	throw new ServiceConfigurationError(service.getName() + ": " + msg);
    }

    private static void fail(Class<?> service, URL u, int line, String msg)
    	throws ServiceConfigurationError {
    	fail(service, u + ":" + line + ": " + msg);
    }

	private ServiceClassLoader(Class<S> serviceClass, ClassLoader loader) {
		this.serviceClass = serviceClass;
		this.loader = loader;
		this.reload();
	}

	private int parseLine(Class<S> serviceClass, URL u, BufferedReader r, int lc, List<String> names)
		throws IOException, ServiceConfigurationError {
		String ln = r.readLine();
		if (null == ln) {
		    return -1;
		}
		// Read everything on line prior to start of comment
		int ci = ln.indexOf('#');
		if (0 <= ci) ln = ln.substring(0, ci);
		ln = ln.trim();
		int n = ln.length();
		if (0 != n) {
			if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0)) {
				fail(serviceClass, u, lc, "Illegal configuration-file syntax");
			}
			int cp = ln.codePointAt(0);
			if (!Character.isJavaIdentifierStart(cp)) {
				fail(serviceClass, u, lc, "Illegal provider-class name: " + ln);
			}
			for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
				cp = ln.codePointAt(i);
				if (!Character.isJavaIdentifierPart(cp) && ('.' != cp)) {
					fail(serviceClass, u, lc, "Illegal provider-class name: " + ln);
				}
			}
			if (!providerClasses.containsKey(ln) && !names.contains(ln)) {
				names.add(ln);
			}
		}
		return lc + 1;
	}

	private Iterator<String> parse(Class<S> serviceClass, URL u)
		throws ServiceConfigurationError {
		InputStream in = null;
		BufferedReader r = null;
		ArrayList<String> names = new ArrayList<String>();
		try {
			in = u.openStream();
			r = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
			int lc = 1;
			while ((lc = parseLine(serviceClass, u, r, lc, names)) >= 0);
		} catch (IOException ioe) {
			fail(serviceClass, "Error reading configuration file", ioe);
		} finally {
			try {
				if (null != r) r.close();
				if (null != in) in.close();
			} catch (IOException ioe) {
				fail(serviceClass, "Error closing configuration file", ioe);
			}
		}
		return names.iterator();
	}

	private class LazyIterator implements Iterator<Class<? extends S>> {
	
		Class<S> serviceClass;
		ClassLoader loader;
		Enumeration<URL> configs = null;
		Iterator<String> pending = null;
		String nextName = null;
	
		private LazyIterator(Class<S> serviceClass, ClassLoader loader) {
			this.serviceClass = serviceClass;
			this.loader = loader;
		}
	
		public boolean hasNext() {
			if (null != nextName) {
				return true;
			}
			if (null == configs) {
				try {
					String fullName = PREFIX + serviceClass.getName();
					configs = (null == loader)
							   ? ClassLoader.getSystemResources(fullName)
							   : loader.getResources(fullName);
				} catch (IOException ioe) {
					fail(serviceClass, "Error locating configuration files", ioe);
				}
			}
			while ((null == pending) || !pending.hasNext()) {
				if (!configs.hasMoreElements()) {
					return false;
				}
				pending = parse(serviceClass, configs.nextElement());
			}
			nextName = pending.next();
			return true;
		}
	
		public Class<? extends S> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			String cn = nextName;
		    nextName = null;
		    Class<?> c = null;
		    try {
		    	c = Class.forName(cn, false, loader);
		    } catch (ClassNotFoundException cnfe) {
		    	fail(serviceClass, "Provider " + cn + " not found");
		    }
		    if (!serviceClass.isAssignableFrom(c)) {
		    	fail(serviceClass, "Provider " + cn  + " not a subtype");
		    }
		    try {
			    Class<? extends S> providerClass = c.asSubclass(serviceClass);
			    providerClasses.put(cn, providerClass);
			    return providerClass;
		    } catch (Throwable t) {
		    	fail(serviceClass, "Provider " + cn + " could not be cast to subtype: " + t, t);
		    }
		    // This cannot happen
		    throw new Error();
		}
	
		public void remove() {
			throw new UnsupportedOperationException();	
		}
	}
}
