package com.yglab.nlp.postag.lang.ko;

import java.util.ArrayList;
import java.util.List;
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
			"([^E][^C]\\+|^)([^/\\+\\(\\)]*)/([VN][A-Z]+|XSV|XSA|NNB)");
	
	/** the pattern for finding noun term */
	private static final Pattern NOUN_TAG_PATTERN = Pattern.compile(
			"([^/\\+\\(\\)]*)/(NNG|NNP|NP)");
	
	/** the pattern for finding verb term */
	private static final Pattern VERB_TAG_PATTERN = Pattern.compile(
			"([^/\\+\\(\\)]*)/(VV|VA|VX|XSV|XSA|VCP|VCN)");

	@Override
	public Token lemmatize(Token token) {
		String head = token.getHead();
		String lemma = head;
		
		Morpheme leftMorpheme = null;
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
							//System.err.println("lemma(+)=" + lemma);
						}
						/* deletion */
						else if (rule.startsWith("-")) {
							lemma = KoreanMorphemeUtil.truncateRight(lemma, rule.substring(1));
							//System.err.println("lemma(-)=" + lemma);
						}
					}
					/* add lemma */
					addLemma(token, tag, lemma);
					//System.err.println("lemma(+-)=" + lemma);
				}
				
				if (i == token.size() - 1) {
					if (morpheme.isAnalyzed()) {
						morpheme.setTag(lemma + tag);
						morpheme.setSurface(lemma + morpheme.getSurface());
					}
				}
				else if (i == token.size() - 2) {
					if (leftMorpheme != null && !leftMorpheme.isAnalyzed()) {
						leftMorpheme.setTag(lemma + "/" + leftMorpheme.getTag());
						leftMorpheme.setSurface(lemma);
						/* add lemma */
						addLemma(token, leftMorpheme.getTag(), lemma);
						//System.err.println("lemma(1)=" + lemma);
					}
				}
			}
			
			if (leftMorpheme == null || !leftMorpheme.getTag().endsWith("EC")) {
				Matcher lemmaMatcher = LEMMA_TAG_PATTERN.matcher(tag);
				while (lemmaMatcher.find()) {
					if (lemma == null) {
						lemma = lemmaMatcher.group(2);
					}
					else {
						lemma += lemmaMatcher.group(2);
					}
					//System.err.println("lemma(2)=" + lemma);
				}
				/* add lemma */
				addLemma(token, tag, lemma);
			}
			leftMorpheme = morpheme;
		}
		
		return token;
	}
	
	@SuppressWarnings("unchecked")
	private static void addLemma(Token token, String tag, String lemma) {
		Matcher nounMatcher = NOUN_TAG_PATTERN.matcher(tag);
		if (nounMatcher.find()) {
			if (token.containsAttributeKey("noun")) {
				List<String> nouns = (List<String>) token.getAttribute("noun");
				if (!nouns.contains(lemma)) {
					nouns.add(lemma);
				}
			}
			else {
				List<String> nouns = new ArrayList<String>();
				nouns.add(lemma);
				token.setAttribute("noun", nouns);
			}
		}
		
		Matcher verbMatcher = VERB_TAG_PATTERN.matcher(tag);
		if (verbMatcher.find()) {
			if (token.containsAttributeKey("verb")) {
				List<String> verbs = (List<String>) token.getAttribute("verb");
				if (!verbs.contains(lemma)) {
					verbs.add(lemma);
				}
			}
			else {
				List<String> verbs = new ArrayList<String>();
				verbs.add(lemma);
				token.setAttribute("verb", verbs);
			}
		}
	}

}
