package com.yglab.nlp.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.yglab.nlp.util.Span;

/**
 * Sample instance for tokenizer.
 * 
 * @author Younggue Bae
 */
public class TokenSample {

	private String text;
	private List<Span> tokenSpans;

  /**
   * Initializes the current instance.
   *
   * @param text the text which contains the tokens.
   * @param tokenSpans the spans which mark the begin and end of the tokens.
   */
  public TokenSample(String text, Span tokenSpans[]) {
    
    if (text == null)
      throw new IllegalArgumentException("text must not be null!");
    
    if (tokenSpans == null)
      throw new IllegalArgumentException("tokenSpans must not be null! ");
    
    this.text = text;
    this.tokenSpans = Collections.unmodifiableList(new ArrayList<Span>(Arrays.asList(tokenSpans)));

    for (Span tokenSpan : tokenSpans) {
      if (tokenSpan.getStart() < 0 || tokenSpan.getStart() > text.length() ||
          tokenSpan.getEnd() > text.length() || tokenSpan.getEnd() < 0) {
        throw new IllegalArgumentException("Span " + tokenSpan.toString() +
            " is out of bounds, text length: " + text.length() + "!");
      }
    }
  }
  
  /**
   * Retrieves the text.
   */
  public String getText() {
    return text;
  }

  /**
   * Retrieves the token spans.
   */
  public Span[] getTokenSpans() {
    return tokenSpans.toArray(new Span[tokenSpans.size()]);
  }

}
