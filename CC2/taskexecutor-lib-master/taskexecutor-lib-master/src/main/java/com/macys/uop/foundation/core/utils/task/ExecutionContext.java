package com.macys.uop.foundation.core.utils.task;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExecutionContext implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<String, Object> map = new HashMap<>();

	public void put(String key, Object value) {
		if (value != null) {
			map.put(key, value);
		} else {
			map.remove(key);
		}
	}

	public Object get(String key) {
		return this.map.get(key);
	}
}
