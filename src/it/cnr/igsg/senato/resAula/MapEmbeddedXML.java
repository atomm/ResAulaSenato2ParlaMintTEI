package it.cnr.igsg.senato.resAula;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.cnr.igsg.senato.datiSenato.DatiSenato;
import util.dom.UtilDom;

public class MapEmbeddedXML {
	
	int uId = 1;
	int segId = 1;

	
	DatiSenato datiSenato;
	
	HashMap<String,Integer> totalTags  = new HashMap<String,Integer>();
	HashMap<String,Integer> totalExtent  = new HashMap<String,Integer>();

	ArrayList<Node> storePresidenza = null;

	public void setDatiSenato(DatiSenato datiSenato) {
		this.datiSenato = datiSenato;
	}


	String TEIid ="";
	
	public void setTEIid(String tEIid) {
		TEIid = tEIid;
	}

	public void resetUtteranceId(){
		uId =1;
	}
	
	public void resetSegId(){
		segId =1;
	}
	
	public int testParseXML(String path) {
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
			//return false;
		}

	}
	
	
	public HashMap<String, Integer> getTotalTags() {
		return totalTags;
	}
	
	public HashMap<String, Integer> getTotalExtent() {
		return totalExtent;
	}
	
	private String cleanUrl(String url) {
		
		String linkPrefix ="http://www.senato.it/japp/bgt/showdoc/frame.jsp?";

		url = url.substring(url.indexOf("?")+1);
		
		if(url.toLowerCase().indexOf("sindisp")!=-1) {
			System.err.println("found sindisp "+url);
			return linkPrefix+url;
		}
		
		
		if(url.toLowerCase().indexOf("ddliter")!=-1) {
			
			//http://www.senato.it/leg/17/BGT/Schede/Ddliter/41436.htm
			
			String leg = url.substring(url.indexOf("leg")+4,url.indexOf("leg")+6);
			String id = url.substring(url.indexOf("id")+3,url.length());
			
			return "http://www.senato.it/leg/"+leg+"/BGT/Schede/Ddliter/"+id+".htm";
		}
		
		if(url.toLowerCase().indexOf("sdocnl")!=-1) {
			
			//leg=17&tipodoc=sdocnl&id=27788
			String leg = url.substring(url.indexOf("leg")+4,url.indexOf("leg")+6);
			String id = url.substring(url.indexOf("id")+3,url.length());
			
			url = "http://www.senato.it/leg/"+leg+"/BGT/Schede/docnonleg/"+id+".htm";

			return url; 

		}
		

		
		return url;
	}


	
	
	
	private ArrayList<Node> processPresidenza(Node presidenza, Document target) {
		
		// SOURCE

//				<PRESIDENZA IDPOLITICO="24525" PROGR_PERS="25411"
//						ORARIO="13,05">
//						<a target="_blank"
//							href="/loc/link.asp?leg=18&amp;tipodoc=sanasen&amp;id=25411">
		//
//							Presidenza del vice presidente ROSSOMANDO
//						</a>
//				</PRESIDENZA>
//				(ore 13,05)
		//
		// TARGET
		//

				ArrayList<Node> list = new ArrayList<Node>();
				
				String orario = UtilDom.getAttributeValueAsString(presidenza, "ORARIO");
				if(orario == null)
					orario = "";

				
				Element note = target.createElement("note");
				note.setAttribute("type", "president");
				String text = cleanText(UtilDom.getRecursiveTextNode(presidenza));
				if(text.trim().length()>0)
					note.setTextContent(text);
								

				
				Element noteOrario = null;
				if(orario.trim().length()>0) {
					noteOrario = target.createElement("note");
					noteOrario.setAttribute("type", "time");
					noteOrario.setTextContent(orario);
				}
				
				list.add(note);
				if(noteOrario!=null)
					list.add(noteOrario);

			
				return list;
				
			}

	
	private String cleanText(String text) {
		if(text!=null) {
			//System.err.println("CLEANING  "+TEIid);
			text = text.trim().replaceAll("\\s+", " ");
			if(text.startsWith(".") && text.trim().length()>2) // leva punti inziali solo se continua con una frase. Se è solo un punto lascialo
				text=text.substring(1).trim();
			
			
			return text.trim();
		}
		return "";
	}
	
	private String cleanTextKeepPunto(String text) {
		if(text!=null) {
			//System.err.println("CLEANING KEEP "+TEIid);
			text = text.trim().replaceAll("\\s+", " ");

			return text.trim();
		}
		return "";
	}
	
	private Element getTrattazioneHead(Node trattazione, Document target) {
		
		Element trattazioneHead = null;
		String trattazioneText = UtilDom.getText(trattazione);

		if(trattazioneText!=null && trattazioneText.trim().length()>0) {
			// WARN: head non è ammesso, uso <note>
			trattazioneHead=target.createElement("note");
			trattazioneHead.setTextContent(cleanText(trattazioneText));		
		}
		
		return trattazioneHead;
	}
	
	private Element getTrattazioneNote(Node trattazione, Document target) {
		
		//		TRATTAZIONE - TIPO
		//		A	538	ESAME DEGLI ARTICOLI
		//		D	274	DISCUSSIONE
		//		F	445	VOTAZIONE FINALE
		//		G	463	DISCUSSIONE GENERALE
		//		N	27  proposta di non passaggio all'esame degli articoli, ai sensi dell'articolo 96 del Regolamento
		//		P	83	QUESTIONI PREGIUDIZIALI
		//		Q	14  QUESTIONE PREGIUDIZIALI E SOSPENSIVE
		//		S	14	QUESTIONE SOSPENSIVA
		//		T	14	VOTAZIONE DEGLI ARTICOLI
		//		U	67	DISCUSSIONE SULLA QUESTIONE DI FIDUCIA
		//		V	304	VOTAZIONE

		String trattazioneCode = UtilDom.getAttributeValueAsString(trattazione, "TIPO");

		String trattazioneTipo = "";
		String trattazioneTipoType = "";


		switch(trattazioneCode) {
		case "A": 
			trattazioneTipo = "ESAME DEGLI ARTICOLI";
			trattazioneTipoType ="articlesExam";
			break;
		case "D": 
			trattazioneTipo = "DISCUSSIONE";
			trattazioneTipoType ="discussion";

			break;	
		case "F": 
			trattazioneTipo = "VOTAZIONE FINALE";
			trattazioneTipoType ="finalVoting";
			break;
		case "G": 
			trattazioneTipo = "DISCUSSIONE GENERALE";
			trattazioneTipoType ="generalDiscussion";
			break;
		case "N": 
			trattazioneTipo = "proposta di non passaggio all'esame degli articoli, ai sensi dell'articolo 96 del Regolamento";
			trattazioneTipoType ="notPassingPoposal";

			break;
		case "P": 
			trattazioneTipo = "QUESTIONI PREGIUDIZIALI";
			trattazioneTipoType ="preliminaryExceptions";

			break;
		case "Q": 
			trattazioneTipo = "QUESTIONE PREGIUDIZIALI E SOSPENSIVE";
			trattazioneTipoType ="preliminarySuspensiveException";

			break;
		case "S": 
			trattazioneTipo = "QUESTIONE SOSPENSIVA";
			trattazioneTipoType ="suspensiveExceptions";
			break;
		case "T": 
			trattazioneTipo = "VOTAZIONE DEGLI ARTICOLI";
			trattazioneTipoType ="articlesVoting";
			break;
		case "U": 
			trattazioneTipo = "DISCUSSIONE SULLA QUESTIONE DI FIDUCIA";
			trattazioneTipoType ="voteOfConfidence";
			break;
		case "V": 
			trattazioneTipo = "VOTAZIONE";
			trattazioneTipoType ="voting";
			break;
		default: trattazioneTipo = "TRATTAZIONE";
				trattazioneTipoType ="treatment";
		}


		Element trattazioneNote = target.createElement("note");
		trattazioneNote.setAttribute("type", trattazioneTipoType);
		trattazioneNote.setTextContent(trattazioneTipo);
		
		return trattazioneNote;
	}
	
	private Document processTrattazione(Node trattazione, Document target, Element debatesection) {

		Element trattazioneHead = getTrattazioneHead(trattazione, target);
		Element trattazioneNote = getTrattazioneNote(trattazione, target);


		if(trattazioneHead!=null)
			debatesection.appendChild(trattazioneHead);
		debatesection.appendChild(trattazioneNote);

		return target;
	}
	
	
	
	private Document processIntervento(Node intervento, Document target, Element debatesection) {
		
		String idSpeaker = UtilDom.getAttributeValueAsString(intervento, "PROGR_PERS");
		String gruppo = UtilDom.getAttributeValueAsString(intervento, "GRUPPO");
		Element utterance = target.createElement("u");
		Element oratoreNote = null;
		Element qualificaNote = null; 
		Element trattazioneHead = null;
		Element trattazioneNote = null;

		
		String speakerPrefix = datiSenato.getSpeakerPrefix(idSpeaker);

		utterance.setAttribute("who", "#"+datiSenato.getIdSenatore2Readable().get(idSpeaker)/*speakerPrefix+"."+idSpeaker*/);
		String uttId = TEIid+".u"+uId;
		utterance.setAttribute("xml:id",uttId);
		uId+=1;
		String sourceRoleText ="";
		String normalizedRole ="regular"; // default role is regular (for members) ..
		
		NodeList nl = intervento.getChildNodes();
		Node lastInsertedNode = null;
		
		ArrayList<Node> presidenzaChange =null;
		
		for(int i=0;i<nl.getLength();i++) {
			Node currentNode = nl.item(i);
			
			// è corretto se per ogni intervento c'è un solo nodo ORATORE e QUALIFICA 
			if(currentNode.getNodeName().equalsIgnoreCase("oratore")) {
				String oratoreText = UtilDom.getText(currentNode);
				if(oratoreText!=null && oratoreText.trim().length()>0) {
					oratoreNote = target.createElement("note");
					oratoreNote.setAttribute("type", "speaker");
					if(gruppo!=null && gruppo.trim().length()>0) {
						oratoreText = cleanText(oratoreText+" "+gruppo);
					}
					oratoreNote.setTextContent(oratoreText);
				}
			}else if(currentNode.getNodeName().equalsIgnoreCase("qualifica")) {
				String qualificaText = UtilDom.getText(currentNode);
				if(qualificaText!=null && qualificaText.trim().length()>0) {
					qualificaNote = target.createElement("note");
					qualificaNote.setAttribute("type", "role");
					sourceRoleText = qualificaText;
					normalizedRole = normalizeRoleXSD(sourceRoleText,speakerPrefix);
					qualificaNote.setTextContent(sourceRoleText);
				}
//			      <note type="speaker">FRANCESCHINI</note>
//			      <note type="role">ministro per i beni e le attività culturali e per il turismo</note>
				// FIXME può andare una nota di tipo role?	
			}
			// FIX: questo è un fix per il caso in cui il Nodo trattazione sia figlio e non fratello di INTERVENTO
			// In questo caso metto la nota relativa alla trattazione *in coda* all'intervento. 
			// Infatti, di solito, in questo caso, il nodo trattazione è l'ultimo figlio di INTERVENTO. ma ci potrebbe essere altro testo dopo. 
			// L'effetto collaterale è che il testo della trattazione potrebbe essere spostato in coda all'intervento (come nota)
			else if(currentNode.getNodeName().equalsIgnoreCase("trattazione")) {
				trattazioneHead = getTrattazioneHead(currentNode, target);
				trattazioneNote = getTrattazioneNote(currentNode, target);
			}
			// #text
			else if(UtilDom.isTextNode(currentNode)) {
				if(currentNode.getTextContent().trim().length()>0) {
					String content = currentNode.getTextContent().trim();
						
					String[] paragraphs = content.split("\\.\n");
					for(int count = 0; count < paragraphs.length; count ++) {
						if(paragraphs[count].trim().length()>0) {
							
							String textContent = cleanText(paragraphs[count]);
							
							if(count<paragraphs.length-1)
								textContent = textContent+".";
							
							Node nodeToInsert;
							if(textContent.startsWith("PRESIDENTE") && textContent.toLowerCase().indexOf("ne ha facoltà")!=-1) {
								Element note = target.createElement("note");
								note.setTextContent(textContent);
								nodeToInsert = note;
								//System.err.println("SKIP: "+textContent);
							}else if(textContent.startsWith("Domando di parlare") ) {
								//System.err.println("SKIP: "+textContent);
								Element note = target.createElement("note");
								note.setTextContent(textContent);
								nodeToInsert = note;
							}else {
								Element seg = target.createElement("seg");
								seg.setAttribute("xml:id", TEIid+".seg"+segId);
								segId+=1;
								seg.setTextContent(textContent);
								nodeToInsert = seg;
							}
							if(textContent.trim().length()>0 /*&& !isPunto(textContent)*/) {
								utterance.appendChild(nodeToInsert);
								lastInsertedNode = nodeToInsert;
							}
						}
					}
				}
			}
			// <i>
			else if(currentNode.getNodeName().equalsIgnoreCase("i")) {
				
				String textContent = cleanText(currentNode.getTextContent());
				String incidentType = null;
				
				if((incidentType=isIncidents(textContent))!=null) {
					
					String incidentAttribute ="";
					if(incidentType.equalsIgnoreCase("vocal"))
						incidentAttribute = getVocal(textContent);
					else if(incidentType.equalsIgnoreCase("kinesic"))
						incidentAttribute = getKinesic(textContent);
					else if(incidentType.equalsIgnoreCase("incident"))
						incidentAttribute = getIncident(textContent);
						
					Element incident = target.createElement(incidentType);
					incident.setAttribute("type", incidentAttribute);
					Element desc = target.createElement("desc");

					String kinesicContent = cleanText(currentNode.getTextContent());
			
					// ci sono nodi successivi di tipo <i> ? (anche non kinesic?) -> accorpali 
					// 	
					//	<i>(Applausi dai Gruppi L-SP-PSd'Az</i>
					//	<i>e</i>
					//	<i>FIBP-UDC)</i>
					
					// FIXME si mangia il punto dopo aver disannotato un <i> 
					// es. 01128261.xml
					do {
						if(i<nl.getLength()-1) {
							Node nextNode = nl.item(i+1);

							if(nextNode == null)
								System.out.println(TEIid+ " nextNode is NULL index = "+(i+1)+" length "+nl.getLength());

							// include <i> successivi solo se consecutivi
							if(nextNode.getNodeName().equalsIgnoreCase("i") 
									// era && cleanText(nextNode.getTextContent()).trim().length()==0
									|| (UtilDom.isTextNode(nextNode) && nextNode.getTextContent().trim().length()==0)
									){
								kinesicContent =(kinesicContent+" "+cleanText(nextNode.getTextContent())).trim();
								i++;
							}else if(nextNode.getNodeName().equalsIgnoreCase("a")){
								kinesicContent =(kinesicContent+" "+cleanText(UtilDom.getText(nextNode))).trim();
								i++;
							}
							else{
								break;
							}
						}
					}while(i<nl.getLength()-1);
					
					String cleanedIncident = cleanIncident(cleanText(kinesicContent));
					//System.out.println("FIX_KINESIC\t"+cleanedIncident);
					
					desc.setTextContent(cleanedIncident);
					incident.appendChild(desc);
					utterance.appendChild(incident);
					lastInsertedNode = incident;
					
					
				}else {  // merge textNode - remove <i> annotation
					if(lastInsertedNode!=null) {
						String prevContent = lastInsertedNode.getTextContent();
						//System.err.println("merging content with previous node "+lastInsertedNode.getNodeName()+" prev: "+prevContent+" new: "+textContent);
						lastInsertedNode.setTextContent((prevContent+" "+textContent).trim());
						// se tolgo l'annotazione <i> devo concatenare anche il nodo successivo
						if(i<nl.getLength()-1) {
							Node nextNode = nl.item(i+1);
							if(nextNode!=null && UtilDom.isTextNode(nextNode)) {
								String nextContent = cleanTextKeepPunto(nextNode.getTextContent());
								String space =" ";
								if(nextContent.startsWith(".")||nextContent.startsWith(",")||nextContent.startsWith(";"))
									space ="";
								// seg trail 
								lastInsertedNode.setTextContent((lastInsertedNode.getTextContent()+space+nextContent).trim());
								i=i+1;
							}else {
								
								//<i>(</i>
								//<a target="_blank" href="/uri-res/N2Ls?urn:nir:senato.repubblica;assemblea:ordine.giorno:18.legislatura;241">
									//<i>Vedi ordine del giorno</i>
								// </a>
								//<i>)</i>
								
								// LOOSING NEXT NODE ? - pare di no
								// System.err.println(TEIid+" A - process Intervento <i> - losing.. nextNode "+nextNode.getNodeName()+" - "+UtilDom.getText(nextNode));
							}
						}else {
							// LOOSING NEXT NODE ? - pare di no
							//System.err.println(TEIid+" B - process Intervento <i> - losing.. nextNode? Last inserted: "+UtilDom.getText(lastInsertedNode));
						}
					// il nodo <i> è il primo figlio dell'intervento
					}else {
						//System.err.println("losing <i> .. (is First node - GRUPPO) "+textContent);
					}

	
				}
			}
			// <a>
			else if(currentNode.getNodeName().equalsIgnoreCase("a")) {

				String textContent = cleanText(UtilDom.getText(currentNode));
				// merge textNode - remove <i> annotation
				if(lastInsertedNode!=null) {
					String prevContent = lastInsertedNode.getTextContent();
					lastInsertedNode.setTextContent(prevContent+" "+textContent);
					// se tolgo l'annotazione <i> devo concatenare anche il nodo successivo
					if(i<nl.getLength()-1) {
						Node nextNode = nl.item(i+1);
						if(nextNode!=null && UtilDom.isTextNode(nextNode)) {
							lastInsertedNode.setTextContent((lastInsertedNode.getTextContent()+" "+cleanText(nextNode.getTextContent())).trim());
							i=i+1;
						}else if(nextNode!=null && nextNode.getNodeName().equalsIgnoreCase("i")) {
							//lastInsertedNode.setTextContent(lastInsertedNode.getTextContent()+" "+cleanText(UtilDom.getText(nextNode)));
							//i=i+1;
						}else {
							// System.err.println(TEIid+" A - process Intervento <i> - losing.. nextNode "+nextNode.getNodeName()+" - "+UtilDom.getText(nextNode));

							System.err.println(TEIid+" A - process Intervento <a> - losing.. nextNode "+nextNode.getNodeName()+" - "+UtilDom.getText(nextNode));
						}
					}else {
						System.err.println("B - process Intervento <a> - losing.. nextNode");
					}
					// il nodo <i> è il primo figlio dell'intervento
				}else {
					System.err.println("losing <a> .. (is First node) "+textContent);
				}

			}
			// <PRESIDENZA>
			else if(currentNode.getNodeName().equalsIgnoreCase("presidenza")) {
				presidenzaChange = processPresidenza(currentNode, target);
				//	System.out.println("Presidenza in the middle");
			// <TABLE>
			}
			else if(currentNode.getNodeName().equalsIgnoreCase("table")) {
				Node tableNote = processTable(currentNode, target);
				if(tableNote!=null) {
					utterance.appendChild(tableNote);
					lastInsertedNode = tableNote;
				}
			}
			else {
				System.err.println("UNRECOGNIZED NODE IN INTERVENTO.."+currentNode.getNodeName());
			}
		}
		
		utterance.setAttribute("ana", "#"+normalizedRole);

		
		if(oratoreNote!=null)
			debatesection.appendChild(oratoreNote);
		if(qualificaNote !=null)
			debatesection.appendChild(qualificaNote);

		
		debatesection.appendChild(utterance);

		// INTERMEZZO FRA INTERVENTI <u> CON NOTA SU NUOVA PRESIDENZA
		// LA NOTA SU NUOVA PRESIDENZA VIENE MESSA IN CODA ALL'Utterance
		// FIXME - non è un granchè - bisognerebbe spezzare il div - debateSection e ricominciarne uno nuovo con nuova Presidenza 
		if(presidenzaChange!=null) {	
			//System.err.println("presidenzaNotes LENGTH "+presidenzaChange.size());
			for(Node presidenzaNote:presidenzaChange) {
				//System.err.println("appending presidenzaNote "+UtilDom.getText(presidenzaNote));
				debatesection.appendChild(presidenzaNote);
			}
		}
		
		debatesection = postprocessDebateSection(debatesection);

		return target;
	}
	
	
	
	
	private String cleanIncident(String kinesicContent) {
		
		kinesicContent = kinesicContent.replaceAll("\\) \\(","\\. ");
		kinesicContent = kinesicContent.replaceAll("\\)\\.\\(","\\. ");
		kinesicContent = kinesicContent.replaceAll("\\)\\. \\(","\\. ");

		if(kinesicContent.startsWith("("))
			kinesicContent = kinesicContent.substring(1);
		if(kinesicContent.startsWith(".("))
			kinesicContent = kinesicContent.substring(2);
		if(kinesicContent.endsWith(")."))
			kinesicContent = kinesicContent.substring(0,kinesicContent.length()-2)+".";
		if(kinesicContent.endsWith(");"))
			kinesicContent = kinesicContent.substring(0,kinesicContent.length()-2)+".";
		if(kinesicContent.endsWith(")..."))
			kinesicContent = kinesicContent.substring(0,kinesicContent.length()-4);
		
		if(kinesicContent.contains(").") && ! kinesicContent.contains("("))
			kinesicContent = kinesicContent.replaceAll("\\)\\.","");

		if(kinesicContent.contains(")("))
			kinesicContent = kinesicContent.replaceAll("\\)\\("," ");

		if(kinesicContent.endsWith(")"))
			kinesicContent = kinesicContent.substring(0,kinesicContent.length()-1);
		return kinesicContent.trim();
	}
	
	
	
	private Element postprocessDebateSection(Element debatesection) {
		
		NodeList nl = debatesection.getChildNodes();
		
		String oratoreText = null;
		for(int i=0;i<nl.getLength();i++) {
			Node currentNode = nl.item(i);
			
			if(currentNode.getNodeName().equalsIgnoreCase("note")) {
				String noteType = UtilDom.getAttributeValueAsString(currentNode, "type"); 
				if(noteType!=null && noteType.equals("speaker"))
					oratoreText = UtilDom.getText(currentNode).trim();
			}else if(currentNode.getNodeName().equalsIgnoreCase("u")) {
				
				NodeList uttChildren = currentNode.getChildNodes();
				ArrayList<Node> removeList = new ArrayList<Node>();
				for(int j=0;j<uttChildren.getLength();j++) {
					Node currUttChild = uttChildren.item(j);
					if(currUttChild.getNodeName().equalsIgnoreCase("seg")) {
						
						
						String textContent = currUttChild.getTextContent();
						
						if(oratoreText!=null && oratoreText.trim().length()>0) {
							if(oratoreText.startsWith("*"))
								oratoreText=oratoreText.substring(1);
							if(textContent.startsWith(oratoreText)) {
								textContent=textContent.substring(oratoreText.length()).trim();
								if(textContent.startsWith("."))
									textContent=textContent.substring(1).trim();
								currUttChild.setTextContent(textContent);
								oratoreText = null;
							}
						}
						
						// POST PROCESS - rimozione punti, virgole, ).
						if(textContent.equals(".") || textContent.equals(")") || textContent.length()<4) {							
							// per ora lo rimuovo brutalmente
							removeList.add(currUttChild);
							//System.out.println("LOSING\t"+textContent+ "\t"+TEIid+ " "+UtilDom.getAttributeValueAsString(currUttChild, "xml:id"));
							
						}
						
					}
				}
				// lista dei nodi da rimuovere - li rimuovo alla fine
				// FIXME è testo che perdo, lo dovrei appiccicare se possibile al nodo precedente
				if(removeList.size()>0) {
					for(Node remove:removeList) {
						String pasteText = remove.getTextContent();
						Node previous = remove.getPreviousSibling();
						if(previous!=null) {
							if(previous.getNodeName().equalsIgnoreCase("kinesic") || previous.getNodeName().equalsIgnoreCase("incident") || previous.getNodeName().equalsIgnoreCase("vocal")) {
								previous.getFirstChild().setTextContent(previous.getFirstChild().getTextContent()+pasteText);
							}	
						}
						currentNode.removeChild(remove);
					}
				}
				
			}else {
				
			}	
			
		}
		
		return debatesection;
	}
	
	
	private Node processTable(Node table, Document target) {
		String tableContent = UtilDom.getText(table);

		if(tableContent!=null && tableContent.trim().length()>0) {
			Node tableNote = target.createElement("note");

			String tableType ="summary";

			if(tableContent.startsWith("Senatori")||tableContent.startsWith("Favorevoli")||tableContent.startsWith("Schede")||tableContent.startsWith("Voti"))
				tableType ="voting";
			UtilDom.setAttributeValue(tableNote, "type", tableType);
			tableNote.setTextContent(tableContent.trim());


			return tableNote;
		}
		return null;
	}

	
