package com.yglab.nlp.tokenizer;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.util.StringUtil;

/**
 * This class generates the contextual features for tokenizer.
 * 
 * @author Younggue Bae
 */
public class DefaultTokenFeatureGenerator implements TokenFeatureGenerator {
	
	public DefaultTokenFeatureGenerator() { }
	
	/**
	 * Generates the context features.
	 * 
	 * @param position The position to split in a word.
	 * @param token A token to split. 
	 * 				(eg. abc<SPLIT>,def<SPLIT>. -> [abc,def.]
	 * @return
	 */
	@Override
	public String[] getFeatures(int position, String token) {
		List<String> features = new ArrayList<String>();
		
		String prefix = token.substring(0, position);
    String suffix = token.substring(position);
    features.add("p=" + prefix);
    features.add("s=" + suffix);
			
    if (position > 0) {
    	this.addCharFeature("p1", token.charAt(position - 1), features);
      if (position > 1) {
      	this.addCharFeature("p2", token.charAt(position - 2), features);
      	features.add("p21=" + token.charAt(position - 2) + token.charAt(position - 1));
      }
      else {
      	features.add("p2=bok");
      }
      features.add("p1f1=" + token.charAt(position - 1) + token.charAt(position));
    }
    else {
    	features.add("p1=bok");
    }
    this.addCharFeature("f1", token.charAt(position), features);
    if (position+1 < token.length()) {
    	this.addCharFeature("f2", token.charAt(position + 1), features);
      features.add("f12=" + token.charAt(position) + token.charAt(position + 1));
    }
    else {
    	features.add("f2=bok");
    }
    if (token.charAt(0) == '&' && token.charAt(token.length() - 1) == ';') {
    	features.add("cc");	//character code
    }
		
		return features.toArray(new String[features.size()]);
	}
	
  protected void addCharFeature(String key, char c, List<String> features) {
  	features.add(key + "=" + c);
    if (Character.isLetter(c)) {
    	features.add(key + "_alpha");
      if (Character.isUpperCase(c)) {
      	features.add(key + "_caps");
      }
    }
    else if (Character.isDigit(c)) {
    	features.add(key + "_num");
    }
    else if (StringUtil.isWhitespace(c)) {
    	features.add(key + "_ws");
    }
    else {
      if (c=='.' || c=='?' || c=='!') {
      	features.add(key + "_eos");
      }
      else if (c=='`' || c=='"' || c=='\'') {
      	features.add(key + "_quote");
      }
      else if (c=='[' || c=='{' || c=='(') {
      	features.add(key + "_lp");
      }
      else if (c==']' || c=='}' || c==')') {
      	features.add(key + "_rp");
      }
    }
  }

}
