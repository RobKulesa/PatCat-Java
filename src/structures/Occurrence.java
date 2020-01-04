package structures;

import java.util.ArrayList;

import driver.Config;
/**
 * The occurrence object holds a keyword category and its respective score for a patent.
 * 
 * @author Robert Kulesa
 *
 */
public class Occurrence implements Comparable<Occurrence> {
	Config config = new Config();
	private ArrayList<String> category;
	private int[] score = {0, 0, 0, 0};
	public final int TITLE = Integer.parseInt(config.getProperty("titleWeight"));
	public final int ABSTRACT = Integer.parseInt(config.getProperty("abstractWeight"));
	public final int PREAMBLE = Integer.parseInt(config.getProperty("preambleWeight"));
	public final int CLAIM = Integer.parseInt(config.getProperty("claimWeight"));
	
	public Occurrence(ArrayList<String> category) {
		this.category = category;
	}
	
	public Occurrence(ArrayList<String> category, int[] score) {
		this.category = category;
		this.score = score;
	}
	
	public int getScore() {
		return this.score[0]*TITLE + this.score[1]*ABSTRACT + this.score[2]*PREAMBLE + this.score[3]*CLAIM;
	}
	
	public ArrayList<String> getCategory() {
		return this.category;
	}
	
	public void addScore(int idx, int score) {
		this.score[idx] = this.score[idx] + score;
	}
	
	@Override
	public String toString() {
		return this.category + ": " + this.getScore();
	}
	
	@Override
	public int compareTo(Occurrence o) {
		return this.getScore() - o.getScore();
	}
}
