package structures;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * @author Robert Kulesa
 *
 */
public class Patent {
	private final String file;
	private final String title;
	private String modTitle;
	private final String abstractInfo;
	private String modAbstract;
	private final String preamble;
	private String modPreamble;
	private final String claim;
	private String modClaim;
	private String category;
	private HashMap<ArrayList<String>, Integer> list;
	private ArrayList<ArrayList<String>> sorted;
	
	public Patent(String file, String title, String abstractInfo, String preamble, String claim) {
		this.file = file;
		this.title = title;
		this.modTitle = title;
		this.abstractInfo = abstractInfo;
		this.modAbstract = abstractInfo;
		this.preamble = preamble;
		this.modPreamble = preamble;
		this.claim = claim;
		this.modClaim = claim;
		this.list = new HashMap<ArrayList<String>, Integer>(20, 2.0f);
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public void addToList(ArrayList<String> key, Integer value) {
		this.list.put(key, value);
	}
	
	public void setModTitle(String str) {
		this.modTitle = str;
	}
	
	public void setModAbstract(String str) {
		this.modAbstract= str;
	}
	
	public void setModPreamble(String str) {
		this.modPreamble = str;
	}
	
	public void setModClaim(String str) {
		this.modClaim = str;
	}
	
	public void resetInfo() {
		this.modTitle = this.title;
		this.modAbstract = this.abstractInfo;
		this.modPreamble = this.preamble;
		this.modClaim = this.claim;
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
	
	public String getModTitle() {
		return this.modTitle;
	}
	
	public String getModAbstract() {
		return this.modAbstract;
	}
	
	public String getModPreamble() {
		return this.modPreamble;
	}
	
	public String getModClaim() {
		return this.modClaim;
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
	
	public String getInfo(int idx) {
		if(idx == 0) return getModTitle();
		else if(idx == 1) return getModAbstract();
		else if (idx == 2) return getModPreamble();
		else if (idx == 3) return getModClaim();
		return null;
	}
	
	public void setInfo(int idx, String str) {
		if(idx == 0) setModTitle(str);
		if(idx == 1) setModAbstract(str);
		if(idx == 2) setModPreamble(str);
		if(idx == 3) setModClaim(str);
	}
	@Override
	/**
	 * @return Returns a String formatted patent object showing its main category, score, and secondary category
	 */
	public String toString() {
		return this.file + ": " + this.category + " | " + this.list;
	}
}