//	<DOCUMENTO NUMDOC="Doc. VIII, n. 1">
//		<NUMEROATTO><a target="_blank" href="/loc/link.asp?leg=18&amp;tipodoc=sdocnl&amp;id=37465">(<i>Doc</i>. VIII, n. 1)</a></NUMEROATTO> 
//		<TITOLOATTO><i>Rendiconto delle entrate e delle spese del Senato per l'anno finanziario 2017</i></TITOLOATTO> 
//	</DOCUMENTO>
//	
//	<DOCUMENTO NUMDOC="Doc. VIII, n. 2">
//		<NUMEROATTO><a target="_blank" href="/loc/link.asp?leg=18&amp;tipodoc=sdocnl&amp;id=37466">(<i>Doc</i>. VIII, n. 2)</a></NUMEROATTO> 
//		<TITOLOATTO><i>Progetto di bilancio interno del Senato per l'anno finanziario 2018</i></TITOLOATTO>
//		<NOTEATTO> <i>(Relazione orale)</i></NOTEATTO>
//	</DOCUMENTO>
	
	
//	<DOCUMENTO NUMDOC="Doc. VIII, n. 1">
//		<NUMEROATTO><a target="_blank" href="/loc/link.asp?leg=18&amp;tipodoc=sdocnl&amp;id=37465">documenti VIII, nn. 1 </a>
//		</NUMEROATTO>
//	</DOCUMENTO>
//	<DOCUMENTO NUMDOC="Doc. VIII, n. 2">
//		<NUMEROATTO><a target="_blank" href="/loc/link.asp?leg=18&amp;tipodoc=sdocnl&amp;id=37466">e 2</a>
//		</NUMEROATTO> 
//	</DOCUMENTO>
//	(ore 16,28)
	
	private Node processDocumento(Node documento, Document target) {
		String documentoContent = UtilDom.getText(documento);

		if(documentoContent!=null && documentoContent.trim().length()>0) {
			Node documentoNote = target.createElement("note");

			String documentoNoteType ="document";

			UtilDom.setAttributeValue(documentoNote, "type", documentoNoteType);
			documentoNote.setTextContent(documentoContent);

			return documentoNote;
		}
		return null;
	}
	
	private String isIncidents(String textContent) {

		textContent = textContent.toLowerCase();
		
		if(getKinesic(textContent)!=null)
			return "kinesic";
		else if(getVocal(textContent)!=null)
			return "vocal";
		else if(getIncident(textContent)!=null)
			return "incident";
		else return null;
					
	}
	
	private String getKinesic(String textContent) {
		
//		  <xs:element name="kinesic" substitutionGroup="ns1:comment">
//		    <xs:complexType>
//		      <xs:sequence>
//		        <xs:element ref="ns1:desc"/>
//		      </xs:sequence>
//		      <xs:attribute name="type">
//		        <xs:simpleType>
//		          <xs:restriction base="xs:token">
//		            <xs:enumeration value="applause"/>
//		            <xs:enumeration value="ringing"/>
//		            <xs:enumeration value="signal"/>
//		            <xs:enumeration value="kinesic"/>
//		            <xs:enumeration value="playback"/>
//		            <xs:enumeration value="gesture"/>
//		            <xs:enumeration value="laughter"/>
//		            <xs:enumeration value="snapping"/>
//		            <xs:enumeration value="noise"/>
//		          </xs:restriction>
//		        </xs:simpleType>
//		      </xs:attribute>
//		      <xs:attribute name="who" type="xs:anyURI"/>
//		    </xs:complexType>
//		  </xs:element>
		
		// aggiungi - Commenti...
		
		textContent = textContent.toLowerCase();
		if(textContent.contains("applaus"))
			return "applause";
		
		return null;
					
	}
	
	
	private String getVocal(String textContent) {
		
//		<xs:element name="vocal" substitutionGroup="ns1:comment">
//	    <xs:complexType>
//	      <xs:sequence>
//	        <xs:element ref="ns1:desc"/>
//	      </xs:sequence>
//	      <xs:attribute name="type" use="required">
//	        <xs:simpleType>
//	          <xs:restriction base="xs:token">
//	            <xs:enumeration value="speaking"/>
//	            <xs:enumeration value="interruption"/>
//	            <xs:enumeration value="noise"/>
//	            <xs:enumeration value="laughter"/>
//	            <xs:enumeration value="exclamat"/>
//	            <xs:enumeration value="shouting"/>
//	            <xs:enumeration value="greeting"/>
//	            <xs:enumeration value="question"/>
//	            <xs:enumeration value="signal"/>
//	            <xs:enumeration value="clarification"/>
//	          </xs:restriction>
//	        </xs:simpleType>
//	      </xs:attribute>
//	      <xs:attribute name="who" type="xs:anyURI"/>
//	    </xs:complexType>
//	  </xs:element>
		
		textContent = textContent.toLowerCase();
		if(textContent.contains("brusio"))
			return "noise";
		if(textContent.contains("comment"))
			return "speaking";
		if(textContent.contains("ilarit"))
			return "laughter";
		if(textContent.contains("protest"))
			return "shouting";
		if(textContent.contains("richiam"))
			return "shouting";

		return null;
					
	}
	
	
	private String getIncident(String textContent) {
		
//		<xs:element name="incident" substitutionGroup="ns1:comment">
//	    <xs:complexType>
//	      <xs:sequence>
//	        <xs:element ref="ns1:desc"/>
//	      </xs:sequence>
//	      <xs:attribute name="type">
//	        <xs:simpleType>
//	          <xs:restriction base="xs:token">
//	            <xs:enumeration value="action"/>
//	            <xs:enumeration value="incident"/>
//	            <xs:enumeration value="leaving"/>
//	            <xs:enumeration value="entering"/>
//	            <xs:enumeration value="break"/>
//	            <xs:enumeration value="sound"/>
//	            <xs:enumeration value="editorial"/>
//	          </xs:restriction>
//	        </xs:simpleType>
//	      </xs:attribute>
//	      <xs:attribute name="who" type="xs:anyURI"/>
//	    </xs:complexType>
//	  </xs:element>
		
		textContent = textContent.toLowerCase();
		if(textContent.contains("cenn"))
			return "action";
		if(textContent.contains("alzan"))
			return "action";
		if(textContent.contains("mostran"))
			return "action";
		if(textContent.contains("in piedi"))
			return "action";
		if(textContent.contains("recan"))
			return "entering";
		if(textContent.contains("abbandon"))
			return "leaving";
		if(textContent.contains("silenz"))
			return "sound";
		if(textContent.contains("microfono"))
			return "sound";
		return null;
					
	}
	
	
	
	// DA RIUSARE IN ROLE/AFFILIATION
	
	
	private String normalizeRoleXSD(String sourceRoleText,String speakerPrefix) {
		
		// il presidente è chair, i senatori sono regular, tutti gli altri sono guest
		
		if(sourceRoleText.equalsIgnoreCase("presidente"))
			return "chair";
		if(speakerPrefix.equalsIgnoreCase("sen"))
			return "regular";
		return "guest";
		
	}

	
	
	private Document processGenBL(Node genBL, Document target, Element body) {
		
//		<GENBL>
//		<GENTIT>
//
//			Sulla scomparsa di Giuseppe Specchia
//		</GENTIT>
//
//		<INTERVENTO IDPOLITICO="24489" PROGR_PERS="32">
//
//			<ORATORE></ORATORE>
//			<QUALIFICA>
//				<a target="_blank"
//					href="/loc/link.asp?leg=18&amp;tipodoc=sanasen&amp;id=32">PRESIDENTE</a>
//			</QUALIFICA>
//			.
//			<i>(Il Presidente e l'Assemblea si levano in piedi)</i>
//			. Signori senatori, desidero rivolgere un pensiero di vicinanza e di
//			partecipazione, a nome di tutta l'Assemblea, ai familiari e agli
//			amici del senatore Specchia, scomparso il 3 maggio scorso nella sua
//			Ostuni.
//
//			
//		</INTERVENTO>
//		<INTERVENTO IDPOLITICO="24519" PROGR_PERS="1103"
//			GRUPPO="(FIBP-UDC)">
//
//			<ORATORE>
//				<a target="_blank"
//					href="/loc/link.asp?leg=18&amp;tipodoc=sanasen&amp;id=1103">GASPARRI</a>
//			</ORATORE>
//			<i>(FIBP-UDC)</i>
//			. Domando di parlare.
//
//			PRESIDENTE. Ne ha facoltà.
//
//			GASPARRI
//			<i>(FIBP-UDC)</i>
//			. Signor Presidente, volevo associarmi al ricordo del senatore
//			Specchia e ringraziarla per avere accolto l'invito a ricordarlo in
//			Aula. Pino Specchia ha fatto parte del Senato per ben diciannove
//			anni, dal 1987 al 2006, quindi è stato un parlamentare importante,
//			non dedito al proscenio, ma espressione di una militanza genuina.
//			Militante della destra politica, del Movimento Sociale Italiano,
//			consigliere comunale ad Ostuni, come ricordavamo prima anche con il
//			senatore Vitali e con il senatore Damiani, con persone del nostro
//			Gruppo che ne hanno conosciuto l'impegno sul territorio, ha
//			collaborato con il Gruppo regionale della destra del Movimento
//			Sociale Italiano alla Regione Puglia e poi con Domenico Mennitti, un
//			protagonista della vita politica pugliese e nazionale, del Parlamento
//			e della città e della Provincia di Brindisi, e con Pinuccio Tatarella
//			nella Regione e poi nella politica nazionale.
//
//		</INTERVENTO>
		
		Element debateSection = target.createElement("div");
		debateSection.setAttribute("type", "debateSection");
		body.appendChild(debateSection);
		
		Node lastInsertedNode = null;

		
		if(storePresidenza!=null) {
			for(Node presidenzaNote:storePresidenza) {
				debateSection.appendChild(presidenzaNote);
			}
			storePresidenza =null; // consumpted
		}
		
		
		NodeList nl = genBL.getChildNodes();
		ArrayList<Node> presidenzaChange =null;

		for(int i=0;i<nl.getLength();i++) {
			Node currNode = nl.item(i);
			if(currNode.getNodeName().equalsIgnoreCase("gentit")) {
				
				String textContent = cleanText(currNode.getTextContent());
				String attributeContent = cleanText(UtilDom.getAttributeValueAsString(currNode, "ININDICE"));
				// FIXME - verifica
				if(attributeContent.trim().length()>0)
					textContent = attributeContent.trim();
				
				if(textContent!=null && textContent.trim().length()>0) {
					Element head = target.createElement("head");
					head.setTextContent(textContent);
					debateSection.appendChild(head);
				}
				
			}else if(currNode.getNodeName().equalsIgnoreCase("intervento")) {
				target = processIntervento(currNode, target, debateSection);
			}else if(currNode.getNodeName().equalsIgnoreCase("trattazione")) {
				target = processTrattazione(currNode, target, debateSection);
			}
			else if(UtilDom.isTextNode(currNode)) {
				if(currNode.getTextContent().trim().length()>0) {
					Element note = target.createElement("note");
					note.setTextContent(cleanText(currNode.getTextContent()));
					
					// HEURISTIC  - note time
					if(note.getTextContent().startsWith("(ore") && note.getTextContent().length()<15)
						note.setAttribute("type", "time");
					debateSection.appendChild(note);
					lastInsertedNode = note;
				}
			}else if(currNode.getNodeName().equalsIgnoreCase("a")) {

				String textContent = cleanText(UtilDom.getText(currNode));
				// merge textNode - remove <i> annotation
				if(lastInsertedNode!=null) {
					String prevContent = lastInsertedNode.getTextContent();
					lastInsertedNode.setTextContent((prevContent+" "+textContent).trim());
					// se tolgo l'annotazione <i> devo concatenare anche il nodo successivo
					if(i<nl.getLength()-1) {
						Node nextNode = nl.item(i+1);
						if(nextNode!=null && UtilDom.isTextNode(nextNode)) {
							lastInsertedNode.setTextContent((lastInsertedNode.getTextContent()+" "+cleanText(nextNode.getTextContent())).trim());
							i=i+1;
						}else {
							System.err.println("process GENBL <a> -losing.. nextNode");
						}
					}else {
						System.err.println("process GENBL <a> -losing.. nextNode");
					}
					// il nodo <i> è il primo figlio dell'intervento
				}else {
					System.err.println("process GENBL -losing <a> .. (is First node) "+textContent);
				}
			}
			else if(currNode.getNodeName().equalsIgnoreCase("table")) {
				Node tableNote = processTable(currNode, target);
				if(tableNote!=null) {
					debateSection.appendChild(tableNote);
					lastInsertedNode = tableNote;
				}
			}else if(currNode.getNodeName().equalsIgnoreCase("DOCUMENTO")) {
				Node documentoNote = processDocumento(currNode, target);
				if(documentoNote!=null) {
					debateSection.appendChild(documentoNote);
				}
			}else if(currNode.getNodeName().equalsIgnoreCase("NOTEBL")) {
							
				String note_bl_content = UtilDom.getText(currNode);
				Node note_bl_Node = null;
				if(note_bl_content!=null && note_bl_content.trim().length()>0) {
					note_bl_Node = target.createElement("note");

					// FIXME NOTE TYPE
					String note_bl_Type ="document";
					UtilDom.setAttributeValue(note_bl_Node, "type", note_bl_Type);
					note_bl_Node.setTextContent(note_bl_content);
				}
				
				if(note_bl_Node!=null) {
					debateSection.appendChild(note_bl_Node);
					lastInsertedNode = note_bl_Node;
				}
			}// POTREI CREARE ALTRE NOTE
			else if(currNode.getNodeName().equalsIgnoreCase("i")) {
				//System.err.println(TEIid+" CHECK NODE <i> IN DDLBL.."+UtilDom.getText(currNode));
				if(currNode.getTextContent().trim().length()>0) {
					Element note = target.createElement("note");
					//note.setAttribute("type", "note");
					note.setTextContent(cleanText(currNode.getTextContent()));
					debateSection.appendChild(note);
				}
			}else if(currNode.getNodeName().equalsIgnoreCase("presidenza")) {
				// FIXME cambia implementazione
				presidenzaChange = processPresidenza(currNode, target);
				if(presidenzaChange!=null) {	
					for(Node presidenzaNote:presidenzaChange) {
						debateSection.appendChild(presidenzaNote);
					}
				}
			// <TABLE>
			}
			else {
				// QUI
				System.err.println(TEIid+"-- UNRECOGNIZED NODE IN GENBL.."+currNode.getNodeName()+" -- "+UtilDom.getText(currNode));
			}
			
		}
		
		return target;
		
	}

	
	private Document processDdlBL(Node ddlBL, Document target, Element body) {
//		<DDLBL>
//		<DDLTIT ORARIO="09,50"
//			ININDICE="Discussione e approvazione del disegno di legge: (1777) - Olimpiadi invernali Milano Cortina 2026 e finali ATP Torino 2021">
//
//			Discussione e approvazione del disegno di legge:
//		</DDLTIT>
//
//		<DDL NUMFASE="1777">
//			<NUMEROATTO>
//				<a target="_blank"
//					href="/loc/link.asp?leg=18&amp;tipodoc=sddliter&amp;id=52916">(1777)</a>
//			</NUMEROATTO>
//			<INIZIATIVAATTO><i>SANTILLO ed altri. -</i></INIZIATIVAATTO>
//			<TITOLOATTO>
//				<i>Conversione in legge, con modificazioni, del decreto-legge 11
//					marzo 2020, n. 16, recante disposizioni urgenti per
//					l'organizzazione e lo svolgimento dei Giochi olimpici e paralimpici
//					invernali Milano Cortina 2026 e delle finali ATP Torino 2021-2025,
//					nonché in materia di divieto di pubblicizzazione parassitaria</i>
//			</TITOLOATTO>
//			<NOTEATTO>
//				<i>(Approvato dalla Camera dei deputati) (Relazione orale)</i>
//			</NOTEATTO>
//		</DDL>
//		(ore 9,50)
//

		Element ddlSection = target.createElement("div");
		ddlSection.setAttribute("type", "debateSection");
		// FIXME non c'è un attributo per identificare un bill debate ... 
		body.appendChild(ddlSection);
		
		if(storePresidenza!=null) {
			for(Node presidenzaNote:storePresidenza) {
				ddlSection.appendChild(presidenzaNote);
			}
			storePresidenza =null; 
		}
		
		String orario = null;
		String ddltit = "";
		String numeroAtto ="";
		String titoloAtto ="";
		String iniziativaAtto = "";
		String noteAtto =null;
		String urlAtto = null;
		

		NodeList nl = ddlBL.getChildNodes();
		ArrayList<Node> presidenzaChange =null;

		for(int i=0;i<nl.getLength();i++) {	
			Node currNode = nl.item(i);
			
			if(currNode.getNodeName().equalsIgnoreCase("ddltit")) {
				orario = UtilDom.getAttributeValueAsString(currNode, "ORARIO");
				ddltit = UtilDom.getText(currNode);
			}else if(currNode.getNodeName().equalsIgnoreCase("ddl")) {
				NodeList ddlChildren = currNode.getChildNodes();
				for(int j=0; j<ddlChildren.getLength();j++) {
					Node curr_ddlChild = ddlChildren.item(j);
					
					if(curr_ddlChild.getNodeName().equalsIgnoreCase("NUMEROATTO")) {
						numeroAtto = UtilDom.getText(curr_ddlChild);
						Node linkAtto = curr_ddlChild.getFirstChild();
						if (linkAtto!=null && linkAtto.getNodeName().equalsIgnoreCase("a")) {
							urlAtto = cleanUrl(UtilDom.getAttributeValueAsString(linkAtto, "href"));
						}
					}else if(curr_ddlChild.getNodeName().equalsIgnoreCase("INIZIATIVAATTO")) {
						iniziativaAtto = UtilDom.getText(curr_ddlChild);
					}else if(curr_ddlChild.getNodeName().equalsIgnoreCase("TITOLOATTO")) {
						titoloAtto = UtilDom.getText(curr_ddlChild);
					}else if(curr_ddlChild.getNodeName().equalsIgnoreCase("NOTEATTO")) {
						noteAtto = UtilDom.getText(curr_ddlChild);
					}else if(UtilDom.isTextNode(curr_ddlChild) || curr_ddlChild.getNodeName().equalsIgnoreCase("i")) {
						if(curr_ddlChild.getTextContent().trim().length()>0) {
							Element note = target.createElement("note");
							// FIXME i node as note - unknown (andrebbe fatto il merge come in intervento)
							//note.setAttribute("type", "note");
							note.setTextContent(cleanText(curr_ddlChild.getTextContent()));
							ddlSection.appendChild(note);
						}
					}else {
						System.err.println(TEIid+ "  UNRECOGNIZED NODE IN DDLBL-DDL.."+curr_ddlChild.getNodeName()+"--- "+UtilDom.getText(curr_ddlChild));
					}
				}
				Element note = target.createElement("note");
				// FIXME ? inizativaatto (quando c'è) potrebbe essere spostato come nota a sè ? 
				note.setTextContent(cleanText(ddltit+" "+numeroAtto+" " +iniziativaAtto+" "+titoloAtto));
				ddlSection.appendChild(note);
				if(orario!=null) {
					Element noteOrario = target.createElement("note");
					noteOrario.setAttribute("type", "time");
					noteOrario.setTextContent(cleanText(orario));
					ddlSection.appendChild(noteOrario);
				}
				if(noteAtto!=null) {
					Element noteAttoNode = target.createElement("note");
					// FIXME note type "status"
					noteAttoNode.setAttribute("type", "status");
					noteAttoNode.setTextContent(cleanText(noteAtto));
					ddlSection.appendChild(noteAttoNode);
				}
				if(urlAtto!=null ) {
					Element urlAttoNode = target.createElement("note");
					// FIXME note type "status"
					urlAttoNode.setAttribute("type", "link");
					urlAttoNode.setTextContent(urlAtto);
					ddlSection.appendChild(urlAttoNode);
				}
				
				
			}else if(currNode.getNodeName().equalsIgnoreCase("intervento")) {
				target = processIntervento(currNode, target, ddlSection);
			}else if(currNode.getNodeName().equalsIgnoreCase("trattazione")) {
				target = processTrattazione(currNode, target, ddlSection);
			}
			else if(UtilDom.isTextNode(currNode)) {
				if(currNode.getTextContent().trim().length()>0) {
					Element note = target.createElement("note");
					// FIXME textNode as note - unknown
					//note.setAttribute("type", "note");
					note.setTextContent(cleanText(currNode.getTextContent()));
					// HEURISTIC  - note time
					if(note.getTextContent().startsWith("(ore") && note.getTextContent().length()<15)
						note.setAttribute("type", "time");
					ddlSection.appendChild(note);
				}
			}
			// POTREI CREARE ALTRE NOTE
			else if(currNode.getNodeName().equalsIgnoreCase("i")) {
				
				//System.err.println(TEIid+" CHECK NODE <i> IN DDLBL.."+UtilDom.getText(currNode));
				
				if(currNode.getTextContent().trim().length()>0) {
					Element note = target.createElement("note");
					// FIXME i node as note - unknown (andrebbe fatto il merge come in intervento)
					//note.setAttribute("type", "note");
					note.setTextContent(cleanText(currNode.getTextContent()));
					if(note.getTextContent().startsWith("(ore") && note.getTextContent().length()<15)
						note.setAttribute("type", "time");
					ddlSection.appendChild(note);
				}
			}
			else if(currNode.getNodeName().equalsIgnoreCase("table")) {
				Node tableNote = processTable(currNode, target);
				if(tableNote!=null) {
					ddlSection.appendChild(tableNote);
				}
			}else if(currNode.getNodeName().equalsIgnoreCase("DOCUMENTO")) {
				Node documentoNote = processDocumento(currNode, target);
				if(documentoNote!=null) {
					ddlSection.appendChild(documentoNote);
				}
			}else if(currNode.getNodeName().equalsIgnoreCase("NOTEBL")) {

				String note_bl_content = UtilDom.getText(currNode);
				Node note_bl_Node = null;
				if(note_bl_content!=null && note_bl_content.trim().length()>0) {
					note_bl_Node = target.createElement("note");

					// FIXME NOTE TYPE
					String note_bl_Type ="document";
					UtilDom.setAttributeValue(note_bl_Node, "type", note_bl_Type);
					note_bl_Node.setTextContent(note_bl_content);
				}

				if(note_bl_Node!=null) {
					ddlSection.appendChild(note_bl_Node);
				}
			}else if(currNode.getNodeName().equalsIgnoreCase("presidenza")) {
				presidenzaChange = processPresidenza(currNode, target);
				if(presidenzaChange!=null) {	
					for(Node presidenzaNote:presidenzaChange) {
						ddlSection.appendChild(presidenzaNote);
					}
				}
			// <TABLE>
			}
			else {
				System.err.println(TEIid+"  UNRECOGNIZED NODE IN DDLBL.."+currNode.getNodeName() +"--- "+UtilDom.getText(currNode));
			}
			
		}

		return target;

	}

	
	/**
	 * 
	 * @param path
	 * @param targetDoc
	 * @param body
	 * @return
	 */
	public Document exportEmbeddedXML(String path, Document targetDoc, Element body) {
		
		Document sourceDoc = null;

		try {
			File sourceFile = new File(path);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setExpandEntityReferences(true); // TRY FIX - era false
			DocumentBuilder domBuilder = dbf.newDocumentBuilder();
			
			
			sourceDoc = domBuilder.parse(sourceFile);
			
			
			Node ressten = sourceDoc.getElementsByTagName("RESSTEN").item(0);
			
			NodeList nl = ressten.getChildNodes();
			
			for(int i=0;i<nl.getLength();i++) {
				Node currentNode = nl.item(i);
				if(currentNode.getNodeName().equalsIgnoreCase("presidenza")) {
					storePresidenza = processPresidenza(currentNode, targetDoc);
				}else if(currentNode.getNodeName().equalsIgnoreCase("genbl")) {
					
					targetDoc = processGenBL(currentNode, targetDoc, body);
					
				}else if(currentNode.getNodeName().equalsIgnoreCase("ddlbl")) {
					targetDoc = processDdlBL(currentNode, targetDoc, body);
					
				}else if(UtilDom.isTextNode(currentNode)) {
					
				}else {
					System.err.println("UNRECOGNIZED NODE.."+currentNode.getNodeName());
				}
				
			}
			
			targetDoc = updateTagCountsXML(targetDoc);
			targetDoc = updateExtentXML(targetDoc);

			
			targetDoc = updateSegId(targetDoc);
			
			
			return targetDoc;

		} catch (Exception ex) {
			// FIXME -- attenzione a queste Eccezioni / FIX
			System.err.print(TEIid +" +++");
			ex.printStackTrace();
			return targetDoc;
			
		}

	}
	

	private Document updateTagCountsXML(Document targetDoc) {
		
		HashMap<String,Integer> tags  = new HashMap<String,Integer>();
		
		tags = countTags(targetDoc.getElementsByTagName("text").item(0),tags);
		updateTotalTags(tags);
		
		Element  newTagsDecl = targetDoc.createElement("tagsDecl");
		Element  namespace = targetDoc.createElement("namespace");
		namespace.setAttribute("name", "http://www.tei-c.org/ns/1.0");
		
		// TAG_USAGE
		for(String item:tags.keySet()) {
			Element  tagUsage1 = targetDoc.createElement("tagUsage");
			tagUsage1.setAttribute("gi", item);
			tagUsage1.setAttribute("occurs", ""+tags.get(item));
			namespace.appendChild(tagUsage1);
		}

		newTagsDecl.appendChild(namespace);
		
		
		Node oldTagsDecl = targetDoc.getElementsByTagName("tagsDecl").item(0);
		oldTagsDecl.getParentNode().replaceChild(newTagsDecl, oldTagsDecl);
		
		
		return targetDoc;
	}
	
	
	private Document updateSegId(Document targetDoc) {
		segId = 1;
		
		NodeList segs = targetDoc.getElementsByTagName("seg");
		for(int k=0;k<segs.getLength();k++) {
			UtilDom.setAttributeValue(segs.item(k), "xml:id", TEIid+".seg"+segId++);
		}
		
		//System.err.println(TEIid+" n. SEGS "+segs.getLength());
		
		return targetDoc;
	}
	
	private Document updateExtentXML(Document targetDoc) {

		HashMap<String,Integer> extent  = new HashMap<String,Integer>();

		extent = countWords(targetDoc);
		updateTotalExtent(extent);
		
		
		Element  newExtent = targetDoc.createElement("extent");
		
		// SPEECHES
		Element measure1_it = targetDoc.createElement("measure");
		measure1_it.setAttribute("unit", "speeches");
		measure1_it.setAttribute("quantity", ""+extent.get("speeches"));
		measure1_it.setAttribute("xml:lang", "it");
		measure1_it.setTextContent(extent.get("speeches")+" discorsi");

		Element measure1_en = targetDoc.createElement("measure");
		measure1_en.setAttribute("unit", "speeches");
		measure1_en.setAttribute("quantity", ""+extent.get("speeches"));
		measure1_en.setAttribute("xml:lang", "en");
		measure1_en.setTextContent(extent.get("speeches")+" speeches");
		
		// WORDS
		
		Element measure2_it = targetDoc.createElement("measure");
		measure2_it.setAttribute("unit", "words");
		measure2_it.setAttribute("quantity", ""+extent.get("words"));
		measure2_it.setAttribute("xml:lang", "it");
		measure2_it.setTextContent(extent.get("words")+" parole");

		Element measure2_en = targetDoc.createElement("measure");
		measure2_en.setAttribute("unit", "words");
		measure2_en.setAttribute("quantity", ""+extent.get("words"));
		measure2_en.setAttribute("xml:lang", "en");
		measure2_en.setTextContent(extent.get("words")+" words");

		newExtent.appendChild(measure1_it);
		newExtent.appendChild(measure1_en);
		newExtent.appendChild(measure2_it);
		newExtent.appendChild(measure2_en);


		Node oldExtent = targetDoc.getElementsByTagName("extent").item(0);
		oldExtent.getParentNode().replaceChild(newExtent, oldExtent);

		return targetDoc;
	}
	
	
	private void updateTotalTags(HashMap<String,Integer> tags) {
		
		
		for(String item:tags.keySet()) {
			
			if(totalTags.get(item)!=null) {
				int count = totalTags.get(item)+tags.get(item);
				totalTags.put(item, count);
			}else {
				totalTags.put(item, tags.get(item));
			}
			
		}
	}
	
	private void updateTotalExtent(HashMap<String,Integer> extent) {
		
		
		for(String item:extent.keySet()) {
			
			if(totalExtent.get(item)!=null) {
				int count = totalExtent.get(item)+extent.get(item);
				totalExtent.put(item, count);
			}else {
				totalExtent.put(item, extent.get(item));
			}
			
		}
	}
	
	
	private HashMap<String,Integer> countTags( Node root, HashMap<String,Integer> tags) {
		
		
		NodeList nodeList=root.getChildNodes();
		
		if(nodeList == null)
			return tags;
		for (int i=0; i<nodeList.getLength(); i++) 
		{
			// Get element
			if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				
				Element element = (Element)nodeList.item(i);

				if(tags.get(element.getNodeName())!=null) {
					int count = tags.get(element.getNodeName())+1;
					tags.put(element.getNodeName(), count);
				}else {
					tags.put(element.getNodeName(), 1);
				}
				tags = countTags(element, tags);
			}
			
		}
	  
		return tags;
		
	}
	
	private HashMap<String,Integer> countWords(Document targetDoc) {

		HashMap<String,Integer> wordsCount = new HashMap<String,Integer>();
		
		NodeList utterances = targetDoc.getElementsByTagName("seg");
		
		int wordCount = 0;
		int speechCount = utterances.getLength();
		
		for(int i=0; i<utterances.getLength();i++) {
			
			Node utt = utterances.item(i);
			String content = UtilDom.getRecursiveTextNodeII(utt);
			int tokenNum = new StringTokenizer(content, " ").countTokens();
			wordCount+=tokenNum;
		}
		
		wordsCount.put("speeches", speechCount);
		wordsCount.put("words", wordCount);
		
		return wordsCount;
		
	}
	
	
	
