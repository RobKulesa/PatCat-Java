package structures;

import driver.Config;

public class Occurrence implements Comparable<Occurrence> {
	Config config = new Config();
	private String patent;
	private int[] score;
	public final int TITLE = Integer.parseInt(config.getProperty("titleWeight"));
	public final int ABSTRACT = Integer.parseInt(config.getProperty("abstractWeight"));
	public final int CLAIM = Integer.parseInt(config.getProperty("claimWeight"));
	
	public Occurrence(String patent, int[] score) {
		this.patent = patent;
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "(" + this.patent + ", [" + this.getFrequency(0) + ", " + this.getFrequency(1) +  "," + this.getFrequency(2) + "])";
	}
	
	public int getScore() {
		return this.score[0]*TITLE + this.score[1]*ABSTRACT + this.score[2]*CLAIM;
	}
	
	public int getFrequency(int idx) {
		if(idx > 2 || idx < 0) return -1;
		return this.score[idx];
	}
	
	public String getFile() {
		return this.patent;
	}
	
	public void addScore(int idx, int score) {
		this.score[idx] = this.score[idx] + score;
	}

	public String scoreToString() {
		return "[" + this.getFrequency(0) + ", " + this.getFrequency(1) +  "," + this.getFrequency(2) + "]";
	}
	
	@Override
	public int compareTo(Occurrence o) {
		return this.getScore() - ((Occurrence) o).getScore();
	}
}
