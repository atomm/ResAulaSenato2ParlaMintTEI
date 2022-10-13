package it.cnr.igsg.senato.datiSenato;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.cnr.igsg.senato.Config;

public class DatiSenato {

	HashMap<String,Senatore> lookupSenatore = new HashMap<String,Senatore>();
	HashMap<String,Gruppo> lookupGruppobyName = new HashMap<String,Gruppo>();
	HashMap<String,List<Gruppo>> lookupGruppibyId = new HashMap<String,List<Gruppo>>();
	HashMap<String,List<Affiliation>> lookupAffiliationsbyId = new HashMap<String,List<Affiliation>>();
	HashMap<String,String> idSenatore2Readable = new HashMap<String,String>();

	public HashMap<String, String> getIdSenatore2Readable() {
		return idSenatore2Readable;
	}

	List<Affiliation> raw_affiliations;

	

	public DatiSenato() {
		readSenatoriTSV();
		readOtherSpeakersTSV();
		readGroupsTSV();
		readEditedAffiliationsTSV();
		makeAffiliationLookup();
	}
	
	

	
	
	private String getIdNumericoSen(String id) {
		id = id.substring(id.lastIndexOf("/")+1,id.length());
		if(id.length()==2)
			return "000000"+id;
		if(id.length()==3)
			return "00000"+id;
		if(id.length()==4)
			return "0000"+id;
		if(id.length()==5)
			return "000"+id;
		return id;
	}
	
	
	public String getSpeakerPrefix(String idSpeaker) {
		//returns "sen", "dep", "oth";
		Senatore senatoreItem = lookupSenatore.get(idSpeaker);
		
		if(senatoreItem ==null) {
			System.err.println("[ID_SPEAKER] "+idSpeaker +"  NOT FOUND");
			return "err";
		}
		
		for(LegislativeTerm lt:senatoreItem.getListLegislativeTerms()) {
			if(lt.getTipoMandato().equalsIgnoreCase("deputato"))
				return "dep";
		}
		
		for(LegislativeTerm lt:senatoreItem.getListLegislativeTerms()) {
			if(lt.getTipoMandato().equalsIgnoreCase("other"))
				return "oth";
		}
		
		return "sen";
		
	}
	
