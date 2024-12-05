package com.macys.uop.foundation.core.utils.test.model;

import java.util.Enumeration;

public class ValueEnumeration implements Enumeration<String> {
	
	private int count=0;
	private String[] keys= {"V1","V2","V3"};
	
	public ValueEnumeration() { }
	
	@Override
	public boolean hasMoreElements() {
		return (count<3);
	}

	@Override
	public String nextElement() {
		String result=keys[count];
		count=count+1;
		return result;
	}

}
