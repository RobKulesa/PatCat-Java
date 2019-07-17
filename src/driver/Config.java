package driver;

import java.io.FileInputStream;
import java.util.*;

public class Config {
	Properties config;
	public Config() {
		config = new Properties();
		try {
			config.load(new FileInputStream("config.cfg"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public String getProperty(String key) {
		return this.config.getProperty(key);
	}
}
