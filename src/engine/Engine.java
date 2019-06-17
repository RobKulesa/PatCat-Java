package engine;

import java.io.*;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import structures.*;
//12:00 - 1:00 am

public class Engine {
	HashMap<ArrayList<String>, ArrayList<Occurrence>> masterIndex;
	HashMap<ArrayList<String>, ArrayList<Integer>> categories;
	
	public Engine() {
		masterIndex = new HashMap<ArrayList<String>, ArrayList<Occurrence>>(1000,2.0f);
		categories = new HashMap<ArrayList<String>, ArrayList<Integer>>(20,2.0f);
	}
	
	public void makeIndex(String keywordsFile, String patentFile) throws IOException {
		fillCategories(keywordsFile);
	}
	
	public void fillCategories(String fileName) throws IOException {
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(new FileReader(fileName));
		ArrayList<String> keywords = new ArrayList<String>();
		ArrayList<Integer> weights = new ArrayList<Integer>();
		String data;
		for(CSVRecord record : records) {
			Iterator<String> iterator = record.iterator();
			while(iterator.hasNext()) {
				data = iterator.next();
				if(data.contains("0") || data.contains("1") || data.contains("2") || data.contains("3") || data.contains("4") || data.contains("5") || data.contains("6") || data.contains("7") || data.contains("8") || data.contains("9")) {
					weights.add(Integer.parseInt(data));
				} else {
					keywords.add(data);
				}
			}
			if(!weights.isEmpty()) {
				categories.put(keywords, weights);
				keywords = new ArrayList<String>();
				weights = new ArrayList<Integer>();
			}
		}
	}
	
	public void mergeIndex(HashMap<ArrayList<String>, Occurrence> map) {
		for(ArrayList<String> key : map.keySet()) {
			ArrayList<Occurrence> occs = new ArrayList<Occurrence>();
			if(masterIndex.containsKey(key)) occs = masterIndex.get(key);
			occs.add(map.get(key));
			
			ArrayList<Integer> midpoints = insertLastOccurrence(occs);
			if(midpoints != null && midpoints.size() > 0) { 
				int mid = midpoints.get(midpoints.size() - 1);
				if(mid >= 0) {
					if(occs.get(mid).getScore() > occs.get(occs.size() - 1).getScore()) {
						occs.add(mid + 1, occs.remove(occs.size() - 1));
					} else {
						occs.add(mid, occs.remove(occs.size() - 1));
					}
				}
			}
			masterIndex.put(key, occs);
		}
	}
	
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		ArrayList<Integer> midpoints = new ArrayList<Integer>();
		if(occs.size() < 2) return null;
		Occurrence last = occs.get(occs.size() - 1);
		int left = 0; int right = occs.size() - 2;
		int mid = 0;
		while(left <= right) {
			mid = (left + right) / 2;
			midpoints.add(mid);
			if(occs.get(mid).getScore() == last.getScore()) {
				break;
			}
			if(occs.get(mid).getScore() > last.getScore()) left = mid + 1;
			else right = mid - 1;
		}
		
		return midpoints;
	}
	
	public ArrayList<Patent> loadPatents(String fileName) throws IOException {
		ArrayList<Patent> patents = new ArrayList<Patent>();
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new FileReader(fileName));
		for(CSVRecord record: records) {
			String file = record.get("Publication Number"); 
			String title = record.get("Title").toLowerCase();
			String abstractInfo = record.get("Abstract").toLowerCase();
			String claim = record.get("Claims").toLowerCase();
			patents.add(new Patent(file, title, abstractInfo, claim));
		}
		return patents;
	}
	
	public HashMap<ArrayList<String>, Occurrence> loadFromDocument(String fileName) throws IOException {
		HashMap<ArrayList<String>, Occurrence> map = new HashMap<ArrayList<String>, Occurrence>(500, 2.0f);
		ArrayList<Patent> patents = loadPatents(fileName);
		String data;
		for(Patent patent : patents) {
			StringTokenizer titleTk = new StringTokenizer(patent.getTitle());
			while(titleTk.hasMoreTokens()) {
				data = getKeyword(titleTk.nextToken());
				if(data != null && data.length() > 2) {
					boolean contains = false;
					for(ArrayList<String> key : map.keySet()) {
						for(String str : key) {
							contains = true;
							map = addScore(map, key, Occurrence.TITLE);
						}
					}
					if(!contains) {
						for(ArrayList<String> category : categories.keySet()) {
							for(String str : category) {
								if(data.equalsIgnoreCase(str)) {
									map.put(category, new Occurrence(patent.getFile(), Occurrence.TITLE));
								}
							}
						}
					}
				}
			}
			
			StringTokenizer abstractTk = new StringTokenizer(patent.getAbstract());
			while(abstractTk.hasMoreTokens()) {
				data = getKeyword(abstractTk.nextToken());
				if(data != null && data.length() > 2) {
					boolean contains = false;
					for(ArrayList<String> key : map.keySet()) {
						for(String str : key) {
							contains = true;
							map = addScore(map, key, Occurrence.ABSTRACT);
						}
					}
					if(!contains) {
						for(ArrayList<String> category : categories.keySet()) {
							for(String str : category) {
								if(data.equalsIgnoreCase(str)) {
									map.put(category, new Occurrence(patent.getFile(), Occurrence.ABSTRACT));
								}
							}
						}
					}
				}
			}
			
			StringTokenizer claimsTk = new StringTokenizer(patent.getAbstract());
			while(claimsTk.hasMoreTokens()) {
				data = getKeyword(claimsTk.nextToken());
				if(data != null && data.length() > 2) {
					boolean contains = false;
					for(ArrayList<String> key : map.keySet()) {
						for(String str : key) {
							contains = true;
							map = addScore(map, key, Occurrence.CLAIM);
						}
					}
					if(!contains) {
						for(ArrayList<String> category : categories.keySet()) {
							for(String str : category) {
								if(data.equalsIgnoreCase(str)) {
									map.put(category, new Occurrence(patent.getFile(), Occurrence.CLAIM));
								}
							}
						}
					}
				}
			}
		}
		return map;
	}
	
	public HashMap<ArrayList<String>, Occurrence> addScore(HashMap<ArrayList<String>, Occurrence> map, ArrayList<String> key, int weight) {
			if(weight == Occurrence.TITLE) map.get(key).addScore(Occurrence.TITLE);
			else if(weight == Occurrence.ABSTRACT) map.get(key).addScore(Occurrence.ABSTRACT);
			else map.get(key).addScore(Occurrence.CLAIM);
		return map;
	}
	
	
	public String getKeyword(String word) {
		if(word == null || word.equals("") || word.equals(" ")) return null;
		word = word.toLowerCase();
		for(int i = 0; i < word.length(); i++) {
			char curr = word.charAt(i);
			if(curr != 'a' && curr != 'b' && curr != 'c' && curr != 'd' && curr != 'e' && curr != 'f' && curr != 'g' && curr != 'h' && curr != 'i' && curr != 'j' && curr != 'k' && curr != 'l' && curr != 'm' && curr != 'n' && curr != 'o' && curr != 'p' && curr != 'q' && curr != 'r' && curr != 's' && curr != 't' && curr != 'u' && curr != 'v' && curr != 'w' && curr != 'x' && curr != 'y' && curr != 'z') {
				try { word = word.substring(0, i) + word.substring(i + 1); }
				catch(StringIndexOutOfBoundsException e) {
					word = word.substring(0, i);
				}
				i--;
			}
		}
		return word;
	}
}
