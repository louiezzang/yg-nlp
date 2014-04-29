package com.yglab.nlp.postag.lang.ko;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.yglab.nlp.postag.POSSampleParser;
import com.yglab.nlp.postag.morph.Morpheme;
import com.yglab.nlp.postag.morph.MorphemeDictionary;
import com.yglab.nlp.postag.morph.Token;
import com.yglab.nlp.util.lang.ko.MorphemeUtil;
import com.yglab.nlp.util.trie.TrieSuffixMatcher;

/**
 * This class analyzes the morphemes by using morpheme dictionary.
 * 
 * @author Younggue Bae
 */
public class KoreanMorphemeAnalyzer {

	private MorphemeDictionary dic;
	private final String[] validTags;
	private TrieSuffixMatcher<Integer> validTagTrie;
	
	private List<String> defaultTagCandidates = new ArrayList<String>();
	private List<List<Token>> tokensTailCandidates = new ArrayList<List<Token>>();
	private List<List<String>> tokensTagCandidates = new ArrayList<List<String>>();

	/**
	 * Constructor.
	 * 
	 * @param dic	The morpheme dictionary
	 * @param tags	The valid unique tags or labels
	 */
	public KoreanMorphemeAnalyzer(MorphemeDictionary dic, String[] tags) {
		this.dic = dic;
		this.validTags = tags;
		this.validTagTrie = new TrieSuffixMatcher<Integer>();
		
		if (tags == null || tags.length == 0) {
			return;
		}
		
		// make default tag candidates
		this.makeDefaultTagCandidates(tags);
		
		// make trie of valid tags
		this.makeValidTagTrie(tags);
	}
	
	/**
	 * Makes the default tag candidates which consist of single tag.
	 * 
	 * @param tags
	 */
	private void makeDefaultTagCandidates(final String[] tags) {
		if (tags != null) {
			for (String tag : tags) {
				if (tag.indexOf("+") < 0) {
					defaultTagCandidates.add(tag);
				}
			}
		}		
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
	 * Gets the tag candidates for the current all tokens.
	 * 
	 * @return
	 */
	public List<List<String>> getCurrentTokensTagCandidates() {
		return this.tokensTagCandidates;
	}
	
	/**
	 * Gets the tag candidates for the token at the specific position of the tokens.
	 * 
	 * @param position
	 * @return
	 */
	public List<String> getCurrentTokenTagCandidates(int position) {
		return this.tokensTagCandidates.get(position);
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
		
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			List<Token> tailValidCandidates = new ArrayList<Token>();
			List<Token> tailCandidates = this.findTailCandidates(token);
			
			for (Token tail : tailCandidates) {
				System.out.println("*" + tokens[i] + ": " + tail.getTag() + ", " + tail.getNumTag());

				for (int ti = tail.size() - 1; ti >= 0; ti--) {
					Token subTail = tail.getSubTail(ti);
					if (isValidTail(subTail)) {
						if (!tailValidCandidates.contains(subTail)) {
							tailValidCandidates.add(subTail);
							System.out.println("**" + tokens[i] + ": " + subTail.getTag() + ", " + subTail.getNumTag());
						}
						if (subTail.getNumTag() >= 3) {
							break;	// exit this 'for' loop
						}
					}
				}
			}
			tokensTailCandidates.add(tailValidCandidates);
		}
	}
	
	public void findTagCandidates(String[] tokens) {
		// initializes the current tag candidates for tokens
		tokensTagCandidates.clear();
		
		for (int position = 0; position < tokens.length; position++) {
			String token = tokens[position];
			List<Token> tailCandidates = tokensTailCandidates.get(position);
			
			if (tailCandidates.size() == 0) {
				if (token.equals(".") || token.equals("!") || token.equals("?")) {
					String[] sf = { "SF" };
					tokensTagCandidates.add(Arrays.asList(sf));
				}
				else if (token.equals(",")) {
					String[] sp = { "SP" };
					tokensTagCandidates.add(Arrays.asList(sp));
				}
				else {
					tokensTagCandidates.add(defaultTagCandidates);
				}
				continue;
			}
			
			List<String> tagList = new ArrayList<String>();
			for (Token tailCandidate : tailCandidates) {
				// for debugging
				System.out.println(token + ", tail=" + tailCandidate.getTag() + ", size=" + tailCandidate.size());
				
				String postag = tailCandidate.getPos();
				if (tailCandidate.size() == 1 && tailCandidate.getHead().equals("")) {
					for (String validTag : validTags) {
						if (validTag.equals(validTag)) {
							tagList.add(validTag);
						}
					}
				}
				else if (tailCandidate.getNumTag() >= 3) {
					for (String validTag : validTags) {
						if (validTag.endsWith(postag)) {
							if (!tagList.contains(validTag)) {
								tagList.add(validTag);
							}
						}				
					}
				}
				else {	
					for (String validTag : validTags) {
						int validTagNum = validTag.split("\\+").length;
						int maxTagNum = tailCandidate.getNumTag() + 1;
						if (validTagNum == maxTagNum && validTag.endsWith(postag)) {
							if (!tagList.contains(validTag)) {
								tagList.add(validTag);
							}
						}
					}
				}
			}
			
			if (tagList.size() == 0) {
				tagList.add("NNG");
			}
			
			System.out.println(position + ": " + tagList);

			tokensTagCandidates.add(tagList);
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
		String matchMorphDic = dic.findSuffix(token);

		if (matchMorphDic == null) {
			return tailCandidates;
		}

		//System.out.println("match = " + matchMorphDic);

		String[] morphItems = matchMorphDic.split("\\|");
		String tailSurface = matchMorphDic.split("\t")[0];
		String head = MorphemeUtil.truncateRight(token, tailSurface);

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
		String matchMorphDic = dic.findSuffix(surface);

		if (matchMorphDic == null) {
			return;
		}

		String[] morphItems = matchMorphDic.split("\\|");
		String tailSurface = matchMorphDic.split("\t")[0];
		String head = MorphemeUtil.truncateRight(surface, tailSurface);

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
	 * Converts the dictionary string to {@link Morpheme} object. 
	 * 
	 * @param str
	 * @return
	 */
	// TODO: 형태소 사전에 정의된 rule을 반영하도록 업데이트 필요.
	private static Morpheme dictionaryToMorpheme(String str) {
		Morpheme morph = new Morpheme();
		String[] fields = str.split("\t");

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

}
