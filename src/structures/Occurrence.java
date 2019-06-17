package structures;

import java.util.ArrayList;

public class Occurrence {
	private String patent;
	private int score;
	public static final int TITLE = 5;
	public static final int ABSTRACT = 2;
	public static final int CLAIM = 1;
	
	public Occurrence(String patent, int score) {
		this.patent = patent;
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "(" + this.patent + ", " + this.score + ")";
	}
	
	public int getScore() {
		return this.score;
	}
	
	public String getFile() {
		return this.patent;
	}
	public void addScore(int score) {
		this.score = score;
	}
}
