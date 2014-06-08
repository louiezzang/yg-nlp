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
	private boolean analyzed;
	private Map<String, Object> attributes = new HashMap<String, Object>();
	
	/**
	 * Default constructor.
	 */
	public Morpheme() {
		
	}
	
	/**
	 * Deep copies the given morpheme.
	 * 
	 * @param morpheme
	 */
	public Morpheme(Morpheme morpheme) {
		this.key = morpheme.getKey();
		this.surface = morpheme.getSurface();
		this.tag = morpheme.getTag();
		this.pos = morpheme.getPos();
		this.analyzed = morpheme.isAnalyzed();
		this.attributes.putAll(morpheme.getAttributes());
	}
	
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

	public boolean isAnalyzed() {
		return analyzed;
	}

	public void setAnalyzed(boolean analyzed) {
		this.analyzed = analyzed;
	}
	
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}
	
	public void setAttribute(String key, Object value) {
		this.attributes.put(key, value);
	}
	
	public Object getAttribute(String key) {
		return this.attributes.get(key);
	}
	
	public boolean containsAttributeKey(String key) {
		return attributes.containsKey(key);
	}
	
	@Override
	public String toString() {
		return "Morpheme [key=" + key + ", surface=" + surface + ", tag=" + tag + ", pos=" + pos
				+ ", attributes=" + attributes + "]";
	}

}
