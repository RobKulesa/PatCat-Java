package driver;

import java.io.*;
import java.util.*;

import engine.Engine;

public class Driver {
	public static void main(String[] args) {
		Config config = new Config();
		Engine engine = new Engine();
		String categories = config.getProperty("categoriesFile");
		String patents = config.getProperty("patentsFile");
		
		try { 
			engine.makeIndex(categories, patents);
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		 
		
		
		/*try {
			Scanner sc = new Scanner(new File(fileName));
			sc.useDelimiter(",");
			int counter = 0;
			while(sc.hasNext()) {
				System.out.println(sc.next().toLowerCase());
				System.out.println("\t\t\t\t" + ++counter);
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}*/
	}
}
