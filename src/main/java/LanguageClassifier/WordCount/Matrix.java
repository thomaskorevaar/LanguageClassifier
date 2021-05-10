package LanguageClassifier.WordCount;

import java.util.ArrayList;

public class Matrix {
	private ArrayList<Pair<Character, ArrayList<Character>>> characterMatrix = new ArrayList<Pair<Character, ArrayList<Character>>>();

	public ArrayList<String> getAllCombinations(char key)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		getMatrix(key).forEach((item) -> {
			result.add(key + "" + item);
		});
		
		return result;
	}
	
	public void addToMatrix(char key, char value)
	{
		ArrayList<Character> result = getMatrix(key);
		
		if (result == null)
		{
			Pair<Character, ArrayList<Character>> newPair = new Pair<Character, ArrayList<Character>>(key, new ArrayList<Character>(value));
			characterMatrix.add(newPair);
		}
		else
		{
			result.add(value);
		}
	}
	
	private ArrayList<Character> getMatrix(char key)
	{
		for (Pair<Character, ArrayList<Character>> item: characterMatrix)
		{
			if (item.key == key)
			{
				return item.value;
			}
		}
		
		return null;
	}
}
