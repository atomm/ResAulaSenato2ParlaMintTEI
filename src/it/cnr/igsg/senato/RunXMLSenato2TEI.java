package it.cnr.igsg.senato;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import it.cnr.igsg.senato.datiSenato.DatiSenato;
import it.cnr.igsg.senato.resAula.MakeCorpusXML;
import it.cnr.igsg.senato.resAula.MapEmbeddedXML;


public class RunXMLSenato2TEI {

	// TRUE for  ANA version Resp Statements
	private static boolean ANA = false;

	private static HashMap<String,String> resAulaGrid;

	public static void main(String[] args) {

		resAulaGrid = readResAulaGrid(Config.UNIQUE_GRID_CSV_PATH);
		exportTEI();		

	}


	private static void exportTEI() {

		String[] testFiles= {"00698190.xml","01046694.xml","01155422.xml","01180442.xml"};


		////////		EXPORT SELECTION in TEI
		//		MapEmbeddedXML mex = new MapEmbeddedXML();
		//		DatiSenato datiSenato = new DatiSenato();
		//		mex.setDatiSenato(datiSenato);
		//		createYearFolders(Config.RES_AULA_TEI_OUTPUT);  
		//
		//		// flag: corpus o reference ? 
		//		// date / n. seduta? 
		//		for(String testFileName: testFiles) {
		//			String idDoc = testFileName.substring(0,testFileName.indexOf("."));
		//			String tsvMeta = resAulaGrid.get(idDoc);
		//			buildTeiDoc(mex, testFileName,tsvMeta, Config.RES_AULA_TEI_OUTPUT);
		//		}



		//// 	EXPORT ALL in TEI

		MapEmbeddedXML mex = new MapEmbeddedXML();
		DatiSenato datiSenato = new DatiSenato();
		mex.setDatiSenato(datiSenato);

		createYearFolders(Config.RES_AULA_TEI_OUTPUT);  

		String[] allFiles = getAllEmbeddedXMLFiles();
		for(String fileName:allFiles) {
			String idDoc = fileName.substring(0,fileName.indexOf("."));
			String tsvMeta = resAulaGrid.get(idDoc);
			buildTeiDoc(mex, fileName,tsvMeta,Config.RES_AULA_TEI_OUTPUT);

		}

		buildTeiCorpus(Config.RES_AULA_TEI_OUTPUT, mex.getTotalTags(), mex.getTotalExtent());

	}




	private static void buildTeiDoc(MapEmbeddedXML mex, String testFileName, String tsvMeta, String outFolder) {
		
		Document targetDoc = null;


		String[] fields = tsvMeta.split("\t");
		String idDoc = fields[0];

		if(fields==null || fields.length<2)
			System.out.println(testFileName);

		String legislatura = fields[2].substring(fields[2].indexOf("-")+1,fields[2].length());
		
		// corpus (reference/covid) comes from TSV
		String corpus = fields[3];
		String seduta = fields[6];
		String data = fields[7];


		String day = data.substring(6,8);
		String month = data.substring(4,6);
		String year = data.substring(0,4);


		// ParlaMint-IT_2020-05-06_LEG18-Sed-214
		String TEIid = "ParlaMint-IT_"+year+"-"+month+"-"+day+"-LEG"+legislatura+"-Sed-"+seduta;
		String TEIid4FileName = TEIid/*+"-"+idDoc*/;
		
		// subcorpus information goes in TEI/@ana and text/@ana
		
//		<TEI xmlns="http://www.tei-c.org/ns/1.0"
//			     ana="#parla.meeting.regular #covid"
//			     xml:id="ParlaMint-IT_2019-12-11-LEG18-Sed-172"
//			     xml:lang="it">
//		
//		<text ana="#covid" xml:lang="it">
//	      <body>
		
		String corpusAttribute = corpus.equalsIgnoreCase("covid")?"#covid":"#reference";


		String sourceFilePath = Config.RES_AULA_EMBEDDED_XML+"/"+testFileName;

		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			dbf.setExpandEntityReferences(true);

			DocumentBuilder domBuilder = dbf.newDocumentBuilder();
			targetDoc = domBuilder.newDocument();

			Element TEI = targetDoc.createElement("TEI");
			TEI.setAttribute("xmlns", "http://www.tei-c.org/ns/1.0");
			TEI.setAttribute("xml:lang", "it");
			TEI.setAttribute("xml:id", TEIid);
			// #parla.meeting.regular or #parla.sitting
			TEI.setAttribute("ana", "#parla.meeting.regular "+corpusAttribute);
			targetDoc.appendChild(TEI);

			Element  teiHeader = targetDoc.createElement("teiHeader");


			teiHeader.appendChild(mex.createFileDesc(targetDoc,idDoc,legislatura,corpus,seduta,data, ANA));
			teiHeader.appendChild(mex.createEncodingDesc(targetDoc));
			teiHeader.appendChild(mex.createProfileDesc(targetDoc,data));
			teiHeader.appendChild(mex.createRevisionDesc(targetDoc));

			TEI.appendChild(teiHeader);


			// END TEI HEADER

			Element  text = targetDoc.createElement("text");
			text.setAttribute("xml:lang", "it");
			text.setAttribute("ana",corpusAttribute);

			Element  body = targetDoc.createElement("body");
			text.appendChild(body);

			TEI.appendChild(text);

			// EXPORT TEXTUAL CONTENT..
			mex.resetSegId();
			mex.resetUtteranceId();
			mex.setTEIid(TEIid);

			targetDoc = mex.exportEmbeddedXML(sourceFilePath, targetDoc, body);

			writeXmlFile(targetDoc, outFolder+"/"+year+"/"+TEIid4FileName+".xml");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}




