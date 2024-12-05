package com.macys.uop.foundation.core.utils.test.model;

import java.util.Enumeration;

public class KeyEnumeration implements Enumeration<String> {
	
	private int count=0;
	private String[] values= {"K1","K2","K3"};
	
	public KeyEnumeration() { }
	
	@Override
	public boolean hasMoreElements() {
		return (count<3);
	}

	@Override
	public String nextElement() {
		String result=values[count];
		count=count+1;
		return result;
	}

}
