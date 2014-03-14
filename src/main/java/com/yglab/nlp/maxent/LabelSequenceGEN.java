package com.yglab.nlp.maxent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yglab.nlp.model.Datum;



/**
 * GEN is a function that maps an input x to a set of candidates GEN(x). 
 * e.g. Tagging: GEN(x) is the set of all possible tag sequences with the specified length.
 * 
 * @author Younggue Bae
 */
public class LabelSequenceGEN {
	
	private FeatureGenerator<String> featureGenerator;
	private String[] labels;
	
	public LabelSequenceGEN(FeatureGenerator<String> featureGenerator, String[] labels) {
		this.featureGenerator = featureGenerator;
		this.labels = labels;
	}

	public String[][] generateLabelSequenceCandidates(int length) {
		int[] pos = new int[length];
		int total = (int) Math.pow(labels.length, length);

		String[][] sequences = new String[total][length];

		for (int i = 0; i < total; i++) {
			for (int x = 0; x < length; x++) {
				if (pos[x] == labels.length) {
					pos[x] = 0;
					if (x + 1 < length) {
						pos[x + 1]++;
					}
				}
				sequences[i][x] = labels[pos[x]];
			}
			pos[0]++;
		}

		return sequences;
	}
	
	/**
	 * Gets the all possible previous label sequence candidates of each token.
	 * 
	 * @param tokens	the token of sentence for test
	 * @param prevSequenceLength	the length of previous labels sequence(bigram, trigram)
	 * @param featureGenerator	the context feature generator
	 * @return
	 */
	public List<List<Datum>> getCandidates(String[] tokens, int prevSequenceLength) {
		List<List<Datum>> instanceCandidates = new ArrayList<List<Datum>>();
		
		for (int position = 0; position < tokens.length; position++) {
			String token = tokens[position];

			if (position == 0) {
				List<Datum> candidates = new ArrayList<Datum>();
				String[] prevLabelSequence = new String[prevSequenceLength];
				for (int i = 0; i < prevSequenceLength; i++) {
					prevLabelSequence[i] = "O";
				}

				Datum datum = new Datum(token, "O");
				datum.setFeatures(Arrays.asList(featureGenerator.getFeatures(position, tokens, prevLabelSequence)));
				datum.setPreviousLabel(prevLabelSequence[prevSequenceLength - 1]);
				candidates.add(datum);
				instanceCandidates.add(candidates);
			} 
			else if (prevSequenceLength > 1 && position > 0 && position < prevSequenceLength ) {
				List<Datum> candidates = new ArrayList<Datum>();
				String[][] tmpLabelSequences = generateLabelSequenceCandidates(position);
				
				for (int i = 0; i < tmpLabelSequences.length; i++) {
					String[] prevLabelSequenceCandidates = new String[prevSequenceLength];
					for (int j = 0; j < prevSequenceLength; j++) {
						if (j < prevSequenceLength - position) {
							prevLabelSequenceCandidates[j] = "O";
						}
						else {
							for (int k = 0; k < tmpLabelSequences[i].length; k++) {
								prevLabelSequenceCandidates[j] = tmpLabelSequences[i][k];
							}
						}
					}
					Datum datum = new Datum(token, "O");
					datum.setFeatures(Arrays.asList(featureGenerator.getFeatures(position, tokens, prevLabelSequenceCandidates)));
					datum.setPreviousLabel(prevLabelSequenceCandidates[prevSequenceLength - 1]);
					candidates.add(datum);
				}
				instanceCandidates.add(candidates);
			}
			else {
				List<Datum> candidates = new ArrayList<Datum>();
				String[][] prevLabelSequenceCandidates = generateLabelSequenceCandidates(prevSequenceLength);
				
				for (int i = 0; i < prevLabelSequenceCandidates.length; i++) {
					Datum datum = new Datum(token, "O");
					datum.setFeatures(Arrays.asList(featureGenerator.getFeatures(position, tokens, prevLabelSequenceCandidates[i])));
					datum.setPreviousLabel(prevLabelSequenceCandidates[i][1]);
					candidates.add(datum);
				}
				instanceCandidates.add(candidates);
			}
		}
		
		return instanceCandidates;
	}

}
