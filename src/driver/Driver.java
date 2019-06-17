package driver;

import java.io.*;
import java.util.*;

import engine.Engine;

public class Driver {
	public static void main(String[] args) {
		Engine engine = new Engine();
		String categories = "keyword.W+Qa.csv";
		String files = "W+Q_TAC.csv";
		
		try { 
			engine.fillCategories(categories); 
			engine.loadFromDocument(files); 
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