	private void readOtherSpeakersTSV() {

		//id_speaker	nome	cognome	sesso	legislatura	inizioMandato	fineMandato	tipoMandato	tipoFineMandato	dataNascita	cittaNascita	cittaNascita_geoname	provinciaNascita	nazioneNascita	idCamera	url_scheda

		
		File otherSpeakersTable = new File(Config.OTHER_SPEAKERS_TSV);
		if(!otherSpeakersTable.exists()){
			System.err.println(" PROBLEMS READING SOURCE FILE "+Config.SENATORI_TSV);
		}

		try{
			BufferedReader reader = new BufferedReader( new FileReader(otherSpeakersTable));
			String line  = null;

			while( ( line = reader.readLine() ) != null) {
				// SKIP HEADER
				if(!line.startsWith("id_speaker\tnome")) {
					String[] fields = line.split("\t");
					String id = fields[0];
					String nome	= fields[1];
					String cognome	= fields[2];
					String sesso = fields[3];
					String legislatura	= fields[4];
					String inizioMandato = fields[5];	
					String fineMandato	= fields[6];
					String tipoMandato	= fields[7];
					String tipoFineMandato	= fields[8];
					String dataNascita	= fields[9];
					String cittaNascita = fields[10];	
					String cittaNascita_geoname = fields[11];	
					String provinciaNascita= fields[12];	
					String nazioneNascita= fields[13];
					String idCamera = fields[14];
					String urlSchedaSenato = fields[15];

					
					// FORZA FINE MANDATO legislatura 18
					if(legislatura.equalsIgnoreCase("18") && fineMandato.trim().length()==0) {
						fineMandato = "2022-09-24"; // giorno prima delle elezioni
					}
					

					
					Senatore S;
					if((S=lookupSenatore.get(id))==null){
						S = new Senatore();
						S.setId(id);
						S.setCognome(cognome);
						S.setNome(nome);
						S.setDataNascita(dataNascita);
						S.setSesso(sesso);
						S.setCittaNascita(cittaNascita);
						S.setCittaNascita_geoname(cittaNascita_geoname);
						S.setProvinciaNascita(provinciaNascita);
						S.setNazioneNascita(nazioneNascita);
						
						// non è tanto corretto perchè una persona potrebbe diventare deputato in una legislatura successiva. Andrebbe spostato sul mandato. Tanto idCamera non serve a niente (solo per futura interop..)
						if(tipoMandato.equalsIgnoreCase("deputato"))
							S.setIdCamera(idCamera);
						
						LegislativeTerm lT = new LegislativeTerm();
						lT.setInizioMandato(inizioMandato);
						lT.setFineMandato(fineMandato);
						lT.setTipoMandato(tipoMandato);
						lT.setTipoFineMandato(tipoFineMandato);
						lT.setLegislatura(legislatura);
												
						lT.setUrlSchedaSenato(urlSchedaSenato);

						List<LegislativeTerm> listLegislativeTerms = new ArrayList<LegislativeTerm>();
						listLegislativeTerms.add(lT);
						S.setListLegislativeTerms(listLegislativeTerms);
						lookupSenatore.put(id, S);
						String readableId = getReadableId(S);
						idSenatore2Readable.put(id, readableId);
						//System.out.println(readableId);
					}else {
						List<LegislativeTerm> listLegislativeTerms = S.getListLegislativeTerms();
						LegislativeTerm lT = new LegislativeTerm();
						lT.setInizioMandato(inizioMandato);
						lT.setFineMandato(fineMandato);
						lT.setTipoMandato(tipoMandato);
						lT.setTipoFineMandato(tipoFineMandato);
						lT.setLegislatura(legislatura);
						lT.setUrlSchedaSenato(urlSchedaSenato);
						listLegislativeTerms.add(lT);
						S.setListLegislativeTerms(listLegislativeTerms);
						lookupSenatore.put(id, S);
					}
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void readSenatoriTSV() {

		File senatoriTable = new File(Config.SENATORI_TSV);
		if(!senatoriTable.exists()){
			System.err.println(" PROBLEMS READING SOURCE FILE "+Config.SENATORI_TSV);
		}

		try{
			BufferedReader reader = new BufferedReader( new FileReader(senatoriTable));
			String line  = null;

			while( ( line = reader.readLine() ) != null) {
				// SKIP HEADER
				if(!line.startsWith("senatore\tnome")) {
					String[] fields = line.split("\t");
					String id = fields[0];
					String nome	= fields[1];
					String cognome	= fields[2];
					String sesso = fields[3];
					String legislatura	= fields[4];
					String inizioMandato = fields[5];	
					String fineMandato	= fields[6];
					String tipoMandato	= fields[7];
					String tipoFineMandato	= fields[8];
					String dataNascita	= fields[9];
					String cittaNascita = fields[10];	
					String cittaNascita_geoname = fields[11];	
					String provinciaNascita= fields[12];	
					String nazioneNascita= fields[13];
					
					// FORZA FINE MANDATO legislatura 18
					if(legislatura.equalsIgnoreCase("18") && fineMandato.trim().length()==0) {
						fineMandato = "2022-09-24"; // giorno prima delle elezioni
					}

					String urlSchedaPrefix = "http://www.senato.it/leg";
					String idNumerico = getIdNumericoSen(id);id.substring(id.lastIndexOf("/")+1,id.length());
					Senatore S;
					if((S=lookupSenatore.get(id))==null){
						S = new Senatore();
						S.setId(id);
						S.setCognome(cognome);
						S.setNome(nome);
						S.setDataNascita(dataNascita);
						S.setSesso(sesso);
						S.setCittaNascita(cittaNascita);
						S.setCittaNascita_geoname(cittaNascita_geoname);
						S.setProvinciaNascita(provinciaNascita);
						S.setNazioneNascita(nazioneNascita);
						LegislativeTerm lT = new LegislativeTerm();
						lT.setInizioMandato(inizioMandato);
						lT.setFineMandato(fineMandato);
						lT.setTipoMandato(tipoMandato);
						lT.setTipoFineMandato(tipoFineMandato);
						lT.setLegislatura(legislatura);
												
						lT.setUrlSchedaSenato(urlSchedaPrefix+"/"+legislatura+"/BGT/Schede/Attsen/"+idNumerico+".htm");
						List<LegislativeTerm> listLegislativeTerms = new ArrayList<LegislativeTerm>();
						listLegislativeTerms.add(lT);
						S.setListLegislativeTerms(listLegislativeTerms);
						lookupSenatore.put(id, S);
						String readableId = getReadableId(S);
						idSenatore2Readable.put(id, readableId);
						//System.out.println(readableId);
					}else {
						List<LegislativeTerm> listLegislativeTerms = S.getListLegislativeTerms();
						LegislativeTerm lT = new LegislativeTerm();
						lT.setInizioMandato(inizioMandato);
						lT.setFineMandato(fineMandato);
						lT.setTipoMandato(tipoMandato);
						lT.setTipoFineMandato(tipoFineMandato);
						lT.setLegislatura(legislatura);
						lT.setUrlSchedaSenato(urlSchedaPrefix+"/"+legislatura+"/BGT/Schede/Attsen/"+idNumerico+".htm");
						listLegislativeTerms.add(lT);
						S.setListLegislativeTerms(listLegislativeTerms);
						lookupSenatore.put(id, S);
					}
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private String getReadableId(Senatore S) {
		
		String nome = S.getNome();
		String cognome = S.getCognome();
		String annoNascita = S.getDataNascita();
		if(annoNascita != null & annoNascita.trim().length()>0 && annoNascita.indexOf("-")!=-1)
			annoNascita = annoNascita.substring(0,3);
		
		String readableId = cognome.replaceAll(" ", "").replaceAll("'", "")+nome.replaceAll(" ", "").replaceAll("'", "");
		
		if(idSenatore2Readable.values().contains(readableId))
			readableId=readableId+annoNascita;
		
		return readableId;
	}

	
	
	private void readGroupsTSV() {
				
		File gruppiTable = new File(Config.TAB_GRUPPI_TSV);
		if(!gruppiTable.exists()){
			System.err.println(" PROBLEMS READING SOURCE FILE "+Config.TAB_GRUPPI_TSV);
		}

		try{
			BufferedReader reader = new BufferedReader( new FileReader(gruppiTable));
			String line  = null;

			while( ( line = reader.readLine() ) != null) {
				// SKIP HEADER
				if(!line.startsWith("idGruppo\tdenominazione") && !line.startsWith("#")) {
					String[] fields = line.split("\t");
				
					String idGruppo = fields[0];
					String denominazione	= fields[1];
					String abbreviazione	= fields[2];
					String inizio_denominazione = fields[3];
					String fine_denominazione = "";
					if(fields.length>4)
						fine_denominazione = fields[4];
					
					Gruppo G = new Gruppo();
					G.setIdGruppo(idGruppo);
					G.setDenominazione(denominazione);
					G.setAbbreviazione(abbreviazione);
					G.setIdDenominazioneXML(abbreviazione);
					G.setInizio_denominazione(inizio_denominazione);
					G.setFine_denominazione(fine_denominazione);
					
					if((lookupGruppobyName.get(denominazione))==null){	
						lookupGruppobyName.put(denominazione, G);
					}
					
					List<Gruppo> listDG;
					if((listDG=lookupGruppibyId.get(idGruppo))==null){
						listDG = new ArrayList<Gruppo>();
						listDG.add(G);
						lookupGruppibyId.put(idGruppo, listDG);
					}else {
						listDG.add(G);
						lookupGruppibyId.put(idGruppo, listDG);
					}
						
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void readEditedAffiliationsTSV() {
		
		raw_affiliations = new ArrayList<Affiliation>();

		
		File affiliationAll = new File(Config.GROUP_AFFILIATION_ALL_TSV);
		if(!affiliationAll.exists()){
			System.err.println(" PROBLEMS READING SOURCE FILE "+Config.GROUP_AFFILIATION_ALL_TSV);
		}
		
		
		File affiliationGov = new File(Config.GROUP_AFFILIATION_GOV_TSV);
		if(!affiliationGov.exists()){
			System.err.println(" PROBLEMS READING SOURCE FILE "+Config.GROUP_AFFILIATION_GOV_TSV);
		}
	
		// 1 Affiliation ALL
		try{
			BufferedReader reader = new BufferedReader( new FileReader(affiliationAll));
			String line  = null;
			
			//gruppo	nomeGruppo	senatore	nome	cognome	carica	inizioAdesione	legislatura

			while( ( line = reader.readLine() ) != null) {
				// SKIP HEADER
				if(!line.startsWith("gruppo\tnomeGruppo") && !line.startsWith("#")) {
					String[] fields = line.split("\t");
				
					String nomeGruppo = fields[1];
					String idSenatore	= fields[2];
					if(idSenatore.startsWith("http"))
						idSenatore = idSenatore.substring(idSenatore.lastIndexOf("/")+1,idSenatore.length());
					String carica	= fields[5];
					String carica_en	= fields[6];
					String inizio_adesione = fields[7];
					String fine_adesione = fields[8];			
					String legislatura = fields[9];
					
					if(legislatura.equalsIgnoreCase("18") && fine_adesione.trim().length()==0) {
						fine_adesione = "2022-09-24"; // insediamento 19 legislatura
					}
					
					
					Affiliation A = new Affiliation();
					A.setNomeGruppo(nomeGruppo);
					A.setIdSenatore(idSenatore);
					A.setInizioAdesione(inizio_adesione);
					A.setFineAdesione(fine_adesione);
					A.setLegislatura(legislatura);
					A.setCarica(carica);
					A.setCarica_en(carica_en);
					A.setProvenance("all");
					
					if(!raw_affiliations.contains(A))
						raw_affiliations.add(A);
					//else {
					//System.err.println("*** FOUND DUPLICATE AFFILIATION : "+line);
					//}
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		
		// 2 Affiliation GOV
		try{
			BufferedReader reader = new BufferedReader( new FileReader(affiliationGov));
			String line  = null;

			//idSpeaker	affilialtion_to	role	from	to


			while( ( line = reader.readLine() ) != null) {
				// SKIP HEADER
				if(!line.startsWith("idSpeaker\taffilialtion_to")) {
					String[] fields = line.split("\t");

					String nomeGruppo = fields[1];
					String idSenatore	= fields[0];
					if(idSenatore.startsWith("http"))
						idSenatore = idSenatore.substring(idSenatore.lastIndexOf("/")+1,idSenatore.length());
					String inizio_adesione = fields[4];
				
					String fine_adesione = fields[5];
					
					String carica_it = fields[2];
					String carica_en = fields[3];



					Affiliation A = new Affiliation();
					A.setNomeGruppo(nomeGruppo);
					A.setIdSenatore(idSenatore);
					A.setInizioAdesione(inizio_adesione);
					A.setFineAdesione(fine_adesione);
					A.setLegislatura("");
					A.setCarica(carica_it);
					A.setCarica_en(carica_en);

					A.setProvenance("gov");

					if(!raw_affiliations.contains(A))
						raw_affiliations.add(A);
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
				
	}
	
	
	private void makeAffiliationLookup() {
		// FIXME - dopo averli inseriti potrei ordinarli per data
		//System.err.println("AFF length: "+raw_affiliations.size());
		for(Affiliation A:raw_affiliations) {
			String idSenatore = A.getIdSenatore();
			if(lookupAffiliationsbyId.get(idSenatore)==null) {
				List<Affiliation> affList = new ArrayList<Affiliation>();
				affList.add(A);
				lookupAffiliationsbyId.put(idSenatore, affList);
			}else {
				List<Affiliation> affList = lookupAffiliationsbyId.get(idSenatore);
				affList.add(A);
			}
		}
	}
	
			
	
	public HashMap<String, Senatore> getLookupSenatore() {
		return lookupSenatore;
	}

	
	public HashMap<String, Gruppo> getLookupGruppobyName() {
		return lookupGruppobyName;
	}
	

	public HashMap<String, List<Gruppo>> getLookupGruppibyId() {
		return lookupGruppibyId;
	}
	
	public List<Affiliation> getRaw_affiliations() {
		return raw_affiliations;
	}
	
	public HashMap<String, List<Affiliation>> getLookupAffiliationsbyId() {
		return lookupAffiliationsbyId;
	}
	
	
}
