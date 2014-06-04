package com.yglab.nlp.postag.lang.ko;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yglab.nlp.postag.morph.Morpheme;
import com.yglab.nlp.postag.morph.Token;
import com.yglab.nlp.util.lang.ko.KoreanMorphemeUtil;
import com.yglab.nlp.util.lang.ko.KoreanUnicode;

/**
 * This class analyzes the morphemes by using morpheme dictionary.
 * 
 * @author Younggue Bae
 */
public class KoreanMorphemeAnalyzer {

	private static final Pattern COLLOCATE_PATTERN = Pattern.compile(
			"((XSA|XSV|VX)\\+(EP|ETN|ETM|EC|EF))" + "|" +
			"((EP|EC)\\+(EF|EC|ETM))"
			);
	private KoreanMorphemeDictionary dic;
	//private final String[] validTags;
	private Map<String, List<String>> validTagMap = new HashMap<String, List<String>>();
	private List<List<Token>> tokensCandidates = new ArrayList<List<Token>>();
	private List<List<Token>> tokensTailCandidates = new ArrayList<List<Token>>();

	/**
	 * Constructor.
	 * 
	 * @param dic	The morpheme dictionary
	 * @param tags	The valid unique tags or labels
	 */
	public KoreanMorphemeAnalyzer(KoreanMorphemeDictionary dic, String[] validTags) {
		this.dic = dic;
		//this.validTags = validTags;
		
		this.validTagMap = indexingValidTags(validTags);
		
		System.out.println("--------------------------");
		System.out.println("valid tags map: " + validTagMap.size());
		System.out.println("--------------------------");
		for (Map.Entry<String, List<String>> entry : validTagMap.entrySet()) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}
	}
	
	/**
	 * Gets the analyzed morpheme candidates for the current all tokens.
	 * 
	 * @return
	 */
	public List<List<Token>> getCurrentCandidates() {
		return this.tokensCandidates;
	}
	
	/**
	 * Gets the analyzed morpheme candidates for the token at the specific position of the tokens.
	 * 
	 * @param position
	 * @return
	 */
	public List<Token> getCurrentCandidates(int position) {
		return this.tokensCandidates.get(position);
	}
	
	/**
	 * Gets the analyzed tail candidates for the token at the specific position of the tokens.
	 * 
	 * @param position
	 * @return
	 */
	public List<Token> getCurrentTailCandidates(int position) {
		return this.tokensTailCandidates.get(position);
	}
	
	/**
	 * Analyzes the morphemes for the current tokens.
	 * 
	 * @param predictedTags	The predicted tags for tokens
	 * @return
	 */
	//TODO
	public List<Token> analyze(String[] predictedTags) {
		List<Token> tokens = new ArrayList<Token>();
		
		for (int position = 0; position < predictedTags.length; position++) {
			String predictedTag = predictedTags[position];
			
			List<Token> tailCandidates = this.getCurrentCandidates(position);
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
		
		return tokens;
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
		tokensTailCandidates.clear();
		
		for (int position = 0; position < tokens.length; position++) {
			String strToken = tokens[position];
			Map<String, Token> validCandidates = new HashMap<String, Token>();
			Map<String, Token> validTailCandidates = new HashMap<String, Token>();
			
			// identify morpheme candidates for a token
			List<Token> candidates = new ArrayList<Token>();
			this.identifyMorphemeCandidates(candidates, new Token(strToken), strToken);
			
			// sort descending by the number of tag of the token
			Collections.sort(candidates);
			
			int maxNumTag = 0;
			outerLoop:
			for (int ci = 0; ci < candidates.size(); ci++) {
				Token candidate = candidates.get(ci);
				System.out.println(candidate.getToken() + ": " + candidate.getTag() + ", " + candidate.getPos() + ", " + candidate.getNumTag());
				
				if (this.addValidCandidate(validCandidates, validTailCandidates, candidate)) {
					System.out.println(" -> " + candidate.getToken() + ": " + candidate.getTag() + ", " + candidate.getPos() + ", " + candidate.getNumTag());
					int numTag = candidate.getNumTag();
					if (numTag > maxNumTag) {
						maxNumTag = numTag;
					}
				}
				// if the candidate has been full analyzed but doesn't match with valid tag, 
				// it is added into the valid candidates though.
				else if (candidate.isAnalyzed()) {
					if (!candidate.getPos().startsWith("E")) {
						System.out.println(" (+) " + candidate.getToken() + ": " + candidate.getTag() + ", " + candidate.getPos() + ", " + candidate.getNumTag());
						candidate.setValidated(false);
						validCandidates.put(candidate.getTag(), candidate);
					}
				}
				
				if (maxNumTag >= 2) {
					Matcher m = COLLOCATE_PATTERN.matcher(candidate.getPos());
					if (m.find()) {
						System.out.println("**continue: " + candidate.getTag());
						continue;
					}
				}
				
				for (int mi = candidate.size() - 2; mi >= 0; mi--) {
					Token tail = candidate.getTail(mi);
					
					if (this.addValidCandidate(validCandidates, validTailCandidates, tail)) {
						int tailNumTag = tail.getNumTag();
						if (tailNumTag > maxNumTag) {
							maxNumTag = tailNumTag;
						}
						System.out.println(" ->> " + tail.getToken() + ": " + tail.getTag() + ", " + tail.getPos() + ", numTag=" + tail.getNumTag() + ", maxNumTag=" + maxNumTag);
						if (maxNumTag >= 3 && (tailNumTag == 2 || tailNumTag == 3)) {
							System.out.println("**exit outer loop");
							break outerLoop;
						}
						else if (tail.getNumTag() == 2) {
							System.out.println("**exit inner loop");
							break;
						}
					}
				}    
			}
			tokensCandidates.add(new ArrayList<Token>(validCandidates.values()));
			tokensTailCandidates.add(new ArrayList<Token>(validTailCandidates.values()));
		}
	}
	
	/**
	 * Adds a valid candidate.
	 * 
	 * @param candidates
	 * @param tailCandidates
	 * @param candidate
	 * @return
	 */
	private boolean addValidCandidate(Map<String, Token> candidates, Map<String, Token> tailCandidates, Token candidate) {
		if (!isValid(candidate)) {
			return false;
		}

		if (candidate.isAnalyzed()) {
			if (!candidates.containsKey(candidate.getTag())) {
				List<String> validTags = validTagMap.get(candidate.getPos());
				// this condition is important!!!
				if (validTags.contains(candidate.getPos())) {
					candidate.setValidated(true);
				}
				candidates.put(candidate.getTag(), candidate);
			}
			
			if (!tailCandidates.containsKey(candidate.getTag())) {
				tailCandidates.put(candidate.getTag(), candidate);
			}
			return true;
		}

		String pos = candidate.getPos();
		List<String> validTagList = validTagMap.get(pos);
		
		for (String validTag : validTagList) {
			String left = validTag.substring(0, validTag.lastIndexOf(pos));
			String leftPos = left.split("\\+")[0];
			
			if (leftPos.length() > 0) {
				Morpheme morpheme = new Morpheme();
				morpheme.setSurface(candidate.getHead());
				morpheme.setPos(leftPos);
				morpheme.setTag(leftPos);
				morpheme.setAnalyzed(false);
				
				Token clonedCandidate = new Token(candidate);
				clonedCandidate.add(morpheme);
				if (!candidates.containsKey(clonedCandidate.getTag())) {
					clonedCandidate.setValidated(true);
					candidates.put(clonedCandidate.getTag(), clonedCandidate);
				}
			}
		}
		
		if (validTagList.size() > 0) {
			if (!tailCandidates.containsKey(candidate.getTag())) {
				candidate.setValidated(true);
				tailCandidates.put(candidate.getTag(), candidate);
			}			
		}
		
		return true;
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
		List<List<Morpheme>> matchMorphemes = dic.findSuffixes(surface);

		if (matchMorphemes == null || matchMorphemes.size() == 0) {
			if (token == null || token.size() == 0) {
				return;
			}
			if (token.getHead().length() == 0) {
				token.setAnalyzed(true);
			}
			candidates.add(token);
			return;
		}
		
		System.err.println(surface + ": " + matchMorphemes);

		for (List<Morpheme> morphemeList : matchMorphemes) {
			String tail = null;
			String head = null;

			for (int i = 0; i < morphemeList.size(); i++) {
				Morpheme morpheme = morphemeList.get(i);
				morpheme.setAnalyzed(true);
				
				// split head and tail
				if (i == 0) {
					tail = morpheme.getSurface();
					head = KoreanMorphemeUtil.truncateRight(surface, tail);
				}
				
				// clone the exist token to new token
				Token clonedToken = new Token(token);
				
				String type = (String) morpheme.getAttribute("type");
				
				if (!checkPrimaryCombineCondition(morpheme, clonedToken)) {
					if (token.size() > 1) {
						clonedToken.remove(clonedToken.size() - 1);
						candidates.add(clonedToken);
					}
					continue;
				}

				if (checkSecondaryCombineCondition(morpheme, clonedToken)) {
					if (type.equals("suffix")) {
						if (token.getToken().endsWith(morpheme.getSurface())) {
							clonedToken.add(morpheme);
							clonedToken.setHead(surface);
							clonedToken.setAnalyzed(true);
							candidates.add(clonedToken);
						}
						else {
							if (token.size() > 0) candidates.add(token);
						}
						//return;
					}
					else if (type.equals("word-ind")) {
						if (token.getToken().equals(morpheme.getSurface())) {
							clonedToken.add(morpheme);
							clonedToken.setHead(head);
							clonedToken.setAnalyzed(true);
							candidates.add(clonedToken);
						}
						else {
							if (token.size() > 0) candidates.add(token);
						}
						//return;
					}
					else if (type.startsWith("head")) {
						if (head.equals("")) {
							clonedToken.add(morpheme);
							clonedToken.setHead(head);
							clonedToken.setAnalyzed(true);
							candidates.add(clonedToken);
						}
						else {
							if (token.size() > 0) candidates.add(token);
						}
						// do not return!!!
						//return;
					}
					else {
						clonedToken.add(morpheme);
						clonedToken.setHead(head);
						this.identifyMorphemeCandidates(candidates, clonedToken, head);			
					}
				}
				else {
					if (token.size() > 0) candidates.add(token);
				}
			}
		}
	}

	/**
	 * Checks if satisfying the primary condition to combine the token with the new morpheme to the left.
	 * 
	 * @param left
	 * @param token
	 * @return
	 */
	private boolean checkPrimaryCombineCondition(Morpheme left, Token token) {
		if (token.size() < 1) {
			if (left.containsAttributeKey("leftLetterCondition(+)")) {
				String head = KoreanMorphemeUtil.truncateRight(token.getToken(), left.getSurface());
				String decomposedHead = String.valueOf(KoreanUnicode.decompose(head));
				Pattern pattern = (Pattern) left.getAttribute("leftLetterCondition(+)");
				Matcher m = pattern.matcher(decomposedHead);
				
				if (!m.find()) {
					return false;
				}
			}
			if (left.containsAttributeKey("leftLetterCondition(-)")) {
				String head = KoreanMorphemeUtil.truncateRight(token.getToken(), left.getSurface());
				String decomposedHead = String.valueOf(KoreanUnicode.decompose(head));
				Pattern pattern = (Pattern) left.getAttribute("leftLetterCondition(-)");
				Matcher m = pattern.matcher(decomposedHead);
				
				if (m.find()) {
					return false;
				}
			}
			return true;
		}
		
		Morpheme right = token.getLast();
		
		//System.err.println("*leftLetterCondition(+)");
		
		// left letter condition
		if (right.containsAttributeKey("leftLetterCondition(+)")) {
			String decomposedSurface = String.valueOf(KoreanUnicode.decompose(left.getSurface()));
			Pattern pattern = (Pattern) right.getAttribute("leftLetterCondition(+)");
			Matcher m = pattern.matcher(decomposedSurface);
			
			if (!m.find()) {
				return false;
			}
		}
		
		//System.err.println("*leftLetterCondition(-)");

		if (right.containsAttributeKey("leftLetterCondition(-)")) {
			String decomposedSurface = String.valueOf(KoreanUnicode.decompose(left.getSurface()));
			Pattern pattern = (Pattern) right.getAttribute("leftLetterCondition(-)");
			Matcher m = pattern.matcher(decomposedSurface);
			
			if (m.find()) {
				return false;
			}
		}
		
		String head = KoreanMorphemeUtil.truncateRight(token.getHead(), left.getSurface());
		if (head.length() == 0 && (left.containsAttributeKey("leftLetterCondition(+)") || 
				left.containsAttributeKey("leftLetterCondition(-)"))) {
			return false;
		}
		
		//System.err.println("*leftPhonemeCondition");
		
		// left phoneme condition
		if (right.containsAttributeKey("leftPhonemeCondition") && left.containsAttributeKey("phonemeProperty")) {
			Pattern pattern = (Pattern) right.getAttribute("leftPhonemeCondition");
			String leftProperty = (String) left.getAttribute("phonemeProperty");
			Matcher m = pattern.matcher(leftProperty);
			
			if (!m.find()) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if satisfying the secondary condition to combine the token with the new morpheme to the left.
	 * 
	 * @param left
	 * @param token
	 * @return
	 */
	private boolean checkSecondaryCombineCondition(Morpheme left, Token token) {
		if (token.size() < 1) {
			return true;
		}
		
		Morpheme right = token.getLast();

		//System.err.println("*leftMorphemeCondition");
		
		// left morpheme condition
		if (right.containsAttributeKey("leftMorphemeCondition") && left.containsAttributeKey("morphemeProperty")) {
			Pattern pattern = (Pattern) right.getAttribute("leftMorphemeCondition");
			String leftProperty = (String) left.getAttribute("morphemeProperty");
			Matcher m = pattern.matcher(leftProperty);
			
			if (!m.find()) {
				return false;
			}
		}
		
		//System.err.println("*leftPosCondition");
		
		// left POS condition
		if (right.containsAttributeKey("leftPosCondition")) {
			Pattern pattern = (Pattern) right.getAttribute("leftPosCondition");
			String leftTag = left.getTag();
			leftTag = leftTag.replaceAll("\\([^a-zA-Z]+\\)$", "");
			Matcher m = pattern.matcher(leftTag);
			
			if (!m.find()) {
				return false;
			}
		}
		
		//System.err.println("*pass all conditions");

		return true;
	}
	
	/**
	 * Creates the indexing of the valid tags.
	 * 
	 * @param validTags
	 * @return
	 */
	private static Map<String, List<String>> indexingValidTags(String[] validTags) {
		Map<String, List<String>> validTagMap = new HashMap<String, List<String>>();
		
		for (String validTag : validTags) {
			String tail = null;
			int first = validTag.indexOf('+');
			if (first >= 0) {
				tail = validTag.substring(first + 1);
			}
			else {
				tail = validTag;
			}
			
			if (validTagMap.containsKey(tail)) {
				List<String> tags = validTagMap.get(tail);
				if (!tags.contains(validTag)) {
					tags.add(validTag);
				}
			}
			else {
				List<String> tags = new ArrayList<String>();
				tags.add(validTag);
				validTagMap.put(tail, tags);
			}
			
			if (validTagMap.containsKey(validTag)) {
				List<String> tags = validTagMap.get(validTag);
				if (!tags.contains(validTag)) {
					tags.add(validTag);
				}
			}
			else {
				List<String> tags = new ArrayList<String>();
				tags.add(validTag);
				validTagMap.put(validTag, tags);
			}
		}
		
		return validTagMap;
	}

	/**
	 * Checks if the morphemes of a token is valid or not.
	 * 
	 * @param token	The morphemes of token
	 * @return
	 */
	private boolean isValid(Token token) {
		if (validTagMap.containsKey(token.getPos())) {
			return true;
		}
		
		return false;
	}

}
