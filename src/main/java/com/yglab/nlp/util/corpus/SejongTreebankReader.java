package com.yglab.nlp.util.corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yglab.nlp.parser.Parse;
import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.util.Span;


/**
 * A reader for the Sejong treebank file format.
 * 
 * @author Younggue Bae
 */
public class SejongTreebankReader implements TreebankReader {
	
  public static final String BRACKET_LRB = "(";
  public static final String BRACKET_RRB = ")";
  public static final String BRACKET_LCB = "{";
  public static final String BRACKET_RCB = "}";

  /**
   * The pattern used to find the base constituent label of a
   * Penn Treebank labeled constituent.
   */
	protected static Pattern typePattern = Pattern.compile("^([^ =-]+)");

  /**
   * The pattern used to identify tokens in Penn Treebank labeled constituents.
   */
	protected static Pattern tokenPattern = Pattern.compile("^[^ ()]+ ([^()]+)\\s*\\)");
	
	protected static List<String> punctuations = Arrays.asList(new String[] { ".", "!", "?", "," });
  
	protected BufferedReader inputReader;
	
	private String strSentence;

	/**
	 * Constructor.
	 */
	public SejongTreebankReader() {
	}

	@Override
	public boolean startReading(String file) throws IOException {

		InputStream is = getClass().getResourceAsStream(file);

		if (is != null) {
			inputReader = new BufferedReader(new InputStreamReader(is, "utf-8"));
		} else {
			inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
		}
		
		return true;
	}
	
