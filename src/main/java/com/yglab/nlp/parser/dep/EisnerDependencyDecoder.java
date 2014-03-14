package com.yglab.nlp.parser.dep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yglab.nlp.model.Index;
import com.yglab.nlp.parser.Parse;
import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.perceptron.PerceptronDecoder;
import com.yglab.nlp.perceptron.PerceptronModel;



/**
 * This is Eisner's projective parsing algorithm to decode the K-best parse structures based on dynamic programming.
 * 
 * The reference papers for this implementation are as belows:
 * "Bilexical grammars and their cubic-time parsing algorithms."
 *	Jason Eisner, In Advances in Probabilistic and Other Parsing Technologies, 2000
 * "Three new probabilistic models for dependency parsing: An exploration."
 * 	Jason Eisner, COLING, 1996  
 * "Discriminative learning and spanning tree algorithms for dependency parsing."
 *	Ryan McDonald, 2006
 *
 * @author Younggue Bae
 */
public class EisnerDependencyDecoder implements PerceptronDecoder<ParseSample, Parse> {
	
	private DependencyFeatureGenerator<ParseSample> featureGenerator;
	private PerceptronModel model;
	private boolean labeled = false;
	
	/**
	 * Creates a default dependency decoder.
	 * 
	 * @param featureGenerator
	 * @param parameter
	 * @param labeled
	 */
	public EisnerDependencyDecoder(DependencyFeatureGenerator<ParseSample> featureGenerator, PerceptronModel model, boolean labeled) {
		this.featureGenerator = featureGenerator;
		this.model = model;
		this.labeled = labeled;
	}
	
	@Override
	public boolean isLabeled() {
		return this.labeled;
	}
	
	@Override
	public PerceptronModel getModel() {
		return this.model;
	}
	
	@Override
	public List<Parse> getGoldenStructures(ParseSample instance) {
		List<Parse> goldenParses = new ArrayList<Parse>();
		for (int position = 1; position < instance.length(); position++) {
			Parse parse = new Parse(position, instance.heads[position], instance.deprels[position]);
			String[] features = featureGenerator.getFeatures(instance, instance.heads[position], position);
			parse.setFeatures(Arrays.asList(features));
			parse.setGold(true);
			goldenParses.add(parse);
		}
		return goldenParses;
	}
	
	@Override
	public Parse getGoldenStructure(Parse estimate, ParseSample instance) {
		int position = estimate.getIndex();
		Parse gold = new Parse(position, instance.heads[position], instance.deprels[position]);
		String[] features = featureGenerator.getFeatures(instance, instance.heads[position], position);
		gold.setFeatures(Arrays.asList(features));
		gold.setGold(true);
		
		return gold;
	}
	
	/**
	 * Decodes the best parse by Esiner's parsing algorithm.
	 * 
	 * @param instance	The sample instance
	 * @return List<Parse>	The best parse
	 */
	@Override
	public List<Parse> decode(ParseSample instance) {
		List<List<Parse>> kBestParses = this.decode(instance, 1);
		return kBestParses.get(0);
	}
	
