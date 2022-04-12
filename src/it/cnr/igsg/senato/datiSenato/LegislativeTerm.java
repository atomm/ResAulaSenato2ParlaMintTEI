package it.cnr.igsg.senato.datiSenato;

public class LegislativeTerm {
	private String legislatura;
	private String inizioMandato;	
	private String fineMandato;	
	private String tipoMandato;	
	private String tipoFineMandato;
	private String urlSchedaSenato;
	
	
	public LegislativeTerm() {
		
	}	

	public String getLegislatura() {
		return legislatura;
	}

	public void setLegislatura(String legislatura) {
		this.legislatura = legislatura;
	}

	public String getInizioMandato() {
		return inizioMandato;
	}

	public void setInizioMandato(String inizioMandato) {
		this.inizioMandato = inizioMandato;
	}

	public String getFineMandato() {
		return fineMandato;
	}

	public void setFineMandato(String fineMandato) {
		this.fineMandato = fineMandato;
	}

	public String getTipoMandato() {
		return tipoMandato;
	}

	public void setTipoMandato(String tipoMandato) {
		this.tipoMandato = tipoMandato;
	}

	public String getTipoFineMandato() {
		return tipoFineMandato;
	}

	public void setTipoFineMandato(String tipoFineMandato) {
		this.tipoFineMandato = tipoFineMandato;
	}
	
	public String getUrlSchedaSenato() {
		return urlSchedaSenato;
	}

	public void setUrlSchedaSenato(String urlSchedaSenato) {
		this.urlSchedaSenato = urlSchedaSenato;
	}
	
}
