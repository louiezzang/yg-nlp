package com.yglab.nlp.postag.lang.ko;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yglab.nlp.maxent.DefaultTagSequenceGenerator;
import com.yglab.nlp.maxent.FeatureGenerator;
import com.yglab.nlp.model.Datum;
import com.yglab.nlp.postag.morph.Token;


/**
 * GEN is a function that maps an input x to a set of candidates GEN(x). 
 * e.g. Tagging: GEN(x) is the set of all possible tag sequences with the specified length.
 * KoreanTagSequenceGenerator provides only bi-gram previous tag sequence candidates.
 * 
 * @author Younggue Bae
 */
public class KoreanTagSequenceGenerator extends DefaultTagSequenceGenerator {
	
	private static final Pattern ALPHABET_PATTERN = Pattern.compile("^[a-zA-Z]+$");
	private final List<String> defaultTagCandidates = new ArrayList<String>();
	
	/**
	 * Constructor.
	 * 
	 * @param featureGenerator The context feature generator
	 * @param tags	The valid tags
	 */
	public KoreanTagSequenceGenerator(FeatureGenerator<String> featureGenerator, String[] tags) {
		super(featureGenerator, tags, 2);
		
		this.makeDefaultTagCandidates(tags);
	}
	
	private void makeDefaultTagCandidates(final String[] tags) {
		if (tags != null) {
			for (String tag : tags) {
				if (tag.split("\\+").length == 1 
						//&& 	(tag.startsWith("N") || tag.startsWith("V") || tag.startsWith("M") || 
						//		tag.startsWith("S") || tag.startsWith("I"))
						) {
					defaultTagCandidates.add(tag);
				}
			}
		}		
	}
	
	private List<List<String>> getTagCandidates(String[] tokens) {
		List<List<String>> tokensTagCandidates = new ArrayList<List<String>>(tokens.length);
		
		for (int position = 0; position < tokens.length; position++) {
			String token = tokens[position];
			List<Token> tokenCandidates = ((KoreanPOSFeatureGenerator) featureGenerator).getCurrentTokenCandidates(position);
			
			if (tokenCandidates.size() == 0) {
				if (token.equals(".") || token.equals("!") || token.equals("?")) {
					String[] sf = { "SF" };
					tokensTagCandidates.add(Arrays.asList(sf));
				}
				else if (token.equals(",")) {
					String[] sp = { "SP" };
					tokensTagCandidates.add(Arrays.asList(sp));
				}
				else {
					Matcher m = ALPHABET_PATTERN.matcher(token);
					if (m.find()) {
						String[] sp = { "SL" };
						tokensTagCandidates.add(Arrays.asList(sp));
					}
					else {
						tokensTagCandidates.add(defaultTagCandidates);
					}
				}
				continue;
			}
			
			List<String> tagList = new ArrayList<String>();
			
			for (Token tokenCandidate : tokenCandidates) {
				// for debugging
				//System.out.println(token + ", tokenCandidate=" + tokenCandidate.getTag() + ", size=" + tokenCandidate.size());
				
				String postag = tokenCandidate.getPos();
				if (tokenCandidate.isValidated()) {
					tagList.add(postag);
				}
			}	
			
			if (tagList.size() == 0) {
				//throw new IllegalArgumentException("'" + postag + "' tag doesn't exist in the valid tag label set.");
				tagList.add("NNG");
				//tagList.addAll(defaultTagCandidates);
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
		
		List<List<String>> tokensTagCandidates = this.getTagCandidates(tokens);

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
