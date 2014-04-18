package com.yglab.nlp.postag;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yglab.nlp.io.SampleParser;
import com.yglab.nlp.tokenizer.WhitespaceTokenizer;
import com.yglab.nlp.util.InvalidFormatException;

/**
 * The parser for POS sample.
 * 
 * @author Younggue Bae
 */
public class POSSampleParser implements SampleParser<POSSample> {

	private static final Pattern TAG_PATTERN = Pattern.compile("<([^<>\\s]*)?>");
	private static final Pattern POS_PATTERN = Pattern.compile("([^/\\+\\(\\)]*)?");
	private static final Pattern MORPH_POS_PATTERN = Pattern.compile("([^/\\+\\(\\)]*)/([^/\\+\\(\\)]*)?");
	private static final Pattern RULE_PATTERN = Pattern.compile("([\\(])([^/\\(\\)]*)([\\)])?");

	private WhitespaceTokenizer tokenizer;

	public POSSampleParser() {
		tokenizer = new WhitespaceTokenizer();
	}
	
	@Override
	public POSSample parse(String sentence) throws InvalidFormatException {

		String[] parts = tokenizer.tokenize(sentence);

		List<String> tokens = new ArrayList<String>();
		List<String> labels = new ArrayList<String>();

		for (int pi = 0; pi < parts.length; pi++) {
			boolean match = false;
			Matcher tagMatcher = TAG_PATTERN.matcher(parts[pi]);
			while (tagMatcher.find()) {
				match = true;
				String tag = tagMatcher.group();
				tag = tag.replaceAll("<", "").replaceAll(">", "");
				labels.add(tag);
			}
			
			if (!match) {
				throw new InvalidFormatException("Not found the right matched tag inside token '" + parts[pi] + 
						"'! Please check the train sample file whether the tag was defined as the correct pattern: " + TAG_PATTERN);
			}
			
			String[] headtails = parts[pi].split(TAG_PATTERN.toString());
			
			for (String headtail : headtails) {
				tokens.add(headtail);
			}
		}

		return new POSSample(tokens.toArray(new String[tokens.size()]), labels.toArray(new String[labels.size()]));
	}
	
	/**
	 * Parses postype from a single tag(eg. "을/JKO" -> "JKO").
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
	 * Parses morpheme from a single tag(eg. "을/JKO" -> "을").
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
	 * Parses rule from a single tag(eg. "VV(+ㅂ)" -> "+ㅂ").
	 * 
	 * @param tag
	 * @return
	 */
	public static final String parseRule(String tag) {
		Matcher matcher = RULE_PATTERN.matcher(tag);
		if (matcher.find()) {
			return matcher.group(2);
		}
		return null;
	}

}
