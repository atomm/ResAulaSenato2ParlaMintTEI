package it.cnr.igsg.senato;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import it.cnr.igsg.senato.resAula.ResAulaItem;
import it.cnr.igsg.senato.resAula.ResAulaParser;



public class RunHTMLSenato2XML {

	
	
	public static void main(String[] args) {
		
		ResAulaParser rap = readResAula();
		
		// 1. table *unique* files from SOURCE (corpus HTML Senato)
		// data/Resaula-ParlaMINT/SOURCE
		// data/unique_grid.csv
		rap.printUNIQUETABLEtoFile();
		
		
		// 2. extract embedded XML annotation from HTML source and save embedded XML 
		//  remove html annotations, clean and unescape text
		//  check well-formedness of *embedded XML*
		//  output: data/Resaula-ParlaMINT/Resaula-embedded-XML 
		
		// WARNING!!  overwrites manually enforced well-formedness
		export_EmbeddedXML_corpus(rap);
	}
	

	
	
	private static void export_EmbeddedXML_corpus(ResAulaParser rap) {
		
		int valid = 0;
		int invalid = 0;
		
		HashMap<String,ResAulaItem> uniqueRes = rap.getUniqueResocontiAula();
		int covidTokens = 0;
		int referenceTokens =0;
		int tokens;
		for(ResAulaItem RA:uniqueRes.values()) {
			if((tokens = rap.extract_embedded_XML(new File(RA.getFilePath())))!=-1) {
				if(RA.getCorpus().equalsIgnoreCase("covid"))
					covidTokens+=tokens;
				else
					referenceTokens+=tokens;
				valid++;
			}
			else 
				invalid++;
		}
		
		System.out.println("++EMBEDDED XML++");
		System.out.println("");
		System.out.println("VALID: "+valid);
		System.out.println("INVALID: "+invalid);
		System.out.println("TOTAL XML: "+(invalid+valid));
		System.out.println("COVID_TOKENS "+covidTokens);
		System.out.println("REFERENCE_TOKENS "+referenceTokens);


	}

	
	/**
	 * Legge i file XML degli emendamenti in Commissione Senato e produce un output CSV con i metadati
	 */
	private static ResAulaParser readResAula() {
		
		File dir = new File(Config.RES_AULA_FOLDER);
		
		ResAulaParser parser = new ResAulaParser();
		
		parser = readDir(parser, dir);
		System.err.println("SIZE: "+parser.getResocontiAula().size());
		
		
		// UNIQUE RESOCONTI - divisi fra reference e corpus
		
		// HTML Only
		HashMap<String, ResAulaItem> uniqueRes = getUniqueResHTML(parser.getResocontiAula());
		parser.setUniqueResocontiAula(uniqueRes);
			
		System.err.println("DEDUP SIZE: "+uniqueRes.values().size());
		
		
		return parser;
	}
	

	
	
	private static HashMap<String, ResAulaItem> getUniqueResHTML(Set<ResAulaItem> corpusList){
		
		HashMap<String, ResAulaItem> uniqueRes = new HashMap<String, ResAulaItem>();
	
		
		Iterator iter = corpusList.iterator();

	    while (iter.hasNext()) {
	    	 ResAulaItem item = (ResAulaItem)iter.next();
	    	 if(item.getExtension().equalsIgnoreCase("htm"/*"akn"*/) && item.getCorpus().equalsIgnoreCase("covid")) {
	    		 if(uniqueRes.get(item.getDocId())==null)
	    			 uniqueRes.put(item.getDocId(),item);
	    	 }
	    }
		
	    iter = corpusList.iterator();
	    
	    while (iter.hasNext()) {
	    	 ResAulaItem item = (ResAulaItem)iter.next();
	    	 if(item.getExtension().equalsIgnoreCase("htm"/*"akn"*/) && item.getCorpus().equalsIgnoreCase("reference")) {
	    		 if(uniqueRes.get(item.getDocId())==null)
	    			 uniqueRes.put(item.getDocId(),item);
	    	 }
	    }
		
		return uniqueRes;
	}
	
	

	
	private static ResAulaParser readDir(ResAulaParser parser, File dir) {
		
		File[] files = dir.listFiles();
		
		for(int i = 0; i < files.length; i++) {

			File file = files[i];

			if(file.isDirectory()) {
				readDir(parser, file);
			} else {
				if(file.getName().endsWith("xml") || file.getName().endsWith("htm") || file.getName().endsWith("akn")) {
					
					ResAulaItem RA = new ResAulaItem();
					String docId = file.getName().substring(0,file.getName().indexOf("."));
					String ext = file.getName().substring(file.getName().indexOf(".")+1);
					String leg = file.getParentFile().getName();
					String corpus = file.getParentFile().getParentFile().getName();
					RA.setDocId(docId);
					RA.setExtension(ext);
					RA.setLegislatura(leg);
					RA.setCorpus(corpus);
					RA.setFilePath(file.getPath());

					//RA.setFilePath(file.getAbsolutePath());

					parser.parse(file, RA);
					

				}
			}
		}
		return parser;
	}
}
