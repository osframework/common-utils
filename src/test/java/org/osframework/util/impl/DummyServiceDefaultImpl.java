package org.osframework.util.impl;

import org.osframework.util.DummyService;

public class DummyServiceDefaultImpl implements DummyService {

	private final transient String myClassName;

	public DummyServiceDefaultImpl() {
		this.myClassName = this.getClass().getName();
	}

	public String echoClassName() {
		return myClassName;
	}

}
