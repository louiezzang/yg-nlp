package com.yglab.nlp.sbd;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Simple sentence detector.
 * 
 * @author Younggue Bae
 */
public class SimpleSentenceDetector implements SentenceDetector {

	public static final char[] defaultEosCharacters = new char[] { '.', '!', '?' };
	private EOSFinder eosFinder;
	private Set<String> eosCharacters = new HashSet<String>();
	
	/**
	 * Initializes the simple sentence detector.
	 */
  public SimpleSentenceDetector() {
  	eosFinder = new EOSFinder(defaultEosCharacters);
  	
  	for (char eos : defaultEosCharacters) {
  		eosCharacters.add(String.valueOf(eos));
  	}
  }
	
  public static final String[] detectNaively(String s) {
		String[] sentences = s.split("(?<=[\\w\\W])[\\.?!]\\s+");
		
		return sentences;
	}
  
	@Override
	public String[] detect(String s) {
  	List<String> sentences = new ArrayList<String>();
  	List<Integer> positions = eosFinder.getPositions(s);
  	
  	int prevPosition = 0;
  	for (int split = 0; split < positions.size(); split++) {
  		int position = positions.get(split);
  		
  		String sentence = null;
  		if (split == 0) {
  			sentence = s.substring(0, position + 1).trim();
  		}
  		else {
  			sentence = s.substring(prevPosition + 1, position + 1).trim();
  		}
  		if (!eosCharacters.contains(sentence)) {
				sentences.add(sentence);
			}
  		
  		prevPosition = position;
  	}
  	
  	if (prevPosition < s.length() - 1) {
  		String sentence = s.substring(prevPosition + 1).trim();
  		sentences.add(sentence);
  	}
  	
  	if (sentences.size() == 0) {
  		return new String[] { s };
  	}
  	else {
  		return sentences.toArray(new String[sentences.size()]);
  	}
	}

}
