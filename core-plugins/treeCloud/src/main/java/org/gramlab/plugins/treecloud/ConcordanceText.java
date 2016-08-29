package org.gramlab.plugins.treecloud;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * This class is used to generate distance matrix from
 * Concordance HTML file
 * @author Aleksandra
 *
 */
public class ConcordanceText {
	/*
	 * locatetarget is locate pattern of the concordance (should be present in the tree)
	 */
	String locatetarget;   
	ArrayList<String> words;
	ArrayList<ArrayList<Double>> distancematrix;
	String edgecolor;
	
	final String [] fontsizelevel = {"8", "9", "10", "11", "12",
			"13", "14", "15", "16", "17", "18"};
	
	final String [] fontcoloryahoo = {"rgb(223,223,223)", "rgb(184,201,214)", "rgb(102,138,168)",
			"rgb(71,123,123)", "rgb(231,101,0)", "rgb(231,98,0)", "rgb(231,98,0)",
			"rgb(231,98,0)", "rgb(231,98,0)", "rgb(231,98,0)", "rgb(255,51,0)"};	
	
	final String [] fontcolorberry = {"rgb(220,220,220)", "rgb(200,200,200)", "rgb(180,180,180)", "rgb(160,160,160)",
			"rgb(140,140,140)", "rgb(120,120,120)", "rgb(70,70,70)",
			"rgb(50,50,50)", "rgb(30,30,30)", "rgb(20,20,20)", "rgb(0,0,0)"};

    /**
     * reads "concord.html" file, adds each line to an ArrayList,
     * removes HTML tags and punctuation from each line,
     * puts sepchar 'aaaaaaaaa' at the end of each line
     * 
     * @throws IOException
     */
    public ArrayList<String> readConcor(String concorpath) throws IOException{ 
    	ArrayList<String> concorlist = new ArrayList<String>();
    	
		FileReader concorfr = new FileReader(concorpath);
		BufferedReader concorfrreader = new BufferedReader(concorfr);
		while (true){
			String line = concorfrreader.readLine();
			if (line == null) {
				break;
			}else if (line.startsWith("<tr>")){
			    concorlist.add(line);}
			}
		concorfrreader.close();
		ArrayList<String> finalconcorlist = new ArrayList<String>();
		
		String tmp = concorlist.get(0);
		Pattern pat = Pattern.compile("<a.+?>([a-zA-Z]+?)</a>");
		Matcher m = pat.matcher(tmp);
		if(m.find()){
			locatetarget = m.group(1).toLowerCase();
		}

		for (String line : concorlist) {
			String s = line.replaceAll("</td></tr>", "aaaaaaaaa ");
			String p = s.replaceAll("<a.+?>|</a>|&nbsp;|\\{S\\}|<tr><td nowrap>", " ");
			String z = p.replaceAll("[\\.,\\?!\\*;\\:\"]", "");
			z.trim();
			finalconcorlist.add(z);
			//fw.write(z);
		    }
		//fw.close();
		return finalconcorlist;
	
	
	}
    
    /*TODO: add caseSensitivity (lower all the words or not)
     * 
     */
    
    
    /**
     * splits each line of concordance by whitespace
     * 
     * @param stringlist ArrayList of concordance lines
     * @return ArrayList of concordance words
     */
    private ArrayList<String> text(ArrayList<String> stringlist){
		   ArrayList<String> words = new ArrayList<String>();
		   
		   for(String line : stringlist){
			   for(String word : line.split("\\s+")){
				   if(!word.equals("")){ //while splitting empty words may occur
					   //words.add(word);
					   words.add(word.toLowerCase());
			   }}   
		   }
		   return words;
	   }
    /**
     * Compare token list made by local chunking function (text(ArrayList<String>)) with tokens.txt made by Unitex
     * Remove tokens which do not appear in tokens.txt
     * @param tokensfilepath absolute path to "tokens.txt"
     * @param words list of words to check
     * @return list of words
     * @throws IOException
     */
    
    
    private ArrayList<String> compareWithTokens(String tokensfilepath, ArrayList<String> words) throws IOException{
    	ArrayList<String> result = new ArrayList<String>();
    	ArrayList<String> tokens = new ArrayList<String>();
    	FileReader fr = new FileReader(tokensfilepath);
    	BufferedReader bf = new BufferedReader(fr);
    	while(true){
    		String line = bf.readLine();
    		if(line == null){
    			break;
    		}else{
    			tokens.add(line);
    		}
    	}
    	bf.close();
    	
    	for(String w : words){
    		if(tokens.contains(w) | w.equals("aaaaaaaaa")){
    			result.add(w);
    		}
    	}
    	return result;
    }
    /**
     * generates a map {word=number of occurrences in the concordance}
     * @param textwords
     * @return
     */
    
