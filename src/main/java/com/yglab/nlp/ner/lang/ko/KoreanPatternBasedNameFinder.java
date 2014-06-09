package com.yglab.nlp.ner.lang.ko;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Span;
import com.yglab.nlp.ner.PatternBasedNameFeatureGenerator;
import com.yglab.nlp.ner.PatternBasedNameFinder;
import com.yglab.nlp.ner.TokenPostagPairGenerator;
import com.yglab.nlp.postag.morph.Token;
import com.yglab.nlp.tokenizer.Tokenizer;

/**
 * This class trains the pattern based name finder and finds the named entity.
 * 
 * @author Younggue Bae
 */
public class KoreanPatternBasedNameFinder extends PatternBasedNameFinder {

	/**
	 * Initializes the name finder with the specified model.
	 * 
	 * @param model
	 * @param featureGenerator
	 * @param tokenizer
	 * @param tokenPairGenerator
	 */
	public KoreanPatternBasedNameFinder(AbstractModel model, PatternBasedNameFeatureGenerator featureGenerator,
			Tokenizer tokenizer, TokenPostagPairGenerator tokenPairGenerator) {
		super(model, featureGenerator, tokenizer, tokenPairGenerator);
	}

	@Override
	public Span[] find(String s) {
		KoreanTokenPostagPairGenerator koTokenGenerator = (KoreanTokenPostagPairGenerator) tokenPairGenerator;
		List<Span> nameSpans = new ArrayList<Span>();
		
		Span[] tokenSpans = this.tokenize(s);
		String[] tokens = Span.spansToStrings(tokenSpans, s);
		
		tokens = koTokenGenerator.generate(tokens);
		List<Token> analTokens = koTokenGenerator.getCurrentAnalyzedTokens();
		Span[] nameTokenSpans = this.findMaxent(tokens);
		
		for (Span nameTokenSpan : nameTokenSpans) {
			int start = nameTokenSpan.getStart();
			int end = nameTokenSpan.getEnd();
			String type = nameTokenSpan.getType();
			
			List<Token> nameTokens = new ArrayList<Token>();
			for (int i = start; i < end; i++) {
				Token analToken = analTokens.get(i);
				nameTokens.add(analToken);
			}

			int origNameStart = tokenSpans[start].getStart();
			int origNameEnd = tokenSpans[end - 1].getEnd();
			Span nameSpan = new Span(origNameStart, origNameEnd, type);
			nameSpan.setAttribute("tokens", nameTokens);
			nameSpans.add(nameSpan);			
		}
		
		return nameSpans.toArray(new Span[nameSpans.size()]);
	}

}
