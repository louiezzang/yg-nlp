package com.yglab.nlp.postag.lang.ko;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yglab.nlp.postag.TagPattern;
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

	/** the pattern for finding collocated pos tags */
	private static final Pattern COLLOCATE_PATTERN = Pattern.compile(
			"((XSA|XSV|VX)\\+(EP|ETN|ETM|EC|EF))" + "|" +
			"((EP|EC)\\+(EF|EC|ETM))"
			);
	
	/** the pattern for finding tail tag */
	private static final Pattern TAILTAG_PATTERN = Pattern.compile(
			"([^/\\+\\(\\)]*)/([XEJ][A-Z]+|VX|VCP).*");
	
	/** the pattern for finding extra feature from tag */
	private static final Pattern TAG_FEATURE_PATTERN = Pattern.compile(
			"([^/\\+\\(\\)]*)/(NNB)");
	
	private KoreanMorphemeDictionary dic;
	private KoreanLemmatizer lemmatizer;
	//private final String[] validTags;
	private Map<String, List<String>> validTagMap = new HashMap<String, List<String>>();
	private String[] tokensArray;
	private List<List<Token>> tokensCandidates = new ArrayList<List<Token>>();
	private List<List<String>> tokensTailCandidates = new ArrayList<List<String>>();
	private List<List<String>> tokensFeatures = new ArrayList<List<String>>();
	
	/** comparator for sorting tail descending by length */
	private static final Comparator<String> tailComparator = new Comparator<String>() {
		public int compare(String o1, String o2) {
			int numTag1 = o1.split("\\+").length;
			int numTag2 = o1.split("\\+").length;
			if (numTag1 == numTag2) {
				int len1 = o1.replaceAll("\\+", "").length();
				int len2 = o2.replaceAll("\\+", "").length();
				return Integer.valueOf(len2).compareTo(Integer.valueOf(len1));
			}
			else {
				return Integer.valueOf(numTag2).compareTo(Integer.valueOf(numTag1));
			}
		}
	};

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
		this.lemmatizer = new KoreanLemmatizer();
		
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
	public List<String> getCurrentTailCandidates(int position) {
		return this.tokensTailCandidates.get(position);
	}
	
	/**
	 * Gets the features for the token at the specific position of the tokens.
	 * 
	 * @param position
	 * @return
	 */
	public List<String> getCurrentFeatures(int position) {
		return this.tokensFeatures.get(position);
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
			
			List<Token> candidates = this.getCurrentCandidates(position);
			
			if (candidates.size() == 0) {
				String strToken = tokensArray[position];
				Token token = new Token(strToken);
				Morpheme morpheme = new Morpheme();
				morpheme.setSurface(strToken);
				morpheme.setTag(strToken + "/" + predictedTag);
				morpheme.setPos(predictedTag);
				token.add(morpheme);
				token = lemmatizer.lemmatize(token);
				tokens.add(token);
				System.out.println(position + "*: " + token.getToken() + "[" +  token.getTag() + "], analyzed=" + token.isAnalyzed() + ", head=" + token.getHead());
				continue;
			}
			
			boolean matchWithCandidate = false;
			for (Token token : candidates) {
				if (predictedTag.equals(token.getPos())) {
					token = lemmatizer.lemmatize(token);
					System.out.println(position + ": " + token.getToken() + "[" +  token.getTag() + "], analyzed=" + token.isAnalyzed() + ", head=" + token.getHead());
					tokens.add(token);
					matchWithCandidate = true;
					break;
				}
			}
			
			if (!matchWithCandidate) {
				String strToken = tokensArray[position];
				Token token = new Token(strToken);
				Morpheme morpheme = new Morpheme();
				morpheme.setSurface(strToken);
				if (predictedTag.split("\\+").length == 1) {
					morpheme.setTag(strToken + "/" + predictedTag);
				}
				else {
					morpheme.setTag(predictedTag);
				}
				morpheme.setPos(predictedTag);
				token.add(morpheme);
				token = lemmatizer.lemmatize(token);
				tokens.add(token);
				System.out.println(position + "**: " + token.getToken() + "[" +  token.getTag() + "], analyzed=" + token.isAnalyzed() + ", head=" + token.getHead());
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
		/* initializes the candidates for the current tokens */
		this.tokensArray = tokens;
		this.tokensCandidates.clear();
		this.tokensTailCandidates.clear();
		this.tokensFeatures.clear();
		
		for (int position = 0; position < tokens.length; position++) {
			String strToken = tokens[position];
			Map<String, Token> validCandidates = new HashMap<String, Token>();
			List<String> validTailCandidates = new ArrayList<String>();
			List<String> tokenFeatures = new ArrayList<String>();
			
			/* identify morpheme candidates for a token */
			List<Token> candidates = new ArrayList<Token>();
			this.identifyMorphemeCandidates(candidates, new Token(strToken), strToken);
			
			/* sort descending by the number of tag of the token */
			Collections.sort(candidates);
			
			int maxNumTag = 0;
			outerLoop:
			for (int ci = 0; ci < candidates.size(); ci++) {
				Token candidate = candidates.get(ci);
				System.out.println(candidate.getToken() + ": " + candidate.getTag() + ", " + candidate.getPos() + ", " + candidate.getNumTag());
				
				if (this.addValidCandidate(candidate, validCandidates, validTailCandidates, tokenFeatures)) {
					System.out.println(" -> " + candidate.getToken() + ": " + candidate.getTag() + ", " + candidate.getPos() + ", " + candidate.getNumTag());
					int numTag = candidate.getNumTag();
					if (numTag > maxNumTag) {
						maxNumTag = numTag;
					}
				}
				/*
				 * if the candidate has been full analyzed but doesn't match with valid tag, 
				 * it is added into the valid candidates though.
				 */
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
					
					if (this.addValidCandidate(tail, validCandidates, validTailCandidates, tokenFeatures)) {
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
			
			/* sort the valid tail candidates descending by length */
			Collections.sort(validTailCandidates, tailComparator);
			tokensTailCandidates.add(validTailCandidates);
			
			tokensFeatures.add(tokenFeatures);
		}
	}
	
	/**
	 * Adds a valid candidate.
	 * 
	 * @param candidate	The token candidate
	 * @param candidates	The token candidates
	 * @param tailCandidates	The tail candidates
	 * @param tokenFeatures	The token features
	 * @return
	 */
	private boolean addValidCandidate(Token candidate, Map<String, Token> candidates, List<String> tailCandidates, List<String> tokenFeatures) {
		if (!isValid(candidate)) {
			return false;
		}

		String tag = candidate.getTag();
		String pos = candidate.getPos();
		
		if (candidate.isAnalyzed()) {
			/* add valid token candidate */
			if (!candidates.containsKey(tag)) {
				List<String> validTags = validTagMap.get(pos);
				/* this condition is important!!! */
				if (validTags.contains(pos)) {
					candidate.setValidated(true);
				}
				candidates.put(tag, candidate);
			}
			
			/* add tail candidate */
			String tailtag = candidate.getTagStartsWith(TAILTAG_PATTERN);
			String tail = extractTailMorphemes(tailtag);
			if (tail != null && !tail.equals("") && !tailCandidates.contains(tail)) {
				tailCandidates.add(tail);
			}
			
			/* add token features */
			String feature = extractFeature(tag);
			if (!tokenFeatures.contains(feature)) {
				tokenFeatures.add(feature);
			}
			
			return true;
		}

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
				/* add valid token candidate */
				if (!candidates.containsKey(clonedCandidate.getTag())) {
					clonedCandidate.setValidated(true);
					candidates.put(clonedCandidate.getTag(), clonedCandidate);
				}
			}
			else {
				/* add valid token candidate */
				if (!candidates.containsKey(tag)) {
					candidate.setValidated(true);
					candidates.put(tag, candidate);
				}
			}
		}
		
		/* add tail candidate */
		if (validTagList.size() > 0) {
			String tailtag = candidate.getTagStartsWith(TAILTAG_PATTERN);
			String tail = extractTailMorphemes(tailtag);
			if (tail != null && !tail.equals("") && !tailCandidates.contains(tail)) {
				tailCandidates.add(tail);
			}	
		}
		
		/* add token features */
		if (validTagList.size() > 0) {
			String feature = extractFeature(tag);
			if (!tokenFeatures.contains(feature)) {
				tokenFeatures.add(feature);
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
		/* 
		 * find the all suffixes matched with the dictionary.
		 * the matched morphemes should not be referenced with those of morpheme dictionary!!!
		 */
		List<List<Morpheme>> matchMorphemes = new ArrayList<List<Morpheme>>(dic.findSuffixes(surface));

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
		
		//System.err.println(surface + ": " + matchMorphemes);

		for (List<Morpheme> morphemeList : matchMorphemes) {
			String tail = null;
			String head = null;

			for (int i = 0; i < morphemeList.size(); i++) {
				Morpheme morpheme = morphemeList.get(i);
				morpheme.setAnalyzed(true);
				
				/* split head and tail */
				if (i == 0) {
					tail = morpheme.getSurface();
					head = KoreanMorphemeUtil.truncateRight(surface, tail);
				}
				
				/* clone the exist token to new token */
				Token clonedToken = new Token(token);
				
				String type = (String) morpheme.getAttribute("type");
				
				if (!checkPrimaryCombineCondition(morpheme, clonedToken)) {
					if (token.size() > 1) {
						String revivedHead = clonedToken.getHead() + clonedToken.get(clonedToken.size() - 1).getSurface();
						clonedToken.remove(clonedToken.size() - 1);
						/* revive head */
						clonedToken.setHead(revivedHead);
						candidates.add(clonedToken);
					}
					continue;
				}

				if (checkSecondaryCombineCondition(morpheme, clonedToken)) {
					if (type.equals("suffix")) {
						if (token.getToken().endsWith(morpheme.getSurface())) {
							morpheme.setSurface(token.getToken());
							morpheme.setTag(token.getToken() + "/" + morpheme.getPos());
							clonedToken.add(morpheme);
							clonedToken.setHead("");
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
					else if (type.equals("word-prefix")) {
						if (head.equals("")) {
							clonedToken.add(morpheme);
							clonedToken.setHead(head);
							clonedToken.setAnalyzed(true);
							candidates.add(clonedToken);
						}
						else {
							if (token.size() > 0) candidates.add(token);
						}
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
						/* do not return!!! */
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
		
		/* left letter condition */
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
		
		/* left phoneme condition */
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
		
		/* left morpheme condition */
		if (right.containsAttributeKey("leftMorphemeCondition") && left.containsAttributeKey("morphemeProperty")) {
			Pattern pattern = (Pattern) right.getAttribute("leftMorphemeCondition");
			String leftProperty = (String) left.getAttribute("morphemeProperty");
			Matcher m = pattern.matcher(leftProperty);
			
			if (!m.find()) {
				return false;
			}
		}
		
		//System.err.println("*leftPosCondition");
		
		/* left POS condition */
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
	
	private static String extractTailMorphemes(String tailtag) {
		if (tailtag == null || tailtag.trim().equals("")) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Matcher m = TagPattern.MORPH_POS_PATTERN.matcher(tailtag);
		while (m.find()) {
			if (sb.length() > 0) {
				sb.append("+");
			}
			sb.append(m.group(1));
		}
		
		return sb.toString();
	}
	
	private static String extractFeature(String tag) {
		if (tag == null || tag.trim().equals("")) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Matcher m = TAG_FEATURE_PATTERN.matcher(tag);
		while (m.find()) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(m.group(1) + "/" + m.group(2));
		}
		
		return sb.toString();
	}

}