	   private Map<String, Integer> freqsDict(ArrayList<String> textwords){
		   Map<String, Integer> freqs = new HashMap<String, Integer>();
		   for(String word : textwords){
			   Integer value = freqs.get(word);
			   if (!freqs.containsKey(word)){
				   freqs.put(word, 1);
			   }else{
				   freqs.put(word, value + 1);}
			   }
		   return freqs;
	   }
    /**
     * generates a map {n1={word1=1,word2=1},n2={word3=1,word4=1}}
     * where n1, n2 are numbers of occurrences of the words in concordance
     * word1, word2 are words that occurred number1 times in concordance.
     * 
     * @param frequency map {word=number_of_occurrences}
     * @return a map
     */
    public Map<Integer, Map<String,Integer>> sortByFrequency(Map<String,Integer> frequencies){
		 
		   Map<Integer, Map<String,Integer>> sortedfreqs = new HashMap<Integer, Map<String,Integer>>();
		   ArrayList<String> words = new ArrayList<String>(frequencies.keySet());
		   Collections.sort(words);
		   for (String word : words){
			   Integer val = frequencies.get(word);
			   if (sortedfreqs.containsKey(val)){
				   sortedfreqs.get(val).put(word, 1);
			   }else{
				   Map<String, Integer> pair = new HashMap<String, Integer>();
				   pair.put(word , 1);
				   sortedfreqs.put(val, pair);
			   }}
		   return sortedfreqs;   
	   }
   
    /**
     * remove stopwords from list of words
     * @param freqsdict words with associated frequencies
     * @param stopwords list of stopwords
     * @return list of words with no stopwords
     */
    private ArrayList<String> removeStopWords(Map<String, Integer> freqsdict , ArrayList<String> stopwords){
    	ArrayList<String> cleaned = new ArrayList<String>();
    	
    	for(String w : freqsdict.keySet()){
    		if(!stopwords.contains(w) | w.equals(locatetarget)){
    			cleaned.add(w);
    		}
    	}
    	return cleaned;
    }
    /**
     * Remove words from list which frequencies are less than minnb
     * @param freqs words with associated number of occurrences
     * @param minnb minimum number of occurrences a word should have in order to be present in the tree 
     * @return Map of words with associated nb of occurrences
     */
    public Map<String, Integer> getWordsMinFreq(Map<String, Integer> freqs, int minnb){
    	Map<String, Integer> result = new HashMap<String, Integer>();
    	for(String w: freqs.keySet()){
    		if(freqs.get(w)>=minnb){
    			result.put(w, freqs.get(w));
    		}
    	}
    	return result;
    }
    
