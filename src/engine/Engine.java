package engine;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.opencsv.CSVWriter;

import driver.Driver;
import structures.Occurrence;
import structures.Patent;

/**
 * Contains all the methods executed by the driver in categorizing and scoring patents.
 * 
 * @author Robert Kulesa
 *
 */
public class Engine {
	public HashMap<Patent, ArrayList<Occurrence>> masterIndex;
	public HashMap<ArrayList<String>, ArrayList<Integer>> categories;
	
	public Engine() {
		masterIndex = new HashMap<Patent, ArrayList<Occurrence>>(20,2.0f);
		categories = new HashMap<ArrayList<String>, ArrayList<Integer>>(20,2.0f);
	}
	
	public void makeIndex(String keywordsFile, String patentFile) throws IOException {
		Driver.addTextNew("**populatePatents started**");
		ArrayList<Patent> patents = populatePatents(patentFile);
		Driver.addTextNew("**populatePatents finished**\n\n");
		Driver.addTextNew("**populateCategories started**");
		populateCategories(keywordsFile, patents);
		Driver.addTextNew("**populateCategories finished**\n\n");
		Driver.addTextNew("**findOccurrences started**"); 
		for(Patent patent : patents) {
			findOccurrences(patent);
		}
		if(Driver.chckbxIndexDebug.isSelected()) {
			for(Patent patent : masterIndex.keySet()) {
				Driver.addTextNew(patent.getFile() + ": " + masterIndex.get(patent).toString() + "\n\n");
			}
		}
		Driver.addTextNew("**findOccurrences finished**\n\n");
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yy..HH.mm.ss");
		String date = format.format(new Date(System.currentTimeMillis()));
		export("output." + date + ".csv", patents);
		Driver.addTextNew("exported to output." + date + ".csv");
		 
	}
	
	/**
	 * Parses patents file and organizes into patent objects with title, abstract, preamble, and claim
	 * 
	 * @param fileName
	 * @return list of patent objects to be categorized
	 * @throws IOException
	 */
	public ArrayList<Patent> populatePatents(String fileName) throws IOException {
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
	 * Parses categories file and creates index of categories with category as key and their respective weights as the value.
	 * Also assigns priority-based category to patents (which category appears in patent first)
	 * 
	 * @param fileName
	 * @param patents
	 * @throws IOException
	 */
	public void populateCategories(String fileName, ArrayList<Patent> patents) throws IOException {
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(new FileReader(fileName));
		ArrayList<String> category = new ArrayList<String>();
		ArrayList<Integer> weights = new ArrayList<Integer>();
		while(records.iterator().hasNext()) {
			CSVRecord record = records.iterator().next();
			boolean hasComment = false;
			for(String data : record) if(data.contains("#")) hasComment = true;
			if(hasComment) continue;
			if(category.isEmpty()) { //fill keywords
				for(String keyword : record) {
					if(keyword.length() > 0) category.add(keyword.toLowerCase());
				}
			} else {
				for(String weight : record) { //fill weights
					if(weight.length() > 0) weights.add(Integer.parseInt(weight));
				}
			}
			if(!category.isEmpty() && !weights.isEmpty()) {
				if(category.size() != weights.size()) throw new IOException("Category and weight size do not match in keyword file");
				categories.put(category, weights);
				if(Driver.chckbxCategoriesDebug.isSelected()) Driver.addTextNew(category + ": " + weights);
				if(!Driver.chckbxScorebasedCategorizing.isSelected()) {
					for(int i = 0; i < category.size(); i++) {
						if(weights.get(i) == 0) continue;
						for(Patent patent : patents) {
							if(patent.contains(category.get(i)) != -1 && patent.getCategory() == null) {
								patent.setCategory(category.get(0));
								if(Driver.chckbxCategoriesDebug.isSelected()) Driver.addTextNew(patent.getFile() + ": " + patent.getCategory());
							}
						}
					}
				}
				category = new ArrayList<String>();
				weights = new ArrayList<Integer>();
			}
		}
	}
	/**
	 * Find occurrences of keywords in passed patent and insert into sorted list. Then add patent and its list to the master index.
	 * 
	 * @param patent
	 */
	public void findOccurrences(Patent patent) {
		ArrayList<Occurrence> occs = new ArrayList<Occurrence>();
		for(ArrayList<String> category : categories.keySet()) {
			Occurrence occ = new Occurrence(category);
			for(int keywordIndex = 0; keywordIndex < category.size(); keywordIndex++) {
				if(categories.get(category).get(keywordIndex) > 0) {
					String keyword = category.get(keywordIndex);
					for(int idx = 0; idx < 4; idx++) {
						String info = patent.getInfo(idx);
						int count = 0;
						int indexOfKey = info.indexOf(keyword);
						while(indexOfKey != -1) {
							count++;
							info = info.substring(indexOfKey + 1);
							indexOfKey = info.indexOf(keyword);
						}
						int score = count*categories.get(category).get(keywordIndex);
						occ.addScore(idx, score);
					}
				}
			}
			if(occ.getScore() > 0) {
				if(occs.isEmpty()) {
					occs.add(occ);
				} else {
					for(int i = 0; i < occs.size(); i++) {
						if(occ.compareTo(occs.get(i)) > 0) {
							occs.add(i, occ);
							break;
						} else if(i == occs.size() - 1) {
							occs.add(occ);
							break;
						}
					}
				}
			}	
		}
		masterIndex.put(patent, occs);
	}
	
	/**
	 * Exports the categorized and scored patents into a csv file.
	 * 
	 * @param fileName
	 * @param patents
	 * @throws IOException
	 */
	public void export(String fileName, ArrayList<Patent> patents) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(fileName));
		String[] header = {"File", "Title", "Category", "List"};
		writer.writeNext(header);
		for(Patent patent : patents) {
			if(Driver.chckbxScorebasedCategorizing.isSelected() && !masterIndex.get(patent).isEmpty()) {
				patent.setCategory(masterIndex.get(patent).get(0).getCategory().get(0));
			}
			
			ArrayList<String> line = new ArrayList<String>();
			line.add(patent.getFile()); line.add(patent.getTitle()); line.add(patent.getCategory());
			for(Occurrence o : masterIndex.get(patent)) {
				line.add(o.getCategory().get(0) + ": " + o.getScore());
			}
			String[] nextLine = new String[0];
			writer.writeNext(line.toArray(nextLine));
		}
		writer.close();
	}
}
