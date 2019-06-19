package engine;

import java.io.*;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.opencsv.CSVWriter;

import structures.*;
//12:00 - 1:00 am
//11:30 - 3 pm
//10:45 - 12:22 am

public class Engine {
	private final int TITLE = 0;
	private final int ABSTRACT = 1;
	private final int CLAIM = 2;
	HashMap<ArrayList<String>, ArrayList<Occurrence>> masterIndex;
	HashMap<ArrayList<String>, ArrayList<Integer>> categories;
	
	public Engine() {
		masterIndex = new HashMap<ArrayList<String>, ArrayList<Occurrence>>(1000,2.0f);
		categories = new HashMap<ArrayList<String>, ArrayList<Integer>>(20,2.0f);
	}
	
	public void makeIndex(String keywordsFile, String patentFile) throws IOException {
		fillCategories(keywordsFile);
		ArrayList<Patent> patents = loadPatents(patentFile);
		for(Patent patent : patents) {
			mergeIndex(loadFromDocument(patent)); 
		}
		
		//for(ArrayList<String> key : masterIndex.keySet()) {
			//System.out.print(key + " = " + masterIndex.get(key) + "\n");
		//}
		patents = insertCategories(patents);
		for(Patent patent : patents) {
			System.out.println(patent);
		}
		export("output.csv", patents);
	}
	
	public void fillCategories(String fileName) throws IOException {
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(new FileReader(fileName));
		ArrayList<String> keywords = new ArrayList<String>();
		ArrayList<Integer> weights = new ArrayList<Integer>();
		String data;
		for(CSVRecord record : records) {
			Iterator<String> iterator = record.iterator();
			while(iterator.hasNext()) {
				data = iterator.next().toLowerCase();
				if(data.length() > 0) {
					//System.out.println(data);
					if(data.matches(".*\\d.*")) {
						weights.add(Integer.parseInt(data));
					} else {
						keywords.add(data);
					}
				}	
			}
			if(!weights.isEmpty()) {
				categories.put(keywords, weights);
				keywords = new ArrayList<String>();
				weights = new ArrayList<Integer>();
			}
		}
		//System.out.println(categories);
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
		//System.out.println(patents);
		return patents;
	}
	
	public HashMap<ArrayList<String>, Occurrence> loadFromDocument(Patent patent) throws IOException {
		HashMap<ArrayList<String>, Occurrence> map = new HashMap<ArrayList<String>, Occurrence>(500, 2.0f);
		
		map = buildMap(patent, TITLE, new StringTokenizer(patent.getTitle()), map);
		map = buildMap(patent, ABSTRACT, new StringTokenizer(patent.getAbstract()), map);
		map = buildMap(patent, CLAIM, new StringTokenizer(patent.getClaim()), map);
		return map;
	}
	
	public HashMap<ArrayList<String>, Occurrence> buildMap(Patent patent, int idx, StringTokenizer tk, HashMap<ArrayList<String>, Occurrence> map) {
		ArrayList<String> keywords;
		while(tk.hasMoreTokens()) {
			keywords = getKeywords(tk.nextToken());
			for(String data : keywords) {
				if(data != null && data.length() > 2) {
					boolean contains = false;
					for(ArrayList<String> key : map.keySet()) {
						for(int i = 0; i < key.size(); i++) {
							String str = key.get(i);
							if(data.equalsIgnoreCase(str)) {
								contains = true;
								map.get(key).addScore(idx, categories.get(key).get(i));
								//System.out.println(map.get(key) + " increased");
							}
						}
					}
					if(!contains) {
						for(ArrayList<String> category : categories.keySet()) {
							for(int i = 0; i < category.size(); i++) {
								String str = category.get(i);
								if(data.equalsIgnoreCase(str) && categories.get(category).get(i) > 0) {
									int[] arr = {0, 0, 0};
									if(idx == TITLE) arr[TITLE] = 1;
									else if(idx == ABSTRACT) arr[ABSTRACT] = 1;
									else arr[CLAIM] = 1;
									map.put(category, new Occurrence(patent.getFile(), arr));
									//System.out.println(map.get(category) + " added");
								}
							}
						}
					}
				}
			}
		}
		return map;
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
					if(occs.get(mid).compareTo(occs.get(occs.size() - 1)) > 0) {
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
			if(occs.get(mid).compareTo(last) == 0) {
				break;
			}
			if(occs.get(mid).compareTo(last) > 0) left = mid + 1;
			else right = mid - 1;
		}
		
		return midpoints;
	}
	
	public ArrayList<String> getKeywords(String str) {
		if(str == null || str.equals("") || str.equals(" ")) return null;
		int idx = str.indexOf("-");
		ArrayList<String> keywords = new ArrayList<String>();
		if(idx == 0) keywords.add(str.substring(1));
		else if(idx == str.length() - 1) keywords.add(str.substring(0, str.length() - 1));
		else if(idx > 1) {
			keywords.add(str.substring(0, idx));
			keywords.add(str.substring(idx + 1));
		} else {
			keywords.add(str);
		}
			
		for(int i = 0; i < keywords.size(); i++) {
			keywords.add(keywords.remove(i).replaceAll("[^a-z]", ""));
		}
		return keywords;
	}
	
	public ArrayList<Patent> insertCategories(ArrayList<Patent> patents) {
		for(ArrayList<String> key : masterIndex.keySet()) {
			ArrayList<Occurrence> occList = masterIndex.get(key);
			for(Occurrence o : occList) {
				for(Patent p : patents) {
					if(o.getFile().equals(p.getFile())) {
						if(o.getScore() > p.getScore()) {
							p.setCategory(key);
							p.setScore(o.getScore());
						} else if(o.getScore() == p.getScore()) {
							p.setSecondary(key);
						}
					}
				}
			}
		}
		return patents;
	}
	
	public void export(String fileName, ArrayList<Patent> patents) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(fileName));
		String[] header = {"File", "Title", "Category", "Score", "Secondary (if tie)"};
		writer.writeNext(header);
		for(Patent patent : patents) {
			String file = patent.getFile(); String title = patent.getTitle(); String score = patent.getScore() + "";
			String category = null;
			if(patent.getCategory() != null) {
				for(String keyword : patent.getCategory()) {
					if(category == null) category = keyword;
					else category = category + ", " + keyword;
				}
			}	
			String secondary = null;
			if(patent.getSecondary() != null) {
				for(String keyword : patent.getSecondary()) {
					if(secondary == null) secondary = keyword;
					else secondary = secondary + ", " + keyword;
				}
			}	
			String[] line = {file, title, category, score, secondary};
			writer.writeNext(line);
		}
		writer.close();
	}
}
