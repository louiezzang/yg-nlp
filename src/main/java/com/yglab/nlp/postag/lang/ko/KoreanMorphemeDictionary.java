package com.yglab.nlp.postag.lang.ko;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.yglab.nlp.postag.POSSampleParser;
import com.yglab.nlp.postag.morph.AbstractSuffixDictionary;
import com.yglab.nlp.postag.morph.Morpheme;
import com.yglab.nlp.util.lang.ko.KoreanMorphemeUtil;
import com.yglab.nlp.util.lang.ko.KoreanUnicode;


/**
 * Dictionary for morphemes based on suffix(backward direction) trie structure.
 * 
 * @author Younggue Bae
 */
public class KoreanMorphemeDictionary extends AbstractSuffixDictionary<List<Morpheme>>{
	
	private static Map<String, Pattern> mapPattern = new HashMap<String, Pattern>();
	
	public KoreanMorphemeDictionary(String... files) throws IOException {
		super(files);
	}
	
	public KoreanMorphemeDictionary(int keyColumnIndex, String... files) throws IOException {
		super(keyColumnIndex, files);
	}
	
	@Override
	public void addDictionary(String str) {
		Morpheme morpheme = this.parseMorpheme(str);
		
		System.out.println(morpheme);
		
		String key = morpheme.getKey();
		List<Morpheme> match = trieSuffix.longestMatch(key);
		if (match != null && match.size() > 0) {
			String existKey = match.get(0).getKey();
			// if duplicate morpheme surface, add new one to the exist dictionary
			if (existKey.equals(key)) {
				match.add(morpheme);
			}
			else {
				List<Morpheme> morphemes = new ArrayList<Morpheme>();
				morphemes.add(morpheme);
				trieSuffix.add(key, morphemes);
			}
		}
		else {
			List<Morpheme> morphemes = new ArrayList<Morpheme>();
			morphemes.add(morpheme);
			trieSuffix.add(key, morphemes);
		}
	}
	
	@Override
	public String decompose(String str) {
		char[] ch = KoreanUnicode.decompose(str);

		return String.valueOf(ch);
	}
	
	/**
	 * Parses the dictionary text to {@link Morpheme} object. 
	 * 
	 * @param str
	 * @return
	 */
	private Morpheme parseMorpheme(String str) {
		Morpheme morpheme = new Morpheme();
		String[] field = str.split("\t", -1);

		String strKey = field[keyColumnIndex].trim();
		String key = this.decompose(strKey);
		
		String surface = field[0];
		String tag = field[1];
		String type = field[2];
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

		morpheme.setKey(key);
		morpheme.setSurface(surface);
		morpheme.setTag(tag);
		morpheme.setPos(sbPos.toString());
		morpheme.setAttribute("type", type);

		this.addLeftLetterCondition(morpheme, field[3]);
		this.addLeftMorphemeCondition(morpheme, field[4]);
		this.addLeftPhonemeCondition(morpheme, field[5]);
		this.addLeftPosCondition(morpheme, field[6]);
		this.addLeftLemmatizationRule(morpheme, field[7]);
		this.addMorphemeProperty(morpheme, field[8]);
		this.addPhonemeProperty(morpheme, field[9]);

		return morpheme;
	}
	
	/**
	 * Adds a left letter condition.
	 * 
	 * @param morpheme
	 * @param condition
	 */
	private void addLeftLetterCondition(Morpheme morpheme, String condition) {
		String[] arrCondition = condition.split("\\s");
		
		StringBuilder sbInclude = new StringBuilder();
		StringBuilder sbExclude = new StringBuilder();
		for (String cond : arrCondition) {
			if (cond.startsWith("+")) {
				if (sbInclude.length() > 0) {
					sbInclude.append("|");
				}
				sbInclude.append(this.decompose(cond.substring(1).trim()) + "$");
			}
			else if (cond.startsWith("-")) {
				if (sbExclude.length() > 0) {
					sbExclude.append("|");
				}
				sbExclude.append(this.decompose(cond.substring(1).trim()) + "$");
			}
		}
		
		if (sbInclude.length() > 0) {
			Pattern pattern = null;
			if (mapPattern.containsKey(sbInclude.toString())) {
				pattern = mapPattern.get(sbInclude.toString());
			}
			else {
				pattern = Pattern.compile(sbInclude.toString());
				mapPattern.put(sbInclude.toString(), pattern);
			}
			morpheme.setAttribute("leftLetterCondition(+)", pattern);
		}
		
		if (sbExclude.length() > 0) {
			Pattern pattern = null;
			if (mapPattern.containsKey(sbExclude.toString())) {
				pattern = mapPattern.get(sbExclude.toString());
			}
			else {
				pattern = Pattern.compile(sbExclude.toString());
				mapPattern.put(sbExclude.toString(), pattern);
			}
			morpheme.setAttribute("leftLetterCondition(-)", pattern);
		}
	}
	
