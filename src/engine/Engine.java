package engine;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.opencsv.CSVWriter;

import driver.Driver;
import structures.*;
//12:00 - 1:00 am
//11:30 - 3 pm
//10:45 - 12:22 am
//8/1 10:00pm - 11pm
//8/5 10:00pm - 1:00 am
//8/6 10:15am - 1:30 pm
//8/7 1:00 am - 

public class Engine {
	private final int TITLE = 0;
	private final int ABSTRACT = 1;
	private final int CLAIM = 2;
	HashMap<ArrayList<String>, ArrayList<Occurrence>> masterIndex;
	HashMap<ArrayList<String>, ArrayList<Integer>> categories;
	
	public Engine() {
		masterIndex = new HashMap<ArrayList<String>, ArrayList<Occurrence>>(20,2.0f);
		categories = new HashMap<ArrayList<String>, ArrayList<Integer>>(20,2.0f);
	}
	/**
	 * Fills category index with occurrences, fills categories of patents, and exports into a csv file
	 * @param keywordsFile 
	 * @param patentFile
	 */
	public void makeIndex(String keywordsFile, String patentFile) throws IOException {
		ArrayList<Patent> patents = loadPatents(patentFile);
		Driver.addTextNew("loadPatents finished\n");
		fillCategories(keywordsFile, patents);
		Driver.addTextNew("fillCategories finished\n");
		for(Patent patent : patents) {
			mergeIndex(loadFromPatent(patent)); 
		}
		Driver.addTextNew("mergeIndex finished\n");
		patents = insertCategories(patents);
		Driver.addTextNew("insertCategories finished\n");
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yy.HH.mm.ss");
		String date = format.format(new Date(System.currentTimeMillis()));
		export("output." + date +".csv", patents);
		Driver.addTextNew("exported to output." + date +".csv\n");
	}
	/**
	 * Loads patents from patents csv file and creates patent objects
	 * @param fileName
	 * @return patents
	 */
	public ArrayList<Patent> loadPatents(String fileName) throws IOException {
		ArrayList<Patent> patents = new ArrayList<Patent>();
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new FileReader(fileName));
		for(CSVRecord record: records) {
			String file = record.get("Publication Number"); 
			String title = record.get("Title").toLowerCase();
			String abstractInfo = record.get("Abstract").toLowerCase();
			String claim = record.get("Claims").toLowerCase();
			patents.add(new Patent(file, title, abstractInfo, claim));
			if(Driver.chckbxPatentsDebug.isSelected()) Driver.addTextNew("Added " + file + ": " + title);
		}
		return patents;
	}
	/**
	 * Fills categories hash table with keywords and weights from csv file
	 * @param fileName
	 */
	public ArrayList<Patent> fillCategories(String fileName, ArrayList<Patent> patents) throws IOException {
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(new FileReader(fileName));
		ArrayList<String> category = new ArrayList<String>();
		ArrayList<Integer> weights = new ArrayList<Integer>();
		String data;
		for(CSVRecord record : records) {
			Iterator<String> iterator = record.iterator();
			while(iterator.hasNext()) {
				data = iterator.next().toLowerCase();
				if(data.length() > 0) {
					if(data.matches(".*\\d.*")) {
						weights.add(Integer.parseInt(data));
					} else {
						for(Patent patent : patents) {
							if(patent.getCategory() == null) {
								if(patent.getTitle().contains(data)) patent.setCategory(data);
								if(patent.getAbstract().contains(data)) patent.setCategory(data);
								if(patent.getClaim().contains(data)) patent.setCategory(data);
							}	
						}
						category.add(data);
					}
				}	
			}
			if(!weights.isEmpty()) {
				categories.put(category, weights);
				if(Driver.chckbxCategoriesDebug.isSelected()) Driver.addTextNew(category + ": " + weights);
				category = new ArrayList<String>();
				weights = new ArrayList<Integer>();
			}
		}
		return patents;
	}
	/**
	 * Builds map of occurrences for each patent using {@link engine.Engine#buildMap(Patent, int, StringTokenizer, HashMap)}
	 * @param patent
	 * @return HashMap of occurrences
	 */
	public HashMap<ArrayList<String>, Occurrence> loadFromPatent(Patent patent) throws IOException {
		HashMap<ArrayList<String>, Occurrence> map = new HashMap<ArrayList<String>, Occurrence>(5, 2.0f);
		
		map = buildMap(patent, TITLE, new StringTokenizer(patent.getTitle()), map);
		map = buildMap(patent, ABSTRACT, new StringTokenizer(patent.getAbstract()), map);
		map = buildMap(patent, CLAIM, new StringTokenizer(patent.getClaim()), map);
		if(Driver.chckbxIndexDebug.isSelected()) {
			Driver.addText(patent.getFile() + ": {");
			for(ArrayList<String> category : map.keySet()) {
				Driver.addText(category.get(0) + "=" + map.get(category).scoreToString() + ", ");
			}
			Driver.addTextNew("}");
		}
		return map;
	}
	/**
	 * Builds HashMap of occurrences for each patent's title, abstract, and claim
	 * @param patent
	 * @param idx TITLE, ABSTRACT, or CLAIM [0, 1, 2]
	 * @param tk StringTokenizer for patent description
	 * @param map 
	 * @return
	 */
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
								}
							}
						}
					}
				}
			}
		}
		return map;
	}
	/**
	 * Merges HashMap of occurrences for each patent into master index
	 * @param map
	 */
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
	/**
	 * Keeps occurrences sorted from greatest to least
	 * @param occs
	 * @return
	 */
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
	/**
	 * Filters str for letters only and splits hyphened words into several words
	 * @param str
	 * @return
	 */
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
	/**
	 * Sets primary, secondary category and score for each patent
	 * @param patents
	 * @return
	 */
	public ArrayList<Patent> insertCategories(ArrayList<Patent> patents) {
		for(ArrayList<String> key : masterIndex.keySet()) {
			ArrayList<Occurrence> occList = masterIndex.get(key);
			for(Occurrence o : occList) {
				for(Patent p : patents) {
					if(o.getFile().equals(p.getFile())) {
						p.addToList(key, o.getScore());
					}
				}
			}
		}
		if(Driver.chckbxApplyCategoriesDebug.isSelected()) {
			for(Patent patent : patents) {
				Driver.addTextNew(patent.toString());
			}
		}
		return patents;
	}
	/**
	 * Exports patent-category data into a csv file
	 * @param fileName
	 * @param patents
	 * @throws IOException
	 */
	public void export(String fileName, ArrayList<Patent> patents) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(fileName));
		String[] header = {"File", "Title", "Category", "List"};
		writer.writeNext(header);
		for(Patent patent : patents) {
			String file = patent.getFile(); String title = patent.getTitle();
			String category = patent.getCategory();
			ArrayList<ArrayList<String>> sorted = new ArrayList<ArrayList<String>>();
			for(ArrayList<String> key : patent.getList().keySet()) {
				if(!sorted.isEmpty()) {
					for(int i = 0; i < sorted.size(); i++) {
						if(patent.getList().get(key) >= patent.getList().get(sorted.get(i))) {
							sorted.add(i, key);
							break;
						} else if(i == sorted.size() - 1) {
							sorted.add(key);
							break;
						}
					}
				} else {
					sorted.add(key);
				}
			}
			StringBuilder builder = new StringBuilder();
			for(ArrayList<String> key : sorted) { 
				builder.append(key + ": " + patent.getList().get(key) + " ");
			}
				
			String[] line = {file, title, category, builder.toString()};
			writer.writeNext(line);
		}
		writer.close();
	}
}
