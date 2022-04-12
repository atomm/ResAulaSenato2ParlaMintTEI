package it.cnr.igsg.senato;

public class Config {
	
	
	
	public static String SEP = "\t";
	
	// phase 1 - source preprocessing and cleaning HTML to EMBEDDED_XML
	
	public static String RES_AULA_FOLDER = "data/Resaula-ParlaMINT/SOURCE"; 
	public static String UNIQUE_GRID_CSV_PATH = "data/unique_grid.csv"; 

	public static String CSV_OUT = "data/";	
	public static String RES_AULA_EMBEDDED_XML = "data/Resaula-ParlaMINT/Resaula-embedded-XML"; 
	public static String TXT_OUT = "data/txt/";


	// phase 2 -  XML  2 TEI conversion

	// TEI OUTPUT
	// ALL
	public static String RES_AULA_TEI_OUTPUT = "data/Resaula-ParlaMINT/Resaula-TEI-output"; 
	// TEST
	//public static String RES_AULA_TEI_OUTPUT = "data/Resaula-ParlaMINT/Resaula-TEI-test"; 
	

	
	// DATI SENATO
	public static String SENATORI_TSV ="data/Resaula-ParlaMINT/dati_senato/senatori_leg17_e_leg18.tsv";
	public static String OTHER_SPEAKERS_TSV ="data/Resaula-ParlaMINT/dati_senato/oratori_not_senato.tsv";
	public static String TAB_GRUPPI_TSV ="data/Resaula-ParlaMINT/dati_senato/tab_denominazione_gruppi_17e18.tsv";
	public static String GROUP_AFFILIATION_ALL_TSV ="data/Resaula-ParlaMINT/dati_senato/raw_affiliations_edit.tsv";
	public static String GROUP_AFFILIATION_GOV_TSV ="data/Resaula-ParlaMINT/dati_senato/affiliazioni_GOV.tsv";
	
	
//	public static String GROUP_AFFILIATION1_TSV ="data/Resaula-ParlaMINT/dati_senato/composizione_gruppi_inizio_leg17_e_leg18.tsv";
//	public static String GROUP_AFFILIATION2_TSV ="data/Resaula-ParlaMINT/dati_senato/variazioni_gruppi_leg_17_e_leg18.tsv";
//	public static String GROUP_AFFILIATION3_TSV ="data/Resaula-ParlaMINT/dati_senato/composizione_gruppi_fine_leg17_e_leg18.tsv";

	

	
}
