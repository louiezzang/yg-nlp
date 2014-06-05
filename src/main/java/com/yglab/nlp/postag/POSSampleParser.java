package com.yglab.nlp.postag;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.yglab.nlp.io.SampleParser;
import com.yglab.nlp.tokenizer.WhitespaceTokenizer;
import com.yglab.nlp.util.InvalidFormatException;

/**
 * The parser for POS sample.
 * 
 * @author Younggue Bae
 */
public class POSSampleParser implements SampleParser<POSSample> {

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
			Matcher tagMatcher = TagPattern.TAG_PATTERN.matcher(parts[pi]);
			while (tagMatcher.find()) {
				match = true;
				String tag = tagMatcher.group();
				tag = tag.replaceAll("<", "").replaceAll(">", "");
				labels.add(tag);
			}
			
			if (!match) {
				throw new InvalidFormatException("Not found the right matched tag inside token '" + parts[pi] + 
						"'! Please check the train sample file whether the tag was defined as the correct pattern: " + TagPattern.TAG_PATTERN);
			}
			
			String[] headtails = parts[pi].split(TagPattern.TAG_PATTERN.toString());
			
			for (String headtail : headtails) {
				tokens.add(headtail);
			}
		}

		return new POSSample(tokens.toArray(new String[tokens.size()]), labels.toArray(new String[labels.size()]));
	}

}
