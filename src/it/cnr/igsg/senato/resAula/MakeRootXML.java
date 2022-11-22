package it.cnr.igsg.senato.resAula;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.cnr.igsg.senato.Config;
import it.cnr.igsg.senato.datiSenato.Affiliation;
import it.cnr.igsg.senato.datiSenato.DatiSenato;
import it.cnr.igsg.senato.datiSenato.Gruppo;
import it.cnr.igsg.senato.datiSenato.LegislativeTerm;
import it.cnr.igsg.senato.datiSenato.Senatore;
import util.dom.UtilDom;

public class MakeRootXML {

	DatiSenato dati;

	public MakeRootXML() {
		dati = new DatiSenato();
		//Test();
	}

	HashMap<String,Integer> totalTags  = new HashMap<String,Integer>();
	HashMap<String,Integer> totalExtent  = new HashMap<String,Integer>();



	public void setTotalExtent(HashMap<String, Integer> totalExtent) {
		this.totalExtent = totalExtent;
	}

	public void setTotalTags(HashMap<String, Integer> totalTags) {
		this.totalTags = totalTags;
	}

	
	private String getIdGruppiList(String[] gruppiList) {
		
		HashMap<String, Gruppo> gruppi = dati.getLookupGruppobyName();
	
		String gruppi_ids = "";
		for(String item: gruppiList) {
			
			if(gruppi.get(item)!=null)
				gruppi_ids+="#group."+gruppi.get(item).getIdDenominazioneXML()+" ";
			else
				System.err.println("cannot find gruppo "+item);
		}
		gruppi_ids = gruppi_ids.trim();

		return gruppi_ids; 
	}
	