	/**
	 * Decodes the K-best parses by Esiner's parsing algorithm.
	 * 
	 * @param instance	The sample instance
	 * @param K	The k best
	 * @return List<List<Parse>>	The K-best parses
	 */
	@Override
	public List<List<Parse>> decode(ParseSample instance, int K) {
		//System.out.println("labeled? " + labeled);
		
		int length = instance.length();	// ROOT is in an index 0
		
		// esiner's algorithm
		KBestParseForest forest = new KBestParseForest(K, length);
		
		for (int s = 0; s < length; s++) {
			forest.add(s, s, 0, 0.0);
			forest.add(s, s, 1, 0.0);
		}
		
		//int shift = 0;
		for (int k = 1; k < length; k++) {
			//System.out.println("shift = " + shift);
			for (int s = 0; s < length - 1; s++) {
				int t = s + k;
				if (t > length - 1) {
					break;
				}
				
				/*
				 * 1. create incomplete items:
				 * C[s,t,<-,0] = S(t,s) + max[s<=r<t](c[s,r,->,1] + c[r + 1,t,<-,1])
				 * C[s,t,->,0] = S(s,t) + max[s<=r<t](c[s,r,->,1] + c[r + 1,t,<-,1])
				 */
				Parse parse_00 = this.getParse(s, t, 0, instance);
				Parse parse_10 = this.getParse(s, t, 1, instance);
				for (int r = s; r < t; r++) {
					ParseForestItem[] pfi_11 = forest.getItems(s, r, 1, 1);
					ParseForestItem[] pfi_01 = forest.getItems(r + 1, t, 0, 1);
					
					if (pfi_11 != null && pfi_01 != null) {
						int[][] pairs = forest.getKBestPairs(pfi_11, pfi_01);

						for (int pi = 0; pi < pairs.length; pi++) {

							if (pairs[pi][0] == -1 || pairs[pi][1] == -1) {
								break;
							}

							int comp1 = pairs[pi][0];
							int comp2 = pairs[pi][1];

							double score = pfi_11[comp1].getScore() + pfi_01[comp2].getScore();
							
							double score_00 = score;
							if (s == 0) { // root
								score_00 += Double.NEGATIVE_INFINITY;
							}
							else {
								score_00 += parse_00.getScore();
							}
							forest.add(s, t, 0, 0, score_00, parse_00, pfi_11[comp1], pfi_01[comp2]);
							
							double score_10 = score;
							if (t == 0) { // root
								score_10 += Double.NEGATIVE_INFINITY;
							}
							else {
								score_10 += parse_10.getScore();
							}
							forest.add(s, t, 1, 0, score_10, parse_10, pfi_11[comp1], pfi_01[comp2]);
						}
					}
				}
				
				/*
				 * 2. create complete items:
				 * C[s,t,<-,1] = max[s<=r<t](c[s,r,<-,1] + c[r,t,<-,0])
				 * C[s,t,->,1] = max[s<r<=t](c[s,r,->,0] + c[r,t,->,1])
				 */
				Parse parse_01 = this.getParse(s, t, 0, instance);
				Parse parse_11 = this.getParse(s, t, 1, instance);
				for (int r = s; r <= t; r++) {
					if (r != t) {
						ParseForestItem[] pfi_01 = forest.getItems(s, r, 0, 1);
						ParseForestItem[] pfi_00 = forest.getItems(r, t, 0, 0);
						if (pfi_01 != null && pfi_00 != null) {
							int[][] pairs = forest.getKBestPairs(pfi_01, pfi_00);
							for (int pi = 0; pi < pairs.length; pi++) {

								if (pairs[pi][0] == -1 || pairs[pi][1] == -1) {
									break;
								}

								int comp1 = pairs[pi][0];
								int comp2 = pairs[pi][1];

								double score = pfi_01[comp1].getScore() + pfi_00[comp2].getScore();

								if (!forest.add(s, t, 0, 1, score, parse_01, pfi_01[comp1], pfi_00[comp2])) {
									break;
								}
							}
						}
					}
					
					if (r != s) {
						ParseForestItem[] pfi_10 = forest.getItems(s, r, 1, 0);
						ParseForestItem[] pfi_11 = forest.getItems(r, t, 1, 1);
						if (pfi_10 != null && pfi_11 != null) {
							int[][] pairs = forest.getKBestPairs(pfi_10, pfi_11);
							for (int pi = 0; pi < pairs.length; pi++) {

								if (pairs[pi][0] == -1 || pairs[pi][1] == -1) {
									break;
								}

								int comp1 = pairs[pi][0];
								int comp2 = pairs[pi][1];

								double score = pfi_10[comp1].getScore() + pfi_11[comp2].getScore();

								if (!forest.add(s, t, 1, 1, score, parse_11, pfi_10[comp1], pfi_11[comp2])) {
									break;
								}
							}
						}						
					}
				}
			}
			//shift++;
			//System.out.println("--------------------------------------");
		}
		
		return forest.getBestParses();
	}

