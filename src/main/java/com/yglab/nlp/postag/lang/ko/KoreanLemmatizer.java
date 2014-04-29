package com.yglab.nlp.postag.lang.ko;

import com.yglab.nlp.postag.morph.Lemmatizer;
import com.yglab.nlp.postag.morph.Token;

/**
 * The Korean lemmatizer extracts the lemma from the surface word.
 * 
 * @author Younggue Bae
 */
public class KoreanLemmatizer implements Lemmatizer {

	//private static final Pattern RULE_PATTERN = Pattern.compile("([-\\+_])([^-\\+_]+)");

	@Override
	public CharSequence lemmatize(Token token) {
		return null;
	}
	
	/*
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
	*/

}
