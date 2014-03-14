package com.yglab.nlp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maintains a two-way map between a set of objects and contiguous integers from 0 to the number of objects. Use get(i)
 * to look up object i, and indexOf(object) to look up the index of an object.
 * 
 */
public class Index implements Serializable {

	private static final long serialVersionUID = 7359057727178680622L;
	List<Object> objects = new ArrayList<Object>();
	Map<Object, Integer> indexes = new HashMap<Object, Integer>();

	public boolean add(Object o) {
		Integer index = indexes.get(o);
		if (index == null) {
			index = objects.size();
			objects.add(o);
			indexes.put(o, index);
			return true;
		}
		return false;
	}

	public int indexOf(Object o) {
		Integer index = indexes.get(o);
		if (index == null) {
			return -1;
		} else {
			return index;
		}
	}

	public Object get(int i) {
		return objects.get(i);
	}

	public int size() {
		return objects.size();
	}

	public String toString() {
		StringBuilder buff = new StringBuilder("[");
		int sz = objects.size();
		int i;
		for (i = 0; i < sz; i++) {
			Object e = objects.get(i);
			buff.append(i).append("=").append(e);
			if (i < (sz - 1))
				buff.append(",");
		}
		buff.append("]");
		return buff.toString();
	}
}