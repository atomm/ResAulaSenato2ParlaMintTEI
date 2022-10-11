package it.cnr.igsg.senato.resAula;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.unbescape.html.HtmlEscape;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.cnr.igsg.senato.Config;
import util.dom.UtilDom;

public class ResAulaParser {
	
	
	Set<String> proprietaryTagSet = new HashSet<String>();
	Set<String> htmlTagSet = new HashSet<String>();

	org.jsoup.safety.Whitelist whitelist = new Whitelist().relaxed();



	public void setResocontiAula(Set<ResAulaItem> resocontiAula) {
		this.resocontiAula = resocontiAula;
	}

	public Set<ResAulaItem> resocontiAula = new HashSet<ResAulaItem>();
	public HashMap<String, ResAulaItem> uniqueResocontiAula;

	
	public HashMap<String, ResAulaItem> getUniqueResocontiAula() {
		return uniqueResocontiAula;
	}



	public void setUniqueResocontiAula(HashMap<String, ResAulaItem> uniqueResocontiAula) {
		this.uniqueResocontiAula = uniqueResocontiAula;
	}


	public Set<ResAulaItem> getResocontiAula() {
		return resocontiAula;
	}
	
	

	
	private String normalizeSedutaHtml(String seduta) {
        
        if(seduta.startsWith("000"))
        	return seduta.substring(3);
        if(seduta.startsWith("00"))
        	return seduta.substring(2);
        if(seduta.startsWith("0"))
        	return seduta.substring(1);
        
      
		return seduta;
	}

	
	private String normalizeSedutaXML(String FRBRWorkId) {
        
		String seduta ="";
		if(FRBRWorkId!=null && FRBRWorkId.startsWith("http")) {
			seduta = FRBRWorkId.substring(FRBRWorkId.indexOf("osr")).split("/")[3];
		}
        
		return seduta;
	}
	
	private String normalizeDataXML(String FRBRWorkId) {
        
		String norm_data ="";
		if(FRBRWorkId!=null && FRBRWorkId.startsWith("http")) {
			String data = FRBRWorkId.substring(FRBRWorkId.indexOf("osr")).split("/")[2];
			norm_data = data;
			if(data.length()>0)
				norm_data = data.split("-")[0]+ data.split("-")[1]+data.split("-")[2];
		}
        
		return norm_data;
	}
	
	private String normalizeDataHtml(String data) {
		String norm_data = data.split("/")[2]+ data.split("/")[1]+data.split("/")[0];
		return norm_data;
	}
	
