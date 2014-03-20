package com.yglab.nlp.parser.dep.lang.ko;

import java.util.Collections;
import java.util.List;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.parser.Parse;
import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.parser.dep.DependencyFeatureGenerator;
import com.yglab.nlp.parser.dep.DependencyParser;
import com.yglab.nlp.postag.POSTagger;
import com.yglab.nlp.postag.lang.ko.Eojeol;
import com.yglab.nlp.postag.lang.ko.KoreanPOSTagger;
import com.yglab.nlp.postag.lang.ko.Morpheme;
import com.yglab.nlp.tokenizer.Tokenizer;

/**
 * The dependency parser for Korean.
 * 
 * @author Younggue Bae
 */
public class KoreanDependencyParser extends DependencyParser {
	
	private Tokenizer tokenizer;
	
	/**
	 * Initializes the dependency parser with the specified model.
	 * 
	 * @param model
	 * @param featureGenerator
	 * @param posTagger
	 */
	public KoreanDependencyParser(AbstractModel model,
			DependencyFeatureGenerator<ParseSample> featureGenerator, Tokenizer tokenizer, POSTagger posTagger) {
		super(model, featureGenerator, posTagger);
		this.tokenizer = tokenizer;
	}
	
	@Override
	public List<Parse> parse(String[] tokens) {
		String[] atokens = new String[tokens.length + 1];
		String[] cpostags = new String[tokens.length + 1];
		String[] postags = new String[tokens.length + 1];
		String[] lemmas = new String[tokens.length + 1];
		
		// add dummy ROOT
		atokens[0] = "<root>";
		cpostags[0] = "<root-CPOS>";
		postags[0] = "<root-POS>";
		lemmas[0] = "<root-LEMMA>";
		
		KoreanPOSTagger posTaggerKo = (KoreanPOSTagger) posTagger;
		List<Eojeol> eojeols = posTaggerKo.analyze(tokens);
		
		for (int i = 0; i < eojeols.size(); i++) {
			Eojeol eojeol = eojeols.get(i);
			int index = i + 1;
			atokens[index] = eojeol.getSurface();
			cpostags[index] = eojeol.getTag();
			postags[index] = eojeol.getTag();
			lemmas[index] = eojeol.getSurface();
			
			if (eojeol.containsPos("J") || eojeol.containsPos("E")) {
				List<Morpheme> morphs = eojeol.getMorphemes();
				if (morphs.size() > 0) {
					StringBuilder sbTag = new StringBuilder();
					for (int j = 0; j < morphs.size(); j++) {
						Morpheme morph = morphs.get(j);
						String postag = morph.getPos();
						if (postag.startsWith("J") || postag.startsWith("E")) {
							sbTag.append(postag + "_" + morph.getStem());
							if (j < morphs.size() - 1) {
								sbTag.append(",");
							}
						}
						else {
							if (postag.startsWith("V")) {
								lemmas[index] = morph.getStem();
							}
							sbTag.append(postag);
							if (j < morphs.size() - 1) {
								sbTag.append(",");
							}
						}
					}
					postags[index] = sbTag.toString();
				}
			}
		}
		
		ParseSample instance = new ParseSample(atokens, lemmas, cpostags, postags, null);
		List<Parse> parses = super.parse(instance, 1).get(0);
		for (int i = 0; i < parses.size(); i++) {
			Parse parse = parses.get(i);
			int index = parse.getIndex() - 1;	// because index 0 is "root" node
			Eojeol eojeol = eojeols.get(index);
			parse.setAttribute("eojeol", eojeol);
		}
		// sort by index
		Collections.sort(parses);
		
		return parses;
	}
	
	public List<Parse> parse(String s) {
		String[] tokens = tokenizer.tokenize(s);
		
		return this.parse(tokens);
	}

}
