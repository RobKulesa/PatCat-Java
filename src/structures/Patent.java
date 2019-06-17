package structures;


public class Patent {
	private String file;
	private String title;
	private String abstractInfo;
	private String claim;
	
	public Patent(String file, String title, String abstractInfo, String claim) {
		this.file = file;
		this.title = title;
		this.abstractInfo = abstractInfo;
		this.claim = claim;
	}
	
	public Patent(String file) {
		this.file = file;
	}
	
	public Patent() {
		
	}
	
	public void setFile(String str) {
		this.file = str;
	}
	
	public void setTitle(String str) {
		this.title = str;
	}
	
	public void setAbstract(String str) {
		this.abstractInfo = str;
	}
	
	public void setClaim(String str) {
		this.claim = str;
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
	
	public String getClaim() {
		return this.claim;
	}

}
