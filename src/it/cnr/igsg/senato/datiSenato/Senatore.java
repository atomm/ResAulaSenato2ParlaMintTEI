package it.cnr.igsg.senato.datiSenato;

import java.util.List;

public class Senatore {

	private String id;
	private String nome;
	private String cognome;	
	private String sesso;	
	private String dataNascita;	
	private String cittaNascita;	
	private String cittaNascita_geoname;	
	private String provinciaNascita;
	private String nazioneNascita;
	private String idCamera=null;

	

	public String getIdCamera() {
		return idCamera;
	}

	public void setIdCamera(String idCamera) {
		this.idCamera = idCamera;
	}

	// http://www.senato.it/leg/17/BGT/Schede/Attsen/00000656.htm
	private List<LegislativeTerm> listLegislativeTerms = null;

	public Senatore() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getSesso() {
		return sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	public String getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(String dataNascita) {
		this.dataNascita = dataNascita;
	}

	public String getCittaNascita() {
		return cittaNascita;
	}

	public void setCittaNascita(String cittaNascita) {
		this.cittaNascita = cittaNascita;
	}

	public String getCittaNascita_geoname() {
		return cittaNascita_geoname;
	}

	public void setCittaNascita_geoname(String cittaNascita_geoname) {
		this.cittaNascita_geoname = cittaNascita_geoname;
	}

	public String getProvinciaNascita() {
		return provinciaNascita;
	}

	public void setProvinciaNascita(String provinciaNascita) {
		this.provinciaNascita = provinciaNascita;
	}

	public String getNazioneNascita() {
		return nazioneNascita;
	}

	public void setNazioneNascita(String nazioneNascita) {
		this.nazioneNascita = nazioneNascita;
	}

	public List<LegislativeTerm> getListLegislativeTerms() {
		return listLegislativeTerms;
	}

	public void setListLegislativeTerms(List<LegislativeTerm> listLegislativeTerms) {
		this.listLegislativeTerms = listLegislativeTerms;
	}
	
	

	
	
}