    /**
     * generates an array of three elements:
     * 1: a map with words from concordance as keys and unique IDs for each word as values
     * 2: an ArrayList of words from concordance; ID of the word is its
     *    index in this ArrayList
     * 3: an ArrayList of frequencies of the words from concordance; 
     *    ID of the word is index of its frequency in this ArrayList
     *    
     * @param freqsdict
     * @return
     */
    public Object [] wordList(Map<Integer, Map<String,Integer>>freqsdict, Map<String,Integer> frequencies, ArrayList<String> stopwords, int nbwords){
		   Object[] theresult = new Object[3];
		   Map <String,Integer> wordsID = new HashMap<String,Integer>();
		   ArrayList<Integer> freqs = new ArrayList<Integer>(freqsdict.keySet());
		   Collections.sort(freqs , Collections.reverseOrder());
		  
		  int n = 0;
		  for(int f : freqs){
				  for(String s : freqsdict.get(f).keySet()){
					  if(n == nbwords){
						  break;
					  }
					  if(s.equals(locatetarget)){
						  wordsID.put(s, n);
						  n +=1;
					  }
					  else if(!stopwords.contains(s) && !s.equals("aaaaaaaaa")){
						  wordsID.put(s, n);
						  n += 1;
					  }
			  }
		  }
    	  int [] keptwordsfrequencies = new int[wordsID.size()];
		  String [] keptwords = new String[wordsID.size()]; 
		  for (String word : wordsID.keySet()){
			  keptwords[wordsID.get(word)] = word;
			  keptwordsfrequencies[wordsID.get(word)] = frequencies.get(word);
		  }
		  
		  theresult[0] = wordsID;
		  theresult[1] = keptwords;
		  theresult[2] = keptwordsfrequencies;
		return theresult;
		   
	   }
    
    /**
     * Substitute words of the text which will not appear in the tree with ""
     * @param text List of all words of the text
     * @param keptWordsId Map of words of the tree with associated ID
     * @return list of words
     */
    private ArrayList<String> filterText(ArrayList<String> text, Map<String,Integer> keptWordsId){
    	
    	int i=0;
    	while(i<text.size()){
    		if(!keptWordsId.containsKey(text.get(i))){
    			if(!text.get(i).equals("aaaaaaaaa")){
    				text.set(i, "");
    			}
    		}
    		i += 1;
    	}
    	
    	return text;
  
    }
    /**
     * Computes cooccurrence of the words; word x and word y cooccurred if
     * they appeared in one line of the concordance.
     * @param keptWordsid
     * @param words
     * @return
     */
    public ArrayList<ArrayList<ArrayList<Integer>>> computeCooccurrenceDisjoint(Map <String, Integer>keptWordsid, ArrayList<String> words ){
 	   ArrayList<ArrayList<ArrayList<Integer>>> coocc = new ArrayList<ArrayList<ArrayList<Integer>>>(keptWordsid.size());
 	   ArrayList<Integer> freqwin = new ArrayList<Integer>(keptWordsid.size());
 	   int winnb = 0;
 	   int i = 0;
 	   int [] freqwin1 = new int [keptWordsid.size()];
 	   while(i < keptWordsid.size()){
 		   ArrayList<ArrayList<Integer>> coocrow = new ArrayList<ArrayList<Integer>>(keptWordsid.size());
 		   freqwin.add(0);
 		   freqwin1[i]=0;
 		   int j = 0;
 		   while(j < keptWordsid.size()){
 			   ArrayList<Integer> coocCase = new ArrayList<Integer>(4);
 			   coocCase.add(0);
 			   coocCase.add(0);
 			   coocCase.add(0);
 			   coocCase.add(0);
 			   coocrow.add(coocCase);
 			   j +=1;
 		   }
 		   coocc.add(coocrow);
 		   i += 1;
 	   }
 	   
 	   Map<String,Integer> window = new HashMap<String,Integer>();
 	   int ii = 0;
 	   while(ii < words.size()){
 		   if(!words.get(ii).equals("aaaaaaaaa")){
 			   if(!words.get(ii).equals("")){     
 			   window.put(words.get(ii), 1);
 			   }
 			   }else{
 				   winnb += 1;
 				   ArrayList<String> windowWords = new ArrayList<String>(window.keySet());
 				   int jj = 0;
 				   while(jj < windowWords.size()){
 					   int k = jj + 1;
 					   int posj = keptWordsid.get(windowWords.get(jj));
 					   if(window.get(windowWords.get(jj))>0){
 						   freqwin.set(posj, freqwin.get(posj) + 1);
 					   }
 					   while(k < windowWords.size()){
 						   int posk = keptWordsid.get(windowWords.get(k));
 						   coocc.get(posj).get(posk).set(0, coocc.get(posj).get(posk).get(0) +1);
 						   coocc.get(posk).get(posj).set(0, coocc.get(posj).get(posk).get(0));
 						   k += 1;}
 					   jj += 1;}
 				   window.clear();
 					   }
 		   ii += 1;
 				   }
 	   if(!words.get(ii-1).equals("aaaaaaaaa")){
 		   winnb += 1;
 		   ArrayList<String> windowWords = new ArrayList<String>(window.keySet());
 		   int j = 0;
 		   while(j < windowWords.size()){
 			   int k = j + 1;
 			   int posj = keptWordsid.get(windowWords.get(j));
 			   if(window.get(windowWords.get(j))>0){
 				   freqwin.set(posj, freqwin.get(posj)+1);
 			   }
 			   while(k < windowWords.size()){
 				   int posk = keptWordsid.get(windowWords.get(k));
 				   coocc.get(posj).get(posk).set(0, coocc.get(posj).get(posk).get(0) + 1);
 				   coocc.get(posk).get(posj).set(0, coocc.get(posj).get(posk).get(0));
 				   k += 1;
 			   }
 			   j += 1;
 		   }
 		   
 	   }
 	   int j = 0;
 	   while(j < freqwin.size()){
 		   int k = 0;
 		   while(k < freqwin.size()){
 			   coocc.get(j).get(k).set(1, freqwin.get(j)-coocc.get(j).get(k).get(0));
 			   coocc.get(k).get(j).set(2, coocc.get(j).get(k).get(1));
 			   coocc.get(k).get(j).set(1, freqwin.get(k)-coocc.get(k).get(j).get(0));
 			   coocc.get(j).get(k).set(2, coocc.get(k).get(j).get(1));
 			   coocc.get(j).get(k).set(3, winnb-coocc.get(j).get(k).get(0)-coocc.get(j).get(k).get(1)-coocc.get(j).get(k).get(2));
 			   coocc.get(k).get(j).set(3, coocc.get(j).get(k).get(3));
 			   k += 1;
 		   }
 		   j += 1;
 	   }   
 	return coocc;

    }
    
