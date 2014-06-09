package com.yglab.nlp.ner.lang.ko;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yglab.nlp.ner.TokenPostagPairGenerator;
import com.yglab.nlp.postag.TagPattern;
import com.yglab.nlp.postag.lang.ko.KoreanPOSTagger;
import com.yglab.nlp.postag.morph.Morpheme;
import com.yglab.nlp.postag.morph.Token;


/**
 * This class generations the pairs of token with its postag.
 * 
 * @author Younggue Bae
 */
public class KoreanTokenPostagPairGenerator implements TokenPostagPairGenerator {
	
	/** the pattern for finding tail tag, for example, josa or eomi */
	private static final Pattern TAIL_TAG_PATTERN = Pattern.compile(
			"([^/\\+\\(\\)]*)/([XEJ][A-Z]+|VX|VCP)");
	
	private KoreanPOSTagger posTagger;
	private String delimiter;
	private List<Token> currentTokens = new ArrayList<Token>();
	
	public KoreanTokenPostagPairGenerator(KoreanPOSTagger posTagger) {
		this(posTagger, "\t");
	}
	
	public KoreanTokenPostagPairGenerator(KoreanPOSTagger posTagger, String delimiter) {
		this.posTagger = posTagger;
		this.delimiter = delimiter;
	}

	@Override
	public String[] generate(String[] tokens) {
		/* initialize current tokens */
		this.currentTokens.clear();
		this.currentTokens = posTagger.analyze(tokens);
		
		List<String> tokenList = new ArrayList<String>();
		
		for (int ti = 0; ti < currentTokens.size(); ti++) {
			Token analToken = currentTokens.get(ti);
			String strToken = analToken.getToken();
			
			StringBuilder sbTag = new StringBuilder();
			for (int mi = analToken.size() - 1; mi >= 0; mi--) {
				Morpheme morph = analToken.get(mi);
				String[] tags = morph.getTag().split("\\+");
				for (String tag : tags) {
					if (sbTag.length() > 0) {
						sbTag.append("+");
					}
					Matcher m = TAIL_TAG_PATTERN.matcher(tag);
					if (m.find()) {
						sbTag.append(m.group());
					}
					else {
						sbTag.append(TagPattern.parsePos(tag));
					}
				}
			}
			strToken = strToken + delimiter + sbTag.toString();
			tokenList.add(strToken);
		}
		
		return tokenList.toArray(new String[tokenList.size()]);
	}
	
	/**
	 * Gets the current analyzed tokens.
	 * 
	 * @return List<Token>	The analyzed tokens
	 */
	public List<Token> getCurrentAnalyzedTokens() {
		return this.currentTokens;
	}
}
