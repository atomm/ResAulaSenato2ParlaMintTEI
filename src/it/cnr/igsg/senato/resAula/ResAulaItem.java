package it.cnr.igsg.senato.resAula;

import it.cnr.igsg.senato.Config;

public class ResAulaItem {
	
	
	private String plainText;
	private int tokenNum;
	private String extension;
	private String legislatura;
	private String corpus;
	private String filePath;
	private String docId;
	private String seduta; 
	private String data; 
	private String anno;
	
	
	
	public String getSeduta() {
		return seduta;
	}





	public void setSeduta(String seduta) {
		this.seduta = seduta;
	}





	public String getData() {
		return data;
	}





	public void setData(String data) {
		this.data = data;
	}





	public String getAnno() {
		return anno;
	}





	public void setAnno(String anno) {
		this.anno = anno;
	}
	
	
	
	public String getDocId() {
		return docId;
	}


	
	

	public void setDocId(String docId) {
		this.docId = docId;
	}





	public String getExtension() {
		return extension;
	}





	public void setExtension(String extension) {
		this.extension = extension;
	}


	public String getPlainText() {
		return plainText;
	}



	public void setPlainText(String plainText) {
		this.plainText = plainText;
	}



	public int getTokenNum() {
		return tokenNum;
	}



	public void setTokenNum(int tokenNum) {
		this.tokenNum = tokenNum;
	}



	public String getLegislatura() {
		return legislatura;
	}





	public void setLegislatura(String legislatura) {
		this.legislatura = legislatura;
	}





	public String getCorpus() {
		return corpus;
	}





	public void setCorpus(String corpus) {
		this.corpus = corpus;
	}





	public String getFilePath() {
		return filePath;
	}





	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	
//	public String toString() { 
//       String ret = this.getWorkURI();
////        ret+="\n id= "+this.getId()+" isODG "+this.isODG();
//       return ret+"\n"+this.getAmendentContentHTML();
//		//return this.getIdModifica() + " \t  fileName: "+this.getFileName();
//     } 
//	
	public String toCsv() {
		
		String csv = docId + Config.SEP  +extension+ Config.SEP + legislatura + Config.SEP + corpus + Config.SEP + filePath /*+Config.SEP+plainText*/ + Config.SEP+tokenNum+ Config.SEP+seduta+ Config.SEP+data+ Config.SEP+anno;
		return csv;
	}
	
}
