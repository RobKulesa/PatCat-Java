package structures;

import java.util.ArrayList;

public class Occurrence {
	private String patentFile;
	private int score;
	public final int TITLE = 5;
	public final int ABSTRACT = 2;
	public final int CLAIM = 1;
	
	public Occurrence(String patentFile, int score) {
		this.patentFile = patentFile;
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "(" + this.patentFile + ", " + this.score + ")";
	}
	
	public int getScore() {
		return this.score;
	}
	
	public String getFile() {
		return this.patentFile;
	}
}