    /**
     * computes distance matrix from cooccurrences using chisquared method
     * @param coocc
     * @return
     */
    public ArrayList<ArrayList<Double>> distanceFromCooccurrence(ArrayList<ArrayList<ArrayList<Integer>>> coocc){
		
		   ArrayList<ArrayList<Double>> distance = new ArrayList<ArrayList<Double>>();   //is it okay to use float instead of double?
		   
		   int j = 0;
		   while(j < coocc.get(0).size()){
			   int k = 0;
			   ArrayList<Double> distancerow = new ArrayList<Double>();
			   while(k < coocc.get(0).size()){
				   if(k == j){
					   distancerow.add((double) 0);
				   }else{
					   int O11 = coocc.get(j).get(k).get(0);
					   int O12 = coocc.get(j).get(k).get(1);
					   int O21 = coocc.get(j).get(k).get(2);
					   int O22 = coocc.get(j).get(k).get(3);
					   int R1 = O11+O12;
					   int R2 = O21+O22;
					   int C1 = O11+O21;
					   int C2 = O12+O22;
					   int N = R1+R2;
					   if(R1*R2*C1*C2 > 0){
						   distancerow.add(1000-(1.0*N*(O11*O22-O12*O21)*(O11*O22-O12*O21)/(R1*R2*C1*C2)));
					   }else{
						   distancerow.add((double) 0);
					   }
				   }
				   k += 1;
			   }
			   distance.add(distancerow);
			   j += 1;
		   }
		   System.out.println(distance);
		
		   return distance;
		   
	   }
    
    /**
     * normalizes distance matrix using linear method
     * 
     * @param mat
     * @return
     */
    public ArrayList<ArrayList<Double>> normalizeMatrix(ArrayList<ArrayList<Double>> mat){
		   double themax = 1;
		   double themin = mat.get(0).get(0);
		   for(int i=0; i < mat.get(0).size(); i++){
			   for(int j=0; j < mat.get(0).size(); j++){
				   themax = Math.max(themax, mat.get(i).get(j));
				   themin = Math.max(themin, mat.get(i).get(j));
			   }
		   }
		   for(int i=0; i < mat.get(0).size(); i++){
			   for(int j=0; j < mat.get(0).size(); j++){
				   mat.get(i).set(j, mat.get(i).get(j)/themax);
			   }
		   }
		   return mat; 
	   }
    
