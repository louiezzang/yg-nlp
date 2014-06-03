package com.yglab.nlp.postag.lang.ko;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.postag.morph.Morpheme;

public class Eojeol {

	private boolean analyzed = false;
	private List<Morpheme> morphemes = new ArrayList<Morpheme>();
	private String surface;
	private String tag;
	private String tagDescription;
	
	public Eojeol(String surface, String tag, int size) {
		this.surface = surface;
		this.tag = tag;
		for (int i = 0; i < size; i++) {
			morphemes.add(new Morpheme());
		}
	}

	public boolean isAnalyzed() {
		return analyzed;
	}

	public void setAnalyzed(boolean analyzed) {
		this.analyzed = analyzed;
		
		// if failed in analyzing the full morphemes, clears the morphemes.
		if (analyzed == false) {
			morphemes.clear();
		}
	}

	public List<Morpheme> getMorphemes() {
		return morphemes;
	}

	public void setMorphemes(List<Morpheme> morphemes) {
		this.morphemes = morphemes;
	}

	public String getSurface() {
		return surface;
	}

	public void setSurface(String surface) {
		this.surface = surface;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public String getTagDescription() {
		return tagDescription;
	}

	public void seTagDescription(String tagDescription) {
		this.tagDescription = tagDescription;
	}

	public void add(Morpheme morpheme) {
		morphemes.add(morpheme);
	}
	
	public void set(int index, Morpheme morpheme) {
		morphemes.set(index, morpheme);
	}

	public Morpheme get(int index) {
		return morphemes.get(index);
	}
	
	public boolean containsPos(String pos) {
		String[] tags = tag.split(",");
		for (String tag : tags) {
			if (tag.startsWith(pos)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(surface);
		
		if (morphemes.size() == 0) {
			sb.append("[").append(tag).append(":").append(tagDescription).append("]");
		}
		else {
			sb.append("[").append(tag).append("]");
		}
		
		for (int i = 0; i < morphemes.size(); i++) {
			if (i == 0) {
				sb.append(" -> ");
			}
			Morpheme morpheme = morphemes.get(i);
			if (morpheme.getPos().equals(morpheme.getTag())) {
				sb.append(morpheme.getSurface()).append("[").append(morpheme.getPos()).append(":").append("]");
			}
			else {
				sb.append(morpheme.getSurface()).append("[")
					.append(morpheme.getTag()).append(":").append("]");
			}
			
			if (i < morphemes.size() - 1) {
				sb.append("+");
			}
		}
		return sb.toString();
	}
	
}
