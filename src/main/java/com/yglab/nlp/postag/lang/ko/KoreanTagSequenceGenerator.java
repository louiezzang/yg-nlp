package com.yglab.nlp.postag.lang.ko;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	private static final Pattern MORPH_POS_PATTERN = Pattern.compile("([^/\\+\\(\\)]*)/([^/\\+\\(\\)]*)?");
	
	private Map<String, List<String>> suffixTagMap = new HashMap<String, List<String>>();
	
	/**
	 * Constructor.
	 * 
	 * @param featureGenerator The context feature generator
	 */
	public KoreanTagSequenceGenerator(FeatureGenerator<String> featureGenerator) {
		this(featureGenerator, null);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param featureGenerator The context feature generator
	 * @param tags The unique tag labels
	 */
	public KoreanTagSequenceGenerator(FeatureGenerator<String> featureGenerator, String[] tags) {
		super(featureGenerator, tags, 2);
		
		// set default tag list
		this.setDefaultTagList(tags);
	}
	
	@Override
	public void setTags(final String[] tags) {
		this.tags = tags;
		
		// set default tag list
		this.setDefaultTagList(tags);
	}
	
	private void setDefaultTagList(final String[] tags) {
		if (tags != null) {
			List<String> tagList = new ArrayList<String>();
			for (String tag : tags) {
				if (tag.indexOf("+") < 0) {
					tagList.add(tag);
				}
			}
			suffixTagMap.put("default", tagList);
		}		
	}
	
	private List<List<String>> getTokensTagCandidates(String[] tokens) {
		List<List<String>> tokensTagCandidates = new ArrayList<List<String>>(tokens.length);
		
		for (int position = 0; position < tokens.length; position++) {
			String token = tokens[position];
			List<String> matchSuffixList = ((KoreanPOSFeatureGenerator) featureGenerator).findTokenSuffix(token);
			
			if (matchSuffixList.size() == 0) {
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
					tokensTagCandidates.add(suffixTagMap.get("default"));
					//System.out.println(position + ": " + suffixTagMap.get("default"));
				}
				continue;
			}
			
			List<String> tagList = new ArrayList<String>();
			for (String matchSuffix : matchSuffixList) {
				//System.out.println(token + ", matchSuffix = " + matchSuffix);
				
				String[] fields = matchSuffix.split("\t"); 
				//String tail = fields[0];
				String tag = fields[1];
				
				StringBuilder sbSuffixTag = new StringBuilder();
				Matcher matcher = MORPH_POS_PATTERN.matcher(tag);
				while (matcher.find()) {
					String pos = matcher.group(2);
					sbSuffixTag.append(pos).append("+");
				}
				
				String suffixTag = sbSuffixTag.toString();
				if (suffixTag.endsWith("+")) {
					suffixTag = suffixTag.substring(0, suffixTag.length() - 1);
					//System.out.println(token + ", suffixTag = " + suffixTag);
				}
				
				if (suffixTagMap.containsKey(suffixTag)) {
					tagList.addAll(suffixTagMap.get(suffixTag));
				}
				else {
					// TODO: 매칭되는(endsWith) 어미가 E**(어미)가 하나인 경우에는 uniqueTag의 "+"로 split되는 수가 2개인 경우에만 
					// tagList에 넣어줌.
					for (String uniqueTag : tags) {
						if (uniqueTag.endsWith(suffixTag)) {
							tagList.add(uniqueTag);
						}
					}
					
					if (tagList.size() == 0) {
						throw new IllegalArgumentException("'" + suffixTag + "' tag doesn't exist in the unique tag label set.");
					}
					
					// TODO: "ㄴ, ㄹ"으로 끝나는 어미로 인식했거나, 매칭되는(endsWith) 어미가 E**(어미)가 하나인 경우,
					// 태깅 분석오류를 줄이기 위해 디폴트 품사인 NNG를 후보군에 추가해줌.
					// add the default tag
//					if () {
//						tagList.add("NNG");
//					}
					
					suffixTagMap.put(suffixTag, tagList);
				}
			}
			
			//System.out.println(position + ": " + tagList);

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