    /**
     * Export matrix as CSV
     * @param distmatrix matrix
     * @param words words of the tree
     * @param output absolute path to output file
     * @throws IOException
     */
    
    public void saveMatrixToCSV(ArrayList<ArrayList<Double>> distmatrix, String [] words, String output) throws IOException{
    	FileWriter w = new FileWriter(output);
    	int j = 0;
    	w.write(";");
    	for(String word : words){
    		w.write(word + "; ");
    	}
    	w.write("\r\n");
    	while(j<distmatrix.size()){
    		ArrayList<Double> row = distmatrix.get(j);
    		w.write(words[j] + "; ");
    		j += 1;
    		int i = 0;
    		while(i<row.size()){
    			w.write(row.get(i) + "; ");
    			i += 1;
    		}
    		w.write("\r\n");
    	}
    	w.close();
    }
    
    /**
     * Compute matrix using Unitex Stats module
     * @param filepath absolute path to "conocrd.html" file
     * @param keptwords list of words to be presented in the tree (extracted from Unitex Stats module) 
     * @return matrix
     * @throws IOException
     */
    
    public ArrayList<ArrayList<Double>> computeMatrix(String filepath, ArrayList<String> keptwords) throws IOException{
    	
    	ArrayList<String> lines = readConcor(filepath);
    	Map<String, Integer> freqs = freqsDict(text(lines));
    	Map<Integer, Map<String, Integer>> sortedbyfreq = sortByFrequency(freqs);
    	Object [] ob = wordList(sortedbyfreq, freqs, keptwords, 30);
    	Map<String, Integer> ids = (Map<String, Integer>) ob[0];
    	String [] labels = (String []) ob[1];
    	ArrayList<String> taxa = new ArrayList<String>(Arrays.asList(labels));
    	words = taxa;
    	ArrayList<String> filteredtext =  filterText(text(lines), ids);
    	
    	return normalizeMatrix(distanceFromCooccurrence(computeCooccurrenceDisjoint(ids, filteredtext)));
    }
    
    /**
     * Get list of words presented in the tree
     * @return list of words
     */
    
    public ArrayList<String> getLabelList(){
    	return this.words;
    }
    
    /**
     * Read stopwords from file
     * @param filepath absolute path to stopwords file
     * @return list of stopwords
     * @throws IOException
     */
    
    public ArrayList<String> loadStopWords(String filepath) throws IOException{
    	ArrayList<String> stopwords = new ArrayList<String>();
    	
    	FileReader fw = new FileReader(filepath);
    	BufferedReader bf = new BufferedReader(fw);
    	while(true){
    		String line = bf.readLine();
    		if(line == null){
    			break;
    		}
    		else{
    			stopwords.add(line);
    		}
    	}
    	bf.close();
    	return stopwords;
    }
    
    /**
     * Make up absolute path to "tokens.txt" file from absolute path of "concord.html" file
     * @param concorpath absolute path to "concord.html" file
     * @return
     */
    
    private String makeTokensPath(String concorpath){
    	Pattern p = Pattern.compile("(.*?)(concord\\.html)");
    	Matcher m = p.matcher(concorpath);
    	if(m.find()){
    		return m.group(1) + "tokens.txt";
    	}else{
    		return null;
    	}
    }
    /**
     * Compute word colors
     * @param theword locate pattern of the concordance
     * @param text list of all words of the text
     * @param keptWordsId list of words of the tree with associated ID
     * @param distance distance matrix
     * @return
     */
    
