package com.yglab.nlp.postag.lang.ko;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Index;
import com.yglab.nlp.postag.POSFeatureGenerator;
import com.yglab.nlp.postag.POSSampleParser;
import com.yglab.nlp.postag.POSTagger;
import com.yglab.nlp.util.lang.ko.MorphemeUtil;

/**
 * Korean POS and morpheme tagger based on maximum-entropy markov model or perceptron algorithm.
 * 
 * @author Younggue Bae
 */
public class KoreanPOSTagger extends POSTagger {

	private MorphemeDictionary dic;
	private KoreanStemmer stemmer;
	private Properties posDescriptions;
	
	/**
	 * Initializes the Korean pos tagger with the specified model.
	 * 
	 * @param model The trained model
	 * @param featureGenerator The context feature generator
	 * @param dic The dictionary of josa and eomi
	 * @throws IOException
	 */
	public KoreanPOSTagger(AbstractModel model, POSFeatureGenerator featureGenerator, 
			MorphemeDictionary dic) throws IOException {
		super(model, featureGenerator, new KoreanTagSequenceGenerator(featureGenerator));
		this.dic = dic;
		this.stemmer = new KoreanStemmer();
		
		Index labelIndex = model.getLabelIndex();
		String[] labels = new String[labelIndex.size()];
		for (int i = 0; i < labelIndex.size(); i++) {
			labels[i] = labelIndex.get(i).toString();
		}
		((KoreanTagSequenceGenerator) super.gen).setTags(labels);
		
		InputStream is = getClass().getResourceAsStream("/lang/ko/postag_ko.properties");
		this.posDescriptions = new Properties();
	  posDescriptions.load(is);
	}
	
	/**
	 * Analyzes the morphemes of the given tokens.
	 * 
	 * @param tokens
	 * @return
	 */
	public List<Eojeol> analyze(String[] tokens) {
		
		List<Eojeol> eojeols = new ArrayList<Eojeol>();
		
		String[] tags = this.tag(tokens);
		for (int position = 0; position < tokens.length; position++) {
			String token = tokens[position];
			String tag = tags[position];
			
			String[] morphTags = tag.split(",");
			Eojeol eojeol = new Eojeol(token, tag, morphTags.length);
			eojeol.seTagDescription(getPosDescription(tag));
			
			boolean analyzed = false;
			String left = token;
			
			/* 어절의 오른쪽 부터 매칭되는 조사, 어미가 있는지 형태소 분석을 함. */
			for (int mi = morphTags.length - 1; mi >= 0; mi--) {
				Morpheme morpheme = new Morpheme();
				
				String morphTag = morphTags[mi];
				String morphPos = POSSampleParser.parsePos(morphTag);
				
				morpheme.setTag(morphTag);
				morpheme.setPos(morphPos);
				morpheme.setPosDescription(getPosDescription(morphPos));
				
				String stem = String.valueOf(stemmer.stem(left, morphTag));

				/* 
				 * 중간에 선어말어미(EP)가 들어간 경우, 이전에 분석에 성공(analyzed == true)한 형태소가 있더라도
				 * surface, stem, analyzed 변수를 초기화 함.
				 */
				if (morphPos.startsWith("EP")) {
					morpheme.setSurface("");
					morpheme.setStem("");
					analyzed = false;
				}
				else {
					morpheme.setSurface(left);
					morpheme.setStem(stem);
				}

				//String matchJosa = dicJosa.findSuffix(left);
				//String matchEomi = dicEomi.findSuffix(left);
				String matchJosa = "";
				String matchEomi = "";
				
				/* 남아 있는 단어의 오른쪽 부분이 조사와 매칭되는 부분이 있는 경우 */
				if (matchJosa != null) {
					String rightPos = matchJosa.split("_")[0];
					String right = matchJosa.split("_")[1];
					
					/* 매칭되는 조사의 예측된 POS TAG와 사전의 POS TAG와 일치하는 경우 */
					if (morphPos.equals(rightPos)) {
						analyzed = true;
						left = MorphemeUtil.truncateRight(left, right);
						
						String stemRight = String.valueOf(stemmer.stem(right, morphTag));
						morpheme.setSurface(right);
						morpheme.setStem(stemRight);
					}
					/* 매칭되는 조사의 예측된 POS TAG의 첫글자와 사전의 POS TAG 첫글자와 일치하는 경우 */
					else if (morphPos.charAt(0) == rightPos.charAt(0)) {
						analyzed = true;
						left = MorphemeUtil.truncateRight(left, right);
						
						String stemRight = String.valueOf(stemmer.stem(right, morphTag));
						morpheme.setSurface(right);
						morpheme.setStem(stemRight);
					}
				}
				
				/* 남아 있는 단어의 오른쪽 부분이 어미와 매칭되는 부분이 있는 경우 */
				if (matchEomi != null) {
					String rightPos = matchEomi.split("_")[0];
					String right = matchEomi.split("_")[1];
					
					/* 매칭되는 어미의 예측된 POS TAG와 사전의 POS TAG와 일치하는 경우 */
					if (morphPos.equals(rightPos)) {
						analyzed = true;
						left = MorphemeUtil.truncateRight(left, right);
						
						String stemRight = String.valueOf(stemmer.stem(right, morphTag));
						morpheme.setSurface(right);
						morpheme.setStem(stemRight);
					}
					/* 선어말어미(EP)가 아니면서 매칭되는 어미의 예측된 POS TAG의 첫글자와 사전의 POS TAG의 첫글자와 일치하는 경우 */
					else if (!morphPos.startsWith("EP") && morphPos.charAt(0) == rightPos.charAt(0)) {
						analyzed = true;
						left = MorphemeUtil.truncateRight(left, right);
						
						String stemRight = String.valueOf(stemmer.stem(right, morphTag));
						morpheme.setSurface(right);
						morpheme.setStem(stemRight);
					}
				}
				
				eojeol.set(mi, morpheme);
			}
			
			eojeol.setAnalyzed(analyzed);
			eojeols.add(eojeol);
		}
		
		return eojeols;
	}
	
	/**
	 * Gets the pos description.
	 * 
	 * @param pos
	 * @return
	 */
	private String getPosDescription(String pos) {
		String description = this.posDescriptions.getProperty(pos);
		if (description == null) {
			return "기타";
		}
		else {
			try {
				return new String(description.getBytes("ISO-8859-1"), "UTF-8").trim();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return description;
			}
		}
	}

}