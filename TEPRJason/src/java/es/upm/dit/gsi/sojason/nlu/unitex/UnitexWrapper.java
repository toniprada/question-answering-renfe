package es.upm.dit.gsi.sojason.nlu.unitex;
/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Universit� Paris-Est Marne-la-Vall�e <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */

/*
 * File created and contributed by Gilles Vollant (Ergonotics SAS) 
 * as part of an UNITEX optimization and reliability effort
 *
 * additional information: http://www.ergonotics.com/unitex-contribution/
 * contact : unitex-contribution@ergonotics.com
 *
 */

import jason.asSyntax.Literal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import es.upm.dit.gsi.sojason.services.WebServiceConnector;
import fr.umlv.unitex.jni.UnitexJni;

/**
 *
 * Project: TEPRJason
 * Package: es.upm.dit.gsi.sojason.nlu.unitex
 * Class: UnitexWrapper
 *
 */
public class UnitexWrapper implements WebServiceConnector {

	/** Path separator depending on the OS */
	private static final String pathSeparator = UnitexJni.isUnderWindows() ? "\\" : "/";
	
	/** The Logger */
	static Logger logger = Logger.getLogger("TEPRJason." + UnitexWrapper.class.getName());
	

	/**
	 * This calls Unitex with the given params
	 */
	public Collection<Literal> call(String... params) {
		/* Are parameters correct */
		if (!validateParams(params)) { 
			logger.info("Parameters are not valid:" + Arrays.toString(params));
			return null; 
		}
		
		String xml = query(params[0]);
		logger.info(xml);
		Collection<Literal> res = parseToLiterals(xml);
		logger.info(res.toString());
		return res;
	}

	/**
	 * This checks if the number of params is correct
	 */
	public boolean validateParams(String... params) {
		return params.length == 1;
	}
	
	/*
	 * private methods
	 */
	
