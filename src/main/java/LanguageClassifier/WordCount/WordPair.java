package LanguageClassifier.WordCount;

public class WordPair {
	private String first;
	private String last;
	private int occurrence;
	
	public WordPair(String first, String last)
	{
		this.first = first;
		this.last = last;
		
		this.occurrence = 1;
	}
	
	public void increment()
	{
		this.occurrence++;
	}

	public String getFirst() {
		return first;
	}

	public String getLast() {
		return last;
	}
	
	public int getOccurrence()
	{
		return this.occurrence;
	}
}
