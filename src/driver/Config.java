package driver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
/**
 *
 * @author Robert Kulesa
 *
 */
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
	/**
	 * 
	 * @param key
	 * @return configured property
	 */
	public String getProperty(String key) {
		return this.config.getProperty(key);
	}
	
	public void setProperty(String key, String value) {
		this.config.setProperty(key, value);
		try {
			this.config.store(new FileOutputStream("config.cfg"), null);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