	/**
	 * Adds a left morpheme condition.
	 * 
	 * @param morpheme
	 * @param condition
	 */
	private void addLeftMorphemeCondition(Morpheme morpheme, String condition) {
		String[] arrCondition = condition.split("\\s");
		
		StringBuilder sbRegex = new StringBuilder();
		for (String cond : arrCondition) {
			if (cond.trim().length() > 0) {
				if (sbRegex.length() > 0) {
					sbRegex.append("|");
				}
				sbRegex.append("\\" + cond.trim());
			}
		}
		
		if (sbRegex.length() > 0) {
			Pattern pattern = null;
			if (mapPattern.containsKey(sbRegex.toString())) {
				pattern = mapPattern.get(sbRegex.toString());
			}
			else {
				pattern = Pattern.compile(sbRegex.toString());
				mapPattern.put(sbRegex.toString(), pattern);
			}
			morpheme.setAttribute("leftMorphemeCondition", pattern);
		}
	}
	
	/**
	 * Adds a left phoneme condition.
	 * 
	 * @param morpheme
	 * @param condition
	 */
	private void addLeftPhonemeCondition(Morpheme morpheme, String condition) {
		String[] arrCondition = condition.split("\\s");
		
		StringBuilder sbRegex = new StringBuilder();
		for (String cond : arrCondition) {
			if (cond.trim().length() > 0) {
				if (sbRegex.length() > 0) {
					sbRegex.append("|");
				}
				sbRegex.append("\\" + cond.trim());
			}
		}
		
		if (sbRegex.length() > 0) {
			Pattern pattern = null;
			if (mapPattern.containsKey(sbRegex.toString())) {
				pattern = mapPattern.get(sbRegex.toString());
			}
			else {
				pattern = Pattern.compile(sbRegex.toString());
				mapPattern.put(sbRegex.toString(), pattern);
			}
			morpheme.setAttribute("leftPhonemeCondition", pattern);
		}
	}
	
	/**
	 * Adds a left POS condition.
	 * 
	 * @param morpheme
	 * @param condition
	 */
	private void addLeftPosCondition(Morpheme morpheme, String condition) {
		String[] arrCondition = condition.split("\\s");
		List<String> posList = new ArrayList<String>();
		
		StringBuilder sbIncludeRegex = new StringBuilder();
		StringBuilder sbExcludeRegex = new StringBuilder();
		for (String cond : arrCondition) {
			cond = cond.trim();
			if (cond.length() > 0) {
				if (!cond.startsWith("-")) {
					if (sbIncludeRegex.length() > 0) {
						sbIncludeRegex.append("|");
					}
					//cond = cond.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");
					sbIncludeRegex.append(cond + "$");
					
					if (!posList.contains(cond)) {
						posList.add(cond);
					}
				}
				else {
					sbExcludeRegex.append("(?=");
					for (int i = 1; i < cond.length(); i++) {
						sbExcludeRegex.append("[^").append(cond.charAt(i)).append("]");
					}
					sbExcludeRegex.append("$)");
				}
			}
		}
		
		if (sbIncludeRegex.length() > 0 && sbExcludeRegex.length() == 0) {
			Pattern pattern = null;
			String strPattern = sbIncludeRegex.toString();
			if (mapPattern.containsKey(strPattern)) {
				pattern = mapPattern.get(strPattern);
			}
			else {
				pattern = Pattern.compile(strPattern);
				mapPattern.put(strPattern, pattern);
			}
			morpheme.setAttribute("leftPosCondition", pattern);
		}
		else if (sbIncludeRegex.length() > 0 && sbExcludeRegex.length() > 0) {
			Pattern pattern = null;
			String strPattern = "(?=" + sbIncludeRegex.toString() + ")" + sbExcludeRegex.toString();
			if (mapPattern.containsKey(strPattern)) {
				pattern = mapPattern.get(strPattern);
			}
			else {
				pattern = Pattern.compile(strPattern);
				mapPattern.put(strPattern, pattern);
			}
			morpheme.setAttribute("leftPosCondition", pattern);
		}
		else if (sbIncludeRegex.length() == 0 && sbExcludeRegex.length() > 0) {
			Pattern pattern = null;
			String strPattern = sbExcludeRegex.toString();
			if (mapPattern.containsKey(strPattern)) {
				pattern = mapPattern.get(strPattern);
			}
			else {
				pattern = Pattern.compile(strPattern);
				mapPattern.put(strPattern, pattern);
			}
			morpheme.setAttribute("leftPosCondition", pattern);
		}
		
		if (posList.size() > 0) {
			morpheme.setAttribute("leftPosList", posList);
		}
	}
	