    private ArrayList<Map<String, Double>> computeCooccurrenceColors(String theword, ArrayList<String> text, Map<String, Integer> keptWordsId, ArrayList<ArrayList<Double>> distance){
    	ArrayList<Map<String, Double>> result = new ArrayList<Map<String, Double>>();
    	Map<String, Double> positions = new HashMap<String, Double>();
    	Map<String, Double> dispersion = new HashMap<String, Double>();
    	//double themin = distance.get(0).get(1);
    	//double themax = themin;
    	
    	ArrayList<String> keptWords = new ArrayList<String>(keptWordsId.keySet());
    	
    	for(String word : keptWords){
    		if(!positions.containsKey(word)){
    			double value = distance.get(keptWordsId.get(theword)).get(keptWordsId.get(word));
    			positions.put(word, value);
    		}
    	}
    	Set<Entry<String, Double>> items = positions.entrySet();
    	Map<Double, String> tmp = new HashMap<Double, String>();
    	for(Entry<String, Double> item : items){
    		tmp.put(item.getValue(), item.getKey());
    	}
    	ArrayList<String> words = new ArrayList<String>(keptWords);
    	int i = 0;
    	for(String word : words){
    		i += 1;
    		if(!word.equals(theword)){
    			positions.put(word, Math.floor(255D*i/words.size()));
    		}else{
    			positions.put(word, 0.0);
    		}
    	}
    	result.add(positions);
    	result.add(dispersion);
    	return result;
    }
    /**
     * Method used by other methods for computing colors
     * @param text
     * @param keptWordsId
     * @return
     */
    
