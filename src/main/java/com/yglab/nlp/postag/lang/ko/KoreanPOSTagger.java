package com.yglab.nlp.postag.lang.ko;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.postag.POSFeatureGenerator;
import com.yglab.nlp.postag.POSTagger;
import com.yglab.nlp.postag.morph.Token;

/**
 * Korean POS and morpheme tagger based on maximum-entropy markov model or perceptron algorithm.
 * 
 * @author Younggue Bae
 */
public class KoreanPOSTagger extends POSTagger {

	private Properties posDescriptions;
	
	/**
	 * Initializes the Korean pos tagger with the specified model.
	 * 
	 * @param model The trained model
	 * @param featureGenerator The context feature generator
	 * @param dic The dictionary of josa and eomi
	 * @throws IOException
	 */
	public KoreanPOSTagger(AbstractModel model, POSFeatureGenerator featureGenerator) throws IOException {
		super(model, featureGenerator, new KoreanTagSequenceGenerator(featureGenerator, model.getLabels()));
		//super(model, featureGenerator, new DefaultTagSequenceGenerator(featureGenerator, model.getLabels(), 2));

		InputStream is = getClass().getResourceAsStream("/lang/ko/postag_ko.properties");
		this.posDescriptions = new Properties();
	  posDescriptions.load(is);
	}
	
	/**
	 * Analyzes the morphemes of the given tokens.
	 * 
	 * @param tokens
	 * @return
	 */
	public List<Token> analyze(String[] tokens) {
		String[] tags = this.tag(tokens);
		List<Token> eojeols = ((KoreanPOSFeatureGenerator) featureGenerator).getKoreanMorphemeAnalyzer().analyze(tags);
		return eojeols;
	}
	
	/**
	 * Gets the pos description.
	 * 
	 * @param pos
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getPosDescription(String pos) {
		String description = this.posDescriptions.getProperty(pos);
		if (description == null) {
			return "기타";
		}
		else {
			try {
				return new String(description.getBytes("ISO-8859-1"), "UTF-8").trim();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return description;
			}
		}
	}

}