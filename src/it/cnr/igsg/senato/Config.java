package it.cnr.igsg.senato;

public class Config {
	
	
	
	public static String SEP = "\t";
	
	// phase 1 - source preprocessing and cleaning HTML to EMBEDDED_XML
	
	public static String RES_AULA_FOLDER = "data/Resaula-ParlaMINT/SOURCE-v3"; 
	public static String UNIQUE_GRID_CSV_PATH = "data/unique_grid-v3.csv"; 
	
	
//	public static String RES_AULA_FOLDER = "data/Resaula-ParlaMINT/SOURCE-v2"; 
//	public static String UNIQUE_GRID_CSV_PATH = "data/unique_grid-v2.csv";

	public static String CSV_OUT = "data/";	
	public static String RES_AULA_EMBEDDED_XML = "data/Resaula-ParlaMINT/Resaula-embedded-XML-v3"; 
	
//	public static String RES_AULA_EMBEDDED_XML = "data/Resaula-ParlaMINT/Resaula-embedded-XML-v2"; 

	public static String TXT_OUT = "data/txt/";


	// phase 2 -  XML  2 TEI conversion

	// TEI OUTPUT
	// ALL
	public static String RES_AULA_TEI_OUTPUT = "data/Resaula-ParlaMINT/Resaula-TEI-output"; 
	// TEST
	//public static String RES_AULA_TEI_OUTPUT = "data/Resaula-ParlaMINT/Resaula-TEI-test"; 
	

	
	// DATI SENATO
	public static String SENATORI_TSV ="data/Resaula-ParlaMINT/dati_senato/senatori_leg17_e_leg18_v3.tsv";
	public static String OTHER_SPEAKERS_TSV ="data/Resaula-ParlaMINT/dati_senato/oratori_not_senato_v3.tsv";
	public static String TAB_GRUPPI_TSV ="data/Resaula-ParlaMINT/dati_senato/tab_denominazione_gruppi_17e18_v3.tsv";
	//public static String GROUP_AFFILIATION_ALL_TSV ="data/Resaula-ParlaMINT/dati_senato/raw_affiliations_edit.tsv";
	public static String GROUP_AFFILIATION_ALL_TSV ="data/Resaula-ParlaMINT/dati_senato/affiliazioni_gruppi_LEG17_LEG18_v3.tsv";
	public static String GROUP_AFFILIATION_GOV_TSV ="data/Resaula-ParlaMINT/dati_senato/affiliazioni_GOV_v3.tsv";


	
}
