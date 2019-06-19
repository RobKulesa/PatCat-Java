package structures;

import java.util.ArrayList;

public class Occurrence implements Comparable{
	private String patent;
	private int[] score;
	//
	public static final int TITLE = 10;
	public static final int ABSTRACT = 5;
	public static final int CLAIM = 1;
	
	public Occurrence(String patent, int[] score) {
		this.patent = patent;
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "(" + this.patent + ", [" + this.getFrequency(0) + ", " + this.getFrequency(1) + ", " + this.getFrequency(2) + "])";
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

	@Override
	public int compareTo(Object o) {
		if(!(o instanceof Occurrence) || o == null) return -1;
		return this.getScore() - ((Occurrence) o).getScore();
	}
}
