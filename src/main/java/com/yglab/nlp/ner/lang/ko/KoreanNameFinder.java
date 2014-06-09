package com.yglab.nlp.ner.lang.ko;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Span;
import com.yglab.nlp.ner.NameFeatureGenerator;
import com.yglab.nlp.ner.NameFinder;
import com.yglab.nlp.postag.lang.ko.KoreanPOSTagger;
import com.yglab.nlp.postag.morph.Morpheme;
import com.yglab.nlp.postag.morph.Token;
import com.yglab.nlp.tokenizer.Tokenizer;

/**
 * The named entity recognizer for Korean language.
 * 
 * @author Younggue Bae
 */
public class KoreanNameFinder extends NameFinder {

	private KoreanPOSTagger posTagger;
	private Tokenizer tokenizer;

	/**
	 * Initializes the named entity recognizer with the specified model.
	 * 
	 * @param model
	 * @param featureGenerator
	 * @param tokenizer
	 * @param posTagger
	 */
	public KoreanNameFinder(AbstractModel model, NameFeatureGenerator featureGenerator,
			Tokenizer tokenizer, KoreanPOSTagger posTagger) {
		super(model, featureGenerator);

		this.posTagger = posTagger;
		this.tokenizer = tokenizer;
	}

	/**
	 * Tokenizes the input string by the {@link Tokenizer}, and then re-tokenizes any token that
	 * contains josa by {@link KoreanPOSTagger}.
	 * 
	 * @param s
	 *          the input string
	 * @return the array of span with start and end position for the input string
	 */
	public Span[] tokenize(String s) {
		
		Span[] origSpans = tokenizer.tokenizePos(s);
		String[] tokens = tokenizer.tokenize(s);
		
		/* The size of analyzed tokens is same as that of tokens. */
		List<Token> analTokens = posTagger.analyze(tokens);

		List<Span> analSpans = new ArrayList<Span>();

		int position = 0;
		int origSpanIndex = 0;
		for (int ti = 0; ti < analTokens.size(); ti++) {
			Token analToken = analTokens.get(ti);
			Span origSpan = origSpans[origSpanIndex];

			System.out.println(ti + ": " + analToken);

			/* If the analyzed token contains josa. */
			if (analToken.getPos().contains("+J")) {
				for (int mi = analToken.size() - 1; mi >= 0; mi--) {
					Morpheme morpheme = analToken.get(mi);
					String surface = morpheme.getSurface();
					//System.out.println("surface=" + surface);
					int length = surface.length();
					Span span = new Span(position, position + length);
					analSpans.add(span);
					if (mi > 0) {
						position += length;
					}
					else {
						position += (length + 1);
						origSpanIndex++;
					}
				}
			} else {
				analSpans.add(origSpan);
				position = origSpan.getEnd() + 1;
				origSpanIndex++;
			}
		}
		
		System.out.println(analSpans);

		return analSpans.toArray(new Span[analSpans.size()]);
	}

	/**
	 * Finds the named entities for the given input string.
	 * 
	 * @param s
	 *          the input string
	 * @return the array of span with start and end position of the named entities which were found in
	 *         the given input string
	 */
	public Span[] find(String s) {
		Span[] tokenSpans = this.tokenize(s);

		return super.find(s, tokenSpans);
	}

}