	/**
	 * Get files recursively from the given path
	 * @param path	the path to the folder
	 * @return		list of filenames
	 */
	private static String[] getFiles(String path) {
		ArrayList<String> files = new ArrayList<String>();
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			File ifile = listOfFiles[i];
			String iname = listOfFiles[i].getName();
			String ifpath = UnitexJni.combineUnitexFileComponent(path, iname);
			if (ifile.isFile()) {
				files.add('"' + ifpath + '"');
			} else if (ifile.isDirectory()) {
				String[] newList = getFiles(ifpath);
				Set<String> mySet = new HashSet<String>(Arrays.asList(newList));
				files.addAll(mySet);
			}
		}
		String[] result = new String[files.size()];
		result = files.toArray(result);
		return result;
	}

	/**
	 * <p>Configure the Unitex to work with the given resources, dictionaries,
	 * graphs. It processes the text and returns an XML representation of the 
	 * concordance.</p>
	 *  
	 * @param othersResDir		
	 * @param dictionnaryResDir	
	 * @param graphResDir		
	 * @param corpusPath		
	 * @param corpusText		The text to be processed
	 * @return
	 */
	private static String processUnitexWork(String othersResDir,
											String dictionnaryResDir,
											String graphResDir,
											String corpusPath,
											String corpusText)
	{
		String pSep = pathSeparator;
		UnitexJni.writeUnitexFile(UnitexJni.combineUnitexFileComponent(corpusPath,"corpus.txt"),corpusText);

		String cmdNorm = "Normalize " + UnitexJni.combineUnitexFileComponentWithQuote(corpusPath,"corpus.txt") + " -r "+UnitexJni.combineUnitexFileComponentWithQuote(othersResDir,"Norm.txt") ;
		String cmdTok = "Tokenize " + UnitexJni.combineUnitexFileComponentWithQuote(corpusPath,"corpus.txt") + " -a "+ UnitexJni.combineUnitexFileComponentWithQuote(othersResDir,"Alphabet.txt") ;
		String cmdDico = "Dico -t "+ UnitexJni.combineUnitexFileComponentWithQuote(corpusPath,"corpus.snt")+ " -a " + UnitexJni.combineUnitexFileComponentWithQuote(othersResDir,"Alphabet.txt")+" ";//+UnitexJni.combineUnitexFileComponentWithQuote(dictionnaryResDir,"dela-en-public.bin")


		String[] delas = getFiles(dictionnaryResDir);
		for (int i = 0; i < delas.length; i++){
			logger.fine("Loading dictionary: " + delas[i]);
			cmdDico = cmdDico + " " + delas[i];
		}


		String cmdLocate = "Locate -t "+UnitexJni.combineUnitexFileComponentWithQuote(corpusPath,"corpus.snt")+" "
		+ UnitexJni.combineUnitexFileComponentWithQuote(graphResDir,"travel.fst2")
		+ " -a "+ UnitexJni.combineUnitexFileComponentWithQuote(othersResDir,"Alphabet.txt")+ " -L -R --all -b -Y";

		String cmdConcord = "Concord "+ UnitexJni.combineUnitexFileComponentWithQuote(corpusPath,"corpus_snt","concord.ind")+ " -m " + UnitexJni.combineUnitexFileComponentWithQuote(corpusPath,"corpus.txt") ;
		String cmdConcord2 = "Concord "+ UnitexJni.combineUnitexFileComponentWithQuote(corpusPath,"corpus_snt","concord.ind")+" --xml";

		/*
		 *
           // there is an alternative : using an array of string
          String [] strArrayNormalize={"UnitexTool","{","Normalize",UnitexJni.combineUnitexFileComponent(corpusPath,"corpus.txt"), "-r",UnitexJni.combineUnitexFileComponent(othersResDir,"Norm.txt"),"}"};
          //String [] strArrayNormalizeAlternative={"UnitexTool","Normalize",UnitexJni.combineUnitexFileComponent(corpusPath,"corpus.txt"), "-r",UnitexJni.combineUnitexFileComponent(othersResDir,"Norm.txt")};
          UnitexJni.execUnitexTool(strArrayNormalize);
		 */
		UnitexJni.execUnitexTool("UnitexTool " + cmdNorm);
		UnitexJni.execUnitexTool("UnitexTool " + cmdTok);
		UnitexJni.execUnitexTool("UnitexTool " + cmdDico);
		UnitexJni.execUnitexTool("UnitexTool " + cmdLocate);
		UnitexJni.execUnitexTool("UnitexTool " + cmdConcord);
		UnitexJni.execUnitexTool("UnitexTool " + cmdConcord2);

		// these 6 lines can be replaced by only one execution (with very small speed improvement)			
		/*
          UnitexJni.execUnitexTool("UnitexTool { " + cmdNorm + " } { " + cmdTok + " } { " + cmdDico + " } { "  + cmdLocate + " } { " + cmdConcord + " } { " + cmdConcord2+ " }");
		 */

		String merged =  UnitexJni.getUnitexFileString(UnitexJni.combineUnitexFileComponent(corpusPath,"corpus.txt"));
		String xml = UnitexJni.getUnitexFileString(UnitexJni.combineUnitexFileComponent(corpusPath,"corpus_snt","concord.xml"));
		return xml;
	}

	/**
	 * <p>Processes the query given and produces an XML representation of
	 * the concordance.</p>
	 * @param query		text to process
	 * @return			XML representation of concordance
	 */
	private static String query(String query){
		
//		logger.info(System.getProperty("sun.arch.data.model"));
//		logger.info("is ms-windows:" + UnitexJni.isUnderWindows() + " : " + System.getProperty("os.name") + " " + java.io.File.separator);

		String baseWorkDir = ".";
		String ressourceDir = UnitexJni.isUnderWindows() ? ".\\unitex": "./unitex";
		int nbLoop=1;

//		logger.info("Resource path is: '" + ressourceDir + "' and work path is '" + baseWorkDir + "' and " + nbLoop + " execution");

		String graphResDir = UnitexJni.combineUnitexFileComponent(ressourceDir, "graph");
		String dictionnaryResDir = UnitexJni.combineUnitexFileComponent(ressourceDir, "dictionary");
		String othersResDir = UnitexJni.combineUnitexFileComponent(ressourceDir, "others");


		UnitexJni.setStdOutTrashMode(true);
		//UnitexJni.setStdErrTrashMode(true);


		String CorpusWorkPath = UnitexJni.combineUnitexFileComponent(baseWorkDir, "workUnitexThread" + Thread.currentThread().getId());

//		logger.info("Working on path: " + CorpusWorkPath);
		UnitexJni.createUnitexFolder(CorpusWorkPath);
		UnitexJni.createUnitexFolder(UnitexJni.combineUnitexFileComponent(CorpusWorkPath,"corpus_snt"));
		String res = "";

		for (int i=0;i<nbLoop;i++)
		{
			res = processUnitexWork(othersResDir,dictionnaryResDir,graphResDir,CorpusWorkPath, query);
		}

		UnitexJni.removeUnitexFolder(CorpusWorkPath);
		return res;
	}

	/**
	 * <p>This parse the string to literals</p>
	 * @param str	the string to parse
	 * @return		list of literals
	 */
	private static Collection<Literal> parseToLiterals(String str) {
		
		return UnitexWrapper.toLiterals(UnitexWrapper.parseXMLToMap(str));
		
	}
	
	/**
	 * <p>This parse the string to amap</p>
	 * @param str	the string to parse
	 * @return		map
	 */
    public static Map<String,String> parseXMLToMap (String in){
    	
    	Map<String, String> res = new HashMap<String, String>();
    	
    	String[] input = in.split("<concordance");
    	
    	for(String interm : input) {
	    	input = interm.split("</");
	    	for (int i = 0; i< input.length-2; i++ ){
	    		String[] temp = input[i].split(">");
	    		if(temp.length <= 2){ continue; }
	    		String attr = temp[temp.length-2];
	    		attr = attr.replaceAll("<", "");
	    		String val = temp[temp.length-1].toLowerCase();
	    		
	    		res.put(attr, val); // we may need to check with previous
	    	}
    	}
	    	
    	return res;
    }
    
    /**
     * This generates a collection of literalsfrom the map
     * @param map
     * @return
     */
    public static Collection<Literal> toLiterals (Map<String,String> map) {
    	Collection<Literal> result = new HashSet<Literal>();
    	for(String key : map.keySet()){
    		result.add( Literal.parseLiteral(key+"("+ map.get(key).toLowerCase()+")") );
    	}
    	return result;
    }
	
	/* 
	 * try me 
	 */
	public static void main(String[] args) {
		String res = UnitexWrapper.query("Quiero un viaje de madrid a valencia para 3 personas");
		System.out.println("");
		System.out.println("result:");
		System.out.println(res);
		System.out.println(UnitexWrapper.parseToLiterals(res));
	}

}