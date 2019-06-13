package engine;

import java.io.*;
import java.util.*;
import structures.Occurrence;

public class Engine {
	HashMap<ArrayList<String>, ArrayList<Occurrence>> masterIndex;
	HashMap<ArrayList<String>, ArrayList<Integer>> categories;
	
	public Engine() {
		masterIndex = new HashMap<ArrayList<String>, ArrayList<Occurrence>>(1000,2.0f);
		categories = new HashMap<ArrayList<String>, ArrayList<Integer>>(20,2.0f);
	}
	
	
	public void fillCategories(String fileName) throws FileNotFoundException {
		BufferedReader reader = null;
		try {
			String data;
			reader = new BufferedReader(new FileReader(fileName));
			ArrayList<String> keywords = new ArrayList<String>();
			ArrayList<Integer> weights = new ArrayList<Integer>();
			while((data = reader.readLine()) != null) {
				StringTokenizer tk = new StringTokenizer(data.toLowerCase(), ",");
				while(tk.hasMoreTokens()) {
					data = tk.nextToken();
					//System.out.println(data);
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
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public String categoryCleaner(String word) {
		if(word == null || word.equals("") || word.equals(" ")) return null;
		for(int i = 0; i < word.length(); i++) {
			char curr = word.charAt(i);
			if(curr != 'a' && curr != 'b' && curr != 'c' && curr != 'd' && curr != 'e' && curr != 'f' && curr != 'g' && curr != 'h' && curr != 'i' && curr != 'j' && curr != 'k' && curr != 'l' && curr != 'm' && curr != 'n' && curr != 'o' && curr != 'p' && curr != 'q' && curr != 'r' && curr != 's' && curr != 't' && curr != 'u' && curr != 'v' && curr != 'w' && curr != 'x' && curr != 'y' && curr != 'z') {
				try { word = word.substring(0, i) + word.substring(i + 1); }
				catch(StringIndexOutOfBoundsException e) {
					word = word.substring(0, i);
				}
			}
		}
		//System.out.println(word);
		return word;
	}
}
