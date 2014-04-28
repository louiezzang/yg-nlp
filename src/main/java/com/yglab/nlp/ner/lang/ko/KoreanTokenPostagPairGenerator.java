package com.yglab.nlp.ner.lang.ko;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.ner.TokenPostagPairGenerator;
import com.yglab.nlp.postag.lang.ko.Eojeol;
import com.yglab.nlp.postag.lang.ko.KoreanPOSTagger;
import com.yglab.nlp.postag.lang.ko.Morpheme;


/**
 * This class generations the pairs of token with its postag.
 * 
 * @author Younggue Bae
 */
public class KoreanTokenPostagPairGenerator implements TokenPostagPairGenerator {
	
	private KoreanPOSTagger posTagger;
	private String delimiter;
	
	public KoreanTokenPostagPairGenerator(KoreanPOSTagger posTagger) {
		this(posTagger, "\t");
	}
	
	public KoreanTokenPostagPairGenerator(KoreanPOSTagger posTagger, String delimiter) {
		this.posTagger = posTagger;
		this.delimiter = delimiter;
	}

	@Override
	public String[] generate(String[] tokens) {
		List<Eojeol> eojeols = posTagger.analyze(tokens);
		
		List<String> tokenList = new ArrayList<String>();
		
		for (int i = 0; i < eojeols.size(); i++) {
			Eojeol eojeol = eojeols.get(i);
			String token = eojeol.getSurface();
			//System.out.println(eojeol);
			
			if (eojeol.containsPos("J") || eojeol.containsPos("E")) {
				StringBuilder sbTag = new StringBuilder();
				List<Morpheme> morphs = eojeol.getMorphemes();
				if (morphs.size() > 0) {
					for (int j = 0; j < morphs.size(); j++) {
						Morpheme morph = morphs.get(j);
						String postag = morph.getPos();
						if (postag.startsWith("J") || postag.startsWith("E")) {
							sbTag.append(postag + "_" + morph.getLemma());
							if (j < morphs.size() - 1) {
								sbTag.append("+");
							}
						}
						else {
							sbTag.append(postag);
							if (j < morphs.size() - 1) {
								sbTag.append("+");
							}
						}
					}
				}
				else {
					sbTag.append(eojeol.getTag().replaceAll(",", "+"));
				}
				token = token + delimiter + sbTag.toString();
			}
			else {
				token = token + delimiter + eojeol.getTag().replaceAll(",", "+");
			}
			
			tokenList.add(token);
		}
		
		return tokenList.toArray(new String[tokenList.size()]);
	}
	
	public List<Eojeol> getEojeols(String[] tokens) {
		return posTagger.analyze(tokens);
	}
}