	public Document exportRelazioni(Document targetCorpus, Element appendTo) {
		Element listRelation = targetCorpus.createElement("listRelation");

		// 1. renaming relations
		for(String idGruppo:dati.getLookupGruppibyId().keySet()) {
			List<Gruppo> denominazioni = dati.getLookupGruppibyId().get(idGruppo);
			Collections.sort(denominazioni);
			Collections.reverse(denominazioni);
			// se ci sono state variazioni
			if(denominazioni.size()>1) {
				for(int i=0;i<denominazioni.size()-1;i++) {

					Gruppo current = denominazioni.get(i);
					Gruppo previous = denominazioni.get(i+1);


					Element relation = targetCorpus.createElement("relation");
					relation.setAttribute("name", "renaming");
					relation.setAttribute("active","#group."+current.getIdDenominazioneXML());
					relation.setAttribute("passive","#group."+previous.getIdDenominazioneXML());
					relation.setAttribute("when", current.getInizio_denominazione());
					listRelation.appendChild(relation);

				}

			}
		}
		

		// LETTA.1
		String[] coalition_letta1_groups_1= {"Partito Democratico", "Il Popolo della Libertà", "Scelta Civica per l'Italia", "Per le Autonomie (SVP, UV, PATT, UPT) - PSI"};
			
			Element coalition_letta1_1 = targetCorpus.createElement("relation");
			coalition_letta1_1.setAttribute("name", "coalition");
			coalition_letta1_1.setAttribute("mutual",getIdGruppiList(coalition_letta1_groups_1));
			coalition_letta1_1.setAttribute("from","2013-04-28");
			coalition_letta1_1.setAttribute("to", "2013-07-09");
			coalition_letta1_1.setAttribute("ana", "#GOV.LETTA.1");
			listRelation.appendChild(coalition_letta1_1);
		
		
		String[] coalition_letta1_groups_2= {"Partito Democratico", "Il Popolo della Libertà", "Scelta Civica per l'Italia", "Per le Autonomie (SVP-UV-PATT-UPT)-PSI-MAIE"};
		
			Element coalition_letta1_2 = targetCorpus.createElement("relation");
			coalition_letta1_2.setAttribute("name", "coalition");
			coalition_letta1_2.setAttribute("mutual",getIdGruppiList(coalition_letta1_groups_2));
			coalition_letta1_2.setAttribute("from","2013-07-10");
			coalition_letta1_2.setAttribute("to", "2013-11-19");
			coalition_letta1_2.setAttribute("ana", "#GOV.LETTA.1");
			listRelation.appendChild(coalition_letta1_2);
		
		String[] coalition_letta1_groups_3= {"Partito Democratico", "Nuovo Centrodestra", "Per l'Italia", "Per le Autonomie (SVP-UV-PATT-UPT)-PSI-MAIE"};
		
			Element coalition_letta1_3 = targetCorpus.createElement("relation");
			coalition_letta1_3.setAttribute("name", "coalition");
			coalition_letta1_3.setAttribute("mutual",getIdGruppiList(coalition_letta1_groups_3));
			coalition_letta1_3.setAttribute("from","2013-11-20");
			coalition_letta1_3.setAttribute("to", "2014-02-21");
			coalition_letta1_3.setAttribute("ana", "#GOV.LETTA.1");
			listRelation.appendChild(coalition_letta1_3);
		
		String[] opposition_letta1_groups_1= {"Lega Nord e Autonomie", "Movimento 5 Stelle"};
		
			Element opposition_letta1_1 = targetCorpus.createElement("relation");
			opposition_letta1_1.setAttribute("name", "opposition");
			opposition_letta1_1.setAttribute("active",getIdGruppiList(opposition_letta1_groups_1));
			opposition_letta1_1.setAttribute("from","2013-04-28");
			opposition_letta1_1.setAttribute("to", "2013-11-19");
			opposition_letta1_1.setAttribute("passive", "#GOV.LETTA.1");
			listRelation.appendChild(opposition_letta1_1);
		
		String[] opposition_letta1_groups_2= {"Forza Italia-Il Popolo della Libertà XVII Legislatura", "Lega Nord e Autonomie", "Movimento 5 Stelle"};

			Element opposition_letta1_2 = targetCorpus.createElement("relation");
			opposition_letta1_2.setAttribute("name", "opposition");
			opposition_letta1_2.setAttribute("active",getIdGruppiList(opposition_letta1_groups_2));
			opposition_letta1_2.setAttribute("from","2013-11-20");
			opposition_letta1_2.setAttribute("to", "2014-02-21");
			opposition_letta1_2.setAttribute("passive", "#GOV.LETTA.1");
			listRelation.appendChild(opposition_letta1_2);
		
		// RENZI.1
			
		String[] coalition_renzi1_groups_1= {"Partito Democratico", "Nuovo Centrodestra", "Per l'Italia", "Per le Autonomie (SVP-UV-PATT-UPT)-PSI-MAIE"};
		
			Element coalition_renzi1_1 = targetCorpus.createElement("relation");
			coalition_renzi1_1.setAttribute("name", "coalition");
			coalition_renzi1_1.setAttribute("mutual",getIdGruppiList(coalition_renzi1_groups_1));
			coalition_renzi1_1.setAttribute("from","2014-02-22");
			coalition_renzi1_1.setAttribute("to", "2014-12-10");
			coalition_renzi1_1.setAttribute("ana", "#GOV.RENZI.1");
			listRelation.appendChild(coalition_renzi1_1);
		
		
		String[] coalition_renzi1_groups_2= {"Partito Democratico", "Area Popolare (NCD-UDC)", "Per l'Italia", "Per le Autonomie (SVP-UV-PATT-UPT)-PSI-MAIE"};
		
			Element coalition_renzi1_2 = targetCorpus.createElement("relation");
			coalition_renzi1_2.setAttribute("name", "coalition");
			coalition_renzi1_2.setAttribute("mutual",getIdGruppiList(coalition_renzi1_groups_2));
			coalition_renzi1_2.setAttribute("from","2014-12-11");
			coalition_renzi1_2.setAttribute("to", "2016-12-12");
			coalition_renzi1_2.setAttribute("ana", "#GOV.RENZI.1");
			listRelation.appendChild(coalition_renzi1_2);
		
		String[] opposition_renzi1_groups_1= {"Forza Italia-Il Popolo della Libertà XVII Legislatura", "Lega Nord e Autonomie", "Movimento 5 Stelle"};
		
			Element opposition_renzi1_1 = targetCorpus.createElement("relation");
			opposition_renzi1_1.setAttribute("name", "opposition");
			opposition_renzi1_1.setAttribute("active",getIdGruppiList(opposition_renzi1_groups_1));
			opposition_renzi1_1.setAttribute("from","2014-02-22");
			opposition_renzi1_1.setAttribute("to", "2016-12-12");
			opposition_renzi1_1.setAttribute("passive", "#GOV.RENZI.1");
			listRelation.appendChild(opposition_renzi1_1);
		
		
		// GENTILONI.1
		String[] coalition_gentiloni1_groups_1= {"Partito Democratico", "Per le Autonomie (SVP-UV-PATT-UPT)-PSI-MAIE"};
		
			Element coalition_gentiloni1_1 = targetCorpus.createElement("relation");
			coalition_gentiloni1_1.setAttribute("name", "coalition");
			coalition_gentiloni1_1.setAttribute("mutual",getIdGruppiList(coalition_gentiloni1_groups_1));
			coalition_gentiloni1_1.setAttribute("from","2016-12-12");
			coalition_gentiloni1_1.setAttribute("to", "2017-05-08");
			coalition_gentiloni1_1.setAttribute("ana", "#GOV.GENTILONI.1");
			listRelation.appendChild(coalition_gentiloni1_1);
		
		
		String[] coalition_gentiloni1_groups_2= {"Partito Democratico", "Alternativa Popolare - Centristi per l'Europa - NCD", "Per le Autonomie (SVP-UV-PATT-UPT)-PSI-MAIE"};
		
			Element coalition_gentiloni1_2 = targetCorpus.createElement("relation");
			coalition_gentiloni1_2.setAttribute("name", "coalition");
			coalition_gentiloni1_2.setAttribute("mutual",getIdGruppiList(coalition_gentiloni1_groups_2));
			coalition_gentiloni1_2.setAttribute("from","2017-05-09");
			coalition_gentiloni1_2.setAttribute("to", "2018-03-22");
			coalition_gentiloni1_2.setAttribute("ana", "#GOV.GENTILONI.1");
			listRelation.appendChild(coalition_gentiloni1_2);
		
		String[] coalition_gentiloni1_groups_3= {"Partito Democratico", "Per le Autonomie (SVP-PATT, UV)"};
		
			Element coalition_gentiloni1_3 = targetCorpus.createElement("relation");
			coalition_gentiloni1_3.setAttribute("name", "coalition");
			coalition_gentiloni1_3.setAttribute("mutual",getIdGruppiList(coalition_gentiloni1_groups_3));
			coalition_gentiloni1_3.setAttribute("from","2018-03-23");
			coalition_gentiloni1_3.setAttribute("to", "2018-06-01");
			coalition_gentiloni1_3.setAttribute("ana", "#GOV.GENTILONI.1");
			listRelation.appendChild(coalition_gentiloni1_3);
		
		String[] opposition_gentiloni1_groups_1= {"Forza Italia-Il Popolo della Libertà XVII Legislatura", "Lega Nord e Autonomie", "Movimento 5 Stelle"};
		
			Element opposition_gentiloni1_1 = targetCorpus.createElement("relation");
			opposition_gentiloni1_1.setAttribute("name", "opposition");
			opposition_gentiloni1_1.setAttribute("active",getIdGruppiList(opposition_gentiloni1_groups_1));
			opposition_gentiloni1_1.setAttribute("from","2016-12-12");
			opposition_gentiloni1_1.setAttribute("to", "2017-02-27");
			opposition_gentiloni1_1.setAttribute("passive", "#GOV.GENTILONI.1");
			listRelation.appendChild(opposition_gentiloni1_1);
	
		
//		String[] opposition_gentiloni1_groups_2= {"Forza Italia-Il Popolo della Libertà XVII Legislatura", "Lega Nord e Autonomie", "Movimento 5 Stelle", "Articolo 1 - Movimento democratico e progressista"};
//		
//			Element opposition_gentiloni1_2 = targetCorpus.createElement("relation");
//			opposition_gentiloni1_2.setAttribute("name", "opposition");
//			opposition_gentiloni1_2.setAttribute("active",getIdGruppiList(opposition_gentiloni1_groups_2));
//			opposition_gentiloni1_2.setAttribute("from","2017-02-28");
//			opposition_gentiloni1_2.setAttribute("to", "2017-12-12");
//			opposition_gentiloni1_2.setAttribute("passive", "#GOV.GENTILONI.1");
//			listRelation.appendChild(opposition_gentiloni1_2);
//		
//		String[] opposition_gentiloni1_groups_3= {"Forza Italia-Il Popolo della Libertà XVII Legislatura", "Lega Nord e Autonomie", "Movimento 5 Stelle", "Articolo 1 - Movimento Democratico e Progressista - Liberi e Uguali"};
//		
//			Element opposition_gentiloni1_3 = targetCorpus.createElement("relation");
//			opposition_gentiloni1_3.setAttribute("name", "opposition");
//			opposition_gentiloni1_3.setAttribute("active",getIdGruppiList(opposition_gentiloni1_groups_3));
//			opposition_gentiloni1_3.setAttribute("from","2017-12-13");
//			opposition_gentiloni1_3.setAttribute("to", "2018-03-22");
//			opposition_gentiloni1_3.setAttribute("passive", "#GOV.GENTILONI.1");
//			listRelation.appendChild(opposition_gentiloni1_3);
			
		String[] opposition_gentiloni1_groups_2= {"Forza Italia-Il Popolo della Libertà XVII Legislatura", "Lega Nord e Autonomie", "Movimento 5 Stelle", "Articolo 1 - Movimento democratico e progressista"};
			
			Element opposition_gentiloni1_2 = targetCorpus.createElement("relation");
			opposition_gentiloni1_2.setAttribute("name", "opposition");
			opposition_gentiloni1_2.setAttribute("active",getIdGruppiList(opposition_gentiloni1_groups_2));
			opposition_gentiloni1_2.setAttribute("from","2017-02-28");
			opposition_gentiloni1_2.setAttribute("to", "2018-03-22");
			opposition_gentiloni1_2.setAttribute("passive", "#GOV.GENTILONI.1");
			listRelation.appendChild(opposition_gentiloni1_2);
		
			
		// rimossa denominazione Articolo 1 - Movimento Democratico e Progressista - Liberi e Uguali in tab_denominazione_gruppi (non a affiliati)
//		String[] opposition_gentiloni1_groups_3= {"Forza Italia-Il Popolo della Libertà XVII Legislatura", "Lega Nord e Autonomie", "Movimento 5 Stelle", "Articolo 1 - Movimento Democratico e Progressista - Liberi e Uguali"};
//		
//			Element opposition_gentiloni1_3 = targetCorpus.createElement("relation");
//			opposition_gentiloni1_3.setAttribute("name", "opposition");
//			opposition_gentiloni1_3.setAttribute("active",getIdGruppiList(opposition_gentiloni1_groups_3));
//			opposition_gentiloni1_3.setAttribute("from","2017-12-13");
//			opposition_gentiloni1_3.setAttribute("to", "2018-03-22");
//			opposition_gentiloni1_3.setAttribute("passive", "#GOV.GENTILONI.1");
//			listRelation.appendChild(opposition_gentiloni1_3);
			
		String[] opposition_gentiloni1_groups_4= {"Forza Italia-Berlusconi Presidente", "Lega-Salvini Premier", "Fratelli d'Italia", "MoVimento 5 Stelle"};

			Element opposition_gentiloni1_4 = targetCorpus.createElement("relation");
			opposition_gentiloni1_4.setAttribute("name", "opposition");
			opposition_gentiloni1_4.setAttribute("active",getIdGruppiList(opposition_gentiloni1_groups_4));
			opposition_gentiloni1_4.setAttribute("from","2018-03-23");
			opposition_gentiloni1_4.setAttribute("to", "2018-06-01");
			opposition_gentiloni1_4.setAttribute("passive", "#GOV.GENTILONI.1");
			listRelation.appendChild(opposition_gentiloni1_4);
		
		
		// CONTE.1
		String[] coalition_conte1_groups_1= {"MoVimento 5 Stelle", "Lega-Salvini Premier"};
		
			Element coalition_conte1_1 = targetCorpus.createElement("relation");
			coalition_conte1_1.setAttribute("name", "coalition");
			coalition_conte1_1.setAttribute("mutual",getIdGruppiList(coalition_conte1_groups_1));
			coalition_conte1_1.setAttribute("from","2018-06-01");
			coalition_conte1_1.setAttribute("to", "2018-07-29");
			coalition_conte1_1.setAttribute("ana", "#GOV.CONTE.1");
			listRelation.appendChild(coalition_conte1_1);
		
		String[] coalition_conte1_groups_2= {"MoVimento 5 Stelle", "Lega-Salvini Premier-Partito Sardo d'Azione"};
		
			Element coalition_conte1_2 = targetCorpus.createElement("relation");
			coalition_conte1_2.setAttribute("name", "coalition");
			coalition_conte1_2.setAttribute("mutual",getIdGruppiList(coalition_conte1_groups_2));
			coalition_conte1_2.setAttribute("from","2018-07-30");
			coalition_conte1_2.setAttribute("to", "2019-09-05");
			coalition_conte1_2.setAttribute("ana", "#GOV.CONTE.1");
			listRelation.appendChild(coalition_conte1_2);
			
		String[] opposition_conte1_groups_1= {"Partito Democratico", "Forza Italia-Berlusconi Presidente", "Fratelli d'Italia", "Per le Autonomie (SVP-PATT, UV)"};

			Element opposition_conte1_1 = targetCorpus.createElement("relation");
			opposition_conte1_1.setAttribute("name", "opposition");
			opposition_conte1_1.setAttribute("active",getIdGruppiList(opposition_conte1_groups_1));
			opposition_conte1_1.setAttribute("from","2018-06-01");
			opposition_conte1_1.setAttribute("to", "2019-09-05");
			opposition_conte1_1.setAttribute("passive", "#GOV.CONTE.1");
			listRelation.appendChild(opposition_conte1_1);
		
		// CONTE.2
		String[] coalition_conte2_groups_1= {"MoVimento 5 Stelle", "Partito Democratico", "Per le Autonomie (SVP-PATT, UV)"};
		
			Element coalition_conte2_1 = targetCorpus.createElement("relation");
			coalition_conte2_1.setAttribute("name", "coalition");
			coalition_conte2_1.setAttribute("mutual",getIdGruppiList(coalition_conte2_groups_1));
			coalition_conte2_1.setAttribute("from","2019-09-05");
			coalition_conte2_1.setAttribute("to", "2019-09-17");
			coalition_conte2_1.setAttribute("ana", "#GOV.CONTE.2");
			listRelation.appendChild(coalition_conte2_1);
		
		
		String[] coalition_conte2_groups_2= {"MoVimento 5 Stelle", "Partito Democratico", "Italia Viva-P.S.I.", "Per le Autonomie (SVP-PATT, UV)"};
		
			Element coalition_conte2_2 = targetCorpus.createElement("relation");
			coalition_conte2_2.setAttribute("name", "coalition");
			coalition_conte2_2.setAttribute("mutual",getIdGruppiList(coalition_conte2_groups_2));
			coalition_conte2_2.setAttribute("from","2019-09-18");
			coalition_conte2_2.setAttribute("to", "2021-02-12");
			coalition_conte2_2.setAttribute("ana", "#GOV.CONTE.2");
			listRelation.appendChild(coalition_conte2_2);
		
		String[] opposition_conte2_groups_1= {"Forza Italia-Berlusconi Presidente", "Lega-Salvini Premier-Partito Sardo d'Azione", "Fratelli d'Italia"};
		
			Element opposition_conte2_1 = targetCorpus.createElement("relation");
			opposition_conte2_1.setAttribute("name", "opposition");
			opposition_conte2_1.setAttribute("active",getIdGruppiList(opposition_conte2_groups_1));
			opposition_conte2_1.setAttribute("from","2019-09-05");
			opposition_conte2_1.setAttribute("to", "2019-12-10");
			opposition_conte2_1.setAttribute("passive", "#GOV.CONTE.2");
			listRelation.appendChild(opposition_conte2_1);
		
		
		String[] opposition_conte2_groups_2= {"Forza Italia Berlusconi Presidente-UDC", "Lega-Salvini Premier-Partito Sardo d'Azione", "Fratelli d'Italia"};

		
			Element opposition_conte2_2 = targetCorpus.createElement("relation");
			opposition_conte2_2.setAttribute("name", "opposition");
			opposition_conte2_2.setAttribute("active",getIdGruppiList(opposition_conte2_groups_2));
			opposition_conte2_2.setAttribute("from","2019-12-11");
			opposition_conte2_2.setAttribute("to", "2021-02-12");
			opposition_conte2_2.setAttribute("passive", "#GOV.CONTE.2");
			listRelation.appendChild(opposition_conte2_2);

			
		// DRAGHI.1
		String[] coalition_draghi1_groups_1= {"MoVimento 5 Stelle", "Partito Democratico", "Per le Autonomie (SVP-PATT, UV)", "Forza Italia-Berlusconi Presidente", "Lega-Salvini Premier-Partito Sardo d'Azione"};
		
			Element coalition_draghi1_1 = targetCorpus.createElement("relation");
			coalition_draghi1_1.setAttribute("name", "coalition");
			coalition_draghi1_1.setAttribute("mutual",getIdGruppiList(coalition_draghi1_groups_1));
			coalition_draghi1_1.setAttribute("from","2021-02-13");
			coalition_draghi1_1.setAttribute("to", "2022-10-22");
			coalition_draghi1_1.setAttribute("ana", "#GOV.DRAGHI.1");
			listRelation.appendChild(coalition_draghi1_1);
			
		String[] opposition_draghi1_groups_1= {"Fratelli d'Italia"};
		
			Element opposition_draghi1_1 = targetCorpus.createElement("relation");
			opposition_draghi1_1.setAttribute("name", "opposition");
			opposition_draghi1_1.setAttribute("active",getIdGruppiList(opposition_draghi1_groups_1));
			opposition_draghi1_1.setAttribute("from","2021-02-13");
			opposition_draghi1_1.setAttribute("to", "2022-10-22");
			opposition_draghi1_1.setAttribute("passive", "#GOV.DRAGHI.1");
			listRelation.appendChild(opposition_draghi1_1);

		appendTo.appendChild(listRelation);

		return targetCorpus;
	}