	/**
	 * Adds a left lemmatization rule.
	 * 
	 * @param morpheme
	 * @param rule
	 */
	private void addLeftLemmatizationRule(Morpheme morpheme, String rule) {
		if (rule.trim().length() > 0) {
			morpheme.setAttribute("leftLemmatizationRule", rule);
		}
	}
	
	/**
	 * Adds a morpheme property.
	 * 
	 * @param morpheme
	 * @param property
	 */
	private void addMorphemeProperty(Morpheme morpheme, String property) {
		String[] arrProp = property.split("\\s");
		
		String surface = morpheme.getSurface();
		String type = (String) morpheme.getAttribute("type");
		
		StringBuilder sbProp = new StringBuilder();
		for (String prop : arrProp) {
			if (prop.trim().length() > 0) {
				if (sbProp.length() > 0) {
					sbProp.append(" ");
				}
				sbProp.append(prop);
			}
		}
		
		if (type.equals("eomi-ep") || type.equals("head") || type.equals("bojo-verb") || type.equals("bojo-verb-space")) {
			char lastCh = surface.charAt(surface.length() - 1);
			char jongseong = KoreanMorphemeUtil.getJongseongConsonant(lastCh);
			if (jongseong != '\0' && !sbProp.toString().contains("+" + jongseong)) {
				if (sbProp.length() > 0) {
					sbProp.append(" ");
				}
				sbProp.append("+" + jongseong);
			}
			
			String[] arrTag = morpheme.getTag().split("\\+");
			if (arrTag.length == 1) {
				String lemma = arrTag[0].split("/")[0];
				if (surface.equals(lemma) && !sbProp.toString().contains("+어간")) {
					if (sbProp.length() > 0) {
						sbProp.append(" ");
					}
					sbProp.append("+어간");
				}
			}
		}
		
		if (sbProp.length() > 0) {
			morpheme.setAttribute("morphemeProperty", sbProp.toString());
		}
	}
	
	/**
	 * Adds a phoneme property.
	 * 
	 * @param morpheme
	 * @param property
	 */
	private void addPhonemeProperty(Morpheme morpheme, String property) {
		String[] arrProp = property.split("\\s");
		
		String surface = morpheme.getSurface();
		String type = (String) morpheme.getAttribute("type");
		
		StringBuilder sbProp = new StringBuilder();
		for (String prop : arrProp) {
			if (prop.trim().length() > 0) {
				if (sbProp.length() > 0) {
					sbProp.append(" ");
				}
				sbProp.append(prop);
			}
		}
		
		if (type.equals("eomi-ep") || type.equals("head") || type.equals("word") || 
				type.equals("bojo") || type.equals("bojo-verb") || type.equals("bojo-verb-space")) {
			char lastCh = surface.charAt(surface.length() - 1);
			boolean containsJonseongConsonant = KoreanMorphemeUtil.containsJongseongConsonant(lastCh);
			boolean containsPositiveVowel = KoreanMorphemeUtil.containsPositiveVowel(lastCh);
			
			if (containsJonseongConsonant && !sbProp.toString().contains("+자음")) {
				if (sbProp.length() > 0) {
					sbProp.append(" ");
				}
				sbProp.append("+자음");
			}
			else if (!containsJonseongConsonant && !sbProp.toString().contains("+모음")){
				if (sbProp.length() > 0) {
					sbProp.append(" ");
				}
				sbProp.append("+모음");
			}
			
			if (containsPositiveVowel && !sbProp.toString().contains("+양성")) {
				if (sbProp.length() > 0) {
					sbProp.append(" ");
				}
				sbProp.append("+양성");
			}
			else if (!containsPositiveVowel && !sbProp.toString().contains("+음성")){
				if (sbProp.length() > 0) {
					sbProp.append(" ");
				}
				sbProp.append("+음성");
			}
		}
		
		if (sbProp.length() > 0) {
			morpheme.setAttribute("phonemeProperty", sbProp.toString());
		}
	}

}
