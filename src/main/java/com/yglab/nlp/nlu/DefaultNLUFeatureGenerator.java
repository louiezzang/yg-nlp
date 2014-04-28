package com.yglab.nlp.nlu;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.ner.DefaultNameFeatureGenerator;
import com.yglab.nlp.util.RegexFeatureDictionary;



/**
 * This class generates the contextual features for NLU.
 * 
 * @author Younggue Bae
 */
public class DefaultNLUFeatureGenerator extends DefaultNameFeatureGenerator {

	public DefaultNLUFeatureGenerator(RegexFeatureDictionary featureDic) {
		super(featureDic);
	}
	
	/**
	 * Words is a list of the words in the entire corpus, previousLabel is the label for position-1, position-2 (or O if it's the
	 * start of a new sentence), and position is the word you are adding features for. PreviousLabel must be the only
	 * label that is visible to this method.
	 */
	@Override
	public String[] getFeatures(int position, String[] tokens, String[] previousTagSequence) {
		List<String> features = new ArrayList<String>();

		super.addUnigramFeatures(features, position, tokens, previousTagSequence);
		super.addBigramFeatures(features, position, tokens, previousTagSequence);
		super.addTrigramFeatures(features, position, tokens, previousTagSequence);
		super.addContextualFeatures(features, position, tokens, previousTagSequence);
		super.addRegexPatternFeatures(features, position, tokens, previousTagSequence);
		
		this.addCooccurrentNLURegexPatternFeatures(features, position, tokens, previousTagSequence);

		return features.toArray(new String[features.size()]);
	}

	protected void addCooccurrentNLURegexPatternFeatures(List<String> features, int position, String[] tokens, String[] previousTagSequence) {
		for (int i = 0; i < tokens.length; i++) {
			if (i != position) {
				String[] patternFeatures = featureDic.getFeatures(tokens[i]);
				for (String patternFeature : patternFeatures) {
					if (patternFeature.startsWith("OBJECT") || patternFeature.startsWith("ACTION")) {
						features.add("coocNLUPattern=" + patternFeature);
					}
				}
			}
		}
	}

}
