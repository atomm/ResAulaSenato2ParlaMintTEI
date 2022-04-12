package it.cnr.igsg.senato.datiSenato;

public class Affiliation {

	// da composizione inizio
	// gruppo	nomeGruppo	senatore	nome	cognome	carica	inizioAdesione	legislatura
	// gruppo	nomeGruppo	senatore	nome	cognome	carica	inizioAdesione	legislatura

	
	// da variazioni
	// gruppo	nomeGruppo	senatore	nome	cognome	inizio	fine	carica	legislatura

	
	private String idGruppo="";
	private String cognome="";
	private String nome="";

	
	private String idSenatore;
	private String nomeGruppo;
	private String inizioAdesione;
	private String fineAdesione;
	private String carica;
	private String legislatura;
	private String provenance;
	
	
	private String role_XSD;


	
	public String getIdGruppo() {
		return idGruppo;
	}


	public void setIdGruppo(String idGruppo) {
		this.idGruppo = idGruppo;
	}


	public String getCognome() {
		return cognome;
	}


	public void setCognome(String cognome) {
		this.cognome = cognome;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
	public String getRole_XSD() {
		return role_XSD;
	}


	public Affiliation() {
		
	}
	
	
	public String getIdSenatore() {
		return idSenatore;
	}
	public void setIdSenatore(String idSenatore) {
		this.idSenatore = idSenatore;
	}
	public String getNomeGruppo() {
		return nomeGruppo;
	}
	public void setNomeGruppo(String nomeGruppo) {
		this.nomeGruppo = nomeGruppo;
	}
	public String getInizioAdesione() {
		return inizioAdesione;
	}
	public void setInizioAdesione(String inizioAdesione) {
		this.inizioAdesione = inizioAdesione;
	}
	public String getFineAdesione() {
		return fineAdesione;
	}
	public void setFineAdesione(String fineAdesione) {
		this.fineAdesione = fineAdesione;
	}
	public String getCarica() {
		return carica;
	}
	public void setCarica(String carica) {
		this.carica = carica;
		this.role_XSD = carica2role(carica);
	}
	public String getLegislatura() {
		return legislatura;
	}
	public void setLegislatura(String legislatura) {
		this.legislatura = legislatura;
	}

	public String getProvenance() {
		return provenance;
	}
	public void setProvenance(String provenance) {
		this.provenance = provenance;
	}
	
	public String toTSV() {
		String urlSchedaPrefix = "http://www.senato.it/leg";
		String urlScheda = urlSchedaPrefix+"/"+legislatura+"/BGT/Schede/Attsen/"+completeWithZeros(idSenatore)+".htm";
		return(idGruppo+"\t"+nomeGruppo+"\t"+idSenatore+"\t"+nome+"\t"+cognome+"\t"+carica+"\t"+inizioAdesione+"\t"+fineAdesione+"\t"+legislatura+"\t"+provenance+"\t"+urlScheda);
	}
	
	private String completeWithZeros(String idSenatore) {
		
		if(idSenatore.length()==2)
			return "000000"+idSenatore;
		if(idSenatore.length()==3)
			return "00000"+idSenatore;
		if(idSenatore.length()==4)
			return "0000"+idSenatore;
		if(idSenatore.length()==5)
			return "000"+idSenatore;
		if(idSenatore.length()==6)
			return "00"+idSenatore;
		if(idSenatore.length()==7)
			return "0"+idSenatore;
		
		return idSenatore;
	}
	
	private String carica2role(String carica) {

		switch(carica) {
		case("Membro"): return "member";
		case("Presidente"): return "president";
		case("Segretario"): return "secretary";
		case("Segretario d'Aula"): return "secretary";
		case("Tesoriere"): return "member";
		case("Vicepresidente"): return "vicePresident";
		case("Vicepresidente Tesoriere"): return "vicePresident";
		case("Vicepresidente Vicario"): return "vicePresident";
		}


		if(carica.toLowerCase().startsWith("presidente del consiglio"))
			return "primeMinister";
		if(carica.toLowerCase().startsWith("vice presidente del consiglio"))
			return "deputyPrimeMinister";
		if(carica.toLowerCase().startsWith("ministr"))
			return "minister";
		if(carica.toLowerCase().startsWith("sottosegretar")||
				carica.toLowerCase().startsWith("vice ministro"))
			return "deputyMinister";

		//System.err.println("find XSD role for "+carica);
		return "member";

	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual=false;
		
        if(obj!=null && obj instanceof Affiliation) {
        	Affiliation a = (Affiliation) obj;
        	isEqual=(this.idSenatore.equals(a.idSenatore) && 
        			this.nomeGruppo.equals(a.nomeGruppo) &&
        			this.inizioAdesione.equals(a.inizioAdesione) &&
        			this.fineAdesione.equals(a.fineAdesione) &&
        			this.carica.equals(a.carica) &&
        			this.legislatura.equals(a.legislatura) 
        			//&& this.provenance.equals(a.provenance)
        			);
        }
        return isEqual;
	}


	

	
}
