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
public class DefaultTagSequenceGenerator implements TagSequenceGenerator {
	
	protected FeatureGenerator<String> featureGenerator;
	protected String[] tags;
	protected final int prevSequenceLength;
	protected String[][] allPrevTagSequenceCandidates;
	
	/**
	 * Constructor.
	 * 
	 * @param featureGenerator The context feature generator
	 * @param tags The unique labels
	 * @param prevSequenceLength The length of previous labels sequence(bigram, trigram)
	 */
	public DefaultTagSequenceGenerator(FeatureGenerator<String> featureGenerator, String[] tags, int prevSequenceLength) {
		this.featureGenerator = featureGenerator;
		this.tags = tags;
		this.prevSequenceLength = prevSequenceLength;
	
		if (tags != null) {
			this.allPrevTagSequenceCandidates = generateAllTagSequenceCandidates(prevSequenceLength);
			System.out.println("allPrevTagSequenceCandidates size = " + allPrevTagSequenceCandidates.length + " * " + allPrevTagSequenceCandidates[0].length);
		}
	}
	
	public void setTags(final String[] tags) {
		this.tags = tags;
	}

	public String[][] generateAllTagSequenceCandidates(int length) {
		int[] pos = new int[length];
		int total = (int) Math.pow(tags.length, length);

		String[][] sequences = new String[total][length];

		for (int i = 0; i < total; i++) {
			for (int x = 0; x < length; x++) {
				if (pos[x] == tags.length) {
					pos[x] = 0;
					if (x + 1 < length) {
						pos[x + 1]++;
					}
				}
				sequences[i][x] = tags[pos[x]];
			}
			pos[0]++;
		}

		return sequences;
	}
	
	@Override
	public List<List<Datum>> getCandidates(String[] tokens) {
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
			else if (prevSequenceLength > 1 && position > 0 && position < prevSequenceLength) {
				List<Datum> candidates = new ArrayList<Datum>();
				String[][] tmpTagSequences = generateAllTagSequenceCandidates(position);
				
				for (int i = 0; i < tmpTagSequences.length; i++) {
					String[] prevTagSequenceCandidates = new String[prevSequenceLength];
					for (int j = 0; j < prevSequenceLength; j++) {
						if (j < prevSequenceLength - position) {
							prevTagSequenceCandidates[j] = "O";
						}
						else {
							for (int k = 0; k < tmpTagSequences[i].length; k++) {
								prevTagSequenceCandidates[j] = tmpTagSequences[i][k];
							}
						}
					}
					Datum datum = new Datum(token, "O");
					datum.setFeatures(Arrays.asList(featureGenerator.getFeatures(position, tokens, prevTagSequenceCandidates)));
					datum.setPreviousLabel(prevTagSequenceCandidates[prevSequenceLength - 1]);
					candidates.add(datum);
				}
				instanceCandidates.add(candidates);
			}
			else {
				List<Datum> candidates = new ArrayList<Datum>();
				
				for (int i = 0; i < allPrevTagSequenceCandidates.length; i++) {
					Datum datum = new Datum(token, "O");

					datum.setFeatures(Arrays.asList(featureGenerator.getFeatures(position, tokens, allPrevTagSequenceCandidates[i])));
					datum.setPreviousLabel(allPrevTagSequenceCandidates[i][prevSequenceLength - 1]);
					candidates.add(datum);
				}
				instanceCandidates.add(candidates);
			}
		}
		
		return instanceCandidates;
	}

}
