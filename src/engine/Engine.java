package engine;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.opencsv.CSVWriter;

import driver.Driver;
import structures.*;

public class Engine {
	private final int TITLE = 0;
	private final int ABSTRACT = 1;
	private final int PREAMBLE = 2;
	private final int CLAIM = 3;
	HashMap<ArrayList<String>, ArrayList<Occurrence>> masterIndex;
	public HashMap<ArrayList<String>, ArrayList<Integer>> categories;
	
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
		Driver.addTextNew("loadPatents started");
		ArrayList<Patent> patents = loadPatents(patentFile);
		Driver.addTextNew("loadPatents finished");
		Driver.addTextNew("fillCategories started");
		fillCategories(keywordsFile, patents);
		Driver.addTextNew("fillCategories finished");
		Driver.addTextNew("mergeIndex started");
		for(Patent patent : patents) {
			mergeIndex(loadFromPatent(patent)); 
		}
		Driver.addTextNew("mergeIndex finished");
		Driver.addTextNew("insertCategories started");
		patents = insertCategories(patents);
		Driver.addTextNew("insertCategories finished");
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yy..HH.mm.ss");
		String date = format.format(new Date(System.currentTimeMillis()));
		export("output." + date +".csv", patents);
		Driver.addTextNew("exported to output." + date +".csv");
	}
	/**
	 * Loads patents from patents csv file and creates patent objects
	 * @param fileName
	 * @return patents
	 */
	public ArrayList<Patent> loadPatents(String fileName) throws IOException {
		ArrayList<Patent> patents = new ArrayList<Patent>();
		CSVFormat format = CSVFormat.DEFAULT.withCommentMarker('#');
		Iterable<CSVRecord> records = format.withFirstRecordAsHeader().parse(new FileReader(fileName));
		for(CSVRecord record: records) {
			if(!record.hasComment()) {
				String file = record.get(0); 
				String title = record.get(1).toLowerCase();
				String abstractInfo = record.get(2).toLowerCase();
				String preamble = null;
				String claim = record.get(3).toLowerCase();
				try {
					String[] totalClaim = record.get(3).toLowerCase().split(":", 2);
					preamble = totalClaim[0];
					claim = totalClaim[1];
				} catch(ArrayIndexOutOfBoundsException e) {
					preamble = "";
					claim = record.get(3).toLowerCase();
				}
				patents.add(new Patent(file, title, abstractInfo, preamble, claim));
				if(Driver.chckbxPatentsDebug.isSelected()) Driver.addTextNew("Added " + file + ": " + title + "  |  " + abstractInfo + "  ||  " + preamble + "  |||  " + claim);
			}
		}
		return patents;
	}
	/**
	 * Fills categories hash table with keywords and weights from csv file
	 * @param fileName
	 */
	public ArrayList<Patent> fillCategories(String fileName, ArrayList<Patent> patents) throws IOException {
		CSVFormat format = CSVFormat.DEFAULT.withCommentMarker('#');
		Iterable<CSVRecord> records = format.parse(new FileReader(fileName));
		ArrayList<String> category = new ArrayList<String>();
		ArrayList<Integer> weights = new ArrayList<Integer>();
		String data;
		for(CSVRecord record : records) {
			if(!record.hasComment()) {
				Iterator<String> iterator = record.iterator();
				while(iterator.hasNext()) {
					data = iterator.next().toLowerCase();
					if(data.length() > 0) {
						if(data.matches(".*\\d.*")) {
							weights.add(Integer.parseInt(data));
						} else {
							category.add(data);
						}
					} 
				}
				if(!weights.isEmpty() && category.size() == weights.size()) {
					String keyword;
					for(Patent patent : patents) {
						if(patent.getCategory() == null) {
							for(int i = 0; i < category.size(); i++) {
								if(weights.get(i) == 0) continue;
								keyword = category.get(i);
								for(int j = 0; j < 4; j++) {
									if(patent.getInfo(j).contains(keyword)) {
										patent.setCategory(category.get(0));
										break;
									}
								}
							}
						}
					}
					categories.put(category, weights);
					if(Driver.chckbxCategoriesDebug.isSelected()) Driver.addTextNew(category + ": " + weights);
					category = new ArrayList<String>();
					weights = new ArrayList<Integer>();
				}
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
		for(ArrayList<String> category : categories.keySet()) {
			for(int keyWordIndex = 0; keyWordIndex < category.size(); keyWordIndex++) {
				String keyWord = category.get(keyWordIndex);
				boolean firstRun = true;
				for(int idx = 0; idx < 4; idx++) {
					int indexOfKey = patent.getInfo(idx).indexOf(keyWord);
					while(indexOfKey >= 0) {
						patent.setInfo(idx, patent.getInfo(idx).replaceFirst(keyWord, ""));
						if(categories.get(category).get(keyWordIndex) > 0) {
							if(firstRun) {
								int[] arr = {0, 0, 0, 0};
								if(idx == TITLE) arr[TITLE] = 1;
								else if(idx == ABSTRACT) arr[ABSTRACT] = 1;
								else if(idx == PREAMBLE) arr[PREAMBLE] = 1;
								else arr[CLAIM] = 1;
								map.put(category, new Occurrence(patent.getFile(), arr));
								firstRun = false;
							} else {
								map.get(category).addScore(idx, categories.get(category).get(keyWordIndex));
							}
						}
						indexOfKey = patent.getInfo(idx).indexOf(keyWord);
					}
				}
			}
			patent.resetInfo();
		}
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