	/**
	 * Gets the parse.
	 * 
	 * @param s
	 * @param t
	 * @param direction
	 * @param instance
	 * @return
	 */
	private Parse getParse(int s, int t, int direction, ParseSample instance) {
		Parse maxParse = null;

		if (labeled) {
			Parse parse = null;
			double maxScore = Double.NEGATIVE_INFINITY;
			
			Index labelIndex = model.getLabelIndex();
			for (int i = 0; i < labelIndex.size(); i++) {
				String label = (String) labelIndex.get(i);
				if (direction == 0) {
					parse = new Parse(s, t, label);
					parse.setWord(instance.forms != null ? instance.forms[s] : null);
					parse.setPostag(instance.postags != null ? instance.postags[s] : null);
					parse.setCpostag(instance.cpostags != null ? instance.cpostags[s] : null);

					String[] features = featureGenerator.getFeatures(instance, t, s);
					parse.setFeatures(Arrays.asList(features));
				} else {
					parse = new Parse(t, s, label);
					parse.setWord(instance.forms != null ? instance.forms[t] : null);
					parse.setPostag(instance.postags != null ? instance.postags[t] : null);
					parse.setCpostag(instance.cpostags != null ? instance.cpostags[t] : null);
					
					String[] features = featureGenerator.getFeatures(instance, s, t);
					parse.setFeatures(Arrays.asList(features));
				}

				double score = this.computeLocalScore(parse);
				if (score > maxScore) {
					maxScore = score;
					maxParse = parse;
					maxParse.setScore(score);
				}
			}
		} else {
			Parse parse = null;
			if (direction == 0) {
				parse = new Parse(s, t);
				parse.setWord(instance.forms != null ? instance.forms[s] : null);
				parse.setPostag(instance.postags != null ? instance.postags[s] : null);
				parse.setCpostag(instance.cpostags != null ? instance.cpostags[s] : null);
				
				String[] features = featureGenerator.getFeatures(instance, t, s);
				parse.setFeatures(Arrays.asList(features));
			} else {
				parse = new Parse(t, s);
				parse.setWord(instance.forms != null ? instance.forms[t] : null);
				parse.setPostag(instance.postags != null ? instance.postags[t] : null);
				parse.setCpostag(instance.cpostags != null ? instance.cpostags[t] : null);
				
				String[] features = featureGenerator.getFeatures(instance, s, t);
				parse.setFeatures(Arrays.asList(features));
			}

			double score = this.computeLocalScore(parse);
			maxParse = parse;
			maxParse.setScore(score);
		}
		
		if (maxParse != null) {
			boolean gold = isGoldenParse(maxParse, instance);
			maxParse.setGold(gold);
		}

		return maxParse;
	}
	
	private boolean isGoldenParse(Parse parse, ParseSample instance) {
		int position = parse.getIndex();
		int head = parse.getHead();
		String label = parse.getLabel();
		
		if (instance.heads == null) {
			return false;
		}
		
		parse.setGoldenHead(instance.heads[position]);
		
		if (labeled) {
			parse.setGoldenLabel(instance.deprels[position]);
			if (instance.heads[position] == head && instance.deprels[position].equals(label)) {
				return true;
			}
		}
		else {
			if (instance.heads[position] == head) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Computes the local score.
	 * 
	 * @param parse
	 * @return
	 */
	private double computeLocalScore(Parse parse) {
		if (parse == null) {
			return 0;
		}

		double score = 0;
		String label = null;
		if (labeled) {
			label = parse.getLabel();
		}
		List<String> features = parse.getFeatures();
		for (String feature : features) {
			score += model.getWeight(label, feature);
		}

		return score;
	}

}
