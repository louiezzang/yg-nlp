package com.yglab.nlp.postag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for finding pos tag pattern.
 * 
 * @author Younggue Bae
 */
public class TagPattern {

	public static final Pattern TAG_PATTERN = Pattern.compile("<([^<>\\s]*)?>");
	public static final Pattern POS_PATTERN = Pattern.compile("([^/\\+\\(\\)]*)?");
	//public static final Pattern MORPH_POS_PATTERN = Pattern.compile("([^/\\+\\(\\)]*)/([^/\\+\\(\\)]*)?");
	public static final Pattern MORPH_POS_PATTERN = Pattern.compile("([^/\\+\\(\\)]*)/([^/\\+]*)?");
	public static final Pattern POS_INFO_PATTERN = Pattern.compile("([\\(])([^/\\(\\)]*)([\\)])?");
	
	/**
	 * Parses a postype from a single tag(eg. "을/JKO" -> "JKO").
	 * 
	 * @param tag
	 * @return
	 */
	public static final String parsePos(String tag) {
		Matcher matcher = MORPH_POS_PATTERN.matcher(tag);
		if (matcher.find()) {
			return matcher.group(2);
		}
		
		matcher = POS_PATTERN.matcher(tag);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
	
	/**
	 * Parses a morpheme from a single tag(eg. "을/JKO" -> "을").
	 * 
	 * @param tag
	 * @return
	 */
	public static final String parseMorpheme(String tag) {
		Matcher matcher = MORPH_POS_PATTERN.matcher(tag);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
	
	/**
	 * Parses an information from a single pos(eg. "VV(ㅂ)" -> "ㅂ").
	 * 
	 * @param tag
	 * @return
	 */
	public static final String parsePosInfo(String tag) {
		Matcher matcher = POS_INFO_PATTERN.matcher(tag);
		if (matcher.find()) {
			return matcher.group(2);
		}
		return null;
	}
}
