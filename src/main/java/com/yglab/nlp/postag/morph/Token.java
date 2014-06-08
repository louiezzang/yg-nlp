package com.yglab.nlp.postag.morph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yglab.nlp.util.lang.ko.KoreanMorphemeUtil;

/**
 * This class defines the token.
 * A token consists of one or more morphemes which are head and tail.
 * 
 * @author Younggue Bae
 */
public class Token extends LinkedList<Morpheme> implements Comparable<Token> {

	private static final long serialVersionUID = 1L;
	
	private String token;
	private String stem;
	private String head;
	private boolean analyzed;
	private boolean validated;
	private Map<String, Object> attributes = new HashMap<String, Object>();

	/**
	 * Creates a token.
	 * 
	 */
	public Token(String token) {
		this.token = token;
	}
	
	/**
	 * Creates a cloned token.
	 * 
	 * @param token	The token to clone
	 */
	public Token(Token token) {
		super(token);
		this.token = token.getToken();
		this.head = token.getHead();
	}
	
	@Override
	public boolean add(Morpheme morph) {
		if (morph != null) {
			this.addLast(morph);
		  return true;
		}
	  return false;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public String getStem() {
		return this.stem;
	}
	
	public void setStem(String stem) {
		this.stem = stem;
	}
	
	public String getHead() {
		return this.head;
	}
	
	public void setHead(String head) {
		this.head = head;
	}
	
	public boolean isAnalyzed() {
		return analyzed;
	}

	public void setAnalyzed(boolean analyzed) {
		this.analyzed = analyzed;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
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

	public String getPos() {
		StringBuilder sb = new StringBuilder();
		for (int i = this.size() - 1; i >= 0; i--) {
			Morpheme morph = this.get(i);
			sb.append(morph.getPos());
			if (i > 0) {
				sb.append("+");
			}
		}
		return sb.toString();
	}
	
	public String getPos(int index) {
		StringBuilder sb = new StringBuilder();
		for (int i = index; i >= 0; i--) {
			Morpheme morph = this.get(i);
			sb.append(morph.getPos());
			if (i > 0) {
				sb.append("+");
			}
		}
		return sb.toString();
	}
	
	public String getTag() {
		StringBuilder sb = new StringBuilder();
		for (int i = this.size() - 1; i >= 0; i--) {
			Morpheme morph = this.get(i);
			sb.append(morph.getTag());
			if (i > 0) {
				sb.append("+");
			}
		}
		return sb.toString();
	}
	
	public String getTag(int index) {
		StringBuilder sb = new StringBuilder();
		for (int i = index; i >= 0; i--) {
			Morpheme morph = this.get(i);
			sb.append(morph.getTag());
			if (i > 0) {
				sb.append("+");
			}
		}
		return sb.toString();		
	}
	
	public String getTagStartsWith(Pattern prefix) {
		StringBuilder sb = new StringBuilder();
		boolean found = false;
		for (int i = this.size() - 1; i >= 0; i--) {
			Morpheme morph = this.get(i);
			Matcher m = prefix.matcher(morph.getTag());
			if (!found && m.find()) {
				sb.append(m.group());
				found = true;
				continue;
			}
			if (found) {
				if (sb.length() > 0) {
					sb.append("+");
				}
				sb.append(morph.getTag());
			}
		}
		return sb.toString();
	}
	
	public Token getTail(int index) {
		Token tail = new Token(token);
		String head = tail.token;
		for (int i = 0; i <= index; i++) {
			Morpheme morph = this.get(i);
			tail.add(morph);
			head = KoreanMorphemeUtil.truncateRight(head, morph.getSurface());
		}
		
		tail.setHead(head);
		
		return tail;
	}
	
	public int getNumTag() {
		return this.getPos().split("\\+").length;
	}
	
	public String getSurface() {
		StringBuilder sb = new StringBuilder();
		for (int i = this.size() - 1; i >= 0; i--) {
			Morpheme morph = this.get(i);
			sb.append(morph.getSurface());
		}
		return sb.toString();
	}
	
	@Override
	public int compareTo(Token other) {
		int otherNumTag = ((Token) other).getNumTag(); 
		//descending order
		return otherNumTag - this.getNumTag();
	}

}