//	=============================================================
//	ANALYZE EMBEDDED XML
//	=============================================================
	
	public int inspectEmbeddedXML(String path,DatiSenato dati) {
		org.w3c.dom.Document doc = null;
		
		


		try {
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setExpandEntityReferences(false);
			DocumentBuilder domBuilder = dbf.newDocumentBuilder();
			
			File file = new File(path);
			doc = domBuilder.parse(file);
			
//			0
			
//			NodeList interventi = doc.getElementsByTagName("INTERVENTO");
//			
//			for (int i = 0; i < interventi.getLength(); i++) {
//				System.err.println("INTERVENTO_"+i+" "+UtilDom.getRecursiveTextNode(interventi.item(i)));
//			}
			
//			1-ROOT
			
//			Node ressten = doc.getElementsByTagName("RESSTEN").item(0);
//			Vector v = UtilDom.getAllChildElements(ressten);
//			
//			for (int i = 0; i < v.size(); i++) {
//				System.err.println(file.getName()+" RESSTEN_CHILD"+i+" "+((Node)v.get(i)).getNodeName());
//			}
	
//			2-GENBL

			
//			NodeList genbl = doc.getElementsByTagName("GENBL");
//			
//			for (int i = 0; i < genbl.getLength(); i++) {
//
//				Vector v = UtilDom.getAllChildElements(genbl.item(i));
//				
//				for (int j = 0; j < v.size(); j++) {
//					System.err.println(file.getName()+" GENBL_CHILD"+j+" "+((Node)v.get(j)).getNodeName());
//				}
//			}
//			
//			
//			NodeList ddlbl = doc.getElementsByTagName("DDLBL");
//			
//			for (int i = 0; i < ddlbl.getLength(); i++) {
//
//				Vector v = UtilDom.getAllChildElements(ddlbl.item(i));
//				
//				for (int j = 0; j < v.size(); j++) {
//					System.err.println(file.getName()+" DDLBL_CHILD"+j+" "+((Node)v.get(j)).getNodeName());
//				}
//			}
			
			
//			3- INTERVENTO / TRATTAZIONE / DDL

			
//			NodeList interventi = doc.getElementsByTagName("INTERVENTO");
//			
//			for (int i = 0; i < interventi.getLength(); i++) {
//
//				Vector v = UtilDom.getAllChildElements(interventi.item(i));
//				
//				for (int j = 0; j < v.size(); j++) {
//					System.err.println(file.getName()+" INTERVENTO_CHILD"+j+" "+((Node)v.get(j)).getNodeName());
//				}
//			}
//			
//			NodeList trattazioni = doc.getElementsByTagName("TRATTAZIONE");
//			
//			for (int i = 0; i < trattazioni.getLength(); i++) {
//
//				Vector v = UtilDom.getAllChildElements(trattazioni.item(i));
//				
//				for (int j = 0; j < v.size(); j++) {
//					System.err.println(file.getName()+" TRATTAZIONE_CHILD"+j+" "+((Node)v.get(j)).getNodeName());
//				}
//			}
//			
//			NodeList ddl = doc.getElementsByTagName("DDL");
//			
//			for (int i = 0; i < ddl.getLength(); i++) {
//
//				Vector v = UtilDom.getAllChildElements(ddl.item(i));
//				
//				for (int j = 0; j < v.size(); j++) {
//					System.err.println(file.getName()+" DDL_CHILD"+j+" "+((Node)v.get(j)).getNodeName());
//				}
//			}
			
			
//			4- CORSIVI
//
//			NodeList corsivo = doc.getElementsByTagName("i");
//			
//			for (int i = 0; i < corsivo.getLength(); i++) {
//
//				//System.err.println(file.getName()+" CORSIVO_NODE "+UtilDom.getText((Node)corsivo.item(i)));
//				System.err.println(UtilDom.getText((Node)corsivo.item(i)));
//
//			}
			

			
			
//			5- HREF
//			
//			NodeList links = doc.getElementsByTagName("a");
//			
//			for (int i = 0; i < links.getLength(); i++) {
//
//				System.err.println(file.getName()+" LINK_NODE "+UtilDom.getAttributeValueAsString(((Node)links.item(i)), "href"));
//				
//			}
			
//			6- TRATTAZIONE
//
//			TRATTAZIONE - TIPO
//			A	538	ESAME DEGLI ARTICOLI
//			D	274	DISCUSSIONE
//			F	445	VOTAZIONE FINALE
//			G	463	DISCUSSIONE GENERALE
//			N	27  proposta di non passaggio all'esame degli articoli, ai sensi dell'articolo 96 del Regolamento
//			P	83	QUESTIONI PREGIUDIZIALI
//			Q	14  QUESTIONE PREGIUDIZIALI E SOSPENSIVE
//			S	14	QUESTIONE SOSPENSIVA
//			T	14	VOTAZIONE DEGLI ARTICOLI
//			U	67	DISCUSSIONE SULLA QUESTIONE DI FIDUCIA
//			V	304	VOTAZIONE
			
//			NodeList trattazioni = doc.getElementsByTagName("TRATTAZIONE");
//
//			for (int i = 0; i < trattazioni.getLength(); i++) {
//
////				System.err.println(file.getName()+" TRATTAZIONE_TIPO: "+UtilDom.getAttributeValueAsString((Node)trattazioni.item(i),"TIPO"));
////				System.err.println(file.getName()+" TRATTAZIONE_TEXT: "+UtilDom.getText((Node)trattazioni.item(i)));
////				System.err.println("");
//
//				// TABLEVIEW
//				//System.err.println("TRATTAZIONE_TIPO\t"+UtilDom.getAttributeValueAsString((Node)trattazioni.item(i),"TIPO"));
//				System.err.println(file.getName()+" TRATTAZIONE_PARENT\t"+trattazioni.item(i).getParentNode().getNodeName());
//
//				
//			}
			
	
			// SUPERSCRIPTS
			
//			NodeList superscripts = doc.getElementsByTagName("sup");			
//			for (int i = 0; i < superscripts.getLength(); i++) {
//
//
//				//System.err.println(file.getName()+" SUPERSCRIPT_PARENT\t"+superscripts.item(i).getParentNode().getNodeName());
//				System.err.println(file.getName()+" SUPERSCRIPT_CONTENT\t"+UtilDom.getText(superscripts.item(i)));
//				
//			}
		
			// SUBSCRIPTS
			
//			NodeList superscripts = doc.getElementsByTagName("sub");			
//			for (int i = 0; i < superscripts.getLength(); i++) {
//
//
//				System.err.println(file.getName()+"\tSUB_SCRIPT_PARENT\t"+superscripts.item(i).getParentNode().getNodeName());
//				System.err.println(file.getName()+"\tSUB_CONTENT\t"+UtilDom.getText(superscripts.item(i)));
//				
//			}
			
//			// NOTEBL
//			NodeList nodesbl = doc.getElementsByTagName("NOTEBL");			
//			for (int i = 0; i < nodesbl.getLength(); i++) {
//
//
//				System.err.println(file.getName()+"\tNOTES_BL_PARENT\t"+nodesbl.item(i).getParentNode().getNodeName()+"\tNOTESBL_CONTENT\t"+UtilDom.getText(nodesbl.item(i)));
//				
//			}
			
			// NOTEBL
			NodeList documents = doc.getElementsByTagName("DOCUMENTO");			
			for (int i = 0; i < documents.getLength(); i++) {


				System.err.println(file.getName()+"\tDOCUMENTO_PARENT\t"+documents.item(i).getParentNode().getNodeName()+"\tDOCUMENTO_CONTENT\t"+UtilDom.getText(documents.item(i)));
				
			}
			
//			5- HREF + PARENTS
//						
//			NodeList links = doc.getElementsByTagName("a");		
//			String linkParent="";
//			String href="";
//			String textContent ="";
//			for (int i = 0; i < links.getLength(); i++) {
//
//
//				linkParent = links.item(i).getParentNode().getNodeName();
//				href= UtilDom.getAttributeValueAsString(links.item(i), "href");
//				textContent= UtilDom.getText(links.item(i));
//				// TABLEVIEW
//				//System.err.println("TRATTAZIONE_TIPO\t"+UtilDom.getAttributeValueAsString((Node)trattazioni.item(i),"TIPO"));
//				System.err.println(file.getName()+"\tLINK_PARENT\t"+linkParent+"\t"+textContent+"\t"+href);
//
//				
//			}
			
//			NodeList tables = doc.getElementsByTagName("table");			
//			for (int i = 0; i < tables.getLength(); i++) {
//
//				System.err.println(file.getName()+"\tTABLE_PARENT\t"+tables.item(i).getParentNode().getNodeName()+"\t"+UtilDom.getText(tables.item(i)));
//
//				
//			}
			
				
			// INTERVENTI e ORATORI ALL
			
//			NodeList interventi = doc.getElementsByTagName("INTERVENTO");	
//			
//			for (int i = 0; i < interventi.getLength(); i++) {
//				
//				Node intervento = interventi.item(i);
//				
//				String idSpeaker = UtilDom.getAttributeValueAsString(intervento, "PROGR_PERS");
//
//				String idSpeakerKey = "http://dati.senato.it/senatore/"+idSpeaker;
//				
//				Node[] oratore_list = UtilDom.getElementsByTagName(doc, intervento, "ORATORE");
//				Node[] qualifica_list = UtilDom.getElementsByTagName(doc, intervento, "QUALIFICA");
//
//				
//				String oratoreName = "";
//				String href = "";
//				Node a_href_node = null;
//				
//				if(oratore_list!=null && oratore_list.length>0) {
//					Node oratoreNode = oratore_list[0];
//					oratoreName = UtilDom.getRecursiveTextNode(oratoreNode);
//					Node[] a_href_list = UtilDom.getElementsByTagName(doc, oratoreNode,"a");
//					if(a_href_list!=null && a_href_list.length>0) {
//						a_href_node =a_href_list[0];
//					}
//				}
//				
//				
//				String qualifica = "";
//				
//				if(qualifica_list!=null && qualifica_list.length>0) {
//					Node qualificaNode = qualifica_list[0];
//					qualifica = UtilDom.getRecursiveTextNode(qualificaNode);
//					Node[] a_href_list = UtilDom.getElementsByTagName(doc, qualificaNode,"a");
//					if(a_href_list!=null && a_href_list.length>0) {
//						a_href_node =a_href_list[0];
//					}
//				}
//				
//				
//				
//				String urlScheda ="";
//				
//	//			http://www.senato.it/leg/18/BGT/Schede/Attsen/00005799.htm
//				
//				String celoManca ="manca";
//				if(dati.getLookupSenatore().get(idSpeakerKey)!=null)
//					celoManca ="celo";
//
//				
//				href = UtilDom.getAttributeValueAsString(a_href_node, "href");
//
//				if(href.trim().length()>0) {
//					String leg ="";
//					
//					leg = href.substring(href.indexOf("leg=")+4,href.indexOf("leg=")+6);
//					urlScheda ="http://www.senato.it/leg/"+leg+"/BGT/Schede/Attsen/"+completeWithZeros(idSpeaker)+".htm";
//				}
//				
//				
//			
//				
//					
////				System.err.println(file.getName()+" TRATTAZIONE_TIPO: "+UtilDom.getAttributeValueAsString((Node)trattazioni.item(i),"TIPO"));
////				System.err.println(file.getName()+" TRATTAZIONE_TEXT: "+UtilDom.getText((Node)trattazioni.item(i)));
////				System.err.println("");
//
//				// TABLEVIEW
//				//System.err.println("TRATTAZIONE_TIPO\t"+UtilDom.getAttributeValueAsString((Node)trattazioni.item(i),"TIPO"));
//				System.out.println(file.getName()+"\t"+oratoreName+"\t"+qualifica+"\t"+idSpeaker+"\t"+urlScheda+"\t"+celoManca);
//				
//			}
			
			
			// END INTERVENTI e ORATORI ALL

			
			
			
//			NodeList oratori = doc.getElementsByTagName("ORATORE");	
//			System.out.println("ORATORE COUNT: "+oratori.getLength());
//			for (int i = 0; i < oratori.getLength(); i++) {
//
//				Node oratore = oratori.item(i);
//				String name = UtilDom.getRecursiveTextNode(oratore);
//				Node a_href = UtilDom.getElementsByTagName(doc, oratore,"a")[0];
//				String href = "";
//				if(a_href!=null) {
//					href = UtilDom.getAttributeValueAsString(a_href, "href");
//				}
//
//				Node[] qualifiche = UtilDom.getElementsByTagName(doc, oratore.getParentNode(), "QUALIFICA");
//				String qualifica = "";
//				if(qualifiche!=null)
//					qualifica=UtilDom.getRecursiveTextNode(qualifiche[0]);
//					
////				System.err.println(file.getName()+" TRATTAZIONE_TIPO: "+UtilDom.getAttributeValueAsString((Node)trattazioni.item(i),"TIPO"));
////				System.err.println(file.getName()+" TRATTAZIONE_TEXT: "+UtilDom.getText((Node)trattazioni.item(i)));
////				System.err.println("");
//
//				// TABLEVIEW
//				//System.err.println("TRATTAZIONE_TIPO\t"+UtilDom.getAttributeValueAsString((Node)trattazioni.item(i),"TIPO"));
//				System.out.println(file.getName()+"\t"+name+"\t"+qualifica+"\t"+href);
//
//				
//			}
			
			return -1; 

		} catch (Exception ex) {
			return -1;
		}

		
		
	}
	
	private String completeWithZeros(String idSpeaker) {
		if(idSpeaker.length()==8)
			return idSpeaker;
		if(idSpeaker.length()==7)
			return "0"+idSpeaker;
		if(idSpeaker.length()==6)
			return "00"+idSpeaker;
		if(idSpeaker.length()==5)
			return "000"+idSpeaker;
		if(idSpeaker.length()==4)
			return "0000"+idSpeaker;
		if(idSpeaker.length()==3)
			return "00000"+idSpeaker;
		if(idSpeaker.length()==2)
			return "000000"+idSpeaker;
		if(idSpeaker.length()==1)
			return "000000"+idSpeaker;
		
		return idSpeaker;
				
	}
	
	
	
	
	/////////////////////////////////////
	//
	// EXPORT TEI_HEADER Xml
	//
	/////////////////////////////////////
	
	public Element createFileDesc(Document targetDoc, String idDoc,String legislatura, String corpus,String seduta, String data, boolean ANA) {

		boolean SENATO_RESP = true;

		Element  fileDesc = targetDoc.createElement("fileDesc");


		// 1. titleStmt
		Element  titleStmt = targetDoc.createElement("titleStmt");
		Element  titleMainIT = targetDoc.createElement("title");
		titleMainIT.setAttribute("type", "main");
		titleMainIT.setAttribute("xml:lang", "it");
		if(!ANA)
			titleMainIT.setTextContent("Corpus parlamentare italiano ParlaMint-IT, Legislatura "+legislatura+", Seduta "+seduta+" Senato [ParlaMint]");
		else
			titleMainIT.setTextContent("Corpus parlamentare italiano ParlaMint-IT, Legislatura "+legislatura+", Seduta "+seduta+" Senato [ParlaMint.ana]");


		Element  titleMainEN = targetDoc.createElement("title");
		titleMainEN.setAttribute("type", "main");
		titleMainEN.setAttribute("xml:lang", "en");
		if(!ANA)
			titleMainEN.setTextContent("Italian parliamentary corpus ParlaMint-IT, Term "+legislatura+", Sitting "+seduta+" Senate [ParlaMint]");
		else
			titleMainEN.setTextContent("Italian parliamentary corpus ParlaMint-IT, Term "+legislatura+", Sitting "+seduta+" Senate [ParlaMint.ana]");


		Element  titleSubIT = targetDoc.createElement("title");
		titleSubIT.setAttribute("type", "sub");
		titleSubIT.setAttribute("xml:lang", "it");
		String dateText = "("+data.substring(0,4)+"-"+data.substring(4,6)+"-"+data.substring(6,8)+")";
		String titleSubIT_text = ("Resoconto  della seduta del Senato della  Repubblica italiana, Legislatura "+legislatura+", seduta "+seduta+", giorno "+dateText);
		titleSubIT.setTextContent(titleSubIT_text);

		Element  titleSubEN = targetDoc.createElement("title");
		titleSubEN.setAttribute("type", "sub");
		titleSubEN.setAttribute("xml:lang", "en");
		String titleSubEN_text = ("Report of the session of the Senate of the Italian Republic, Term "+legislatura+", Sitting "+seduta+", Day "+dateText);
		titleSubEN.setTextContent(titleSubEN_text);


		Element  meeting_upper = targetDoc.createElement("meeting");
		meeting_upper.setAttribute("ana", "#parla.upper");
		meeting_upper.setTextContent("Senato");

		Element  meeting_leg = targetDoc.createElement("meeting");
		meeting_leg.setAttribute("n",legislatura+"-upper");
		meeting_leg.setAttribute("ana", "#parla.upper #parla.term #LEG."+legislatura);
		meeting_leg.setTextContent(legislatura+" Legislatura");

		Element  meeting_sit = targetDoc.createElement("meeting");
		meeting_sit.setAttribute("n", seduta+"-upper");
		meeting_sit.setAttribute("ana", "#parla.upper #parla.sitting");
		meeting_sit.setTextContent(seduta +" Seduta");

		

		// RESP_STMT_CORPUS 

		String orcidAgnoloni ="https://orcid.org/0000-0003-3063-2239";
		String orcidBartolini ="https://orcid.org/0000-0002-6829-6309";
		String orcidFrontini ="https://orcid.org/0000-0002-8126-6294";
		String orcidMontemagni ="https://orcid.org/0000-0002-2953-8619";
		String orcidVenturi ="https://orcid.org/0000-0001-5849-0979";
		String orcidQuochi ="https://orcid.org/0000-0002-1321-5444";


		// PERSONS

		Element  persNameAgnoloniORCID = targetDoc.createElement("persName");
		persNameAgnoloniORCID.setTextContent("Tommaso Agnoloni");
		persNameAgnoloniORCID.setAttribute("ref", orcidAgnoloni);
		
		Element  persNameBartoliniORCID = targetDoc.createElement("persName");
		persNameBartoliniORCID.setTextContent("Roberto Bartolini");
		persNameBartoliniORCID.setAttribute("ref", orcidBartolini);

		Element  persNameFrontiniORCID = targetDoc.createElement("persName");
		persNameFrontiniORCID.setTextContent("Francesca Frontini");
		persNameFrontiniORCID.setAttribute("ref", orcidFrontini);

		Element  persNameMontemagniORCID = targetDoc.createElement("persName");
		persNameMontemagniORCID.setTextContent("Simonetta Montemagni");
		persNameMontemagniORCID.setAttribute("ref", orcidMontemagni);

		Element  persNameQuochiORCID = targetDoc.createElement("persName");
		persNameQuochiORCID.setTextContent("Valeria Quochi");
		persNameQuochiORCID.setAttribute("ref", orcidQuochi);

		Element  persNameVenturiORCID = targetDoc.createElement("persName");
		persNameVenturiORCID.setTextContent("Giulia Venturi");
		persNameVenturiORCID.setAttribute("ref", orcidVenturi);

		Element  persNameAgnoloni = targetDoc.createElement("persName");
		persNameAgnoloni.setTextContent("Tommaso Agnoloni");

		Element  persNameQuochi = targetDoc.createElement("persName");
		persNameQuochi.setTextContent("Valeria Quochi");

		Element  persNameVenturi = targetDoc.createElement("persName");
		persNameVenturi.setTextContent("Giulia Venturi");

		Element  persNameRuisi = targetDoc.createElement("persName");
		persNameRuisi.setTextContent("Manuela Ruisi");

		Element  persNameMarchetti = targetDoc.createElement("persName");
		persNameMarchetti.setTextContent("Carlo Marchetti");

		Element  persNameBattistoni = targetDoc.createElement("persName");
		persNameBattistoni.setTextContent("Roberto Battistoni");

//		Element  persNameCimino = targetDoc.createElement("persName");
//		persNameCimino.setTextContent("Andrea Cimino");

		Element  persNameBartolini = targetDoc.createElement("persName");
		persNameBartolini.setTextContent("Roberto Bartolini");


		// RESPONSIBILITY

		// PROJECT

		Element  respStmtProject = targetDoc.createElement("respStmt");
		Element  persName0 = targetDoc.createElement("persName");
		persName0.setTextContent("Tommaso Agnoloni");

		Element  respProjectIT = targetDoc.createElement("resp");
		respProjectIT.setAttribute("xml:lang", "it");
		respProjectIT.setTextContent("Definizione del progetto e metodologia");

		Element  respProjectEN= targetDoc.createElement("resp");
		respProjectEN.setAttribute("xml:lang", "en");
		respProjectEN.setTextContent("Project set-up and methodology");

		respStmtProject.appendChild(persNameAgnoloniORCID);
		respStmtProject.appendChild(persNameBartoliniORCID);
		respStmtProject.appendChild(persNameFrontiniORCID);
		respStmtProject.appendChild(persNameMontemagniORCID);
		respStmtProject.appendChild(persNameQuochiORCID);
		respStmtProject.appendChild(persNameVenturiORCID);

		respStmtProject.appendChild(respProjectIT);
		respStmtProject.appendChild(respProjectEN);


		// SENATO
		Element  respStmtSenato = targetDoc.createElement("respStmt");
		Element  respSenatoIT = targetDoc.createElement("resp");
		respSenatoIT.setAttribute("xml:lang", "it");
		respSenatoIT.setTextContent("Recupero dei dati");

		Element  respSenatoEN= targetDoc.createElement("resp");
		respSenatoEN.setAttribute("xml:lang", "en");
		respSenatoEN.setTextContent("Data retrieval");

		respStmtSenato.appendChild(persNameRuisi);
		respStmtSenato.appendChild(persNameMarchetti);
		respStmtSenato.appendChild(persNameBattistoni);

		respStmtSenato.appendChild(respSenatoIT);
		respStmtSenato.appendChild(respSenatoEN);

		//////////////////////////////////////////

		// CODIFICA
		Element  respStmtCodifica = targetDoc.createElement("respStmt");

		Element  respCodificaIT_0 = targetDoc.createElement("resp");
		respCodificaIT_0.setAttribute("xml:lang", "it");
		respCodificaIT_0.setTextContent("Codifica corpus in ParlaMint TEI XML");

		Element  respCodificaEN_0= targetDoc.createElement("resp");
		respCodificaEN_0.setAttribute("xml:lang", "en");
		respCodificaEN_0.setTextContent("ParlaMint TEI XML corpus encoding");

		Element  respCodificaIT_1 = targetDoc.createElement("resp");
		respCodificaIT_1.setAttribute("xml:lang", "it");
		respCodificaIT_1.setTextContent("Pulizia, normalizzazione e conversione in ParlaMint TEI XML");

		Element  respCodificaEN_1= targetDoc.createElement("resp");
		respCodificaEN_1.setAttribute("xml:lang", "en");
		respCodificaEN_1.setTextContent("Cleaning, normalisation and conversion to ParlaMint TEI XML");

		respStmtCodifica.appendChild(persNameAgnoloni);


		respStmtCodifica.appendChild(respCodificaIT_0);
		respStmtCodifica.appendChild(respCodificaEN_0);
		respStmtCodifica.appendChild(respCodificaIT_1);
		respStmtCodifica.appendChild(respCodificaEN_1);


		//////////////////////////////////////////


		// LINGUISTIC
		Element  respStmtLinguistic = targetDoc.createElement("respStmt");

		Element  respLinguisticIT_0 = targetDoc.createElement("resp");
		respLinguisticIT_0.setAttribute("xml:lang", "it");
		respLinguisticIT_0.setTextContent("Annotazione linguistica automatica");

		Element  respLinguisticEN_0= targetDoc.createElement("resp");
		respLinguisticEN_0.setAttribute("xml:lang", "en");
		respLinguisticEN_0.setTextContent("Automatic Linguistic annotation");

		Element  respLinguisticIT_1 = targetDoc.createElement("resp");
		respLinguisticIT_1.setAttribute("xml:lang", "it");
		respLinguisticIT_1.setTextContent("Riconoscimento Entità Nominate");

		Element  respLinguisticEN_1= targetDoc.createElement("resp");
		respLinguisticEN_1.setAttribute("xml:lang", "en");
		respLinguisticEN_1.setTextContent("NER");

//		Element  respLinguisticIT_2 = targetDoc.createElement("resp");
//		respLinguisticIT_2.setAttribute("xml:lang", "it");
//		respLinguisticIT_2.setTextContent("Allineamento Annotazione linguistica - Entità Nominate");
//
//		Element  respLinguisticEN_2= targetDoc.createElement("resp");
//		respLinguisticEN_2.setAttribute("xml:lang", "en");
//		respLinguisticEN_2.setTextContent("Linguistic annotation - NER Alignment");

//		respStmtLinguistic.appendChild(persNameVenturi);
//		respStmtLinguistic.appendChild(persNameCimino);
		
		respStmtLinguistic.appendChild(persNameBartolini);
		respStmtLinguistic.appendChild(persNameQuochi);

		respStmtLinguistic.appendChild(respLinguisticIT_0);
		respStmtLinguistic.appendChild(respLinguisticEN_0);
		respStmtLinguistic.appendChild(respLinguisticIT_1);
		respStmtLinguistic.appendChild(respLinguisticEN_1);
//		respStmtLinguistic.appendChild(respLinguisticIT_2);
//		respStmtLinguistic.appendChild(respLinguisticEN_2);



		//////////////////////////////////////////

		// ANA
//		Element  respStmtANA = targetDoc.createElement("respStmt");
//		Element  respANA_IT = targetDoc.createElement("resp");
//		respANA_IT.setAttribute("xml:lang", "it");
//		respANA_IT.setTextContent("Conversione annotazione linguistica: da CoNLL-U a ParlaMint TEI XML");
//
//		Element  respANA_EN= targetDoc.createElement("resp");
//		respANA_EN.setAttribute("xml:lang", "en");
//		respANA_EN.setTextContent("Conversion of the linguistic annotation: from CoNLL-U to ParlaMint TEI XML");
//
//		respStmtANA.appendChild(persNameBartolini);
//		respStmtANA.appendChild(persNameQuochi);
//
//		respStmtANA.appendChild(respANA_IT);
//		respStmtANA.appendChild(respANA_EN);

		//////////////////////////////////////////

		Element  funder_1 = targetDoc.createElement("funder");
		Element  orgNameIT = targetDoc.createElement("orgName");
		orgNameIT.setAttribute("xml:lang", "it");
		orgNameIT.setTextContent("Infrastruttura di ricerca CLARIN");
		Element  orgNameEN = targetDoc.createElement("orgName");
		orgNameEN.setAttribute("xml:lang", "en");
		orgNameEN.setTextContent("The CLARIN research infrastructure");
		funder_1.appendChild(orgNameIT);
		funder_1.appendChild(orgNameEN);

		Element  funder_2 = targetDoc.createElement("funder");
		Element  orgNameIT_2 = targetDoc.createElement("orgName");
		orgNameIT_2.setAttribute("xml:lang", "it");
		orgNameIT_2.setTextContent("Commissione Europea");
		Element  orgNameEN_2 = targetDoc.createElement("orgName");
		orgNameEN_2.setAttribute("xml:lang", "en");
		orgNameEN_2.setTextContent("European Commission");
		funder_2.appendChild(orgNameIT_2);
		funder_2.appendChild(orgNameEN_2);

		Element  funder_5 = targetDoc.createElement("funder");
		Element  orgNameIT_5 = targetDoc.createElement("orgName");
		orgNameIT_5.setAttribute("xml:lang", "it");
		orgNameIT_5.setTextContent("Senato della Repubblica italiana");
		Element  orgNameEN_5 = targetDoc.createElement("orgName");
		orgNameEN_5.setAttribute("xml:lang", "en");
		orgNameEN_5.setTextContent("Senate of the Italian Republic");
		funder_5.appendChild(orgNameIT_5);
		funder_5.appendChild(orgNameEN_5);

		Element  funder_3 = targetDoc.createElement("funder");
		Element  orgNameIT_3 = targetDoc.createElement("orgName");
		orgNameIT_3.setAttribute("xml:lang", "it");
		orgNameIT_3.setTextContent("Istituto di Linguistica Computazionale del Consiglio Nazionale delle Ricerche (CNR-ILC)");
		Element  orgNameEN_3 = targetDoc.createElement("orgName");
		orgNameEN_3.setAttribute("xml:lang", "en");
		orgNameEN_3.setTextContent("Institute of Computational linguistics of the Italian National Research Council (CNR-ILC)");
		funder_3.appendChild(orgNameIT_3);
		funder_3.appendChild(orgNameEN_3);

		Element  funder_4 = targetDoc.createElement("funder");
		Element  orgNameIT_4 = targetDoc.createElement("orgName");
		orgNameIT_4.setAttribute("xml:lang", "it");
		orgNameIT_4.setTextContent("Istituto di Informatica Giuridica e Sistemi Giudiziari del Consiglio Nazionale delle Ricerche (CNR-IGSG)");
		Element  orgNameEN_4 = targetDoc.createElement("orgName");
		orgNameEN_4.setAttribute("xml:lang", "en");
		orgNameEN_4.setTextContent("Institute of Legal Informatics and Judicial Systems of the Italian National Research Council (CNR-IGSG)");
		funder_4.appendChild(orgNameIT_4);
		funder_4.appendChild(orgNameEN_4);

		titleStmt.appendChild(titleMainIT);
		titleStmt.appendChild(titleMainEN);
		titleStmt.appendChild(titleSubIT);
		titleStmt.appendChild(titleSubEN);
		titleStmt.appendChild(meeting_upper);
		titleStmt.appendChild(meeting_leg);
		titleStmt.appendChild(meeting_sit);


		// RESP_STMT_DOCS
		titleStmt.appendChild(respStmtProject);
		if(SENATO_RESP)
			titleStmt.appendChild(respStmtSenato);
		titleStmt.appendChild(respStmtCodifica);

		if(ANA) {
			titleStmt.appendChild(respStmtLinguistic);
			//titleStmt.appendChild(respStmtANA);
		}


		titleStmt.appendChild(funder_1);
		titleStmt.appendChild(funder_2);
		titleStmt.appendChild(funder_5);
		titleStmt.appendChild(funder_3);
		titleStmt.appendChild(funder_4);


		//	2. <editionStmt>
		Element  editionStmt = targetDoc.createElement("editionStmt");
		Element edition = targetDoc.createElement("edition");
		edition.setTextContent("3.0");
		editionStmt.appendChild(edition);


		// 3. <extent>
		Element  extent = targetDoc.createElement("extent");
		Element measure1_it = targetDoc.createElement("measure");
		measure1_it.setAttribute("unit", "speeches");
		measure1_it.setAttribute("quantity", "999");
		measure1_it.setAttribute("xml:lang", "it");
		measure1_it.setTextContent("Tot parole");

		Element measure1_en = targetDoc.createElement("measure");
		measure1_en.setAttribute("unit", "speeches");
		measure1_en.setAttribute("quantity", "999");
		measure1_en.setAttribute("xml:lang", "en");
		measure1_en.setTextContent("Tot words");

		extent.appendChild(measure1_it);
		extent.appendChild(measure1_en);

	
		//  4. <publicationStmt>
		Element  publicationStmt = targetDoc.createElement("publicationStmt");
		Element  publisher = targetDoc.createElement("publisher");
		orgNameIT = targetDoc.createElement("orgName");
		orgNameIT.setAttribute("xml:lang", "it");
		orgNameIT.setTextContent("Infrastruttura di ricerca CLARIN");
		orgNameEN = targetDoc.createElement("orgName");
		orgNameEN.setAttribute("xml:lang", "en");
		orgNameEN.setTextContent("The CLARIN research infrastructure");
		Element ref = targetDoc.createElement("ref");
		ref.setAttribute("target", "https://www.clarin.eu/");
		ref.setTextContent("www.clarin.eu");
		publisher.appendChild(orgNameIT);
		publisher.appendChild(orgNameEN);
		publisher.appendChild(ref);


		//	      <idno type="URL">https://github.com/clarin-eric/ParlaMint</idno>
		//	          <pubPlace>
		//	            <ref target="https://github.com/clarin-eric/ParlaMint">https://github.com/clarin-eric/ParlaMint</ref>
		//	          </pubPlace>
		//
		//	  it should just be
		//
		//	          <idno type="URI" subtype="handle">http://hdl.handle.net/11356/1388</idno>
		//		

		Element idno = targetDoc.createElement("idno");

		idno.setAttribute("type", "URI");
		idno.setAttribute("subtype", "handle");

		idno.setTextContent("http://hdl.handle.net/11356/1388");


		//		idno.setAttribute("type", "URL");
		//		idno.setTextContent("https://github.com/clarin-eric/ParlaMint");


		Element availability = targetDoc.createElement("availability");
		availability.setAttribute("status", "free");
		Element licence = targetDoc.createElement("licence");
		licence.setTextContent("http://creativecommons.org/licenses/by/4.0/");
		Element p_it = targetDoc.createElement("p");
		p_it.setAttribute("xml:lang", "it");
		Element ref_licence_it = targetDoc.createElement("ref");
		ref_licence_it.setAttribute("target", "http://creativecommons.org/licenses/by/4.0/");
		ref_licence_it.setTextContent("licenza Creative Commons 4.0 Attribuzione Internazionale");
		Node p_it_text1 = targetDoc.createTextNode("Quest'opera è rilasciata con ");
		Node p_it_text2 = targetDoc.createTextNode(".");
		p_it.appendChild(p_it_text1);
		p_it.appendChild(ref_licence_it);
		p_it.appendChild(p_it_text2);


		Element p_en = targetDoc.createElement("p");
		p_en.setAttribute("xml:lang", "en");
		Element ref_licence_en = targetDoc.createElement("ref");
		ref_licence_en.setAttribute("target", "http://creativecommons.org/licenses/by/4.0/");
		ref_licence_en.setTextContent("Creative Commons Attribution 4.0 International License");
		Node p_en_text1 = targetDoc.createTextNode("This work is licensed under the ");
		Node p_en_text2 = targetDoc.createTextNode(".");
		p_en.appendChild(p_en_text1);
		p_en.appendChild(ref_licence_en);
		p_en.appendChild(p_en_text2);

		licence.appendChild(p_it);
		licence.appendChild(p_en);


		availability.appendChild(licence);
		availability.appendChild(p_it);
		availability.appendChild(p_en);


		Element date_pub = targetDoc.createElement("date");
		date_pub.setAttribute("when", "2022-10-30");
		date_pub.setTextContent("2022-10-30");



		publicationStmt.appendChild(publisher);
		publicationStmt.appendChild(idno);
		//publicationStmt.appendChild(pubPlace);
		publicationStmt.appendChild(availability);
		publicationStmt.appendChild(date_pub);



		//  5. <sourceDesc>
		Element  sourceDesc = targetDoc.createElement("sourceDesc");


		Element  bibl = targetDoc.createElement("bibl");
		Element  title_it = targetDoc.createElement("title");
		title_it.setAttribute("type", "main");
		title_it.setAttribute("xml:lang", "it");
		title_it.setTextContent("Resoconti stenografici delle sedute pubbliche del Senato della Repubblica italiana");

		Element  title_en = targetDoc.createElement("title");
		title_en.setAttribute("type", "main");
		title_en.setAttribute("xml:lang", "en");
		title_en.setTextContent("Minutes of the Senate of the Republic of Italy");

		Element  publisher_bib = targetDoc.createElement("publisher");
		publisher_bib.setTextContent("Senato della Repubblica");


		Element  idno_bib = targetDoc.createElement("idno");
		idno_bib.setAttribute("type", "URI");

		String strippedId = idDoc.startsWith("0")?idDoc.substring(1):idDoc;

		String resUrl = "http://www.senato.it/japp/bgt/showdoc/frame.jsp?tipodoc=Resaula&leg="+legislatura+"&id="+strippedId;
		idno_bib.setTextContent(resUrl);
		Element  date_bib = targetDoc.createElement("date");
		date_bib.setAttribute("when", data.substring(0,4)+"-"+data.substring(4,6)+"-"+data.substring(6,8));
		date_bib.setTextContent(data.substring(6,8)+"."+data.substring(4,6)+"."+data.substring(0,4));

		bibl.appendChild(title_it);
		bibl.appendChild(title_en);
		bibl.appendChild(publisher_bib);
		bibl.appendChild(idno_bib);
		bibl.appendChild(date_bib);

		sourceDesc.appendChild(bibl);



		// END

		fileDesc.appendChild(titleStmt);
		fileDesc.appendChild(editionStmt);
		fileDesc.appendChild(extent);
		fileDesc.appendChild(publicationStmt);
		fileDesc.appendChild(sourceDesc);





		return fileDesc;
	}
	
	
	
	public Element createEncodingDesc(Document targetDoc) {
		Element  encodingDesc =targetDoc.createElement("encodingDesc");
		
		encodingDesc.appendChild(getProjectDesc(targetDoc));
		encodingDesc.appendChild(getTagsDecl(targetDoc));
		
		return encodingDesc;
	}
	
	
	private Element getTagsDecl(Document targetDoc) {
		Element  tagsDecl = targetDoc.createElement("tagsDecl");
		Element  namespace = targetDoc.createElement("namespace");
		namespace.setAttribute("name", "http://www.tei-c.org/ns/1.0");
		
		// TAG_USAGE_1
		Element  tagUsage1 = targetDoc.createElement("tagUsage");
		tagUsage1.setAttribute("gi", "text");
		tagUsage1.setAttribute("occurs", "999");
		namespace.appendChild(tagUsage1);

		tagsDecl.appendChild(namespace);
		
		return tagsDecl;

	}
	
	
	
	public Element createProfileDesc(Document targetDoc, String data) {
		
		Element  profileDesc =targetDoc.createElement("profileDesc");


		Element  settingDesc = targetDoc.createElement("settingDesc");

		Element  setting = targetDoc.createElement("setting");
		Element  address = targetDoc.createElement("name");//Piazza Madama, 00186 Roma RM
		address.setAttribute("type", "address");
		address.setTextContent("Piazza Madama, 11");
		Element  city = targetDoc.createElement("name");
		city.setAttribute("type", "city");
		city.setTextContent("Roma");
		Element  country = targetDoc.createElement("name");
		country.setAttribute("type", "country");
		country.setAttribute("key", "IT");
		country.setTextContent("Italia");
		Element  date = targetDoc.createElement("date");
		date.setAttribute("ana", "#parla.sitting");
		date.setAttribute("when", data.substring(0,4)+"-"+data.substring(4,6)+"-"+data.substring(6,8));
		date.setTextContent(data.substring(6,8)+"."+data.substring(4,6)+"."+data.substring(0,4));


		setting.appendChild(address);
		setting.appendChild(city);
		setting.appendChild(country);
		setting.appendChild(date);

		settingDesc.appendChild(setting);

		profileDesc.appendChild(settingDesc);


		return profileDesc;
		
	}
	
	
	
	
	public Element createRevisionDesc(Document targetDoc) {
		Element  revisionDesc =targetDoc.createElement("revisionDesc");
		
		Element  change1 = targetDoc.createElement("change");
		change1.setAttribute("when", "2021-02-02");
		Element  name1 = targetDoc.createElement("name");
		name1.setTextContent("Tommaso Agnoloni");
		change1.appendChild(name1);
		Node textNode = targetDoc.createTextNode(":Made sample.");
		change1.appendChild(textNode);
		
		
		Element  change2 = targetDoc.createElement("change");
		change2.setAttribute("when", "2021-01-28");
		Element  name2 = targetDoc.createElement("name");
		name2.setTextContent("Tommaso Agnoloni");
		change2.appendChild(name2);
		Node textNode_2 = targetDoc.createTextNode(": Generated corpus in ParlaMint.");
		change2.appendChild(textNode_2);
		
		Element  change3 = targetDoc.createElement("change");
		change3.setAttribute("when", "2021-02-26");
		Element  name3 = targetDoc.createElement("name");
		name3.setTextContent("Tommaso Agnoloni");
		change3.appendChild(name3);
		Node textNode_3 = targetDoc.createTextNode(": Corpus revision, fixing");
		change3.appendChild(textNode_3);
		
		

		revisionDesc.appendChild(change1);
		revisionDesc.appendChild(change2);
		revisionDesc.appendChild(change3);


	
		return revisionDesc;
	}
	
	
	
	private Element getProjectDesc(Document targetCorpus) {
		Element  projectDesc = targetCorpus.createElement("projectDesc");
		
		Element p_it = targetCorpus.createElement("p");
		p_it.setAttribute("xml:lang", "it");
		Element ref_prjD_it = targetCorpus.createElement("ref");
		ref_prjD_it.setAttribute("target", "https://www.clarin.eu/content/parlamint");
		ref_prjD_it.setTextContent("ParlaMint");
		p_it.appendChild(ref_prjD_it);
		
		
		Element p_en = targetCorpus.createElement("p");
		p_en.setAttribute("xml:lang", "en");
		Element ref_prjD_en = targetCorpus.createElement("ref");
		ref_prjD_en.setAttribute("target", "https://www.clarin.eu/content/parlamint");
		ref_prjD_en.setTextContent("ParlaMint");
		p_en.appendChild(ref_prjD_en);
		
		
		p_en.appendChild(ref_prjD_en);
		
		projectDesc.appendChild(p_it);
		projectDesc.appendChild(p_en);

	
		return projectDesc;

	}
	

}
