package com.yglab.nlp.postag.morph;

import java.util.HashMap;
import java.util.Map;


/**
 * This class defines a morpheme.
 * 
 * @author Younggue Bae
 */
public class Morpheme {
	
	private String key;
	private String surface;
	private String tag;
	private String pos;
	private Map<String, Object> attributes = new HashMap<String, Object>();
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSurface() {
		return surface;
	}

	public void setSurface(String surface) {
		this.surface = surface;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public void setAttribute(String name, Object value) {
		this.attributes.put(name, value);
	}
	
	public Object getAttribute(String name) {
		return this.attributes.get(name);
	}

	@Override
	public String toString() {
		return "Morpheme [key=" + key + ", surface=" + surface + ", tag=" + tag + ", pos=" + pos
				+ ", attributes=" + attributes + "]";
	}

}
