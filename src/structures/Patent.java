package structures;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * @author Robert Kulesa
 *
 */
public class Patent {
	private String file;
	private String title;
	private String abstractInfo;
	private String claim;
	private String category;
	private HashMap<ArrayList<String>, Integer> list;
	private ArrayList<ArrayList<String>> sorted;
	
	public Patent(String file, String title, String abstractInfo, String claim) {
		this.file = file;
		this.title = title;
		this.abstractInfo = abstractInfo;
		this.claim = claim;
		this.list = new HashMap<ArrayList<String>, Integer>(20, 2.0f);
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public void addToList(ArrayList<String> key, Integer value) {
		this.list.put(key, value);
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
	
	public String getCategory() {
		return this.category;
	}
	
	public Integer getScore(ArrayList<String> key) {
		return this.list.get(key);
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

	public HashMap<ArrayList<String>, Integer> getList() {
		return this.list;
	}
	
	public ArrayList<ArrayList<String>> getSorted() {
		return this.sorted;
	}
	
	public void addSorted(ArrayList<String> category) {
		this.sorted.add(category);
	}
	@Override
	/**
	 * @return Returns a String formatted patent object showing its main category, score, and secondary category
	 */
	public String toString() {
		return this.file + ": " + this.category + " | " + this.list;
	}
}
