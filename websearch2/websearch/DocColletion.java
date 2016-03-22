package indexingranking;

public class DocColletion {

	public static long _id = 0;
	public long id;
	public String documentname;

	public String[] terms;
	public int[] freq;
	
	public DocColletion(){
		
	}

	public DocColletion(String documentname, String[] terms, int[] freq) {

		this.id = _id++;
		this.documentname = documentname;
		this.terms = terms;
		this.freq = freq;

	}

	public String getDocumentname() {
		return documentname;
	}

	public void setDocumentname(String documentname) {
		this.documentname = documentname;
	}

	public String[] getTerms() {
		return terms;
	}

	public void setTerms(String[] terms) {
		this.terms = terms;
	}

	public int[] getFreq() {
		return freq;
	}

	public void setFreq(int[] freq) {
		this.freq = freq;
	}

}
