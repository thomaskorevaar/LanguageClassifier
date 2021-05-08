package LanguageClassifier.WordCount;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
	private final static IntWritable one = new IntWritable(1);
	
	private static final Pattern WORD_BOUNDARY = Pattern.compile("\\s*\\b\\s*");
	private static final Pattern IS_LETTER_OR_SPACE_OR_PERIOD = Pattern.compile("([A-z\\.\\s])");
	
	private static final char SPACE = " ".toCharArray()[0];
	private static final char PERIOD = ".".toCharArray()[0];
	
	private String CreateStringFromChar(char... args)
	{
		//Create empty string object
		String result = "";
		
		//Concatenate string from characters
		for (char c : args)
		{
			//0 is Null in ASCII
			result += (c == 0 ? "" : c);
		}
		
		return result;
	}
	
	//Helper function to check if character is a letter, space or period
	private boolean isLetterSpaceOrPeriod(char c)
	{
		//Change char to string for use with regex
		return IS_LETTER_OR_SPACE_OR_PERIOD.matcher(CreateStringFromChar(c)).matches();
	}
	
	public void map(LongWritable offset, Text lineText, Context context) throws IOException, InterruptedException 
	{
		//Get current line as String 
		String line = lineText.toString();
		
		//Loop over individual words in string
		for (String word : WORD_BOUNDARY.split(line)) 
		{
			//Empty check
			if (word.isEmpty()) 
			{
				continue;
			}
			
			//Variable to hold previous character in word
			char previousCharacter = 0;
			
			//Loop over individual characters in word
			for (char currentCharacter : word.toCharArray())
			{
				//Guard against irrelevant characters
				if (!isLetterSpaceOrPeriod(currentCharacter))
				{
					continue;
				}
				
				//Guard against sentence ending
				if (previousCharacter == PERIOD)
				{
					if (currentCharacter == SPACE)
					{
						previousCharacter = 0;
						continue;
					}
				}
				
				//Create writable string from character
				String writable = CreateStringFromChar(previousCharacter, currentCharacter);
				
				//If writable is not empty and not a single character, write to output
				if (!writable.isEmpty() && writable.length() > 1)
				{
					context.write(new Text(writable), one);
				}
				
				//Set current character to previous for use in next iteration
				previousCharacter = currentCharacter;
			}
		}
	}
}
