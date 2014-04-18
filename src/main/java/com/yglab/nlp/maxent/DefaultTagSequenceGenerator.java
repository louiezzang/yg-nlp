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
	protected String[] labels;
	protected int prevSequenceLength;
	protected String[][] prevLabelSequenceCandidates;
	
	/**
	 * Constructor.
	 * 
	 * @param featureGenerator The context feature generator
	 * @param labels The unique labels
	 * @param prevSequenceLength The length of previous labels sequence(bigram, trigram)
	 */
	public DefaultTagSequenceGenerator(FeatureGenerator<String> featureGenerator, String[] labels, int prevSequenceLength) {
		this.featureGenerator = featureGenerator;
		this.labels = labels;
		this.prevSequenceLength = prevSequenceLength;
	
		this.prevLabelSequenceCandidates = generateLabelSequenceCandidates(prevSequenceLength);
		System.out.println("prevLabelSequenceCandidates size = " + prevLabelSequenceCandidates.length + " * " + prevLabelSequenceCandidates[0].length);
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
//				String[][] prevLabelSequenceCandidates = generateLabelSequenceCandidates(prevSequenceLength);
				
				// TODO: prevLabelSequenceCandidates를 미리 생성해 놓고 전체 경우의 수에서 후보군을 생성하던 방식에서
				// token별로 prevLabelSequenceCandidates 후보군을 만들어 내는 방식으로 수정 필요.
				for (int i = 0; i < prevLabelSequenceCandidates.length; i++) {
					Datum datum = new Datum(token, "O");
					//TODO: featureGenerator 생성시에 token별로 형태소 자질(어미, 조사 등 체크) 후보군을 미리 생성해서 재사용하도록 로직 추가
					// 그럴러면 featureGenerator의 멤버변수로 token별 형태소 자질을 가지고 있고 input tokens(문장)이 바뀌면 초기화 되어야 함.
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
