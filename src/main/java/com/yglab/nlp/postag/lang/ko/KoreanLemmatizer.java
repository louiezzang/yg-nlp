package com.yglab.nlp.postag.lang.ko;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yglab.nlp.postag.morph.Lemmatizer;
import com.yglab.nlp.postag.morph.Morpheme;
import com.yglab.nlp.postag.morph.Token;
import com.yglab.nlp.util.lang.ko.KoreanMorphemeUtil;

/**
 * The Korean lemmatizer extracts the lemma from the surface word.
 * 
 * @author Younggue Bae
 */
public class KoreanLemmatizer implements Lemmatizer<Token> {
	
	/** the pattern for finding tag to lemmatize */
	private static final Pattern LEMMA_TAG_PATTERN = Pattern.compile(
			"([^/\\+\\(\\)]*)/([VN][A-Z]+|XSV|XSA|NNB)");
	
	/** the pattern for finding noun term */
	private static final Pattern NOUN_TAG_PATTERN = Pattern.compile(
			"([^/\\+\\(\\)]*)/(NNG|NNP|NP)");
	
	/** the pattern for finding verb term */
	private static final Pattern VERB_TAG_PATTERN = Pattern.compile(
			"([^/\\+\\(\\)]*)/(VV|VA|VX|XSV|XSA)");

	@Override
	public Token lemmatize(Token token) {
		String head = token.getHead();
		String lemma = head;
		
		for (int i = token.size() - 1; i >= 0; i--) {
			Morpheme morpheme = token.get(i);
			String tag = morpheme.getTag();
			
			if (lemma != null && !lemma.equals("")) {
				if (morpheme.containsAttributeKey("leftLemmatizationRule")) {	
					String lemmatizeRule = (String) morpheme.getAttribute("leftLemmatizationRule");
					String[] arrRule = lemmatizeRule.split("\\s");
					for (String rule : arrRule) {
						/* insertion */
						if (rule.startsWith("+")) {
							lemma = KoreanMorphemeUtil.appendRight(lemma, rule.substring(1));
						}
						/* deletion */
						else if (rule.startsWith("-")) {
							lemma = KoreanMorphemeUtil.truncateRight(lemma, rule.substring(1));
						}
					}
				}
				
				if (i == token.size() - 1) {
					if (morpheme.isAnalyzed()) {
						morpheme.setTag(lemma + tag);
						morpheme.setSurface(lemma + morpheme.getSurface());
					}
				}
				else if (i == token.size() - 2) {
					Morpheme leftMorpheme = token.get(i + 1);
					if (!leftMorpheme.isAnalyzed()) {
						leftMorpheme.setTag(lemma + "/" + leftMorpheme.getTag());
						leftMorpheme.setSurface(lemma);
						
						Matcher noun = NOUN_TAG_PATTERN.matcher(leftMorpheme.getTag());
						if (noun.find()) {
							token.setAttribute("noun", lemma);
						}
						Matcher verb = VERB_TAG_PATTERN.matcher(leftMorpheme.getTag());
						if (verb.find()) {
							token.setAttribute("verb", lemma);
						}
					}
				}
			}
			
			Matcher lemmaMatcher = LEMMA_TAG_PATTERN.matcher(tag);
			while (lemmaMatcher.find()) {
				if (lemma == null) {
					lemma = lemmaMatcher.group(1);
				}
				else {
					lemma += lemmaMatcher.group(1);
				}
			}
			
			Matcher nounMatcher = NOUN_TAG_PATTERN.matcher(tag);
			if (nounMatcher.find()) {
				token.setAttribute("noun", lemma);
			}
			
			Matcher verbMatcher = VERB_TAG_PATTERN.matcher(tag);
			if (verbMatcher.find()) {
				token.setAttribute("verb", lemma);
			}
		}
		
		return token;
	}

}