    private ArrayList<Map<String, Double>> computeAveragePositions(ArrayList<String> text, Map<String, Integer> keptWordsId){
    	Map<String, Double> numberfound = new HashMap<String, Double>();
    	ArrayList<Map<String, Double>> result = new ArrayList<Map<String, Double>>();
    	Map<String, Double> positions = new HashMap<String, Double>();
    	Map<String, Double> dispersion = new HashMap<String, Double>();
    	
    	for(int i=0; i<text.size(); i++){
    		String word = text.get(i);
    		if(!word.equals("")){
    			if(numberfound.containsKey(word)){
    				double nbfound = numberfound.get(word);
    				positions.put(word, ((positions.get(word)*nbfound+i*255.0/text.size())/(nbfound+1)));
    				numberfound.put(word, nbfound+1);
    			}else{
    				numberfound.put(word, 1.0);
    				positions.put(word, i*255.0/text.size());
    			}
    		}
    	}
    	Map<String, Double> numberfound2 = new HashMap<String, Double>();
    	for(int i=0; i<text.size(); i++){
    		String word = text.get(i);
    		if(!word.equals("")){
    			if(numberfound2.containsKey(word)){
    				double nbfound = numberfound2.get(word);
    				double difference = (i*255.0/text.size()-positions.get(word));
    				dispersion.put(word, (dispersion.get(word)*nbfound+difference*difference)/(nbfound+1));
    				numberfound2.put(word, nbfound+1);
    			}else{
    				numberfound2.put(word, 1.0);
    				double difference = (i*255.0/text.size()-positions.get(word));
    				dispersion.put(word, difference*difference);
    			}
    			
    		}
    	}
    	double theminp = 256.0;
    	double themaxp = 0.0;
    	double themind = 256*256;
    	double themaxd = 0.0;
    	for(String items : positions.keySet()){
    		//double tmp = Math.max(themaxp, positions.get(items));
    		themaxp = Math.max(themaxp, positions.get(items));
    		theminp = Math.min(theminp, positions.get(items));
    		themaxd = Math.max(themaxd, dispersion.get(items));
    		themind = Math.min(themind, dispersion.get(items));
    	}
    	themaxd = Math.sqrt(themaxd);
    	themind = Math.sqrt(themind);
    	if(themaxp>theminp){
    		for(String items : positions.keySet()){
    			positions.put(items, ((positions.get(items))-theminp)*255.0/(themaxp-theminp));
    		}
    	}
    	if(themaxd>themind){
    		for(String items : positions.keySet()){
    			dispersion.put(items, (Math.sqrt(dispersion.get(items))-themind)*100.0/(themaxd-themind));
    		}
    	}
    	result.add(positions);
    	result.add(dispersion);
    	return result;
    }
    /**
     * Set color of the words
     * @param nodes list of leaves of the tree
     * @param keptWordsId list of words of the tree with associated ID
     * @param frequencies list of word frequencies
     * @param text list of all words of the text
     * @param color colormode
     * @param distance distancematrix
     * @return color of the edges
     */
    public String setLabelsColor(ArrayList<TreeNode> nodes, Map<String, Integer> keptWordsId, int [] frequencies, ArrayList<String> text, String color, ArrayList<ArrayList<Double>> distance){
    	String edgecolor = new String();
    	edgecolor = "#CCCCFF";
    	Map<String, Double> positions = new HashMap<String, Double>();
    	Map<String, Double> dispersion = new HashMap<String, Double>();
    	int chronology = 0;
    	if(color.equals("chronology")){
    		chronology = 1;
    	}
    	if(color.equals("chronodisp")){
    		chronology = 2;
    	}
    	if(color.equals("dispersion")){
    		chronology = 3;
    	}
    	if(chronology>0){
    		edgecolor = "#CCCCCC";
    		ArrayList<Map<String, Double>> locations = computeAveragePositions(text, keptWordsId);
    		positions = locations.get(0);
    		dispersion = locations.get(1);
    	}
    	if(color.equals("Target")){
    		chronology += 1;
    		ArrayList<Map<String, Double>> locations = computeCooccurrenceColors(locatetarget, text, keptWordsId, distance);
    		
    		positions = locations.get(0);
    		dispersion = locations.get(1);
    	}
    	
    	ArrayList<Integer> tmp = new ArrayList<Integer>();
    	for(int i : frequencies){
    		tmp.add(i);
    	}
    	int themin = Collections.min(tmp);
    	int themax = Collections.max(tmp);
    	
    	for(TreeNode node : nodes){
    		int averagepos = 0;
    		int averagepos2 = 0;
    		if(chronology>0){
    			if(chronology==1){
    				averagepos = (int) Math.max(0, Math.floor(positions.get(node.name)));
    				averagepos2 = (int) Math.max(0, Math.floor(255.0-positions.get(node.name)));
    			}
    			if(chronology==2){
    				averagepos = (int) Math.max(0, Math.floor(positions.get(node.name))*(100-0.8*dispersion.get(node.name))/100.0);
    				averagepos2 = (int) Math.max(0, Math.floor(255-positions.get(node.name))*(100-0.8*dispersion.get(node.name))/100.0);
    			}
    			if(chronology==3){
    			    averagepos = (int) Math.max(0, Math.floor(dispersion.get(node.name)*255.0/100));
    				averagepos2 = (int) Math.max(0, Math.floor(255-dispersion.get(node.name)*255.0/100));
    			}
    			node.setColor(" rgb(" + averagepos2 + "," + "0" + "," + averagepos + ")");
    			node.setSize(fontsizelevel[(int) (1+9.99999*(Math.log(frequencies[keptWordsId.get(node.name)])-Math.log(themin))/(Math.log(themax)-Math.log(themin)))]);
    		}else{
    			if(color.equals("Grayscale")){
    				node.setColor(fontcolorberry[(int) (1+9.99999*(Math.log(frequencies[keptWordsId.get(node.name)])-Math.log(themin))/(Math.log(themax)-Math.log(themin)))]);
    				node.setSize(fontsizelevel[(int) (1+9.99999*(Math.log(frequencies[keptWordsId.get(node.name)])-Math.log(themin))/(Math.log(themax)-Math.log(themin)))]);
    			}else if(color.equals("Red & blue")){
    				node.setColor(fontcoloryahoo[(int) (1+9.99999*(Math.log(frequencies[keptWordsId.get(node.name)])-Math.log(themin))/(Math.log(themax)-Math.log(themin)))]);
    				node.setSize(fontsizelevel[(int) (1+9.99999*(Math.log(frequencies[keptWordsId.get(node.name)])-Math.log(themin))/(Math.log(themax)-Math.log(themin)))]);
    			}
    			
    		}
    	}
    	return edgecolor;
    }
    
