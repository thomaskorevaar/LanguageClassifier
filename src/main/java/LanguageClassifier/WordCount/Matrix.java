package LanguageClassifier.WordCount;

import java.util.ArrayList;

public class Matrix {
	private ArrayList<WordPair> matrix = new ArrayList<WordPair>();

	public void addWords(String first, String last) 
	{
		first = first.toLowerCase();
		last = last.toLowerCase();
		
		for (WordPair wordPair : matrix)
		{
			if (wordPair.getFirst().equals(first))
			{
				if (wordPair.getLast().equals(last))
				{
					wordPair.increment();
				}
			}
		}
		
		this.matrix.add(new WordPair(first, last));
	}
	
	public boolean containsWordCombination(String first, String last)
	{
		first = first.toLowerCase();
		last = last.toLowerCase();
		
		for (WordPair wordPair : matrix)
		{
			if (wordPair.getFirst().equals(first) && wordPair.getLast().equals(last))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean containsWord(String word)
	{
		word = word.toLowerCase();
		
		for (WordPair wordPair : matrix)
		{
			if (wordPair.getFirst().equals(word) || wordPair.getLast().equals(word))
			{
				return true;
			}
		}
		
		return false;
	}
}