	public Document exportLegislatures(Document targetCorpus, Element appendTo) {

		Element  org = targetCorpus.createElement("org");
		org.setAttribute("xml:id", "LEG");
		org.setAttribute("role", "parliament");
		org.setAttribute("ana", "#parla.national #parla.upper");


		Element  orgName_IT = targetCorpus.createElement("orgName");
		orgName_IT.setAttribute("xml:lang", "it");
		orgName_IT.setAttribute("full", "yes");
		orgName_IT.setTextContent("Senato della Repubblica Italiana");
		Element  orgName_EN = targetCorpus.createElement("orgName");
		orgName_EN.setAttribute("xml:lang", "it");
		orgName_EN.setAttribute("full", "yes");
		orgName_EN.setTextContent("Senate of the Republic of Italy");
		org.appendChild(orgName_IT);
		org.appendChild(orgName_EN);

		//		<event from="1990-05-16">
		//    		<label xml:lang="en">existence</label>
		//    	</event>

		Element  idno_IT = targetCorpus.createElement("idno");
		idno_IT.setAttribute("xml:lang", "it");
		idno_IT.setAttribute("type", "wikimedia");
		idno_IT.setTextContent("https://it.wikipedia.org/wiki/Senato_della_Repubblica");
		Element  idno_EN = targetCorpus.createElement("idno");
		idno_EN.setAttribute("xml:lang", "it");
		idno_EN.setAttribute("type", "wikimedia");
		idno_EN.setTextContent("https://en.wikipedia.org/wiki/Senate_of_the_Republic_(Italy)");
		org.appendChild(idno_IT);
		org.appendChild(idno_EN);

		Element  listEvent = targetCorpus.createElement("listEvent");


		//LEG17 
		Element  LEG17 = targetCorpus.createElement("event");
		LEG17.setAttribute("xml:id", "LEG.17");
		LEG17.setAttribute("from", "2013-03-15");
		LEG17.setAttribute("to", "2018-03-22");
		Element  label1_IT = targetCorpus.createElement("label");
		label1_IT.setAttribute("xml:lang", "it");
		label1_IT.setTextContent("XVII Legislatura");
		LEG17.appendChild(label1_IT);
		Element  label1_EN = targetCorpus.createElement("label");
		label1_EN.setAttribute("xml:lang", "en");
		label1_EN.setTextContent("XVII Legislative Term");
		LEG17.appendChild(label1_EN);

		//LEG18 
		Element  LEG18 = targetCorpus.createElement("event");
		LEG18.setAttribute("xml:id", "LEG.18");
		LEG18.setAttribute("from", "2018-03-23");
		LEG18.setAttribute("to", "2022-09-24");
		Element  label2_IT = targetCorpus.createElement("label");
		label2_IT.setAttribute("xml:lang", "it");
		label2_IT.setTextContent("XVIII Legislatura");
		LEG18.appendChild(label2_IT);
		Element  label2_EN = targetCorpus.createElement("label");
		label2_EN.setAttribute("xml:lang", "en");
		label2_EN.setTextContent("XVIII Legislative Term");
		LEG18.appendChild(label2_EN);


		listEvent.appendChild(LEG17);
		listEvent.appendChild(LEG18);



		org.appendChild(listEvent);


		appendTo.appendChild(org);

		return targetCorpus;
	}




	public Document exportGovernments(Document targetCorpus, Element appendTo) {

		Element  org = targetCorpus.createElement("org");
		org.setAttribute("xml:id", "GOV");
		org.setAttribute("role", "government");

		Element  orgName_IT = targetCorpus.createElement("orgName");
		orgName_IT.setAttribute("xml:lang", "it");
		orgName_IT.setAttribute("full", "yes");
		orgName_IT.setTextContent("Governo della Repubblica Italiana");
		Element  orgName_EN = targetCorpus.createElement("orgName");
		orgName_EN.setAttribute("xml:lang", "it");
		orgName_EN.setAttribute("full", "yes");
		orgName_EN.setTextContent("Government of the Republic of Italy");
		org.appendChild(orgName_IT);
		org.appendChild(orgName_EN);

		//		<event from="1990-05-16">
		//    		<label xml:lang="en">existence</label>
		//    	</event>

		Element  idno_IT = targetCorpus.createElement("idno");
		idno_IT.setAttribute("xml:lang", "it");
		idno_IT.setAttribute("type", "wikimedia");
		idno_IT.setTextContent("https://it.wikipedia.org/wiki/Governo_della_Repubblica_Italiana");
		Element  idno_EN = targetCorpus.createElement("idno");
		idno_EN.setAttribute("xml:lang", "it");
		idno_EN.setAttribute("type", "wikimedia");
		idno_EN.setTextContent("https://en.wikipedia.org/wiki/Government_of_Italy");
		org.appendChild(idno_IT);
		org.appendChild(idno_EN);

		Element  listEvent = targetCorpus.createElement("listEvent");


		//LETTA 
		Element  GOV1 = targetCorpus.createElement("event");
		GOV1.setAttribute("xml:id", "GOV.LETTA.1");
		GOV1.setAttribute("from", "2013-04-28");
		GOV1.setAttribute("to", "2014-02-22");
		Element  label1_IT = targetCorpus.createElement("label");
		label1_IT.setAttribute("xml:lang", "it");
		label1_IT.setTextContent("Governo Letta (28 aprile 2013 - 22 febbraio 2014)");
		GOV1.appendChild(label1_IT);
		Element  label1_EN = targetCorpus.createElement("label");
		label1_EN.setAttribute("xml:lang", "en");
		label1_EN.setTextContent("Government Letta (28 April 2013 - 22 February 2014)");
		GOV1.appendChild(label1_EN);


		//RENZI 
		Element  GOV2 = targetCorpus.createElement("event");
		GOV2.setAttribute("xml:id", "GOV.RENZI.1");
		GOV2.setAttribute("from", "2014-02-22");
		GOV2.setAttribute("to", "2016-12-12");
		Element  label2_IT = targetCorpus.createElement("label");
		label2_IT.setAttribute("xml:lang", "it");
		label2_IT.setTextContent("Governo Renzi (22 febbraio 2014 - 12 dicembre 2016)");
		GOV2.appendChild(label2_IT);
		Element  label2_EN = targetCorpus.createElement("label");
		label2_EN.setAttribute("xml:lang", "en");
		label2_EN.setTextContent("Government Renzi (22 February 2014 - 12 December 2016)");
		GOV2.appendChild(label2_EN);


		//GENTILONI 
		Element  GOV3 = targetCorpus.createElement("event");
		GOV3.setAttribute("xml:id", "GOV.GENTILONI.1");
		GOV3.setAttribute("from", "2016-12-12");
		GOV3.setAttribute("to", "2018-06-01");
		Element  label3_IT = targetCorpus.createElement("label");
		label3_IT.setAttribute("xml:lang", "it");
		label3_IT.setTextContent("Governo Gentiloni (12 dicembre 2016 - 1 giugno 2018)");
		GOV3.appendChild(label3_IT);
		Element  label3_EN = targetCorpus.createElement("label");
		label3_EN.setAttribute("xml:lang", "en");
		label3_EN.setTextContent("Government Gentiloni (12 December 2016 - 1 June 2018)");
		GOV3.appendChild(label3_EN);


		//CONTE1 
		Element  GOV4 = targetCorpus.createElement("event");
		GOV4.setAttribute("xml:id", "GOV.CONTE.1");
		GOV4.setAttribute("from", "2018-06-01");
		GOV4.setAttribute("to", "2019-09-05");
		Element  label4_IT = targetCorpus.createElement("label");
		label4_IT.setAttribute("xml:lang", "it");
		label4_IT.setTextContent("Governo Conte (1 giugno 2018 - 5 settembre 2019)");
		GOV4.appendChild(label4_IT);
		Element  label4_EN = targetCorpus.createElement("label");
		label4_EN.setAttribute("xml:lang", "en");
		label4_EN.setTextContent("Government Conte (1 June 2018 - 5 September 2019)");
		GOV4.appendChild(label4_EN);


		//CONTE2 
		Element  GOV5 = targetCorpus.createElement("event");
		GOV5.setAttribute("xml:id", "GOV.CONTE.2");
		GOV5.setAttribute("from", "2019-09-05");
		GOV5.setAttribute("to", "2021-02-12");
		Element  label5_IT = targetCorpus.createElement("label");
		label5_IT.setAttribute("xml:lang", "it");
		label5_IT.setTextContent("Governo Conte II (5 settembre 2019 - 12 febbraio 2021)");
		GOV5.appendChild(label5_IT);
		Element  label5_EN = targetCorpus.createElement("label");
		label5_EN.setAttribute("xml:lang", "en");
		label5_EN.setTextContent("Government Conte II (5 September 2019 - 12 February 2021)");
		GOV5.appendChild(label5_EN);
		
		
		// FIXME data fine gov draghi non si sa (vedi anche affiliazioni_gov_v2.tsv)
		//DRAGHI1 
		Element  GOV6 = targetCorpus.createElement("event");
		GOV6.setAttribute("xml:id", "GOV.DRAGHI.1");
		GOV6.setAttribute("from", "2021-02-13");
		GOV6.setAttribute("to", "2022-10-22");
		Element  label6_IT = targetCorpus.createElement("label");
		label6_IT.setAttribute("xml:lang", "it");
		label6_IT.setTextContent("Governo Draghi I (13 febbraio 2021 - 22 ottobre 2022)");
		GOV6.appendChild(label6_IT);
		Element  label6_EN = targetCorpus.createElement("label");
		label6_EN.setAttribute("xml:lang", "en");
		label6_EN.setTextContent("Government Draghi I (13 February 2021 - 22 October 2022)");
		GOV6.appendChild(label6_EN);


		listEvent.appendChild(GOV1);
		listEvent.appendChild(GOV2);
		listEvent.appendChild(GOV3);
		listEvent.appendChild(GOV4);
		listEvent.appendChild(GOV5);
		listEvent.appendChild(GOV6);

		org.appendChild(listEvent);


		appendTo.appendChild(org);

		return targetCorpus;
	}

