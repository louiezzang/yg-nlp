package com.yglab.nlp.maxent;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.model.Datum;
import com.yglab.nlp.model.Index;



/**
 * The Viterbi algorithm with backpointers.
 * 
 * @author Younggue Bae
 */
public class Viterbi {
	
	private Index labelIndex;
	private Index featureIndex;
	private String[] labels;
	
	/**
	 * Initializes Viterbi algorithm.
	 * 
	 * @param labelIndex
	 * @param featureIndex
	 * @param length
	 */
	public Viterbi(Index labelIndex, Index featureIndex) {
		this.labelIndex = labelIndex;
		this.featureIndex = featureIndex;
		
		labels = new String[labelIndex.size()];
		for (int i = 0; i < labelIndex.size(); i++) {
			labels[i] = labelIndex.get(i).toString();
		}
	}
	
	// TODO
	public List<List<Datum>> decode(List<List<Datum>> candidates, double[][] weights, int K) {
		List<List<Datum>> kBestSequences = new ArrayList<List<Datum>>();
		List<Datum> bestSequence = this.decode(candidates, weights);
		kBestSequences.add(bestSequence);
		
		return kBestSequences;
	}

	public List<Datum> decode(List<List<Datum>> candidates, double[][] weights) {
		// initializes the best sequence from the source
		List<Datum> bestSequence = new ArrayList<Datum>();
		for (int i = 0; i < candidates.size(); i++) {
			String token = candidates.get(i).get(0).getWord();
			Datum datum = new Datum(token, "");
			bestSequence.add(datum);
		}
		
		int[][] backpointers = new int[candidates.size()][numLabels()];
		double[][] scores = new double[candidates.size()][numLabels()];

		int prevLabel = labelIndex.indexOf(candidates.get(0).get(0).getPreviousLabel());
		double[] localScores = computeScores(candidates.get(0).get(0).getFeatures(), weights);

		int position = 0;
		for (int currLabel = 0; currLabel < localScores.length; currLabel++) {
			backpointers[position][currLabel] = prevLabel;
			scores[position][currLabel] = localScores[currLabel];
		}

		// for each position in data
		for (position = 1; position < candidates.size(); position++) {
			List<Datum> possibleSequence = candidates.get(position);
			// for each previous labels
			for (int j = 0; j < possibleSequence.size(); j++) {
				Datum datum = possibleSequence.get(j);
				
				String previousLabel = datum.getPreviousLabel();
				prevLabel = labelIndex.indexOf(previousLabel);

				localScores = computeScores(datum.getFeatures(), weights);
				for (int currLabel = 0; currLabel < localScores.length; currLabel++) {
					double score = localScores[currLabel] + scores[position - 1][prevLabel];
					if (prevLabel == 0 || score > scores[position][currLabel]) {
						backpointers[position][currLabel] = prevLabel;
						scores[position][currLabel] = score;
					}
				}
			}
		}

		int bestLabel = 0;
		double bestScore = scores[bestSequence.size() - 1][0];

		for (int label = 1; label < scores[bestSequence.size() - 1].length; label++) {
			if (scores[bestSequence.size() - 1][label] > bestScore) {
				bestLabel = label;
				bestScore = scores[bestSequence.size() - 1][label];
			}
		}

		for (position = bestSequence.size() - 1; position >= 0; position--) {
			Datum datum = bestSequence.get(position);
			datum.setGuessLabel((String) labelIndex.get(bestLabel));
			bestLabel = backpointers[position][bestLabel];
		}
		
		return bestSequence;
	}

	private double[] computeScores(List<String> features, double[][] weights) {

		double[] scores = new double[numLabels()];

		for (Object feature : features) {
			int f = featureIndex.indexOf(feature);
			if (f < 0) {
				continue;
			}
			for (int i = 0; i < scores.length; i++) {
				scores[i] += weights[i][f];
			}
		}

		return scores;
	}
	
	private int numLabels() {
		if (labels != null) {
			return labels.length;
		}
		else {
			return 0;
		}
	}

}