package com.yglab.nlp.parser.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yglab.nlp.parser.Parse;
import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.util.Span;


/**
 * A reader for the Penn Treebank file format.
 * 
 * @author Younggue Bae
 */
public class TreebankReader {
	
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
  
	protected BufferedReader inputReader;
	
	/**
	 * Class used to hold constituents when reading parses.
	 */
	class Constituent {

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

	/**
	 * Constructor.
	 */
	public TreebankReader() {
	}

	public boolean startReading(String file) throws IOException {

		InputStream is = getClass().getResourceAsStream(file);

		if (is != null) {
			inputReader = new BufferedReader(new InputStreamReader(is, "utf-8"));
		} else {
			inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
		}
		
		return true;
	}
	
	@SuppressWarnings({ "unused" })
	public ParseSample getNext() throws IOException {
		String source = null;
		StringBuilder text = new StringBuilder();
		int offset = 0;
		Stack<Constituent> stack = new Stack<Constituent>();
		List<Constituent> constituents = new LinkedList<Constituent>();
	   
		String line = inputReader.readLine();
		if (line == null) {
			inputReader.close();
			return null;
		}
		
		if (line != null && line.startsWith(";")) {
			source = line;
		}
		
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
        tokenIndex++;
        tokens.add(token);
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
    
    updateParseSample(txt, instance, p);
		
		return instance;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private void updateParseSample(String text, ParseSample instance, Parse parse) {
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
			updateParseSample(text, instance, part);
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
      //token = token.replaceAll(" ", "");
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
  
  private static ParseSample decodeToken(ParseSample instance, int index) {
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
				String tag = morphMatcher.group(2);
			
				form.append(morph);
				if (!cpostag.toString().equals("")) {
					cpostag.append(",");
				}
				cpostag.append(tag);
				
				if (!postag.toString().equals("")) {
					postag.append(",");
				}
				if (tag.startsWith("J") || tag.startsWith("E")) {
					postag.append(morph).append("_").append(tag);
				}
				else {
					postag.append(tag);
				}
				
				//System.out.println(morph + ": " + postag);
			}
		}
		
		instance.forms[index] = form.toString();
		instance.lemmas[index] = form.toString();
		instance.cpostags[index] = cpostag.toString();
		instance.postags[index] = postag.toString();
		
    return instance;
  }


}