	//	<org xml:id="party.DL" role="political_party">
	//    <orgName full="yes" xml:lang="sl">Državljanska lista</orgName>
	//    <orgName full="yes" xml:lang="en">Civic List</orgName>
	//    <orgName full="init">DL</orgName>
	//    <event from="2012-04-24">
	//       <label xml:lang="en">existence</label>
	//    </event>
	//    <idno type="wikimedia" xml:lang="sl">https://sl.wikipedia.org/wiki/Dr%C5%BEavljanska_lista</idno>
	//    <idno type="wikimedia" xml:lang="en">https://en.wikipedia.org/wiki/Civic_List_(Slovenia)</idno>
	// </org>



	public Document exportGruppi(Document targetCorpus, Element appendTo) {



		HashMap<String, Gruppo> gruppiMap = dati.getLookupGruppobyName();

		for(String nomeGruppo:gruppiMap.keySet()) {
			Gruppo gruppoItem = gruppiMap.get(nomeGruppo);

			Element  org = targetCorpus.createElement("org");
			org.setAttribute("xml:id", "group."+gruppoItem.getIdDenominazioneXML());
			// FIXME: political_party? 
			org.setAttribute("role", "parliamentaryGroup");

			Element  orgName = targetCorpus.createElement("orgName");
			orgName.setAttribute("full", "yes");
			orgName.setAttribute("xml:lang", "it");
			orgName.setTextContent(gruppoItem.getDenominazione());
			org.appendChild(orgName);

			Element  orgNameAbbr = targetCorpus.createElement("orgName");
			orgNameAbbr.setAttribute("full", "abb");
			orgNameAbbr.setTextContent(gruppoItem.getAbbreviazione());
			org.appendChild(orgNameAbbr);

			Element  event = targetCorpus.createElement("event");
			event.setAttribute("from", gruppoItem.getInizio_denominazione());
			if(gruppoItem.getFine_denominazione().trim().length()>0)
				event.setAttribute("to", gruppoItem.getFine_denominazione());

			Element  label = targetCorpus.createElement("label");
			label.setAttribute("xml:lang", "en");
			label.setTextContent("existence");
			event.appendChild(label);

			org.appendChild(event);

			//		    <event from="2012-04-24">
			//	       <label xml:lang="en">existence</label>
			//	    </event>

			appendTo.appendChild(org);


		}

		return targetCorpus;
	}



