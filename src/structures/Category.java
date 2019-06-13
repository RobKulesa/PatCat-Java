package structures;

import java.util.ArrayList;

public class Category {
	private String category;
	private ArrayList<String> keywords;
	
	public Category(String keyword) {
		this.category = keyword;
	}
	
	public void addKeyword(String str) {
		this.keywords.add(str);
	}
	public void addKeywords(ArrayList<String> keywords) {
		for(String str : keywords) this.keywords.add(str);
	}
	
	public String getKeyword() {
		return this.category;
	}
	
	public ArrayList<String> getSynonyms() {
		return this.keywords;
	}
}
