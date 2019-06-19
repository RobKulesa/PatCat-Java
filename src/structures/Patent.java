package structures;

import java.util.ArrayList;

public class Patent {
	private String file;
	private String title;
	private String abstractInfo;
	private String claim;
	private ArrayList<String> category;
	private ArrayList<String> secondary;
	private int score;
	
	public Patent(String file, String title, String abstractInfo, String claim) {
		this.file = file;
		this.title = title;
		this.abstractInfo = abstractInfo;
		this.claim = claim;
		this.score = 0;
	}
	
	public Patent(String file) {
		this.file = file;
		this.score = 0;
	}
	
	public Patent() {
		this.score = 0;
	}
	
	public void setCategory(ArrayList<String> category) {
		this.category = category;
	}
	
	public void setSecondary(ArrayList<String> secondary) {
		this.secondary = secondary;
	}
	
	public void setScore(int score) {
		this.score = score;
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
	
	public int getScore() {
		return this.score;
	}
	
	public ArrayList<String> getCategory() {
		return this.category;
	}
	
	public ArrayList<String> getSecondary() {
		return this.secondary;
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
	
	public String toString() {
		return this.file + ": " + this.category + " (" + this.score + ") (" + this.secondary + ")";
	}
}
