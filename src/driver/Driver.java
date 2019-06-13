package driver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import engine.Engine;

public class Driver {
	public static void main(String[] args) {
		Engine engine = new Engine();
		String fileName = "keyword.W+Qa.csv";
		try {
			engine.fillCategories(fileName);
		} catch (FileNotFoundException e) {
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