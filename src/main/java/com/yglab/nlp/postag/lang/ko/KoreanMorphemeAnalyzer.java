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

	private MorphemeDictionary baseDic;
	private MorphemeDictionary extendedDic;
	private final String[] validTags;
	private TrieSuffixMatcher<Integer> validTagTrie;
	private List<List<Token>> tokensTailCandidates = new ArrayList<List<Token>>();

	/**
	 * Constructor.
	 * 
	 * @param baseDic	The base morpheme dictionary
	 * @param tags	The valid unique tags or labels
	 */
	public KoreanMorphemeAnalyzer(MorphemeDictionary baseDic, String[] tags) {
		this(baseDic, null, tags);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param baseDic	The base morpheme dictionary
	 * @param extendedDic	The extended dictionary such as suffix or word
	 * @param tags	The valid unique tags or labels
	 */
	public KoreanMorphemeAnalyzer(MorphemeDictionary baseDic, MorphemeDictionary extendedDic, String[] tags) {
		this.baseDic = baseDic;
		this.extendedDic = extendedDic;
		this.validTags = tags;
		this.validTagTrie = new TrieSuffixMatcher<Integer>();
		
		if (tags == null || tags.length == 0) {
			return;
		}

		// make trie of valid tags
		this.makeValidTagTrie(tags);		
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
			
			List<Token> tailCandidates = this.getCurrentTokenTailCandidates(position);
			for (Token token : tailCandidates) {
				if (predictedTag.endsWith(token.getPos())) {
					System.out.println(token.getPos());
					Morpheme head = new Morpheme();
					head.setSurface(token.getHead());
					head.setLemma(token.getHead());
					head.setTag(predictedTag.substring(0, predictedTag.lastIndexOf(token.getPos())));
					
					System.out.print("surface = " + head.getSurface());
					System.out.print(", lemma = " + head.getLemma());
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
	 * Gets the tail candidates for the current all tokens.
	 * 
	 * @return
	 */
	public List<List<Token>> getCurrentTokensTailCandidates() {
		return this.tokensTailCandidates;
	}
	
	/**
	 * Gets the tail candidates for the token at the specific position of the tokens.
	 * 
	 * @param position
	 * @return
	 */
	public List<Token> getCurrentTokenTailCandidates(int position) {
		return this.tokensTailCandidates.get(position);
	}
	
	/**
	 * Finds the tail candidates for the each token.
	 * 
	 * @param tokens The tokens' array
	 * @return
	 */
	//TODO: 형태소 사전에 정의된 제약조건을 만족하는지 체크해서 리스트에 추가할 것.
	public void findTailCandidates(String[] tokens) {
		// initializes the current tail candidates for tokens
		tokensTailCandidates.clear();
		
		for (int position = 0; position < tokens.length; position++) {
			String token = tokens[position];
			List<Token> tailValidCandidates = new ArrayList<Token>();
			List<Token> tailCandidates = this.findTailCandidates(token);
			 
			boolean findExtendedDic = true;
			//outerloop:
			for (Token tail : tailCandidates) {
				System.out.println("*" + token + ": " + tail.getTag() + ", " + tail.getNumTag());

				for (int ti = tail.size() - 1; ti >= 0; ti--) {
					Token subTail = tail.getSubTail(ti);
					if (isValidTail(subTail)) {
						if (!tailValidCandidates.contains(subTail)) {
							tailValidCandidates.add(subTail);
							System.out.println("**" + token + ": " + subTail.getTag() + ", " + subTail.getNumTag());
						}
						if (subTail.getNumTag() == 3) {
						//if (subTail.getNumTag() >= 3) {
							findExtendedDic = false;
							break;	// exit this inner 'for' loop
							// TODO: 이 경우 현재 'for' loop를 break했을 때보다 이상하게 precision이 더 낮아짐. 좀 더 테스트 필요.
							//break outerloop;	// exit outer 'for' loop 
						}
					}
				}
			}
			
			if (findExtendedDic && extendedDic != null) {
				tailValidCandidates.addAll(this.findExtendedDictionary(token));
			}
			
			tokensTailCandidates.add(tailValidCandidates);
		}
	}
	
	/**
	 * Finds the tail candidates for the specified token.
	 * 
	 * @param token
	 * @return
	 */
	private List<Token> findTailCandidates(String token) {
		List<Token> tailCandidates = new ArrayList<Token>();
		
		// find the longest matched suffix in the dictionary
		String matchMorphDic = baseDic.findLongestSuffix(token);

		if (matchMorphDic == null) {
			return tailCandidates;
		}

		//System.out.println("match = " + matchMorphDic);

		String[] morphItems = matchMorphDic.split("\\|");
		String tailSurface = matchMorphDic.split("\t")[0];
		String head = KoreanMorphemeUtil.truncateRight(token, tailSurface);

		for (String morphItem : morphItems) {
			Token tail = new Token(token);
			tail.add(dictionaryToMorpheme(morphItem));

			if (!tailCandidates.contains(tail)) {
				tailCandidates.add(tail);
			}
		}
		
		findTailCandidates(tailCandidates, token, head);
		
		for (Token tail : tailCandidates) {
			Integer matchNumTag = validTagTrie.longestMatch(tail.getPos());
			if (matchNumTag != null) {
				tail.setNumValidTag(matchNumTag);
			}
		}
		
		// sort descending by the number of tag of the tail
		Collections.sort(tailCandidates);
		
		// for debugging
		//for (Tail t : tailCandidates) {
		//	System.out.println(t.getToken() + " : " + t.getNumTag());
		//}
		
		return tailCandidates;
	}
	
	/**
	 * Finds the tail candidates for the specified surface.
	 * 
	 * @param tailCandidates	The tail candidate list to store
	 * @param token	The source token text
	 * @param surface	The surface text to find tail in it
	 */
	private void findTailCandidates(List<Token> tailCandidates, String token, String surface) {
		// find the longest matched suffix in the dictionary
		String matchMorphDic = baseDic.findLongestSuffix(surface);

		if (matchMorphDic == null) {
			return;
		}

		String[] morphItems = matchMorphDic.split("\\|");
		String tailSurface = matchMorphDic.split("\t")[0];
		String head = KoreanMorphemeUtil.truncateRight(surface, tailSurface);

		List<Token> prevTails = new ArrayList<Token>(tailCandidates);
		int index = 0;
		for (String morphItem : morphItems) {
			if (prevTails.size() > 0) {
				for (int i = 0; i < prevTails.size(); i++) {
					Token clonedTail = new Token(prevTails.get(i));

					clonedTail.add(dictionaryToMorpheme(morphItem));
					clonedTail.setHead(head);
	
					if (index < prevTails.size()) {
						tailCandidates.set(index, clonedTail);
					} else {
						if (!tailCandidates.contains(clonedTail)) {
							tailCandidates.add(clonedTail);
						}
					}
					index++;
				}
			} else {
				Token tail = new Token(token);
				tail.add(dictionaryToMorpheme(morphItem));
				tail.setHead(head);

				if (!tailCandidates.contains(tail)) {
					tailCandidates.add(tail);
				}
			}
		}

		findTailCandidates(tailCandidates, token, head);
	}
	
	/**
	 * Finds the extended dictionary for the specified token.
	 * 
	 * @param token
	 * @return
	 */
	private List<Token> findExtendedDictionary(String token) {
		List<Token> tailCandidates = new ArrayList<Token>();
		
		// find the longest matched suffix or word in the dictionary
		String matchMorphDic = extendedDic.findLongestSuffix(token);

		if (matchMorphDic == null) {
			return tailCandidates;
		}

		//System.out.println("matched extended dic = " + matchMorphDic);

		String[] morphItems = matchMorphDic.split("\\|");

		for (String morphItem : morphItems) {
			Token tail = new Token(token);
			Morpheme morph = dictionaryToMorpheme(morphItem);
			String morphemeCondition = (String) morph.getAttribute("morphemeCondition");
			if (morphemeCondition.equals("word")) {
				if (morph.getSurface().equals(token)) {
					tail.add(morph);
					tail.setHead("");	// TODO: 자체 단어를 헤드로?
					tail.setNumValidTag(1);
	
					if (!tailCandidates.contains(tail)) {
						tailCandidates.add(tail);
					}
				}
			}
			else {
				tail.add(morph);
				tail.setHead("");	// TODO: 자체 단어를 헤드로?
				tail.setNumValidTag(1);
				
				if (!tailCandidates.contains(tail)) {
					tailCandidates.add(tail);
				}
			}
		}
		
		return tailCandidates;
	}	

	/**
	 * Converts the dictionary string to {@link Morpheme} object. 
	 * 
	 * @param str
	 * @return
	 */
	private static Morpheme dictionaryToMorpheme(String str) {
		Morpheme morph = new Morpheme();
		String[] fields = str.split("\t", -1);

		String surface = fields[0];
		String tag = fields[1];
		String[] tagItems = tag.split("\\+");

		StringBuilder sbPos = new StringBuilder();
		StringBuilder sbMorph = new StringBuilder();
		for (int i = 0; i < tagItems.length; i++) {
			String tagItem = tagItems[i];
			String pos = POSSampleParser.parsePos(tagItem);
			String strMorph = POSSampleParser.parseMorpheme(tagItem);

			sbPos.append(pos);
			sbMorph.append(strMorph);
			if (i < tagItems.length - 1) {
				sbPos.append("+");
			}
		}

		morph.setSurface(surface);
		morph.setLemma(sbMorph.toString());
		morph.setTag(tag);
		morph.setPos(sbPos.toString());
		//morph.setPosDescription();

		morph.setAttribute("phonemeCondition", fields[2]);
		morph.setAttribute("morphemeCondition", fields[3]);
		morph.setAttribute("posCondition", fields[4]);
		morph.setAttribute("irregularCondition", fields[5]);

		return morph;
	}
	
	/**
	 * Checks if the specified tail is valid or not.
	 * 
	 * @param tail	The tail of token
	 * @return
	 */
	private boolean isValidTail(Token tail) {
		if (validTags == null || validTags.length == 0) {
			System.err.println("The valid tags are null!");
			return true;
		}

		for (String validTag : validTags) {
			if (validTag.endsWith(tail.getPos())) {
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

}