	public Document makeFileDesc(Document targetCorpus, Element appendTo, boolean ANA) {


		boolean SENATO_RESP = true;


		Element  fileDesc = targetCorpus.createElement("fileDesc");


		// 1. titleStmt
		Element  titleStmt = targetCorpus.createElement("titleStmt");
		Element  titleMainIT = targetCorpus.createElement("title");
		titleMainIT.setAttribute("type", "main");
		titleMainIT.setAttribute("xml:lang", "it");
		if(!ANA)
			titleMainIT.setTextContent("Corpus parlamentare italiano ParlaMint-IT [ParlaMint]");
		else
			titleMainIT.setTextContent("Corpus parlamentare italiano ParlaMint-IT [ParlaMint.ana]");


		Element  titleMainEN = targetCorpus.createElement("title");
		titleMainEN.setAttribute("type", "main");
		titleMainEN.setAttribute("xml:lang", "en");
		if(!ANA)
			titleMainEN.setTextContent("Italian parliamentary corpus ParlaMint-IT [ParlaMint]");
		else
			titleMainEN.setTextContent("Italian parliamentary corpus ParlaMint-IT [ParlaMint.ana]");
			

		Element  titleSubIT = targetCorpus.createElement("title");
		titleSubIT.setAttribute("type", "sub");
		titleSubIT.setAttribute("xml:lang", "it");
		titleSubIT.setTextContent("Resoconti del Senato della Repubblica italiana, legislature XVII e XVIII (2013 - 2022)");

		Element  titleSubEN = targetCorpus.createElement("title");
		titleSubEN.setAttribute("type", "sub");
		titleSubEN.setAttribute("xml:lang", "en");
		titleSubEN.setTextContent("Minutes of the Senate of the Italian Republic, terms 17 and 18 (2013 - 2022)");

		Element  meeting17 = targetCorpus.createElement("meeting");
		meeting17.setAttribute("n", "17-upper");
		meeting17.setAttribute("corresp", "#LEG");
		meeting17.setAttribute("ana", "#parla.upper #parla.term #LEG.17");
		meeting17.setTextContent("XVII Legislatura");

		Element  meeting18 = targetCorpus.createElement("meeting");
		meeting18.setAttribute("n", "18-upper");
		meeting18.setAttribute("corresp", "#LEG");
		meeting18.setAttribute("ana", "#parla.upper #parla.term #LEG.18");
		meeting18.setTextContent("XVIII Legislatura");


		// RESP_STMT_CORPUS


		//		Element  respStmt = targetCorpus.createElement("respStmt");
		//		Element  persName = targetCorpus.createElement("persName");
		//		persName.setTextContent("Tommaso Agnoloni");
		//		
		//		Element  respIT = targetCorpus.createElement("resp");
		//		respIT.setAttribute("xml:lang", "it");
		//		respIT.setTextContent("Codifica corpus Parla-CLARIN TEI XML");
		//
		//		Element  respEN= targetCorpus.createElement("resp");
		//		respEN.setAttribute("xml:lang", "en");
		//		respEN.setTextContent("Parla-CLARIN TEI XML corpus encoding");
		//		
		//		respStmt.appendChild(persName);
		//		respStmt.appendChild(respIT);
		//		respStmt.appendChild(respEN);


		//////////////////////////////////////////


		// RESP_STMT_CORPUS (NEW)

		String orcidAgnoloni ="https://orcid.org/0000-0003-3063-2239";
		String orcidBartolini ="https://orcid.org/0000-0002-6829-6309";

		String orcidFrontini ="https://orcid.org/0000-0002-8126-6294";
		String orcidMontemagni ="https://orcid.org/0000-0002-2953-8619";
		String orcidVenturi ="https://orcid.org/0000-0001-5849-0979";
		String orcidQuochi ="https://orcid.org/0000-0002-1321-5444";


		// PERSONS

		Element  persNameAgnoloniORCID = targetCorpus.createElement("persName");
		persNameAgnoloniORCID.setTextContent("Tommaso Agnoloni");
		persNameAgnoloniORCID.setAttribute("ref", orcidAgnoloni);
		
		Element  persNameBartoliniORCID = targetCorpus.createElement("persName");
		persNameBartoliniORCID.setTextContent("Roberto Bartolini");
		persNameBartoliniORCID.setAttribute("ref", orcidBartolini);

		Element  persNameFrontiniORCID = targetCorpus.createElement("persName");
		persNameFrontiniORCID.setTextContent("Francesca Frontini");
		persNameFrontiniORCID.setAttribute("ref", orcidFrontini);

		Element  persNameMontemagniORCID = targetCorpus.createElement("persName");
		persNameMontemagniORCID.setTextContent("Simonetta Montemagni");
		persNameMontemagniORCID.setAttribute("ref", orcidMontemagni);

		Element  persNameQuochiORCID = targetCorpus.createElement("persName");
		persNameQuochiORCID.setTextContent("Valeria Quochi");
		persNameQuochiORCID.setAttribute("ref", orcidQuochi);

		Element  persNameVenturiORCID = targetCorpus.createElement("persName");
		persNameVenturiORCID.setTextContent("Giulia Venturi");
		persNameVenturiORCID.setAttribute("ref", orcidVenturi);

		Element  persNameAgnoloni = targetCorpus.createElement("persName");
		persNameAgnoloni.setTextContent("Tommaso Agnoloni");

		Element  persNameQuochi = targetCorpus.createElement("persName");
		persNameQuochi.setTextContent("Valeria Quochi");

		Element  persNameVenturi = targetCorpus.createElement("persName");
		persNameVenturi.setTextContent("Giulia Venturi");

		Element  persNameRuisi = targetCorpus.createElement("persName");
		persNameRuisi.setTextContent("Manuela Ruisi");

		Element  persNameMarchetti = targetCorpus.createElement("persName");
		persNameMarchetti.setTextContent("Carlo Marchetti");

		Element  persNameBattistoni = targetCorpus.createElement("persName");
		persNameBattistoni.setTextContent("Roberto Battistoni");

		Element  persNameCimino = targetCorpus.createElement("persName");
		persNameCimino.setTextContent("Andrea Cimino");

		Element  persNameBartolini = targetCorpus.createElement("persName");
		persNameBartolini.setTextContent("Roberto Bartolini");


		// RESPONSIBILITY

		// PROJECT

		Element  respStmtProject = targetCorpus.createElement("respStmt");
		Element  persName0 = targetCorpus.createElement("persName");
		persName0.setTextContent("Tommaso Agnoloni");

		Element  respProjectIT = targetCorpus.createElement("resp");
		respProjectIT.setAttribute("xml:lang", "it");
		respProjectIT.setTextContent("Definizione del progetto e metodologia");

		Element  respProjectEN= targetCorpus.createElement("resp");
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
		Element  respStmtSenato = targetCorpus.createElement("respStmt");
		Element  respSenatoIT = targetCorpus.createElement("resp");
		respSenatoIT.setAttribute("xml:lang", "it");
		respSenatoIT.setTextContent("Recupero dei dati");

		Element  respSenatoEN= targetCorpus.createElement("resp");
		respSenatoEN.setAttribute("xml:lang", "en");
		respSenatoEN.setTextContent("Data retrieval");

		respStmtSenato.appendChild(persNameRuisi);
		respStmtSenato.appendChild(persNameMarchetti);
		respStmtSenato.appendChild(persNameBattistoni);

		respStmtSenato.appendChild(respSenatoIT);
		respStmtSenato.appendChild(respSenatoEN);

		//////////////////////////////////////////

		// CODIFICA
		Element  respStmtCodifica = targetCorpus.createElement("respStmt");

		Element  respCodificaIT_0 = targetCorpus.createElement("resp");
		respCodificaIT_0.setAttribute("xml:lang", "it");
		respCodificaIT_0.setTextContent("Codifica corpus in ParlaMint TEI XML");

		Element  respCodificaEN_0= targetCorpus.createElement("resp");
		respCodificaEN_0.setAttribute("xml:lang", "en");
		respCodificaEN_0.setTextContent("ParlaMint TEI XML corpus encoding");

		Element  respCodificaIT_1 = targetCorpus.createElement("resp");
		respCodificaIT_1.setAttribute("xml:lang", "it");
		respCodificaIT_1.setTextContent("Pulizia, normalizzazione e conversione in ParlaMint TEI XML");

		Element  respCodificaEN_1= targetCorpus.createElement("resp");
		respCodificaEN_1.setAttribute("xml:lang", "en");
		respCodificaEN_1.setTextContent("Cleaning, normalisation and conversion to ParlaMint TEI XML");

		respStmtCodifica.appendChild(persNameAgnoloni);


		respStmtCodifica.appendChild(respCodificaIT_0);
		respStmtCodifica.appendChild(respCodificaEN_0);
		respStmtCodifica.appendChild(respCodificaIT_1);
		respStmtCodifica.appendChild(respCodificaEN_1);


		//////////////////////////////////////////


		// LINGUISTIC
		Element  respStmtLinguistic = targetCorpus.createElement("respStmt");

		Element  respLinguisticIT_0 = targetCorpus.createElement("resp");
		respLinguisticIT_0.setAttribute("xml:lang", "it");
		respLinguisticIT_0.setTextContent("Annotazione linguistica automatica");

		Element  respLinguisticEN_0= targetCorpus.createElement("resp");
		respLinguisticEN_0.setAttribute("xml:lang", "en");
		respLinguisticEN_0.setTextContent("Automatic Linguistic annotation");

		Element  respLinguisticIT_1 = targetCorpus.createElement("resp");
		respLinguisticIT_1.setAttribute("xml:lang", "it");
		respLinguisticIT_1.setTextContent("Riconoscimento Entità Nominate");

		Element  respLinguisticEN_1= targetCorpus.createElement("resp");
		respLinguisticEN_1.setAttribute("xml:lang", "en");
		respLinguisticEN_1.setTextContent("NER");

//		Element  respLinguisticIT_2 = targetCorpus.createElement("resp");
//		respLinguisticIT_2.setAttribute("xml:lang", "it");
//		respLinguisticIT_2.setTextContent("Allineamento Annotazione linguistica - Entità Nominate");
//
//		Element  respLinguisticEN_2= targetCorpus.createElement("resp");
//		respLinguisticEN_2.setAttribute("xml:lang", "en");
//		respLinguisticEN_2.setTextContent("Linguistic annotation - NER Alignment");

		respStmtLinguistic.appendChild(persNameBartolini);
		respStmtLinguistic.appendChild(persNameQuochi);


		respStmtLinguistic.appendChild(respLinguisticIT_0);
		respStmtLinguistic.appendChild(respLinguisticEN_0);
		respStmtLinguistic.appendChild(respLinguisticIT_1);
		respStmtLinguistic.appendChild(respLinguisticEN_1);
//		respStmtLinguistic.appendChild(respLinguisticIT_2);
//		respStmtLinguistic.appendChild(respLinguisticEN_2);



		//////////////////////////////////////////

//		// ANA
//		Element  respStmtANA = targetCorpus.createElement("respStmt");
//		Element  respANA_IT = targetCorpus.createElement("resp");
//		respANA_IT.setAttribute("xml:lang", "it");
//		respANA_IT.setTextContent("Conversione annotazione linguistica: da CoNLL-U a ParlaMint TEI XML");
//
//		Element  respANA_EN= targetCorpus.createElement("resp");
//		respANA_EN.setAttribute("xml:lang", "en");
//		respANA_EN.setTextContent("Conversion of the linguistic annotation: from CoNLL-U to ParlaMint TEI XML");
//
//		respStmtANA.appendChild(persNameBartolini);
//		respStmtANA.appendChild(persNameQuochi);
//
//		respStmtANA.appendChild(respANA_IT);
//		respStmtANA.appendChild(respANA_EN);

		//////////////////////////////////////////


		Element  funder = targetCorpus.createElement("funder");
		Element  orgNameIT = targetCorpus.createElement("orgName");
		orgNameIT.setAttribute("xml:lang", "it");
		orgNameIT.setTextContent("Infrastruttura di ricerca CLARIN");
		Element  orgNameEN = targetCorpus.createElement("orgName");
		orgNameEN.setAttribute("xml:lang", "en");
		orgNameEN.setTextContent("The CLARIN research infrastructure");
		funder.appendChild(orgNameIT);
		funder.appendChild(orgNameEN);

		titleStmt.appendChild(titleMainIT);
		titleStmt.appendChild(titleMainEN);
		titleStmt.appendChild(titleSubIT);
		titleStmt.appendChild(titleSubEN);
		titleStmt.appendChild(meeting17);
		titleStmt.appendChild(meeting18);

		// RESP_STMT_CORPUS
		titleStmt.appendChild(respStmtProject);
		if(SENATO_RESP)
			titleStmt.appendChild(respStmtSenato);
		titleStmt.appendChild(respStmtCodifica);

		if(ANA) {
			titleStmt.appendChild(respStmtLinguistic);
			//titleStmt.appendChild(respStmtANA);
		}



		titleStmt.appendChild(funder);

		//	2. <editionStmt>
		Element  editionStmt = targetCorpus.createElement("editionStmt");
		Element edition = targetCorpus.createElement("edition");
		edition.setTextContent("3.0");
		editionStmt.appendChild(edition);



		// SPEECHES
		Element  extent = targetCorpus.createElement("extent");
		Element measure1_it = targetCorpus.createElement("measure");
		measure1_it.setAttribute("unit", "speeches");
		measure1_it.setAttribute("quantity",""+totalExtent.get("speeches"));
		measure1_it.setAttribute("xml:lang", "it");
		measure1_it.setTextContent(totalExtent.get("speeches")+" discorsi");

		Element measure1_en = targetCorpus.createElement("measure");
		measure1_en.setAttribute("unit", "speeches");
		measure1_en.setAttribute("quantity",""+totalExtent.get("speeches"));
		measure1_en.setAttribute("xml:lang", "en");
		measure1_en.setTextContent(totalExtent.get("speeches")+" speeches");

		// WORDS

		Element measure2_it = targetCorpus.createElement("measure");
		measure2_it.setAttribute("unit", "words");
		measure2_it.setAttribute("quantity", ""+totalExtent.get("words"));
		measure2_it.setAttribute("xml:lang", "it");
		measure2_it.setTextContent(totalExtent.get("words")+" parole");

		Element measure2_en = targetCorpus.createElement("measure");
		measure2_en.setAttribute("unit", "words");
		measure2_en.setAttribute("quantity", ""+totalExtent.get("words"));
		measure2_en.setAttribute("xml:lang", "en");
		measure2_en.setTextContent(totalExtent.get("words")+" words");

		extent.appendChild(measure1_it);
		extent.appendChild(measure1_en);
		extent.appendChild(measure2_it);
		extent.appendChild(measure2_en);



		//  4. <publicationStmt>
		Element  publicationStmt = targetCorpus.createElement("publicationStmt");
		Element  publisher = targetCorpus.createElement("publisher");
		orgNameIT = targetCorpus.createElement("orgName");
		orgNameIT.setAttribute("xml:lang", "it");
		orgNameIT.setTextContent("Infrastruttura di ricerca CLARIN");
		orgNameEN = targetCorpus.createElement("orgName");
		orgNameEN.setAttribute("xml:lang", "en");
		orgNameEN.setTextContent("The CLARIN research infrastructure");
		Element ref = targetCorpus.createElement("ref");
		ref.setAttribute("target", "https://www.clarin.eu/");
		ref.setTextContent("www.clarin.eu");
		publisher.appendChild(orgNameIT);
		publisher.appendChild(orgNameEN);
		publisher.appendChild(ref);



		// NEW	
		Element idno = targetCorpus.createElement("idno");
		idno.setAttribute("type", "URI");
		idno.setAttribute("subtype", "handle");
		idno.setTextContent("http://hdl.handle.net/11356/1388");


		// OLD
		//		Element idno = targetCorpus.createElement("idno");
		//		idno.setAttribute("type", "URL");
		//		idno.setTextContent("https://github.com/clarin-eric/ParlaMint");
		//		
		//		Element pubPlace = targetCorpus.createElement("pubPlace");
		//		Element refPP = targetCorpus.createElement("ref");
		//		refPP.setAttribute("target", "https://github.com/clarin-eric/ParlaMint");
		//		refPP.setTextContent("https://github.com/clarin-eric/ParlaMint");
		//		pubPlace.appendChild(refPP);



		Element availability = targetCorpus.createElement("availability");
		availability.setAttribute("status", "free");
		Element licence = targetCorpus.createElement("licence");
		licence.setTextContent("http://creativecommons.org/licenses/by/4.0/");
		Element p_it = targetCorpus.createElement("p");
		p_it.setAttribute("xml:lang", "it");
		Element ref_licence_it = targetCorpus.createElement("ref");
		ref_licence_it.setAttribute("target", "http://creativecommons.org/licenses/by/4.0/");
		ref_licence_it.setTextContent("licenza Creative Commons 4.0 Attribuzione Internazionale");
		Node p_it_text1 = targetCorpus.createTextNode("Quest'opera è rilasciata con ");
		Node p_it_text2 = targetCorpus.createTextNode(".");
		p_it.appendChild(p_it_text1);
		p_it.appendChild(ref_licence_it);
		p_it.appendChild(p_it_text2);


		Element p_en = targetCorpus.createElement("p");
		p_en.setAttribute("xml:lang", "en");
		Element ref_licence_en = targetCorpus.createElement("ref");
		ref_licence_en.setAttribute("target", "http://creativecommons.org/licenses/by/4.0/");
		ref_licence_en.setTextContent("Creative Commons Attribution 4.0 International License");
		Node p_en_text1 = targetCorpus.createTextNode("This work is licensed under the ");
		Node p_en_text2 = targetCorpus.createTextNode(".");
		p_en.appendChild(p_en_text1);
		p_en.appendChild(ref_licence_en);
		p_en.appendChild(p_en_text2);

		licence.appendChild(p_it);
		licence.appendChild(p_en);


		availability.appendChild(licence);
		availability.appendChild(p_it);
		availability.appendChild(p_en);


		Element date_pub = targetCorpus.createElement("date");
		date_pub.setAttribute("when", "2022-10-30");
		date_pub.setTextContent("2022-10-30");



		publicationStmt.appendChild(publisher);
		publicationStmt.appendChild(idno);
		//publicationStmt.appendChild(pubPlace);
		publicationStmt.appendChild(availability);
		publicationStmt.appendChild(date_pub);




		//  5. <sourceDesc>
		Element  sourceDesc = targetCorpus.createElement("sourceDesc");




		Element  bibl = targetCorpus.createElement("bibl");
		Element  title_it = targetCorpus.createElement("title");
		title_it.setAttribute("type", "main");
		title_it.setAttribute("xml:lang", "it");
		title_it.setTextContent("Resoconti stenografici delle sedute pubbliche d'aula del Senato della Repubblica italiana");

		Element  title_en = targetCorpus.createElement("title");
		title_en.setAttribute("type", "main");
		title_en.setAttribute("xml:lang", "en");
		title_en.setTextContent("Minutes of the Senate of the Republic of Italy");

		Element  idno_bib = targetCorpus.createElement("idno");
		idno_bib.setAttribute("type", "URI");
		idno_bib.setTextContent("https://www.senato.it");

		Element  date_bib = targetCorpus.createElement("date");
		date_bib.setAttribute("from", "2013-03-15");
		date_bib.setAttribute("to", "2022-09-20");
		date_bib.setTextContent("15.3.2013 - 20.9.2022");

		bibl.appendChild(title_it);
		bibl.appendChild(title_en);
		bibl.appendChild(idno_bib);
		bibl.appendChild(date_bib);

		//sourceDesc.appendChild(bibl0);
		sourceDesc.appendChild(bibl);



		// END

		fileDesc.appendChild(titleStmt);
		fileDesc.appendChild(editionStmt);
		fileDesc.appendChild(extent);
		fileDesc.appendChild(publicationStmt);
		fileDesc.appendChild(sourceDesc);



		appendTo.appendChild(fileDesc);
		return targetCorpus;
	}