    /**
     * After computing the matrix, make up leaves (TreeNode)
     * @param words list of words of the tree
     * @return list of TreeNode
     */
    public ArrayList<TreeNode> makeLeaves(ArrayList<String> words){
    	ArrayList<TreeNode> result = new ArrayList<TreeNode>();
    	for(String word : words){
			TreeNode n = new TreeNode();
				n.setName(word);
				n.setAsLeaf();
				n.setID(words.indexOf(word));
				result.add(n);
			}
    	return result;
    }
    
    /**
     * Compute matrix not using Unitex Stats module
     * @param filepath absolute path to "concord.html"
     * @param stopwordspath absolute path to stopwords file
     * @param nbwords number of words in a tree (30 by default)
     * @param color colormode
     * @return list of leaves of the tree (TreeNode)
     * @throws IOException
     */
    public ArrayList<TreeNode> computeMatrixNoStats(String filepath, String stopwordspath, int nbwords, String color) throws IOException{
    	if(nbwords == 0){
    		nbwords += 30;
    	}
    	/*
    	 * Read lines from "concord.html"; remove html tags, set locate pattern
    	 */
    	ArrayList<String> lines = readConcor(filepath);
    	System.out.println(text(lines));
    	/*
    	 * Split lines into words and compare with "tokens.txt" (this will avoid adding "cut" words from the sides of the concordance lines)
    	 */
    	ArrayList<String> text = compareWithTokens(makeTokensPath(filepath), text(lines));
    	/*
    	 * Frequency dictionnary
    	 */
    	System.out.println("words: " + text);
    	Map<String, Integer> tmp = freqsDict(text);
    	//System.out.println(tmp);
    	/*
    	 * Words sorted by frequency
    	 */
    	Map<Integer, Map<String, Integer>> freqsdict = sortByFrequency(tmp);
    	/*
    	 * remove stopwords, if user has chosen this
    	 */
    	ArrayList<String> stopwords = new ArrayList<String>();
    	if(stopwordspath != null){
    		stopwords = loadStopWords(stopwordspath);
    	}
    	
    	Object ob [] = wordList(freqsdict, tmp, stopwords, nbwords);
    	/*
    	 * Words of the tree asocciated with the ID
    	 */
    	Map<String, Integer> ids = (Map<String, Integer>) ob[0];
    	/*
    	 * List of words of the tree
    	 */
    	String [] labels = (String []) ob[1];
    	ArrayList<String> taxa = new ArrayList<String>(Arrays.asList(labels));
    	/*
    	 * Set list of words to the "words" attribute
    	 */
    	words = taxa;
    	/*
    	 * Filter text (subsitute words which do not appear in the tree with "")
    	 */
    	ArrayList<String> filteredtext =  filterText(text(lines), ids);
    	//System.out.println(words);
    	/*
    	 * Compute distance matrix
    	 */
    	ArrayList<ArrayList<Double>> dm = normalizeMatrix(distanceFromCooccurrence(computeCooccurrenceDisjoint(ids, filteredtext)));
    	/*
    	 * Make TreeNode instances from words of a tree
    	 */
    	//System.out.println(dm);
    	ArrayList<TreeNode> nodes = new ArrayList<TreeNode>(makeLeaves(words));
    	int [] keptwordsfreqs = (int[]) ob[2];
    	
    	//saveMatrixToCSV(dm, labels, "C:/outputttt.csv");
    	/*
    	 * Set labels color
    	 */
    	System.out.println("Computing colors and sizes...");
    	edgecolor = setLabelsColor(nodes, ids, keptwordsfreqs, filteredtext, color, dm);
    	distancematrix = dm;
    	return nodes;
    }
    /**
     * Get distance matrix
     * @return
     */
    public ArrayList<ArrayList<Double>> getMatrix(){
    	return this.distancematrix;
    }
    /**
     * Get edge color
     * @return
     */
    public String getEdgeColor(){
    	return this.edgecolor;
    }
}  