	@Override
	public ParseSample getNext() throws IOException {
		int offset = 0;
		Stack<Constituent> stack = new Stack<Constituent>();
		List<Constituent> constituents = new LinkedList<Constituent>();

		String line = inputReader.readLine();
		if (line == null) {
			inputReader.close();
			return null;
		}
		
		String currentSentence = null;
		while (line.trim().equals("") || line.startsWith(";")) {
			if (line.startsWith(";")) {
				currentSentence = line.substring(1).trim();
			}
			line = inputReader.readLine();
		}
		
		if (currentSentence == null) {
			currentSentence = strSentence;
		}
		
		StringBuilder text = new StringBuilder();
		while (line != null && !line.trim().equals("") && !line.startsWith(";")) {
			line = encodeString(line);
			for (int ci = 0; ci < line.length(); ci++) {
				char c = line.charAt(ci);
				if (c == '(') {
					String rest = line.substring(ci + 1);
					
					String type = getType(rest);
	        if (type == null) {
	          System.err.println("null type for: " + rest);
	        }
	        String token = getToken(rest);
	        
	        stack.push(new Constituent(type, new Span(offset, offset)));
	        
	        if (token != null) {
	        	constituents.add(new Constituent("TOKEN", new Span(offset, offset + token.length())));
            text.append(token).append(" ");
            offset += token.length() + 1;
	        }
				}
	      else if (c == ')') {
	        Constituent constituent = stack.pop();
	        int start = constituent.getSpan().getStart();
	        if (start < offset) {
	        	constituents.add(new Constituent(constituent.getLabel(), new Span(start, offset - 1)));
	        }
	      }
			}
			line = inputReader.readLine();
			if (line != null && line.startsWith(";")) {
				strSentence = line.substring(1).trim();
			}
		}
		
		String txt = text.toString();
		System.out.println(txt);
		
		List<String> tokens = new LinkedList<String>();
    int tokenIndex = 0;
    Parse p = new Parse();
    p.setIndex(0);
    p.setLabel("ROOT");
    p.setAttribute("text", txt);
    p.setAttribute("span", new Span(0, txt.length()));
    p.setAttribute("parts", new LinkedList<Parse>());
    p.setAttribute("parent", null);
    tokens.add("ROOT");
    
    for (int ci = 0; ci < constituents.size(); ci++) {
      Constituent constituent = constituents.get(ci);
      String type = constituent.getLabel();
      
      String token = txt.substring(constituent.getSpan().getStart(), constituent.getSpan().getEnd());
      //System.out.println(tokenIndex + ": " + type + "\t" + token);

			if (type.equals("TOKEN")) {
				tokens.add(token);
				tokenIndex++;
			}
      
      Parse c = new Parse();
      c.setIndex(tokenIndex);
      c.setLabel(type);
      c.setAttribute("text", txt);
      c.setAttribute("span", constituent.getSpan());
      c.setAttribute("parts", new LinkedList<Parse>());
      c.setAttribute("parent", null);
      
      insert(p, c);
    }
		
    ParseSample instance = new ParseSample();
    instance.setForms(tokens.toArray(new String[tokens.size()]));
    instance.setLemmas(new String[tokens.size()]);
    instance.setCpostags(new String[tokens.size()]);
    instance.setPostags(new String[tokens.size()]);
    instance.setDependencyRelations(new String[tokens.size()]);
    instance.setHeads(new int[tokens.size()]);
    
    makeParseSample(txt, instance, p);
    
    retokenizeParseSample(instance);
    
		return instance;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private static void makeParseSample(String text, ParseSample instance, Parse parse) {
		Span span = (Span) parse.getAttribute("span");
		String token = text.substring(span.getStart(), span.getEnd());
		List<Parse> parts = (LinkedList<Parse>) parse.getAttribute("parts");
		for (Parse part : parts) {
			Span partSpan = (Span) part.getAttribute("span");
			String partToken = text.substring(partSpan.getStart(), partSpan.getEnd());
			if (parse.getIndex() != part.getIndex()) {
				//System.out.println("parent = " + parse.getIndex() + ", " + token + 
				//	" ---> part = " + part.getIndex() + ", type = " + part.getLabel() + ", " + partToken);
				
				decodeToken(instance, part.getIndex());
				instance.heads[part.getIndex()] = parse.getIndex();
				instance.deprels[part.getIndex()] = part.getLabel();
				
				System.out.println(parse.getIndex() + ": " + instance.forms[parse.getIndex()] + " ---> " + 
						part.getIndex() + ": " + instance.forms[part.getIndex()] + ": " + instance.deprels[part.getIndex()]);
				
			}
			makeParseSample(text, instance, part);
		}		
	}
	
	@SuppressWarnings("unchecked")
	private static void insert(Parse parse, Parse constituent) {
		Span span = (Span) parse.getAttribute("span");
		Span cs = (Span) constituent.getAttribute("span");
		
		List<Parse> parts = (LinkedList<Parse>) parse.getAttribute("parts");
		List<Parse> cparts = (LinkedList<Parse>) constituent.getAttribute("parts");

		if (span.contains(cs)) {
			int pi = 0;
			int pn = parts.size();
			for (; pi < pn; pi++) {
				Parse subPart = parts.get(pi);

				Span sp = (Span) subPart.getAttribute("span");
				if (sp.getStart() >= cs.getEnd()) {
					break;
				}
				// constituent contains subPart
				else if (cs.contains(sp)) {
					parts.remove(pi);
					pi--;
					cparts.add(subPart);
					subPart.setAttribute("parent", constituent);

					pn = parts.size();
				} else if (sp.contains(cs)) {
					insert(subPart, constituent);
					return;
				}
			}
			parts.add(pi, constituent);
			constituent.setAttribute("parent", parse);
		} else {
			throw new IllegalArgumentException("Inserting constituent not contained in the sentence!");
		}
	}
	
  private static String getType(String rest) {
    Matcher typeMatcher = typePattern.matcher(rest);
    if (typeMatcher.find()) {
      String type = typeMatcher.group(1);
      return type;
    }
    return null;
  }
	
  private static String getToken(String rest) {
    Matcher tokenMatcher = tokenPattern.matcher(rest);
    
    String token = null;
    if (tokenMatcher.find()) {
      token = tokenMatcher.group(1);
 
      // correct the wrong hand-tagged token
      if (token.matches("^[\\s]*[\\+][\\s]+.*")) {
      	token = token.replaceAll("^[\\s]*[\\+][\\s]+", "");	
      }
      
      if (!token.trim().equals("")) {
      	return token;
      }
    }
    
    return null;
  }
  
  private static String encodeString(String token) {
  	token = token.replaceAll("\\(/", "-LRB-/");
  	token = token.replaceAll("\\)/", "-RRB-/");
  	token = token.replaceAll("\\{/", "-LCB-/");
  	token = token.replaceAll("\\}/", "-RCB-/");
    
    return token;
  }
  
  private static String decodeToken(String token) {
    if ("-LRB-".equals(token)) {
      return BRACKET_LRB;
    }
    else if ("-RRB-".equals(token)) {
      return BRACKET_RRB;
    }
    else if ("-LCB-".equals(token)) {
      return BRACKET_LCB;
    }
    else if ("-RCB-".equals(token)) {
      return BRACKET_RCB;
    }
    
    return token;
  }
  
  /**
   * You should override this method for your language.
   * 
   * @param instance
   * @param index
   * @return
   */
  protected static ParseSample decodeToken(ParseSample instance, int index) {
		String token = instance.forms[index];
		
		StringBuilder form = new StringBuilder();
		StringBuilder cpostag = new StringBuilder();
		StringBuilder postag = new StringBuilder();
		
		String[] morphs = token.split(" \\+ ");
		for (String morphPair : morphs) {
			Matcher morphMatcher = Pattern.compile("(.+)/(.+)").matcher(morphPair);
			while (morphMatcher.find()) {
				//String morph = morphMatcher.group(1);
				String morph = decodeToken(morphMatcher.group(1));
				morph = morph.replaceAll("\t", "");
				String tag = morphMatcher.group(2);
				tag = tag.replaceAll("\t", "");
			
				form.append(morph);
				if (!cpostag.toString().equals("")) {
					cpostag.append("+");
				}
				cpostag.append(tag);
				
				if (!postag.toString().equals("")) {
					postag.append("+");
				}
				
				if (tag.startsWith("J") || tag.startsWith("E") || 
						tag.equals("XSV") || tag.startsWith("XSA") ||
						tag.startsWith("VCP") || tag.startsWith("VX")) {
					postag.append(morph).append("/").append(tag);
				}
				else {
					postag.append(tag);
				}
			}
		}
		
		String strPostag = simplifyPostag(postag.toString());
		String strCpostag = simplifyPostag(cpostag.toString());
		
		instance.forms[index] = form.toString();
		instance.lemmas[index] = form.toString();
		instance.cpostags[index] = strCpostag;
		instance.postags[index] = strPostag;
		
    return instance;
  }
  
  /**
   * Retokenize the tokens for the untokenized punctuation marks.
   * 
   * @param instance
   * @return
   */
  protected static ParseSample retokenizeParseSample(ParseSample instance) {
  	//queue
  	List<String> forms = new ArrayList<String>();
  	List<String> lemmas = new ArrayList<String>();
  	List<String> cpostags = new ArrayList<String>();
  	List<String> postags = new ArrayList<String>();
  	List<Integer> heads = new ArrayList<Integer>();
  	List<String> deprels = new ArrayList<String>();
  	List<String[]> feats = new ArrayList<String[]>();
  	//List<Double> scores = new ArrayList<Double>();
  	
  	for (int i = 0; i < instance.length(); i++) {
  		String form = instance.forms[i];
  		String lemma = instance.lemmas[i];
  		String cpostag = instance.cpostags[i];
  		String postag = instance.postags[i];
  		int head = instance.heads[i];
  		String deprel = instance.deprels[i];
  		String[] feat = (instance.feats == null || instance.feats[i] == null) ? null : instance.feats[i];
  		//double score = (instance.confidenceScores == null) ? 0 : instance.confidenceScores[i];
  		
  		forms.add(i, form);
  		lemmas.add(i, lemma);
  		cpostags.add(i, cpostag);
  		postags.add(i, postag);
  		heads.add(i, head);
  		deprels.add(i, deprel);
  		feats.add(i, feat);
  		//scores.add(i, score);
  	}
  	
  	for (int i = 0; i < forms.size(); i++) {
  		String form = forms.get(i);
  		String lemma = lemmas.get(i);
  		String cpostag = cpostags.get(i);
  		String postag = postags.get(i);
  		
  		String lastChar = form.substring(form.length() - 1);
  		if (punctuations.contains(lastChar) && (cpostag.endsWith("+SF") || cpostag.endsWith("+SP"))) {
  			shiftHead(heads, i+1);
  			
  			forms.set(i, form.substring(0, form.length() - 1));
  			lemmas.set(i, lemma.substring(0, lemma.length() - 1));
  			cpostags.set(i, cpostag.substring(0, cpostag.length() - 3));
  			postags.set(i, postag.substring(0, postag.length() - 3));
  			
    		forms.add(i+1, lastChar);
    		lemmas.add(i+1, lastChar);
    		cpostags.add(i+1, lastChar);
    		postags.add(i+1, lastChar);
    		heads.add(i+1, i);
    		deprels.add(i+1, "X");
    		feats.add(i+1, null);
    		//scores.add(i+1, null);
  			
  			i++;
  		}	
  	}
  	
  	int[] arrHeads = new int[forms.size()];
  	//double[] arrScores = new double[forms.size()];
  	for (int i = 0; i < forms.size(); i++) {
  		arrHeads[i] = heads.get(i);
  		//arrScores[i] = scores.get(i);
  	}
  	
  	instance.setForms(forms.toArray(new String[forms.size()]));
  	instance.setLemmas(lemmas.toArray(new String[forms.size()]));
  	instance.setCpostags(cpostags.toArray(new String[forms.size()]));
  	instance.setPostags(postags.toArray(new String[forms.size()]));
  	instance.setHeads(arrHeads);
  	instance.setDependencyRelations(deprels.toArray(new String[forms.size()]));
  	instance.setFeatures(feats.toArray(new String[forms.size()][]));
  	//instance.setConfidenceScores(arrScores);
  	
  	return instance;
  }
  
  private static void shiftHead(List<Integer> heads, int split) {
  	for (int i = 0; i < heads.size(); i++) {
  		int head = heads.get(i);
  		if (head >= split) {
  			heads.set(i, head+1);
  		}
  	}
  }

  private static String simplifyPostag(String postag) {
  	postag = postag.replaceAll("(NNG|NNP|XPN|SN|SL).*(NNG|XSN|NNB)", "NNG");
  	postag = postag.replaceAll("(NNP).*NP", "NP");
  	postag = postag.replaceAll("(SN).*NNB", "NNB");
  	postag = postag.replaceAll("(SN).*NR", "NR");
  	
  	return postag;
  }
  

}