	public Document makeEncodingDesc(Document targetCorpus, Element appendTo) {
		Element  encodingDesc = targetCorpus.createElement("encodingDesc");

		encodingDesc.appendChild(getProjectDesc(targetCorpus));
		encodingDesc.appendChild(getEditorialDecl(targetCorpus));
		encodingDesc.appendChild(getTagsDecl(targetCorpus));
		encodingDesc.appendChild(getClassDecl(targetCorpus));


		appendTo.appendChild(encodingDesc);


		return targetCorpus;
	}


	//    <projectDesc>
	//       <p xml:lang="sl"><ref target="https://www.clarin.eu/content/parlamint">ParlaMint</ref></p>
	//       <p xml:lang="en"><ref target="https://www.clarin.eu/content/parlamint">ParlaMint</ref> is a project that aims to (1) create a multilingual set of comparable corpora of parliamentary proceedings uniformly encoded according to the <ref target="https://github.com/clarin-eric/parla-clarin">Parla-CLARIN recommendations</ref> and covering the COVID-19 pandemic from November 2019 as well as the earlier period from 2015 to serve as a reference corpus; (2) process the corpora linguistically to add Universal Dependencies syntactic structures and Named Entity annotation; (3) make the corpora available through concordancers and Parlameter; and (4) build use cases in Political Sciences and Digital Humanities based on the corpus data.</p>
	//    </projectDesc>

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
		Node p_en_text1 = targetCorpus.createTextNode(" is a project that aims to (1) create a multilingual set of comparable corpora of parliamentary proceedings uniformly encoded according to the ");

		Node p_en_text2 = targetCorpus.createTextNode(" and covering the COVID-19 pandemic from November 2019 as well as the earlier period from 2015 to serve as a reference corpus; (2) process the corpora linguistically to add Universal Dependencies syntactic structures and Named Entity annotation; (3) make the corpora available through concordancers and Parlameter; and (4) build use cases in Political Sciences and Digital Humanities based on the corpus data.");

		Element ref_prjD_en_2 = targetCorpus.createElement("ref");
		ref_prjD_en_2.setAttribute("target", "https://github.com/clarin-eric/parla-clarin");
		ref_prjD_en_2.setTextContent("Parla-CLARIN recommendations");

		p_en.appendChild(ref_prjD_en);
		p_en.appendChild(p_en_text1);
		p_en.appendChild(ref_prjD_en_2);
		p_en.appendChild(p_en_text2);

		projectDesc.appendChild(p_it);
		projectDesc.appendChild(p_en);


