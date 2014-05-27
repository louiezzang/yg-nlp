package com.yglab.nlp.postag.lang.ko;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.yglab.nlp.postag.POSSampleParser;
import com.yglab.nlp.postag.morph.Morpheme;
import com.yglab.nlp.postag.morph.MorphemeDictionary;
import com.yglab.nlp.postag.morph.Token;
import com.yglab.nlp.util.lang.ko.KoreanMorphemeUtil;
import com.yglab.nlp.util.trie.TrieSuffixMatcher;

/**
 * This class analyzes the morphemes by using morpheme dictionary.
 * 
 * @author Younggue Bae
 */
public class KoreanMorphemeAnalyzer {

	private MorphemeDictionary dic;
	private final String[] validTags;
	//private TrieSuffixMatcher<Integer> validTagTrie;
	private List<List<Token>> tokensCandidates = new ArrayList<List<Token>>();
	
	/**
	 * Constructor.
	 * 
	 * @param dic	The morpheme dictionary
	 */
	public KoreanMorphemeAnalyzer(MorphemeDictionary dic) {
		this(dic, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param dic	The morpheme dictionary
	 * @param tags	The valid unique tags or labels
	 */
	public KoreanMorphemeAnalyzer(MorphemeDictionary dic, String[] tags) {
		this.dic = dic;
		this.validTags = tags;
		//this.validTagTrie = new TrieSuffixMatcher<Integer>();
		
		if (tags == null || tags.length == 0) {
			System.err.println("The valid tags are null or empty!");
			return;
		}

		// make trie of valid tags
		//this.makeValidTagTrie(tags);	
	}
	
	/**
	 * Analyzes the morphemes for the current tokens.
	 * 
	 * @param predictedTags	The predicted tags for tokens
	 * @return
	 */
	public List<Token> analyze(String[] predictedTags) {
		List<Token> tokens = new ArrayList<Token>();
		
		for (int position = 0; position < predictedTags.length; position++) {
			String predictedTag = predictedTags[position];
			
			List<Token> tailCandidates = this.getCurrentTokenCandidates(position);
			for (Token token : tailCandidates) {
				if (predictedTag.endsWith(token.getPos())) {
					System.out.println(token.getPos());
					Morpheme head = new Morpheme();
					head.setSurface(token.getHead());
					head.setTag(predictedTag.substring(0, predictedTag.lastIndexOf(token.getPos())));
					
					System.out.print("surface = " + head.getSurface());
					System.out.print(", tag = " + head.getTag());
					System.out.println();
					
					token.add(head);
					break;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the analyzed morpheme candidates for the current all tokens.
	 * 
	 * @return
	 */
	public List<List<Token>> getCurrentTokensCandidates() {
		return this.tokensCandidates;
	}
	
	/**
	 * Gets the analyzed morpheme candidates for the token at the specific position of the tokens.
	 * 
	 * @param position
	 * @return
	 */
	public List<Token> getCurrentTokenCandidates(int position) {
		return this.tokensCandidates.get(position);
	}
	
	/**
	 * Generates the possible candidates of morpheme for the each token.
	 * 
	 * @param tokens The tokens' array
	 * @return
	 */
	public void generateCandidates(String[] tokens) {
		// initializes the candidates for the current tokens
		tokensCandidates.clear();
		
		for (int position = 0; position < tokens.length; position++) {
			String strToken = tokens[position];
			List<Token> validCandidates = new ArrayList<Token>();
			
			List<Token> candidates = new ArrayList<Token>();
			this.identifyMorphemeCandidates(candidates, new Token(strToken), strToken);
			
			// sort descending by the number of tag of the token
			Collections.sort(candidates);
			 
			for (Token candidate : candidates) {
				System.out.println(candidate.getToken() + ": " + candidate.getTag() + ", " + candidate.getPos() + ", " + candidate.getNumTag());
				
				if (isValid(candidate)) {
					if (!validCandidates.contains(candidate)) {
						validCandidates.add(candidate);
					}
				}

				for (int mi = candidate.size() - 2; mi >= 0; mi--) {
					Token tail = candidate.getTail(mi);
					if (isValid(tail)) {
						if (!validCandidates.contains(tail)) {
							validCandidates.add(tail);
							System.out.println(" -> " + tail.getToken() + ": " + tail.getTag() + ", " + candidate.getPos() + ", " + tail.getNumTag());
						}
						if (tail.getNumTag() == 2) {
							break;	// exit this inner 'for' loop
						}
					}
				}
			}
			tokensCandidates.add(validCandidates);
		}
	}
	
	/**
	 * Identifies the morpheme candidates for the specified surface.
	 * 
	 * @param candidates	The candidate list to store
	 * @param token	The token
	 * @param surface	The surface text to find the morpheme in it
	 */
	private void identifyMorphemeCandidates(List<Token> candidates, Token token, String surface) {
		// find the all suffixes matched with the dictionary
		List<String> matchMorphDics = dic.findSuffixes(surface);

		if (matchMorphDics == null || matchMorphDics.size() == 0) {
			candidates.add(token);
			return;
		}

		for (String matchMorphDic : matchMorphDics) {
			String[] morphItems = matchMorphDic.split("\\|");
			String tail = matchMorphDic.split("\t")[0];
			String head = KoreanMorphemeUtil.truncateRight(surface, tail);

			for (String morphItem : morphItems) {
				// clone the exist token to new token
				Token clonedToken = new Token(token);
				
				Morpheme morph = dictionaryToMorpheme(morphItem);
				String type = (String) morph.getAttribute("type");
				
				if (checkCondition(morph, clonedToken)) {
					if (type.equals("suffix")) {
						if (token.getToken().endsWith(morph.getSurface())) {
							clonedToken.add(morph);
							clonedToken.setHead(surface);
							candidates.add(clonedToken);
						}
						return;
					}
					else if (type.equals("word-ind")) {
						if (token.getToken().equals(morph.getSurface())) {
							clonedToken.add(morph);
							clonedToken.setHead(head);
							candidates.add(clonedToken);
						}
						return;
					}
					else if (type.startsWith("head")) {
						if (head.equals("")) {
							clonedToken.add(morph);
							clonedToken.setHead(head);
							candidates.add(clonedToken);
							return;
						}
					}
					else {
						clonedToken.add(morph);
						clonedToken.setHead(head);
						
						this.identifyMorphemeCandidates(candidates, clonedToken, head);					
					}
				}
			}
		}
	}
	
	// (?=.*a)(?=^[^b]+$)
	// (?=[ㄴ])(?=[^는은])
	private boolean checkCondition(Morpheme left, Token token) {
		if (token.size() < 1) {
			return true;
		}
		Morpheme right = token.getLast();
		
		String leftMorphemeCondition = (String) right.getAttribute("leftMorphemeCondition");
		String[] conds = leftMorphemeCondition.split("\\s");
		
		String strHead = left.getSurface();
		char chHeadLast = strHead.charAt(strHead.length() - 1);
		char chHeadLastJongseong = KoreanMorphemeUtil.getJongseongConsonant(chHeadLast);
		
		for (String cond : conds) {
			if (cond.equals("+어간")) {
				return true;
			}
			else if (cond.startsWith("+")) {
				char condCh = cond.charAt(1);
				if (chHeadLastJongseong == condCh || chHeadLast == condCh) {
					return true;
				}
				else {
					return false;
				}
			}
			else if (cond.startsWith("-")) {
				char condCh = cond.charAt(1);
				if (chHeadLastJongseong == condCh || chHeadLast == condCh) {
					return false;
				}
			}
		}
		
		return true;
	}

	/**
	 * Converts the dictionary text to {@link Morpheme} object. 
	 * 
	 * @param str
	 * @return
	 */
	private static Morpheme dictionaryToMorpheme(String str) {
		Morpheme morph = new Morpheme();
		String[] fields = str.split("\t", -1);

		String surface = fields[0];
		String tag = fields[1];
		String type = fields[2];
		String[] tagItems = tag.split("\\+");

		StringBuilder sbPos = new StringBuilder();
		for (int i = 0; i < tagItems.length; i++) {
			String tagItem = tagItems[i];
			String pos = POSSampleParser.parsePos(tagItem);
			sbPos.append(pos);

			if (i < tagItems.length - 1) {
				sbPos.append("+");
			}
		}

		morph.setSurface(surface);
		morph.setTag(tag);
		morph.setPos(sbPos.toString());
		//morph.setPosDescription();

		morph.setAttribute("type", type);
		morph.setAttribute("leftMorphemeCondition", fields[3]);
		morph.setAttribute("leftMorphemeBondCondition", fields[4]);
		morph.setAttribute("leftPhonemeBondCondition", fields[5]);
		morph.setAttribute("leftPosBondCondition", fields[6]);
		morph.setAttribute("leftLemmatizationCondition", fields[7]);
		morph.setAttribute("morphemProperty", fields[8]);
		morph.setAttribute("phonemeProperty", fields[9]);

		return morph;
	}
	
	/**
	 * Checks if the morphemes of a token is valid or not.
	 * 
	 * @param token	The morphemes of token
	 * @return
	 */
	private boolean isValid(Token token) {
		if (validTags == null || validTags.length == 0) {
			return true;
		}

		for (String validTag : validTags) {
			if (validTag.endsWith(token.getPos())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Makes the trie of valid tags.
	 * 
	 * @param tags
	 */
	/*
	private void makeValidTagTrie(final String[] tags) {
		for (String tag : tags) {
			String[] arrTag = tag.split("\\+");
			validTagTrie.add(tag, arrTag.length);
			if (arrTag.length > 1) {
				StringBuilder sb = new StringBuilder();
				for (int i = 1; i < arrTag.length; i++) {
					if (i < arrTag.length - 1) {
						sb.append(arrTag[i]).append("+");
					}
					else {
						sb.append(arrTag[i]);
					}
					validTagTrie.add(sb.toString(), arrTag.length - 1);
				}
			}
		}
	}
	*/

}
