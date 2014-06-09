package com.yglab.nlp.parser.dep.lang.ko;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.parser.Parse;
import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.parser.dep.DependencyFeatureGenerator;
import com.yglab.nlp.parser.dep.DependencyParser;
import com.yglab.nlp.postag.POSTagger;
import com.yglab.nlp.postag.TagPattern;
import com.yglab.nlp.postag.lang.ko.KoreanPOSTagger;
import com.yglab.nlp.postag.morph.Morpheme;
import com.yglab.nlp.postag.morph.Token;
import com.yglab.nlp.tokenizer.Tokenizer;

/**
 * The dependency parser for Korean.
 * 
 * @author Younggue Bae
 */
public class KoreanDependencyParser extends DependencyParser {
	
	/** the pattern for finding tail tag, for example, josa or eomi */
	private static final Pattern TAIL_TAG_PATTERN = Pattern.compile(
			"([^/\\+\\(\\)]*)/([XEJ][A-Z]+|VX|VCP)");
	
	private Tokenizer tokenizer;
	
	/**
	 * Initializes the dependency parser with the specified model.
	 * 
	 * @param model
	 * @param featureGenerator
	 * @param posTagger
	 */
	public KoreanDependencyParser(AbstractModel model,
			DependencyFeatureGenerator<ParseSample> featureGenerator, Tokenizer tokenizer, POSTagger posTagger) {
		super(model, featureGenerator, posTagger);
		this.tokenizer = tokenizer;
	}
	
	@Override
	public List<Parse> parse(String[] tokens) {
		String[] atokens = new String[tokens.length + 1];
		String[] cpostags = new String[tokens.length + 1];
		String[] postags = new String[tokens.length + 1];
		String[] lemmas = new String[tokens.length + 1];
		
		// add dummy ROOT
		atokens[0] = "<root>";
		cpostags[0] = "<root-CPOS>";
		postags[0] = "<root-POS>";
		lemmas[0] = "<root-LEMMA>";
		
		KoreanPOSTagger posTaggerKo = (KoreanPOSTagger) posTagger;
		List<Token> analTokens = posTaggerKo.analyze(tokens);
		
		for (int i = 0; i < analTokens.size(); i++) {
			Token analToken = analTokens.get(i);
			int index = i + 1;
			atokens[index] = analToken.getToken();
			cpostags[index] = analToken.getPos();
			postags[index] = analToken.getTag();
			lemmas[index] = analToken.getToken();

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

			postags[index] = sbTag.toString();
		}
		
		ParseSample instance = new ParseSample(atokens, lemmas, cpostags, postags, null);
		List<Parse> parses = super.parse(instance, 1).get(0);
		for (int i = 0; i < parses.size(); i++) {
			Parse parse = parses.get(i);
			int index = parse.getIndex() - 1;	// because index 0 is "root" node
			Token analToken = analTokens.get(index);
			parse.setAttribute("token", analToken);
		}
		// sort by index
		Collections.sort(parses);
		
		return parses;
	}
	
	public List<Parse> parse(String s) {
		String[] tokens = tokenizer.tokenize(s);
		
		return this.parse(tokens);
	}

}
