package com.yglab.nlp.postag.lang.ko;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yglab.nlp.postag.Stemmer;
import com.yglab.nlp.util.lang.ko.MorphemeUtil;



/**
 * The Korean stemmer extracts its stem from the surface word.
 * 
 * @author Younggue Bae
 */
public class KoreanStemmer implements Stemmer {

	private static final Pattern RULE_PATTERN = Pattern.compile("([-\\+_])([^-\\+_]+)");
	
	@Override
	public CharSequence stem(CharSequence word, String rule) {
		Matcher m = RULE_PATTERN.matcher(rule);

		while (m.find()) {
			String operator = m.group(1);
			String value = m.group(2);
			
			// remove
			if (operator.equals("-")) {
				CharSequence truncatedWord = MorphemeUtil.truncateRight(word.toString(), value);
				if (!truncatedWord.toString().equals(word.toString())) {
					word = truncatedWord;
				}
				// if nothing is truncated
				else {
					//return word;
				}
			}
			// append
			else if (operator.equals("+")) {
				word = MorphemeUtil.appendRight(word.toString(), value);
			}
			// replace
			else if (operator.equals("_")) {
				word = value;
			}
		}
		
		return word;
	}

}
