package structures;

/**
 * Patent object that is intialized with the file, title, abstract, preamble, and claim sections of a patent.
 * The engine will categorize patents and use the 
 * @author Robert Kulesa
 *
 */
public class Patent {
	private final String file;
	private final String title;
	public final String abstractInfo;
	private final String preamble;
	private final String claim;
	private String category;
	
	private final int TITLE = 0; private final int ABSTRACT = 1; private final int PREAMBLE = 2; private final int CLAIM = 3;
	
	public Patent(String file, String title, String abstractInfo, String preamble, String claim) {
		this.file = file;
		this.title = title;
		this.abstractInfo = abstractInfo;
		this.preamble = preamble;
		this.claim = claim;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getCategory() {
		return this.category;
	}
	
	public String getFile() {
		return this.file;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getAbstract() {
		return this.abstractInfo;
	}
	
	public String getPreamble() {
		return this.preamble;
	}
	
	public String getClaim() {
		return this.claim;
	}
	
	public String getInfo(int idx) {
		if(idx == 0) return getTitle();
		else if(idx == 1) return getAbstract();
		else if (idx == 2) return getPreamble();
		else if (idx == 3) return getClaim();
		return null;
	}
	
	/**
	 * Check if the patent contains a string.
	 * 
	 * @param str
	 * @return Returns the section of the patent object that contains the passed string. Returns -1 if it is not contained at all.
	 */
	public int contains(String str) {
		if(this.getTitle().contains(str)) return TITLE;
		else if(this.getAbstract().contains(str)) return ABSTRACT;
		else if(this.getPreamble().contains(str)) return PREAMBLE;
		else if(this.getClaim().contains(str)) return CLAIM;
		return -1;
	}
	
	@Override
	/**
	 * @return Returns a String formatted patent object showing its file and category.
	 */
	public String toString() {
		return this.file + ": " + this.category;
	}
}
