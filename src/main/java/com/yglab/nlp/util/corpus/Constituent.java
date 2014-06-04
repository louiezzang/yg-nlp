package com.yglab.nlp.util.corpus;

import com.yglab.nlp.model.Span;

/**
 * Class used to hold constituents when reading parses.
 * 
 * @author Younggue Bae
 */
public class Constituent {

  private String label;
  private Span span;

  public Constituent(String label, Span span) {
    this.label = label;
    this.span = span;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Span getSpan() {
    return span;
  }
}