	private String getAnno(String normalizedData) {
		return normalizedData.substring(0,4);
	}

	

	
	// TODO
	// prendere solo <RESSTEN> .. </RESSTEN>
	// elimina <p> tutti, <b>, <br>
	// 
	//	if(content.indexOf("<RESSTEN>")==-1 || content.indexOf("</RESSTEN>")==-1) {
	//		System.out.println("CHECK UNESCAPED FILE:  "+doc.getAbsolutePath());
	//		System.out.println("\n\n UNESCAPED CONTENT "+content);
	//	}
	//content = org.jsoup.parser.Parser.unescapeEntities(content, true);
	//content = org.apache.commons.text.StringEscapeUtils.escapeHtml4(content);
	//
	public int extract_embedded_XML(File doc) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(doc.getAbsolutePath())));
			
				
			content = content.substring(content.indexOf("<RESSTEN>"),content.indexOf("</RESSTEN>")+10);
			content = HtmlEscape.unescapeHtml(content); 

			content = content.replaceAll("<p>","");
			content = content.replaceAll("<p align=\"justify\">","");
			content = content.replaceAll("<p align=\"center\">","");
			content = content.replaceAll("<p class=\"AFF\">","");
			content = content.replaceAll("<p class=\"testojustify1\">","");
			content = content.replaceAll("<p class=\"testocenter1\">","");
			content = content.replaceAll("<p class=\"santangelo\">","");
			content = content.replaceAll("<br clear=\"all\">","");
			content = content.replaceAll("<p align=\"right\">","");
			content = content.replaceAll("<p class=\"intestazione3\">","");
			content = content.replaceAll("<p class=\"Fixed\">","");
			content = content.replaceAll("<p class=\"Default\" align=\"center\">","");
			content = content.replaceAll("<img width=\"11\" height=\"266\" src=\"~DocToHTML_file/image001.gif\">","");
			content = content.replaceAll("<p class=\"testojustify\">","");
			content = content.replaceAll("<p class=\"Style7\">","");	
			content = content.replaceAll("<div align=\"center\">","");	

			
			content = content.replaceAll("<A ","<a ");	
			content = content.replaceAll("<em>","<i>");	
			content = content.replaceAll("</em>","</i>");	
			content = content.replaceAll("<strong>","");	
			content = content.replaceAll("</strong>","");	
			content = content.replaceAll("<u>","");	
			content = content.replaceAll("</u>","");	
			content = content.replaceAll("<ul>","");
			content = content.replaceAll("</ul>","");	
			content = content.replaceAll("<li>","");
			content = content.replaceAll("</li>","");	

			content = content.replaceAll("</p>","\n");
			content = content.replaceAll("</div>","\n");
			content = content.replaceAll("</A>","</a>");


			content = content.replaceAll("<b>","");
			content = content.replaceAll("</b>","");
			content = content.replaceAll("<br>","\n");
			content = content.replaceAll("</br>","");
			
			// accorpa annotazioni <i> consecutive
			content = content.replaceAll("</i><i>","");
			
			//content = content.replaceAll("</i>\\.","\\.</i>");

			
			
			// Get Rid of <sup>
			content = content.replaceAll("<sup>\n","<sup>");
			content = content.replaceAll("\n</sup>","</sup>");


			content = content.replaceAll("<sup>a</sup>","&#7491");
			content = content.replaceAll("<sup>a </sup>","&#7491");
			content = content.replaceAll("<sup>ma</sup>","&#7491");
			content = content.replaceAll("<sup>o</sup>","&#7506");
			content = content.replaceAll("<sup>°</sup>","°");
			content = content.replaceAll("<sup> </sup>","");
			content = content.replaceAll("<sup></sup>","");

			// Get Rid of <sup>
			content = content.replaceAll("<sub>2</sub>","&#8322");
			content = content.replaceAll("<sub>2,</sub>","&#8322");
			content = content.replaceAll("<sub></sub>","");
			
			content = content.replaceAll("<INTERVENTO IDPOLITICO=\"15346\" PROGR_PERS=\"3900\">","<INTERVENTO IDPOLITICO=\"23190\" PROGR_PERS=\"29110\">");
			
			
			// one more thing I saw now: you have some cases of NON-BREAKING HYPHEN (U+2011) in your sample, e.g. in "Nord-Est" 
			content = content.replaceAll("&#8209","-");

			
			
			content = HtmlEscape.unescapeHtml(content); 
		
			// è stato lasciato perchè nelle url ci vuole; nel testo no
			content = content.replaceAll("&","&amp;");
			


			String path = Config.RES_AULA_EMBEDDED_XML+"/"+doc.getName().replace(".htm", ".xml");
			Files.write( Paths.get(path), content.getBytes());


			return testParseXML(path);

		}catch(Exception e) {
			e.printStackTrace();
			return -1;
		}

	}
	
	
	private String getEmbeddedXMLasTXT(String path) {
		org.w3c.dom.Document doc = null;

		try {
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setExpandEntityReferences(false);
			DocumentBuilder domBuilder = dbf.newDocumentBuilder();
			
			File file = new File(path);
			doc = domBuilder.parse(file);
			
			
			
			String plainText = getRecursiveTextNode(doc.getDocumentElement()).trim();

			

			return plainText;
			
			//System.out.println("VALID PARSING "+path);


		} catch (Exception ex) {
			return null;
			//System.err.println("ERROR PARSING "+path);
			//ex.printStackTrace();
		}

	}
	
	
	private String getRecursiveTextNode(Node node) {
		String retVal = "";

		if (node.getNodeType() == Node.TEXT_NODE)
			retVal = node.getNodeValue().trim()/*.replaceAll("\\s+", " ")+"\n"*/;
		

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				retVal += " " + getRecursiveTextNode(list.item(i));
			}
		}

		return retVal;
	}
	
	
	private int testParseXML(String path) {
		org.w3c.dom.Document doc = null;

		try {
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setExpandEntityReferences(false);
			DocumentBuilder domBuilder = dbf.newDocumentBuilder();
			
			File file = new File(path);
			doc = domBuilder.parse(file);
			
			
			
			String plainText = UtilDom.getRecursiveTextNode(doc.getDocumentElement()).trim();
			int tokenNum = new StringTokenizer(plainText, " ").countTokens();

		
			return tokenNum;
			//System.out.println("VALID PARSING "+path);


		} catch (Exception ex) {
			return -1;
			//System.err.println("ERROR PARSING "+path);
			//ex.printStackTrace();
		}

	}
	
	
	
	
	
	public boolean parse(File file, ResAulaItem resoconto) {
		
		String plainText= "";
		String normalizedSeduta = "";
		String data ="";
		String anno = "";
		
		/////////////////////
		//
		// PARSE HTML
		//
		/////////////////////
		if(resoconto.getExtension().equals("htm")) {
	
			//<HTML><DOC TIPO="RESAULA"  LEG="18" SED="0154" DATA="10/10/2019" TIPORES="2" IDTESTO="01124360"><HEAD>	
			
			//System.err.println("PARSING: .."+file.getAbsolutePath() );
			
			try {
				org.jsoup.nodes.Document JsoupDoc = Jsoup.parse(file,"UTF-8");
				
				plainText= JsoupDoc.text();
				
				org.jsoup.nodes.Element docTag = JsoupDoc.getElementsByTag("DOC").first();
				String seduta =docTag.attr("SED");
				normalizedSeduta = normalizeSedutaHtml(seduta);
				data = normalizeDataHtml(docTag.attr("DATA"));
				String tipoResoconto = docTag.attr("TIPORES");
				anno = getAnno(data);
				
			
		
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
			
			
		/////////////////////
		//
		// PARSE XML
		//
		/////////////////////
		}else if(resoconto.getExtension().equals("akn")) {
			org.w3c.dom.Document doc = null;

			try {
				
				// <an:FRBRWork>
	            //   <an:FRBRthis value="http://dati.senato.it/osr/RESAULA/2019-10-09/153/main"/>

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder domBuilder = dbf.newDocumentBuilder();
				doc = domBuilder.parse(file);
				plainText = UtilDom.getRecursiveTextNode(doc.getDocumentElement()).trim();
				org.w3c.dom.Node FRBRWork= doc.getElementsByTagName("an:FRBRWork").item(0);
				if(FRBRWork!=null) {
					org.w3c.dom.Node FRBRThis = UtilDom.getElementsByTagName(doc, FRBRWork, "an:FRBRthis")[0];
					String FRBRWorkId = UtilDom.getAttributeValueAsString(FRBRThis, "value");
					normalizedSeduta = normalizeSedutaXML(FRBRWorkId);
					data = normalizeDataXML(FRBRWorkId);
					anno = getAnno(data);
					
				}else {
					System.err.println("FRBRWork is NULL");
				}
				

			} catch (Exception ex) {
				System.err.println("ERROR PARSING "+file.getName());
				//ex.printStackTrace();
				//return false;
			}

		}else {
			// DO  NOT PARSE XML
		}
		
		
		
		int tokenNum = new StringTokenizer(plainText, " ").countTokens();
		resoconto.setPlainText(plainText);
		resoconto.setTokenNum(tokenNum);
		resoconto.setAnno(anno);
		resoconto.setSeduta(normalizedSeduta);
		resoconto.setData(data);
		resoconto.setCorpus(flagCorpusforItem(resoconto));
		
		
		resocontiAula.add(resoconto);
		
		return true;
	}
	
	
	
		
	private static String flagCorpusforItem(ResAulaItem RA){


		String corpusFlag = "";

		Date startReferenceDate = null;
		Date endReferenceDate = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			startReferenceDate = sdf.parse("20130101");
			endReferenceDate = sdf.parse("20191031");
		}catch(Exception ex) {
			ex.printStackTrace();
		}


		String date = RA.getData();

		if(date.length()>0) {
			try {
				Date currentDate = sdf.parse(date);

				if (currentDate.compareTo(startReferenceDate) < 0) {
					corpusFlag ="EXTENDED_REFERENCE";
				} else if (currentDate.compareTo(startReferenceDate) >= 0 && currentDate.compareTo(endReferenceDate) <= 0) {
					corpusFlag = "REFERENCE";
				} else if (currentDate.compareTo(endReferenceDate) > 0) {
					corpusFlag = "COVID";
				}


			}catch(Exception ex) {
				System.err.println("[ERROR] READING DATE IN flagCorpusforItem()");
			}
		}else {
			System.err.println("[ERROR] READING DATE IN flagCorpusforItem()");
			//System.err.println("empty date for "+RA.getDocId()+"."+RA.getExtension());
		}

		return corpusFlag;
	}
		
	
	
	
	public void countUNIQUETABLEtokens() {

		int covid_tokens=0;
		int reference_tokens =0;



		for(ResAulaItem RA : uniqueResocontiAula.values()) {
			
			if(RA.getCorpus().equalsIgnoreCase("covid")) {
				covid_tokens+=RA.getTokenNum();
				
			}else if (RA.getCorpus().equalsIgnoreCase("reference")){
				reference_tokens+=RA.getTokenNum();
			}
			
		}
		
	
		System.out.println("\n COVID_TOKENS " + covid_tokens);
		System.out.println("\n REFERENCE_TOKENS " + reference_tokens);


	}

	
	public void printUNIQUETABLEtoFile() {
		
		BufferedWriter bufferedWriter = null;
		File csv = new File(Config.UNIQUE_GRID_CSV_PATH);
		//Config.CSV_OUT+"unique_grid.csv"
		

		try {

			bufferedWriter = new BufferedWriter(new FileWriter(csv));

			bufferedWriter.newLine();
			
			for(ResAulaItem RA : uniqueResocontiAula.values()) {
				
				bufferedWriter.write(RA.toCsv());
				
				bufferedWriter.newLine();
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			//Close the BufferedWriter
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}		
	}
	
	public void printCORPUSTABLEtoFile() {
		
		BufferedWriter bufferedWriter = null;
		File csv = new File(Config.CSV_OUT+"corpus_grid.csv");


		try {

			bufferedWriter = new BufferedWriter(new FileWriter(csv));
			
			bufferedWriter.newLine();
			
			for(ResAulaItem RA :getResocontiAula() ) {

				bufferedWriter.write(RA.toCsv());

				bufferedWriter.newLine();

			}
			
			
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			//Close the BufferedWriter
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}		
	}
	
	
	public void printEMBEDDED_TXT_toFile() {

		BufferedWriter bufferedWriter = null;

		for(ResAulaItem RA : uniqueResocontiAula.values()) {
			try {

				// SCORRI GLI EMBEDDED XML
				String EmbeddedXMLPath = Config.RES_AULA_EMBEDDED_XML+"/"+RA.getDocId()+ ".xml";
				// SOLO PER QUELLI VALIDI..
				if(testParseXML(EmbeddedXMLPath)!=-1) {

					// PRENDI IL PLAIN TEXT
					String text = getEmbeddedXMLasTXT(EmbeddedXMLPath);

					File txt = new File(Config.TXT_OUT+"/"+RA.getCorpus()+"/"+RA.getDocId()+".txt");
					bufferedWriter = new BufferedWriter(new FileWriter(txt));


					
					bufferedWriter.write(text);
				}
			} catch (Exception e) {
				e.printStackTrace();

			} finally {
				//Close the BufferedWriter
				try {
					if (bufferedWriter != null) {
						bufferedWriter.flush();
						bufferedWriter.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}		

		}
	}
	
	public void printUNIQUETXTtoFile() {

		BufferedWriter bufferedWriter = null;

		for(ResAulaItem RA : uniqueResocontiAula.values()) {
			try {
				File txt = new File(Config.TXT_OUT+RA.getDocId()+".txt");
				bufferedWriter = new BufferedWriter(new FileWriter(txt));
				bufferedWriter.write(RA.getPlainText());
			} catch (Exception e) {
				e.printStackTrace();

			} finally {
				//Close the BufferedWriter
				try {
					if (bufferedWriter != null) {
						bufferedWriter.flush();
						bufferedWriter.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}		

		}
	}
	
	public void printProprietaryTagSet() {
		System.err.println(" ****** HTML IN-VALID TAGS *******");
		for(String tag:proprietaryTagSet) {
			System.err.println(tag);
		}
	}

	
	public void printHtmlTagSet() {
		System.err.println(" ****** HTML VALID TAGS *******");
		for(String tag:htmlTagSet) {
			System.err.println(tag);
		}
	}

	
}
