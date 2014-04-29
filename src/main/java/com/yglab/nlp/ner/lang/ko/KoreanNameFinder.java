package com.yglab.nlp.ner.lang.ko;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.ner.NameFeatureGenerator;
import com.yglab.nlp.ner.NameFinder;
import com.yglab.nlp.postag.lang.ko.Eojeol;
import com.yglab.nlp.postag.lang.ko.KoreanPOSTagger;
import com.yglab.nlp.postag.morph.Morpheme;
import com.yglab.nlp.tokenizer.Tokenizer;
import com.yglab.nlp.util.Span;

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
		
		/* The size of eojeols is same as that of tokens. */
		List<Eojeol> eojeols = posTagger.analyze(tokens);

		List<Span> analSpans = new ArrayList<Span>();

		int position = 0;
		int origSpanIndex = 0;
		for (int i = 0; i < eojeols.size(); i++) {
			Eojeol eojeol = eojeols.get(i);
			Span origSpan = origSpans[origSpanIndex];

			System.out.println(i + ": " + eojeols.get(i).toString());

			/* If the eojeol contains josa. */
			if (eojeol.containsPos("J")) {
				List<Morpheme> morphs = eojeol.getMorphemes();
				/* If the eojeol consists of the analyzed morphemes in it. */
				if (morphs.size() > 0) {
					for (int j = 0; j < morphs.size(); j++) {
						String morph = morphs.get(j).getSurface();
						int length = morph.length();
						Span span = new Span(position, position + length);
						analSpans.add(span);
						if (j < morphs.size() - 1) {
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
			} else {
				analSpans.add(origSpan);
				position = origSpan.getEnd() + 1;
				origSpanIndex++;
			}
		}

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