		return projectDesc;

	}



	// <editorialDecl>
	//  <correction>
	//     <p xml:lang="en">No correction of source texts was performed.</p>
	//  </correction>
	//  <normalization>
	//     <p xml:lang="en">Text has not been normalised, except for spacing.</p>
	//  </normalization>
	//  <hyphenation>
	//     <p xml:lang="en">No end-of-line hyphens were present in the source.</p>
	//  </hyphenation>
	//  <quotation>
	//     <p xml:lang="en">Quotation marks have been left in the text and are not explicitly marked up.</p>
	//  </quotation>
	//  <segmentation>
	//     <p xml:lang="en">The texts are segmented into utterances (speeches) and segments (corresponding to paragraphs in the source transcription).</p>
	//  </segmentation>
	//</editorialDecl>

	private Element getEditorialDecl(Document targetCorpus) {
		Element  editorialDecl = targetCorpus.createElement("editorialDecl");

		Element  correction = targetCorpus.createElement("correction");
		Element p_correction_en = targetCorpus.createElement("p");
		p_correction_en.setAttribute("xml:lang", "en");
		p_correction_en.setTextContent("No correction of source texts was performed.");
		correction.appendChild(p_correction_en);

		Element  normalization = targetCorpus.createElement("normalization");
		Element p_normalization_en = targetCorpus.createElement("p");
		p_normalization_en.setAttribute("xml:lang", "en");
		p_normalization_en.setTextContent("Text has not been normalised, except for spacing.");
		normalization.appendChild(p_normalization_en);

		Element  hyphenation = targetCorpus.createElement("hyphenation");
		Element p_hyphenation_en = targetCorpus.createElement("p");
		p_hyphenation_en.setAttribute("xml:lang", "en");
		p_hyphenation_en.setTextContent("No end-of-line hyphens were present in the source.");
		hyphenation.appendChild(p_hyphenation_en);

		Element  quotation = targetCorpus.createElement("quotation");
		Element p_quotation_en = targetCorpus.createElement("p");
		p_quotation_en.setAttribute("xml:lang", "en");
		p_quotation_en.setTextContent("Quotation marks have been left in the text and are not explicitly marked up.");
		quotation.appendChild(p_quotation_en);

		Element  segmentation = targetCorpus.createElement("segmentation");
		Element p_segmentation_en = targetCorpus.createElement("p");
		p_segmentation_en.setAttribute("xml:lang", "en");
		p_segmentation_en.setTextContent("The texts are segmented into utterances (speeches) and segments (corresponding to paragraphs in the source transcription).");
		segmentation.appendChild(p_segmentation_en);

		editorialDecl.appendChild(correction);
		editorialDecl.appendChild(normalization);
		editorialDecl.appendChild(hyphenation);
		editorialDecl.appendChild(quotation);
		editorialDecl.appendChild(segmentation);


		return editorialDecl;

	}

	//<tagsDecl><!--These numbers do not reflect the size of the sample!-->
	//  <namespace name="http://www.tei-c.org/ns/1.0">
	//     <tagUsage gi="text" occurs="414"/>
	//     <tagUsage gi="body" occurs="414"/>
	//     <tagUsage gi="div" occurs="414"/>
	//     <tagUsage gi="head" occurs="826"/>
	//     <tagUsage gi="note" occurs="85525"/>
	//     <tagUsage gi="u" occurs="75122"/>
	//     <tagUsage gi="seg" occurs="280971"/>
	//     <tagUsage gi="kinesic" occurs="560"/>
	//     <tagUsage gi="desc" occurs="10234"/>
	//     <tagUsage gi="gap" occurs="7897"/>
	//     <tagUsage gi="vocal" occurs="1740"/>
	//     <tagUsage gi="incident" occurs="37"/>
	//  </namespace>
	//</tagsDecl>

	private Element getTagsDecl(Document targetCorpus) {


		Element  tagsDecl = targetCorpus.createElement("tagsDecl");
		Element  namespace = targetCorpus.createElement("namespace");
		namespace.setAttribute("name", "http://www.tei-c.org/ns/1.0");

		// TAG_USAGE
		for(String item:totalTags.keySet()) {
			Element  tagUsage1 = targetCorpus.createElement("tagUsage");
			tagUsage1.setAttribute("gi", item);
			tagUsage1.setAttribute("occurs", ""+totalTags.get(item));
			namespace.appendChild(tagUsage1);
		}

		tagsDecl.appendChild(namespace);


		return tagsDecl;

	}

	// qui vanno tutte le taxonomies...
	private Element getClassDecl(Document targetCorpus) {
		Element  classDecl = targetCorpus.createElement("classDecl");


		// TAXONOMY 1
		Element  taxonomy1 = targetCorpus.createElement("taxonomy");
		taxonomy1.setAttribute("xml:id", "parla.legislature");


		Element  desc_it_tax1 = targetCorpus.createElement("desc");
		desc_it_tax1.setAttribute("xml:lang", "it");
		Element term_desc_it_tax1 = targetCorpus.createElement("term");
		term_desc_it_tax1.setTextContent("Legislatura");
		desc_it_tax1.appendChild(term_desc_it_tax1);

		Element  desc_en_tax1 = targetCorpus.createElement("desc");
		desc_en_tax1.setAttribute("xml:lang", "en");
		Element term_desc_en_tax1 = targetCorpus.createElement("term");
		term_desc_en_tax1.setTextContent("Legislature");
		desc_en_tax1.appendChild(term_desc_en_tax1);


		taxonomy1.appendChild(desc_it_tax1);
		taxonomy1.appendChild(desc_en_tax1);

		Element geopolitical_cat = getTaxonomyCategory(targetCorpus,"parla.geo-political","Unità geo-politica o amministrativa","","Geo-political or administrative units","");

		Element supranational = getTaxonomyCategory(targetCorpus,"parla.supranational","Legislatura sovranazionale","","Supranational legislature","");
		Element national = getTaxonomyCategory(targetCorpus,"parla.national","Legislatura nazionale","","National legislature","");
		Element regional = getTaxonomyCategory(targetCorpus,"parla.regional","Legislatura regionale","","Regional legislature","");
		Element local = getTaxonomyCategory(targetCorpus,"parla.local","Legislatura locale","","Local legislature","");

		Node geoCatDesc = geopolitical_cat.getLastChild();
		UtilDom.insertAfter(supranational, geoCatDesc);
		UtilDom.insertAfter(national, supranational);
		UtilDom.insertAfter(regional,national);
		UtilDom.insertAfter(local,regional);



		Element organization_cat = getTaxonomyCategory(targetCorpus,"parla.organization","Organizzazione","","Organization","");

		Element parla_upper = getTaxonomyCategory(targetCorpus,"parla.upper","Senato della Repubblica","","Upper house","");
		Element parla_lower = getTaxonomyCategory(targetCorpus,"parla.lower","Camera dei Deputati","","Lower house","");
		Element parla_bi = getTaxonomyCategory(targetCorpus,"parla.bi","Bicameralismo","","Bicameralism","");
		Node parla_bi_CatDesc = parla_bi.getLastChild();
		UtilDom.insertAfter(parla_upper, parla_bi_CatDesc);
		UtilDom.insertAfter(parla_lower, parla_upper);

		Element parla_uni = getTaxonomyCategory(targetCorpus,"parla.uni","Monocameralismo","","Unicameralism","");


		Element parla_chamber = getTaxonomyCategory(targetCorpus,"parla.chamber","Camera","","Chamber","");

		Element parla_multi = getTaxonomyCategory(targetCorpus,"parla.multi","Multicameralismo","","Multicameralism","");
		Node parla_multi_CatDesc = parla_multi.getLastChild();
		UtilDom.insertAfter(parla_chamber, parla_multi_CatDesc);


		Element parla_chambers = getTaxonomyCategory(targetCorpus,"parla.chambers","Camere","","Chambers","");
		Node parla_chambers_CatDesc = parla_chambers.getLastChild();
		UtilDom.insertAfter(parla_uni, parla_chambers_CatDesc);
		UtilDom.insertAfter(parla_bi, parla_uni);
		UtilDom.insertAfter(parla_multi, parla_bi);

		Node organization_cat_CatDesc = organization_cat.getLastChild();
		UtilDom.insertAfter(parla_chambers, organization_cat_CatDesc);


		Element term_cat = getTaxonomyCategory(targetCorpus,"parla.term","Legislatura",": mandato del parlamento fra elezioni politiche generali.","Legislative period",": term of the parliament between general elections.");

		Element parla_sitting = getTaxonomyCategory(targetCorpus,"parla.sitting","Seduta","","Sitting",": sitting day");
		Element parla_meeting = getTaxonomyCategory(targetCorpus,"parla.meeting","Riunione","","Meeting",": Each meeting may be a separate session or part of a group of meetings constituting a session. The session/meeting may take one or more days.");

		// FIXME omesso meeting-types <category xml:id="parla.meeting-types">

		Element parla_session = getTaxonomyCategory(targetCorpus,"parla.session","Sessione","","Legislative session",": the period of time in which a legislature is convened for purpose of lawmaking, usually being one of two or more smaller divisions of the entire time between two elections. A session is a meeting or series of connected meetings devoted to a single order of business, program, agenda, or announced purpose.");

		Element parla_meeting_regular = getTaxonomyCategory(targetCorpus,"parla.meeting.regular","Seduta ordinaria","","Regular meeting","");
		Element parla_meeting_types = getTaxonomyCategory(targetCorpus,"parla.meeting-types","Tipi di seduta","","Types of meetings","");
		Node parla_meeting_types_CatDesc = parla_meeting_types.getLastChild();
		UtilDom.insertAfter(parla_meeting_regular, parla_meeting_types_CatDesc);


		Node parla_meeting_CatDesc = parla_meeting.getLastChild();
		UtilDom.insertAfter(parla_meeting_types, parla_meeting_CatDesc);
		UtilDom.insertAfter(parla_sitting, parla_meeting_types);

		Node parla_session_CatDesc = parla_session.getLastChild();
		UtilDom.insertAfter(parla_meeting, parla_session_CatDesc);

		Node term_cat_CatDesc = term_cat.getLastChild();
		UtilDom.insertAfter(parla_session, term_cat_CatDesc);


		taxonomy1.appendChild(geopolitical_cat);
		taxonomy1.appendChild(organization_cat);
		taxonomy1.appendChild(term_cat);



		//		ADD SPEAKER TYPES IN TAXONOMY ? o sono ruoli dell'xsd ? 
		//				if(sourceRoleText.equalsIgnoreCase("presidente"))
		//					return "president"; // FIXME il presidente del Senato potrebbe essere "chairman"
		//				if(sourceRoleText.toLowerCase().startsWith("presidente del consiglio"))
		//					return "primeMinister";
		//				if(sourceRoleText.toLowerCase().startsWith("vice presidente del consiglio"))
		//					return "deputyPrimeMinister";
		//				if(sourceRoleText.toLowerCase().startsWith("ministr"))
		//					return "minister";
		//				if(sourceRoleText.toLowerCase().startsWith("sottosegretar")||
		//						sourceRoleText.toLowerCase().startsWith("vice ministro"))
		//					return "deputyMinister";
		//				if(sourceRoleText.toLowerCase().contains("elatore")||
		//						sourceRoleText.toLowerCase().contains("relatrice"))
		//					return "chairman";
		//				if(sourceRoleText.toLowerCase().startsWith("segretari"))
		//					return "secretary";
		//				if(sourceRoleText.toLowerCase().contains("questore"))
		//					return "presidiumMember";
		//				if(sourceRoleText.toLowerCase().contains("estensore"))
		//					return "verifier";

		// TAXONOMY 2 (SPEAKER TYPES)
		Element  taxonomy2 = targetCorpus.createElement("taxonomy");
		taxonomy2.setAttribute("xml:id", "speaker_types");


		Element  desc_it_tax2 = targetCorpus.createElement("desc");
		desc_it_tax2.setAttribute("xml:lang", "it");
		Element term_desc_it_tax2 = targetCorpus.createElement("term");
		term_desc_it_tax2.setTextContent("Types of speakers");
		desc_it_tax2.appendChild(term_desc_it_tax2);

		Element  desc_en_tax2 = targetCorpus.createElement("desc");
		desc_en_tax2.setAttribute("xml:lang", "en");
		Element term_desc_en_tax2 = targetCorpus.createElement("term");
		term_desc_en_tax2.setTextContent("Types of speakers");
		desc_en_tax2.appendChild(term_desc_en_tax2);


		taxonomy2.appendChild(desc_it_tax2);
		taxonomy2.appendChild(desc_en_tax2);

		taxonomy2.appendChild(getTaxonomyCategory(targetCorpus,"chair","Presidente",": presidente di seduta","Chairperson",": chairman of a meeting"));
		taxonomy2.appendChild(getTaxonomyCategory(targetCorpus,"regular","Membro","","Regular",": a regular speaker at a meeting"));
		taxonomy2.appendChild(getTaxonomyCategory(targetCorpus,"guest","Ospite","","Guest",": a guest speaker at a meeting"));



		// TAXONOMY 3 (SUBCORPUS)
		Element  taxonomy3 = targetCorpus.createElement("taxonomy");
		taxonomy3.setAttribute("xml:id", "subcorpus");


		Element  desc_it_tax3 = targetCorpus.createElement("desc");
		desc_it_tax3.setAttribute("xml:lang", "it");
		Element term_desc_it_tax3 = targetCorpus.createElement("term");
		term_desc_it_tax3.setTextContent("Subcorpora");
		desc_it_tax3.appendChild(term_desc_it_tax3);

		Element  desc_en_tax3 = targetCorpus.createElement("desc");
		desc_en_tax3.setAttribute("xml:lang", "en");
		Element term_desc_en_tax3 = targetCorpus.createElement("term");
		term_desc_en_tax3.setTextContent("Subcorpora");
		desc_en_tax3.appendChild(term_desc_en_tax3);


		taxonomy3.appendChild(desc_it_tax3);
		taxonomy3.appendChild(desc_en_tax3);

		taxonomy3.appendChild(getTaxonomyCategory(targetCorpus,"reference","Reference",": reference subcorpus, fino al 31-10-2019","Reference",": reference subcorpus, until 2019-10-31"));
		taxonomy3.appendChild(getTaxonomyCategory(targetCorpus,"covid","COVID",": COVID subcorpus, dal 01-11-2019","COVID",": COVID subcorpus, from 2019-11-01 onwards"));

		// FIXME ADD CATEGORIES

		//getTaxonomyCategory(Document targetCorpus, String catId, String cat_desc_term_it, String cat_desc_other_it, String cat_desc_term_en, String cat_desc_other_en) {


		// APPEND TAXONOMY
		classDecl.appendChild(taxonomy1);
		classDecl.appendChild(taxonomy2);
		classDecl.appendChild(taxonomy3);

		return classDecl;

	}


	private Element getTaxonomyCategory(Document targetCorpus, String catId, String cat_desc_term_it, String cat_desc_other_it, String cat_desc_term_en, String cat_desc_other_en) {

		Element  category = targetCorpus.createElement("category");
		category.setAttribute("xml:id", catId);


		Element  catDesc_it = targetCorpus.createElement("catDesc");
		catDesc_it.setAttribute("xml:lang", "it");
		Element catDescTerm_it = targetCorpus.createElement("term");
		catDescTerm_it.setTextContent(cat_desc_term_it);
		catDesc_it.appendChild(catDescTerm_it);

		if(cat_desc_other_it.trim().length()>0) {
			Node textNode_it = targetCorpus.createTextNode(cat_desc_other_it);
			catDesc_it.appendChild(textNode_it);
		}

		Element  catDesc_en = targetCorpus.createElement("catDesc");
		catDesc_en.setAttribute("xml:lang", "en");
		Element catDescTerm_en = targetCorpus.createElement("term");
		catDescTerm_en.setTextContent(cat_desc_term_en);
		catDesc_en.appendChild(catDescTerm_en);

		if(cat_desc_other_en.trim().length()>0) {
			Node textNode_en = targetCorpus.createTextNode(cat_desc_other_en);
			catDesc_en.appendChild(textNode_en);
		}


		category.appendChild(catDesc_it);
		category.appendChild(catDesc_en);

		return category;

	}






	//	<settingDesc>
	//    <setting>
	//    <name type="address">Šubičeva ulica 4</name>
	//    <name type="city">Ljubljana</name>
	//    <name type="country" key="SI">Slovenia</name>
	//    <date from="2014-08-01" to="2020-07-16">1.8.2014 - 16.7.2020</date>
	// </setting>
	//</settingDesc>
	public Document makeSettingDesc(Document targetCorpus, Element appendTo) {
		Element  settingDesc = targetCorpus.createElement("settingDesc");

		Element  setting = targetCorpus.createElement("setting");
		Element  address = targetCorpus.createElement("name");//Piazza Madama, 00186 Roma RM
		address.setAttribute("type", "address");
		address.setTextContent("Piazza Madama, 11");
		Element  city = targetCorpus.createElement("name");
		city.setAttribute("type", "city");
		city.setTextContent("Roma");
		Element  country = targetCorpus.createElement("name");
		country.setAttribute("type", "country");
		country.setAttribute("key", "IT");
		country.setTextContent("Italia");
		Element  date = targetCorpus.createElement("date");
		date.setAttribute("from", "2013-03-15");
		date.setAttribute("to", "2022-09-20");
		date.setTextContent("15.3.2013 - 20.9.2022");

		setting.appendChild(address);
		setting.appendChild(city);
		setting.appendChild(country);
		setting.appendChild(date);

		settingDesc.appendChild(setting);

		appendTo.appendChild(settingDesc);
		return targetCorpus;
	}


	public Document makeTextClass(Document targetCorpus, Element appendTo) {

		Element  textClass = targetCorpus.createElement("textClass");

		
		Element  catRef = targetCorpus.createElement("catRef");
		catRef.setAttribute("scheme", "#ParlaMint-taxonomy-parla.legislature");
		catRef.setAttribute("target", "#parla.bi #parla.upper");
		
		textClass.appendChild(catRef);
		

		appendTo.appendChild(textClass);
		return targetCorpus;
	}

	
	//  <revisionDesc>
	//  <change when="2020-12-10"><name>Tomaž Erjavec</name>: Made sample.</change>
	//  <change when="2020-10-06">
	//     <name>Tomaž Erjavec</name>: Small fixes for ParlaMint.</change>
	//</revisionDesc>
	public Document revisionDesc(Document targetCorpus, Element appendTo) {

		Element  revisionDesc = targetCorpus.createElement("revisionDesc");

		Element  change1 = targetCorpus.createElement("change");
		change1.setAttribute("when", "2021-01-28");
		Element  name1 = targetCorpus.createElement("name");
		name1.setTextContent("Tommaso Agnoloni");
		change1.appendChild(name1);
		Node textNode = targetCorpus.createTextNode(":Made sample.");
		change1.appendChild(textNode);
		Element  change3 = targetCorpus.createElement("change");
		change3.setAttribute("when", "2021-02-19");
		Element  name3 = targetCorpus.createElement("name");
		name3.setTextContent("Tommaso Agnoloni");
		change3.appendChild(name3);
		Node textNode_3 = targetCorpus.createTextNode(": Completed corpus, fixing");
		change3.appendChild(textNode_3);

		revisionDesc.appendChild(change1);
		revisionDesc.appendChild(change3);


		appendTo.appendChild(revisionDesc);


		return targetCorpus;
	}

	//	<langUsage>
	//  <language ident="sl" xml:lang="sl">slovenski</language>
	//  <language ident="en" xml:lang="sl">angleški</language>
	//  <language ident="sl" xml:lang="en">Slovenian</language>
	//  <language ident="en" xml:lang="en">English</language>
	//</langUsage>
	public Document exportLangUsage(Document targetCorpus, Element appendTo) {

		Element  langUsage = targetCorpus.createElement("langUsage");

		Element  language_IT_it = targetCorpus.createElement("language");
		language_IT_it.setAttribute("ident", "it");
		language_IT_it.setAttribute("xml:lang", "it");
		language_IT_it.setTextContent("italiano");

		Element  language_IT_en = targetCorpus.createElement("language");
		language_IT_en.setAttribute("ident", "en");
		language_IT_en.setAttribute("xml:lang", "it");
		language_IT_en.setTextContent("inglese");

		Element  language_EN_it = targetCorpus.createElement("language");
		language_EN_it.setAttribute("ident", "it");
		language_EN_it.setAttribute("xml:lang", "en");
		language_EN_it.setTextContent("Italian");

		Element  language_EN_en = targetCorpus.createElement("language");
		language_EN_en.setAttribute("ident", "en");
		language_EN_en.setAttribute("xml:lang", "en");
		language_EN_en.setTextContent("English");

		langUsage.appendChild(language_IT_it);
		langUsage.appendChild(language_IT_en);
		langUsage.appendChild(language_EN_it);
		langUsage.appendChild(language_EN_en);


		appendTo.appendChild(langUsage);


		return targetCorpus;
	}



	public Document exportSenatori(Document targetCorpus, Element appendTo) {

		Element  listPerson = targetCorpus.createElement("listPerson");
		appendTo.appendChild(listPerson);

		Element  head_it = targetCorpus.createElement("head");
		head_it.setAttribute("xml:lang", "it");
		head_it.setTextContent("Lista dei relatori");

		Element  head_en = targetCorpus.createElement("head");
		head_en.setAttribute("xml:lang", "en");
		head_en.setTextContent("List of speakers");

		listPerson.appendChild(head_it);
		listPerson.appendChild(head_en);


		HashMap<String, Senatore> senatoriMap = dati.getLookupSenatore();
		//System.err.println("UNIQUE SENATORI: "+senatoriMap.keySet().size());

		for(String idSenatore:senatoriMap.keySet()) {
			Senatore senatoreItem = senatoriMap.get(idSenatore);
			Element  person = targetCorpus.createElement("person");

			person.setAttribute("xml:id", dati.getIdSenatore2Readable().get(idSenatore)/*speakerPrefix+"."+idSenatoreXML*/);

			Element  persName = targetCorpus.createElement("persName");
			Element  surname = targetCorpus.createElement("surname");
			surname.setTextContent(senatoreItem.getCognome());
			Element  forename = targetCorpus.createElement("forename");
			forename.setTextContent(senatoreItem.getNome());
			persName.appendChild(surname);
			persName.appendChild(forename);
			person.appendChild(persName);

			Element  sex = targetCorpus.createElement("sex");
			sex.setAttribute("value", senatoreItem.getSesso());
			// v2tov3 - change
			//sex.setTextContent(senatoreItem.getSesso().equalsIgnoreCase("m")?"Maschio":"Femmina");
			person.appendChild(sex);

			Element  birth = targetCorpus.createElement("birth");
			birth.setAttribute("when", senatoreItem.getDataNascita());

			Element  placeName = targetCorpus.createElement("placeName");
			placeName.setAttribute("ref", senatoreItem.getCittaNascita_geoname());
			placeName.setTextContent(senatoreItem.getCittaNascita());

			birth.appendChild(placeName);
			person.appendChild(birth);

			// LEGISLATIVE TERM AFFILIATIONS
			for(LegislativeTerm lt:senatoreItem.getListLegislativeTerms()) {
				if(lt.getLegislatura().trim().length()>0) {
					Element  affiliation = targetCorpus.createElement("affiliation");
					affiliation.setAttribute("role", "member");
					affiliation.setAttribute("ref", "#LEG");
					if(lt.getInizioMandato().trim().length()>0)
						affiliation.setAttribute("from",lt.getInizioMandato());
					if(lt.getFineMandato().trim().length()>0)
						affiliation.setAttribute("to",lt.getFineMandato());
					affiliation.setAttribute("ana", "#LEG."+lt.getLegislatura());
					person.appendChild(affiliation);
				}
			}

			// POLITICAL GROUP AFFILIATIONS
			//<affiliation role="member" ref="#party.SAB" from="2018-06-22" to="2018-09-12" ana="#DZ.8"/>
		
			List<Affiliation> groupAffiliations = dati.getLookupAffiliationsbyId().get(idSenatore);
			if(groupAffiliations!=null) {
				for(Affiliation aff:groupAffiliations) {
					Element  affiliation = targetCorpus.createElement("affiliation");
					Element  implied_affiliation = targetCorpus.createElement("affiliation");

					
					if(!aff.getCarica().equalsIgnoreCase("membro")) {
						affiliation.setAttribute("role", aff.getRole_XSD());
						
						Element  roleName_en = targetCorpus.createElement("roleName");
						roleName_en.setAttribute("xml:lang", "en");
						roleName_en.setTextContent(aff.getCarica_en());
						
						Element  roleName_it = targetCorpus.createElement("roleName");
						roleName_it.setAttribute("xml:lang", "it");
						roleName_it.setTextContent(aff.getCarica());

						affiliation.appendChild(roleName_it);
						// SE DISPONIBILE VERSION ENG.
						if(aff.getCarica_en()!=null && aff.getCarica_en().trim().length()>0) {
							affiliation.appendChild(roleName_en);
						}
					}else {
						affiliation.setAttribute("role", aff.getRole_XSD());
					}
					
					implied_affiliation.setAttribute("role", "member");
					
					if(!aff.getNomeGruppo().startsWith("GOV")) {
						String idDenominazioneGruppo = dati.getLookupGruppobyName().get(aff.getNomeGruppo()).getIdDenominazioneXML();
						affiliation.setAttribute("ref", /*"#group."*/"#group."+idDenominazioneGruppo);
						implied_affiliation.setAttribute("ref", /*"#group."*/"#group."+idDenominazioneGruppo);
					}else {
						affiliation.setAttribute("ref", "#GOV");
						affiliation.setAttribute("ana", "#"+aff.getNomeGruppo());
						implied_affiliation.setAttribute("ref", "#GOV");
						implied_affiliation.setAttribute("ana", "#"+aff.getNomeGruppo());
					}
					
					if(aff.getInizioAdesione().trim().length()>0) {
						affiliation.setAttribute("from",aff.getInizioAdesione());
						implied_affiliation.setAttribute("from",aff.getInizioAdesione());

					}
					if(aff.getFineAdesione().trim().length()>0) {
						affiliation.setAttribute("to",aff.getFineAdesione());
						implied_affiliation.setAttribute("to",aff.getFineAdesione());
					}
					if(aff.getLegislatura().trim().length()>0) {
						affiliation.setAttribute("ana", "#LEG."+aff.getLegislatura());
						implied_affiliation.setAttribute("ana", "#LEG."+aff.getLegislatura());
					}
					
					person.appendChild(affiliation);

					
					// FIX FOR ERROR[18] ParlaMint-IT: Missing implied affiliation role 'member'
					
					// FIXME dubbio su implied affiliation - vale per ogni ruolo diverso da member (es. head del gruppo richiede anche implied member del gruppo) o solo per membri del governo ? 
					//
					// https://clarin-eric.github.io/ParlaMint/#sec-affiliation
					//
					// An important point to note is that ParlaMint makes no assumptions on the interconnection between various roles, e.g. we do not assume that if somebody has a minister role 
					// in the government that they are also a member of the government. Therefore it is necessary to specify all the desired affiliations with their particular roles, e.g. both 
					// as minister and as member.
					
					if(!affiliation.getAttribute("role").equals("member")) {							
						person.appendChild(implied_affiliation);
					}
						
				}
					
			}

			// WEBSITE_LINK
			for(LegislativeTerm lt:senatoreItem.getListLegislativeTerms()) {
				Element  idno = targetCorpus.createElement("idno");
				idno.setAttribute("type", "URI");
				idno.setAttribute("subtype", "parliament");
				idno.setAttribute("xml:lang", "it");
				idno.setTextContent(lt.getUrlSchedaSenato());
				person.appendChild(idno);
			}


			listPerson.appendChild(person);


		}


		return targetCorpus;
	}


	





	public void Test() {


		// TEST LETTURA DATI SENATO


		HashMap<String, Senatore> senatoriMap = dati.getLookupSenatore();
		System.err.println("UNIQUE SENATORI: "+senatoriMap.keySet().size());


		HashMap<String, Gruppo> gruppiMap = dati.getLookupGruppobyName();

		for(String s:gruppiMap.keySet()) {
			System.err.println(gruppiMap.get(s).getDenominazione()+"\t"+gruppiMap.get(s).getAbbreviazione()+"\t"+gruppiMap.get(s).getIdDenominazioneXML());
		}

		System.err.println("**********");


		HashMap<String, List<Gruppo>> gruppibyId = dati.getLookupGruppibyId();

		for(String s:gruppibyId.keySet()) {
			System.err.println(s);
		}


		HashMap<String, List<Affiliation>> affLookup = dati.getLookupAffiliationsbyId();

		for(String s:affLookup.keySet()) {
			System.err.println("sen_aff" +s);
		}

	}

}
