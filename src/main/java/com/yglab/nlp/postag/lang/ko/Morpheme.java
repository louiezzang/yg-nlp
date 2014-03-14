package com.yglab.nlp.postag.lang.ko;


/**
 * This class defines a morpheme.
 * 
 * @author Younggue Bae
 */
public class Morpheme {
	
	private String surface;
	private String stem;
	private String tag;
	private String pos;
	private String posDescription;

	public String getSurface() {
		return surface;
	}

	public void setSurface(String surface) {
		this.surface = surface;
	}
	
	public String getStem() {
		return stem;
	}

	public void setStem(String stem) {
		this.stem = stem;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getPosDescription() {
		return posDescription;
	}

	public void setPosDescription(String posDescription) {
		this.posDescription = posDescription;
	}

}
