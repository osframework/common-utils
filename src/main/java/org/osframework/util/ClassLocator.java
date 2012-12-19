/*
 * File: ClassLocator.java
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

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utility for searching accessible classpath for classes.
 * 
 * @author <a href="mailto:dave@osframework.org">Dave Joyce</a>
 */
public class ClassLocator<T> {

	private static final String METHOD_CLASSPATH = "getClassPath";
	private static final String PROPERTY_CLASSPATH = "java.class.path";
	private static final String EXTENSION_CLASS = ".class";

	/**
	 * Classloader to be used to obtain resources from file system.
	 */
	private ClassLoader classloader = null;

	private Class<T> interfaceClass;
	private List<Class<? extends T>> list;

	/**
	 * Constructor - description here.
	 */
	private ClassLocator() {
		this.list = new ArrayList<Class<? extends T>>();
	}

	/**
	 * Search for list of classes on the classpath that implement and/or extend
	 * the services specified by the given interface class.
	 * 
	 * @param interfaceClass
	 * @return
	 */
	public static <T> List<Class<? extends T>> searchProviderClasses(Class<T> interfaceClass) {
		ClassLocator<T> locator = new ClassLocator<T>();
		return locator.findProviderClasses(interfaceClass);
	}

	private List<Class<? extends T>> findProviderClasses(Class<T> interfaceClass) {
		this.interfaceClass = interfaceClass;
		this.classloader = ClassLoader.class.getClassLoader();
		String classpath = null;
		try {
			Method m = this.classloader.getClass().getMethod(METHOD_CLASSPATH, (Class<?>)null);
			if (null != m) {
				classpath = (String)m.invoke(this.classloader, (Object)null);
			}
		} catch (Exception e) {
			// Ignore
		}
		if (null == classpath) {
			classpath = System.getProperty(PROPERTY_CLASSPATH);
		}
		StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);
		String token, name;
		File file;
		while (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			file = new File(token);
			if (file.isDirectory()) {
				this.findInDirectory("", file);
			} else if (file.isFile()) {
				name = file.getName().toLowerCase();
				if (name.endsWith(".jar") || name.endsWith(".jar")) {
					this.findInArchive(file);
				}
			}
		}
		return this.list;
	}

	private void findInDirectory(String name, File directory) {
		File[] files = directory.listFiles();
		File file = null;
		String fileName = null;
		int dirSize = files.length;
		for (int i = 0; i < dirSize; i++) {
			file = files[i];
			fileName = file.getName();
			if (file.isFile() && fileName.toLowerCase().endsWith(EXTENSION_CLASS)) {
				// Strip .class from filename
				fileName = fileName.substring(0, (fileName.length() - 6));
				try {
					Class<?> cls = Class.forName((name + fileName), false, this.classloader);
					if (this.interfaceClass.isAssignableFrom(cls)) {
						@SuppressWarnings("unchecked")
						Class<T> castCls = (Class<T>) this.interfaceClass.asSubclass(cls);
						this.list.add(castCls);
					}
				} catch (ClassNotFoundException cnfe) {
					// Ignore
				} catch (NoClassDefFoundError ncdfe) {
					// Ignore
				} catch (ExceptionInInitializerError eiie) {
					if (eiie.getCause() instanceof HeadlessException) {
						// Ignore
					} else {
						throw eiie;
					}
				}
			} else if (file.isDirectory()) {
				findInDirectory((name + fileName + "."), file);
			}
		}
	}

	private void findInArchive(File archive) {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(archive);
		} catch (IOException ioe) {
			// Ignore
			return;
		}
		Enumeration<JarEntry> jarEntries = jarFile.entries();
		JarEntry curEntry;
		String entryName;
		while (jarEntries.hasMoreElements()) {
			curEntry = jarEntries.nextElement();
			entryName = curEntry.getName();
			if (entryName.toLowerCase().endsWith(EXTENSION_CLASS)) {
				entryName = entryName.substring(0, (entryName.length() - 6)).replace('/', '.');
				try {
					Class<?> cls = Class.forName(entryName, false, this.classloader);
					if (this.interfaceClass.isAssignableFrom(cls)) {
						@SuppressWarnings("unchecked")
						Class<T> castCls = (Class<T>) this.interfaceClass.asSubclass(cls);
						this.list.add(castCls);
					}
				} catch (Throwable t) {
					// Ignore
				}
			}
		}
	}

}