	public static HashMap<String,String> readResAulaGrid(String tsvFilePath) {

		HashMap<String,String> lookupResAulaMeta = new HashMap<String,String>();


		File resAulaGrid = new File(tsvFilePath);
		if(!resAulaGrid.exists()){
			System.err.println(" PROBLEMS READING SOURCE FILE "+tsvFilePath);
		}

		try{
			BufferedReader reader = new BufferedReader( new FileReader(resAulaGrid));
			String line  = null;

			while( ( line = reader.readLine() ) != null) {
				String[] fields = line.split("\t");
				String id = fields[0];

				if(lookupResAulaMeta.get(id)==null){
					lookupResAulaMeta.put(id, line);
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return lookupResAulaMeta;

	}





	private static String[] getAllEmbeddedXMLFiles() {

		File dir = new File(Config.RES_AULA_EMBEDDED_XML);
		return dir.list();

	}

	private static List<String> getExportedTeiDocs(){

		List<String> exportedTEIdocs = new ArrayList<String>();

		File dir = new File(Config.RES_AULA_TEI_OUTPUT);

		String[] dirs = dir.list();

		for(int i = 0; i < dirs.length; i++) {

			String dirName = dirs[i];
			System.err.println(dirName);
			if(dirName.length()==4) {

				File subdir = new File(Config.RES_AULA_TEI_OUTPUT+"/"+dirName);
				String[] files = subdir.list();

				for(int k = 0; k < files.length; k++) {

					String fileName = files[k];
					if(fileName.endsWith("xml") && fileName.contains("_")) {

						exportedTEIdocs.add(dirName+"/"+fileName);

					}
				}
			}
		}

		return exportedTEIdocs;
	}



	private static void buildTeiCorpus(String outFolder, HashMap<String,Integer> totalTags, HashMap<String,Integer> totalExtent) {
		Document targetCorpus = null;

		MakeCorpusXML mcx = new MakeCorpusXML(); 
		//mcx.Test();

		mcx.setTotalExtent(totalExtent);
		mcx.setTotalTags(totalTags);

		List<String> exportedTEIdocs = getExportedTeiDocs();

		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder domBuilder = dbf.newDocumentBuilder();
			targetCorpus = domBuilder.newDocument();


			// teiCorpus
			Element teiCorpus = targetCorpus.createElement("teiCorpus");

			teiCorpus.setAttribute("xmlns", "http://www.tei-c.org/ns/1.0");
			teiCorpus.setAttribute("xml:lang", "it");
			teiCorpus.setAttribute("xml:id", "ParlaMint-IT");

			targetCorpus.appendChild(teiCorpus);

			// teiHeader
			Element  teiHeader = targetCorpus.createElement("teiHeader");
			teiCorpus.appendChild(teiHeader);

			// fileDesc
			targetCorpus = mcx.makeFileDesc(targetCorpus, teiHeader, ANA);


			// encodingDesc  
			targetCorpus = mcx.makeEncodingDesc(targetCorpus, teiHeader);


			Element  profileDesc = targetCorpus.createElement("profileDesc");
			teiHeader.appendChild(profileDesc);

			// settingDesc
			targetCorpus = mcx.makeSettingDesc(targetCorpus, profileDesc);

			targetCorpus = mcx.makeTextClass(targetCorpus, profileDesc);

			

			// particDesc
			Element  particDesc = targetCorpus.createElement("particDesc");
			profileDesc.appendChild(particDesc);

			Element  listOrg = targetCorpus.createElement("listOrg");
			particDesc.appendChild(listOrg);

			targetCorpus = mcx.exportLegislatures(targetCorpus, /* append to */ listOrg);
			targetCorpus = mcx.exportGovernments(targetCorpus, /* append to */ listOrg);
			targetCorpus = mcx.exportGruppi(targetCorpus, /* append to */ listOrg);
			targetCorpus = mcx.exportRelazioni(targetCorpus, /* append to */ listOrg);

			targetCorpus = mcx.exportSenatori(targetCorpus, /* append to */ particDesc);
			targetCorpus = mcx.exportLangUsage(targetCorpus, /* append to */ profileDesc);

			// avoid revisionDesc
			//targetCorpus = mcx.revisionDesc(targetCorpus, /* append to */ teiHeader);

			// ADD INCLUDE xi: (append to teiCorpus)

			for(String teiDocFile:exportedTEIdocs) {

				Element xInclude = targetCorpus.createElement("xi:include");
				String includeFileName = teiDocFile;
				xInclude.setAttribute("xmlns:xi", "http://www.w3.org/2001/XInclude");
				xInclude.setAttribute("href", includeFileName);

				teiCorpus.appendChild(xInclude);
			}



			writeXmlFile(targetCorpus, outFolder+"/"+"ParlaMint-IT.xml");


		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}



	private static void createYearFolder(String year, String outFolder) {
		File directory = new File(outFolder+"/"+year);

		if(!directory.exists()){

			directory.mkdir();

		}
	}


	private static void createYearFolders(String outFolder) {
		createYearFolder("2013",outFolder);  
		createYearFolder("2014",outFolder);    		
		createYearFolder("2015",outFolder);    		
		createYearFolder("2016",outFolder);    		
		createYearFolder("2017",outFolder);    		
		createYearFolder("2018",outFolder);    		
		createYearFolder("2019",outFolder);    		
		createYearFolder("2020",outFolder);
	}





	// This method writes a DOM document to a file 
	private static void writeXmlFile(Document doc, String filename) {
		try {
			// Prepare the DOM document for writing
			Source source = new DOMSource(doc);

			// Prepare the output file
			File file = new File(filename);
			Result result = new StreamResult(file);

			//System.out.println("Serializing DOM to " + file.getAbsolutePath() + "...");

			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
		} catch (TransformerException e) {
		}
	}



}
