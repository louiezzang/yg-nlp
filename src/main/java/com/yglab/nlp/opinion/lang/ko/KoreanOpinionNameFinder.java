package com.yglab.nlp.opinion.lang.ko;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.ner.NameFeatureGenerator;
import com.yglab.nlp.opinion.OpinionNameFinder;
import com.yglab.nlp.opinion.TokenPostagPairGenerator;
import com.yglab.nlp.postag.lang.ko.Eojeol;
import com.yglab.nlp.postag.lang.ko.Morpheme;
import com.yglab.nlp.tokenizer.Tokenizer;
import com.yglab.nlp.util.Span;

/**
 * The class trains the opinion finder and extracts the opinions from the sentence.
 * 
 * @author Younggue Bae
 */
public class KoreanOpinionNameFinder extends OpinionNameFinder {

	/**
	 * Initializes the opinion finder with the specified model.
	 * 
	 * @param model
	 * @param featureGenerator
	 * @param tokenizer
	 * @param tokenGenerator
	 */
	public KoreanOpinionNameFinder(AbstractModel model, NameFeatureGenerator featureGenerator,
			Tokenizer tokenizer, TokenPostagPairGenerator tokenPairGenerator) {
		super(model, featureGenerator, tokenizer, tokenPairGenerator);
	}

	@Override
	public Span[] find(String s) {
		KoreanTokenPostagPairGenerator koTokenGenerator = (KoreanTokenPostagPairGenerator) tokenPairGenerator;
		List<Span> nameSpans = new ArrayList<Span>();
		
		Span[] tokenSpans = this.tokenize(s);
		String[] tokens = Span.spansToStrings(tokenSpans, s);
		List<Eojeol> eojeols = koTokenGenerator.getEojeols(tokens);
		
		tokens = koTokenGenerator.generate(tokens);
		Span[] nameTokenSpans = this.findMaxent(tokens);
		
		for (Span nameTokenSpan : nameTokenSpans) {
			int start = nameTokenSpan.getStart();
			int end = nameTokenSpan.getEnd();
			String type = nameTokenSpan.getType();
			
			StringBuilder stemWords = new StringBuilder();
			for (int i = start; i < end; i++) {
				Eojeol eojeol = eojeols.get(i);
				if (i == end - 1 && (eojeol.containsPos("J") || eojeol.containsPos("E"))) {
					List<Morpheme> morphs = eojeol.getMorphemes();
					if (morphs.size() > 0) {
						for (int j = 0; j < morphs.size(); j++) {
							Morpheme morph = morphs.get(j);
							String postag = morph.getPos();
							if (!postag.startsWith("J") && !postag.startsWith("E")) {
							//if (!postag.startsWith("J")) {
								stemWords.append(morph.getStem());
							}
							//else if (j == morphs.size() - 1 && postag.startsWith("E")) {
							//	stemWords.append("다");
							//}
						}
					}
				}
				else {
					stemWords.append(eojeol.getSurface()).append(" ");
				}
			}

			int origNameStart = tokenSpans[start].getStart();
			int origNameEnd = tokenSpans[end - 1].getEnd();
			Span nameSpan = new Span(origNameStart, origNameEnd, type);
			nameSpan.setAttribute("stemWords", stemWords.toString().trim());
			nameSpans.add(nameSpan);			
		}
		
		return nameSpans.toArray(new Span[nameSpans.size()]);
	}

}
