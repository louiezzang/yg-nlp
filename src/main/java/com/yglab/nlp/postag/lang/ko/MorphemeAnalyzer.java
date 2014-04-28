package com.yglab.nlp.postag.lang.ko;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.yglab.nlp.postag.POSSampleParser;
import com.yglab.nlp.util.lang.ko.MorphemeUtil;

/**
 * This class analyzes the morphemes by using morpheme dictionary.
 * 
 * @author Younggue Bae
 */
public class MorphemeAnalyzer {

	private MorphemeDictionary dic;
	private final String[] validTags;
	
	private List<List<Tail>> tokensTailCandidates = new ArrayList<List<Tail>>();

	public MorphemeAnalyzer(MorphemeDictionary dic, String[] tags) {
		this.dic = dic;
		this.validTags = tags;
	}
	
	private boolean isValidTail(Tail tail) {
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
	 * Converts the dictionary string to {@link Morpheme} object. 
	 * 
	 * @param str
	 * @return
	 */
	// TODO: 형태소 사전에 정의된 rule을 반영하도록 업데이트 필요.
	private static Morpheme dicStringToMorpheme(String str) {
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
		// morph.setPosDescription();

		return morph;
	}
	
	private List<Tail> findTailCandidates(String token) {
		List<Tail> tailCandidates = new ArrayList<Tail>();
		
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
			Tail tail = new Tail(token);
			tail.add(dicStringToMorpheme(morphItem));

			if (!tailCandidates.contains(tail)) {
				tailCandidates.add(tail);
			}
		}
		
		findTailCandidates(tailCandidates, token, head);
		
		Collections.sort(tailCandidates);
		
//		for (Tail t : tailCandidates) {
//			System.out.println(t.getToken() + " : " + t.getTagSize());
//		}
		
		return tailCandidates;
	}
	
	private void findTailCandidates(List<Tail> tailCandidates, String token, String surface) {
		// find the longest matched suffix in the dictionary
		String matchMorphDic = dic.findSuffix(surface);

		if (matchMorphDic == null) {
			return;
		}

		String[] morphItems = matchMorphDic.split("\\|");
		String tailSurface = matchMorphDic.split("\t")[0];
		String head = MorphemeUtil.truncateRight(surface, tailSurface);

		List<Tail> prevTails = new ArrayList<Tail>(tailCandidates);
		int index = 0;
		for (String morphItem : morphItems) {
			if (prevTails.size() > 0) {
				for (int i = 0; i < prevTails.size(); i++) {
					Tail clonedTail = new Tail(prevTails.get(i));

					clonedTail.add(dicStringToMorpheme(morphItem));
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
				Tail tail = new Tail(token);
				tail.add(dicStringToMorpheme(morphItem));
				tail.setHead(head);

				if (!tailCandidates.contains(tail)) {
					tailCandidates.add(tail);
				}
			}
		}

		findTailCandidates(tailCandidates, token, head);
	}
	
	public List<List<Tail>> findTailCandidates(String[] tokens) {
		// initializes the current tail candidates for tokens.
		tokensTailCandidates.clear();
		
		for (int i = 0; i < tokens.length; i++) {
			List<Tail> tailValidCandidates = new ArrayList<Tail>();
			// TODO: 리스트를 tail size 역순으로 정렬할 것.
			List<Tail> tailCandidates = this.findTailCandidates(tokens[i]);
			
			for (Tail tail : tailCandidates) {
				// TODO: 아래 조건을 만족하는 경우, 바로 위의 for문을 exit한다.
				if (tail.getTagSize() >= 3 && isValidTail(tail.getSubTail(tail.size() - 1))) {
					Tail subTail = tail.getSubTail(tail.size() - 1);
					if (!tailValidCandidates.contains(subTail)) {
						tailValidCandidates.add(subTail);
					}
					break;
				}
				else {
					for (int ti = tail.size() - 1; ti >= 0; ti--) {
						Tail subTail = tail.getSubTail(ti);
						if (isValidTail(subTail) && !tailValidCandidates.contains(subTail)) {
							tailValidCandidates.add(subTail);
							if (subTail.getTagSize() >= 3) {
								break;
							}
						}
					}
				}
			}
			
			tokensTailCandidates.add(tailValidCandidates);
		}
		
		return tokensTailCandidates;
	}
	
	public List<List<Tail>> getCurrentTokensTailCandidates() {
		return this.tokensTailCandidates;
	}
	
	public List<Tail> getCurrentTokenTailCandidates(int position) {
		return this.tokensTailCandidates.get(position);
	}

	public static void main(String[] args) throws Exception {
		MorphemeDictionary dic = new MorphemeDictionary(
				"/lang/ko/ko-pos-josa.dic",
				"/lang/ko/ko-pos-eomi.dic", 
				"/lang/ko/ko-pos-bojo.dic");

		MorphemeAnalyzer analyzer = new MorphemeAnalyzer(dic, null);
		//String token = "자아내었고";
		//String token = "부각되기도";
		//String token = "올해에ㄴ";
		String token = "전반적인";
		
		List<Tail> tailCandidates = analyzer.findTailCandidates(token); 
		
		for (Tail tail : tailCandidates) {
			System.out.println(tail.getHead() + " + " + tail.getTag());
		}
	}
}
