package it.cnr.igsg.senato.datiSenato;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Gruppo implements Comparable<Gruppo>{
	
	private String idGruppo;
	private String denominazione;
	private String abbreviazione;
	private String inizio_denominazione;
	private String fine_denominazione;
	private Date inizio_denominazione_asDate;
	private Date fine_denominazione_asDate;
	private String idDenominazioneXML;

	
	
	
	
	public String getIdDenominazioneXML() {
		return idDenominazioneXML;
	}
	public void setIdDenominazioneXML(String abbreviazione) {
		this.idDenominazioneXML = abbr2idXML(abbreviazione);
	}
	
	
	private String abbr2idXML(String abbreviazione) {
		abbreviazione = abbreviazione.replaceAll(",", " ");
		abbreviazione = abbreviazione.replaceAll("'", " ");
		abbreviazione = abbreviazione.replaceAll("\\Q(\\E", " ");
		abbreviazione = abbreviazione.replaceAll("\\Q)\\E", " ");
		abbreviazione = abbreviazione.replaceAll("\\s+"," ").trim();
		abbreviazione= abbreviazione.replaceAll(" ",".");
		
		if(abbreviazione.equals("GAL.GS.MpA.NPSI.PpI.IdV.VGF.FV"))
			return "GAL.GS.MpA";
		if(abbreviazione.equals("GAL.DI.Id.GS.M.MPL.RI.E-E"))
			return "GAL.DI.Id";
		if(abbreviazione.equals("Aut.SVP.UV.PATT.UPT.-.PSI"))
			return "Aut.SVP.UV.PATT.UPT.PSI";
		if(abbreviazione.equals("Aut.SVP.UV.PATT.UPT.-PSI-MAIE"))
			return "Aut.SVP.UV.PATT.UPT.PSI-MAIE";
		
		return abbreviazione;
	}
	
	public String getInizio_denominazione() {
		return inizio_denominazione;
	}
	public void setInizio_denominazione(String inizio_denominazione) {
		this.inizio_denominazione = inizio_denominazione;
		this.inizio_denominazione_asDate = parseDate(inizio_denominazione);

	}
	
	public String getIdGruppo() {
		return idGruppo;
	}
	public void setIdGruppo(String idGruppo) {
		this.idGruppo = idGruppo;
	}
	public String getDenominazione() {
		return denominazione;
	}
	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}
	public String getAbbreviazione() {
		return abbreviazione;
	}
	public void setAbbreviazione(String abbreviazione) {
		this.abbreviazione = abbreviazione;
	}
	public String getFine_denominazione() {
		return fine_denominazione;
	}
	public void setFine_denominazione(String fine_denominazione) {
		this.fine_denominazione = fine_denominazione;
		this.fine_denominazione_asDate = parseDate(fine_denominazione);
	}

	
	private Date parseDate(String data) {
		if(data.trim().length()==0) {
			return null;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(data);

		}catch(Exception ex) {
			ex.printStackTrace();
			return null; 
		}		
	}
	
	public Date getInizio_denominazione_asDate() {
		return inizio_denominazione_asDate;
	}
	public Date getFine_denominazione_asDate() {
		return fine_denominazione_asDate;
	}
	
	
	@Override
	// ordinamento per inizio denominazione (serve per stabilire relazioni di renaming dei gruppi)
	public int compareTo(Gruppo g) {
		if (inizio_denominazione_asDate == null || g.getInizio_denominazione_asDate()==null) {
			return 0;
		}
		return inizio_denominazione_asDate.compareTo(g.getInizio_denominazione_asDate());
	}
	
	
}
