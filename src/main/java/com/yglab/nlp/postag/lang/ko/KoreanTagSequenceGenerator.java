package com.yglab.nlp.postag.lang.ko;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yglab.nlp.maxent.DefaultTagSequenceGenerator;
import com.yglab.nlp.maxent.FeatureGenerator;
import com.yglab.nlp.model.Datum;


/**
 * GEN is a function that maps an input x to a set of candidates GEN(x). 
 * e.g. Tagging: GEN(x) is the set of all possible tag sequences with the specified length.
 * KoreanTagSequenceGenerator provides only bi-gram previous tag sequence candidates.
 * 
 * @author Younggue Bae
 */
public class KoreanTagSequenceGenerator extends DefaultTagSequenceGenerator {
	
	private List<String> defaultTags = new ArrayList<String>();
	
	/**
	 * Constructor.
	 * 
	 * @param featureGenerator The context feature generator
	 * @param tags	The valid tags
	 */
	public KoreanTagSequenceGenerator(FeatureGenerator<String> featureGenerator, String[] tags) {
		super(featureGenerator, tags, 2);
		
		this.setDefaultTagList(tags);
	}
	
	private void setDefaultTagList(final String[] tags) {
		if (tags != null) {
			for (String tag : tags) {
				if (tag.indexOf("+") < 0) {
					defaultTags.add(tag);
				}
			}
		}		
	}
	
	private List<List<String>> getTokensTagCandidates(String[] tokens) {
		List<List<String>> tokensTagCandidates = new ArrayList<List<String>>(tokens.length);
		
		for (int position = 0; position < tokens.length; position++) {
			String token = tokens[position];
			List<Tail> matchTailList = ((KoreanPOSFeatureGenerator) featureGenerator).getCurrentTokenTailCandidates(position);
			
			if (matchTailList.size() == 0) {
				if (token.equals(".") || token.equals("!") || token.equals("?")) {
					String[] sf = { "SF" };
					tokensTagCandidates.add(Arrays.asList(sf));
					//System.out.println(position + ": " + Arrays.asList(sf));
				}
				else if (token.equals(",")) {
					String[] sp = { "SP" };
					tokensTagCandidates.add(Arrays.asList(sp));
					//System.out.println(position + ": " + Arrays.asList(sp));
				}
				else {
					tokensTagCandidates.add(defaultTags);
					//System.out.println(position + ": " + defaultTags);
				}
				continue;
			}
			
			List<String> tagList = new ArrayList<String>();
			for (Tail matchTail : matchTailList) {
				// for debugging
				System.out.println(token + ", matchTail=" + matchTail.getTag() + ", size=" + matchTail.size());
				
				String postag = matchTail.getPos();
				if (matchTail.size() == 1 && matchTail.getHead().equals("")) {
					for (String validTag : tags) {
						if (validTag.equals(postag)) {
							tagList.add(postag);
						}
					}
				}
				else if (postag.split("\\+").length >= 3) {
					for (String validTag : tags) {
						if (validTag.endsWith(postag)) {
							if (!tagList.contains(validTag)) {
								tagList.add(validTag);
							}
						}				
					}
				}
				else {	
					for (String validTag : tags) {
						int validTagLength = validTag.split("\\+").length;
						int maxLength = postag.split("\\+").length + 1;
						if (validTagLength == maxLength && validTag.endsWith(postag)) {
							if (!tagList.contains(validTag)) {
								tagList.add(validTag);
							}
						}
					}
				}
			}
			
			if (tagList.size() == 0) {
				//throw new IllegalArgumentException("'" + postag + "' tag doesn't exist in the valid tag label set.");
				if (!tagList.contains("NNG")) {
					tagList.add("NNG");
				}
			}
			
			System.out.println(position + ": " + tagList);

			tokensTagCandidates.add(tagList);
		}

		return tokensTagCandidates;
	}
	
	private String[][] generateTagSequenceCandidates(List<List<String>> tokensTagCandidates, int position) {
		// the prevSequenceLength is fixed to "2"
		if (position < prevSequenceLength) {
			return null;
		}
		
		List<String> prevPrevCandidates = tokensTagCandidates.get(position - 2);
		List<String> prevCandidates = tokensTagCandidates.get(position - 1);
		int size = prevPrevCandidates.size() * prevCandidates.size();

		String[][] prevTagSequenceCandidates = new String[size][prevSequenceLength];
		
		int index = 0;
		for (int i = 0; i < prevPrevCandidates.size(); i++) {
			for (int j = 0; j < prevCandidates.size(); j++) {
				prevTagSequenceCandidates[index][0] = prevPrevCandidates.get(i);
				prevTagSequenceCandidates[index][1] = prevCandidates.get(j);
				index++;
			}
		}

		
		return prevTagSequenceCandidates;
	}
	
	@Override
	public List<List<Datum>> getCandidates(String[] tokens) {
		List<List<Datum>> instanceCandidates = new ArrayList<List<Datum>>();
		
		List<List<String>> tokensTagCandidates = this.getTokensTagCandidates(tokens);
		
		for (int position = 0; position < tokens.length; position++) {
			String token = tokens[position];

			if (position == 0) {
				List<Datum> candidates = new ArrayList<Datum>();
				String[] prevTagSequence = new String[prevSequenceLength];
				for (int i = 0; i < prevSequenceLength; i++) {
					prevTagSequence[i] = "O";
				}

				Datum datum = new Datum(token, "O");
				datum.setFeatures(Arrays.asList(featureGenerator.getFeatures(position, tokens, prevTagSequence)));
				datum.setPreviousLabel(prevTagSequence[prevSequenceLength - 1]);
				candidates.add(datum);
				instanceCandidates.add(candidates);
			} 
			else if (position ==  1) {
				List<Datum> candidates = new ArrayList<Datum>();
				
				List<String> prevCandidates = tokensTagCandidates.get(0);
				
				for (int i = 0; i < prevCandidates.size(); i++) {
					String[] prevTagSequenceCandidates = new String[prevSequenceLength];
					prevTagSequenceCandidates[0] = "O";
					prevTagSequenceCandidates[1] = prevCandidates.get(i);
					
					Datum datum = new Datum(token, "O");
					datum.setFeatures(Arrays.asList(featureGenerator.getFeatures(position, tokens, prevTagSequenceCandidates)));
					datum.setPreviousLabel(prevTagSequenceCandidates[1]);
					candidates.add(datum);
				}
				instanceCandidates.add(candidates);
			}
			else {
				List<Datum> candidates = new ArrayList<Datum>();
				
				String[][] tagSequenceCandidates = this.generateTagSequenceCandidates(tokensTagCandidates, position);

				for (int i = 0; i < tagSequenceCandidates.length; i++) {
					Datum datum = new Datum(token, "O");

					datum.setFeatures(Arrays.asList(featureGenerator.getFeatures(position, tokens, tagSequenceCandidates[i])));
					datum.setPreviousLabel(tagSequenceCandidates[i][1]);
					candidates.add(datum);
				}
				instanceCandidates.add(candidates);
			}
		}
		
		return instanceCandidates;
	}

}
