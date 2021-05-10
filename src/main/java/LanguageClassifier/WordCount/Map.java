package LanguageClassifier.WordCount;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
	private static final IntWritable one = new IntWritable(1);
	
	private static final Pattern WORD_BOUNDARY = Pattern.compile("\\s*\\b\\s*");

	private int getWordLanguage(String word)
	{
		int verdict = 0;
		
		boolean currentWordIsDutch = WordCount.dutchLanguageHandler.getMatrix().containsWord(word);
		boolean currentWordIsEnglish = WordCount.englishLanguageHandler.getMatrix().containsWord(word);
		
		if (currentWordIsDutch && currentWordIsEnglish)
		{
			verdict = 0;
		}
		else
		{
			if (currentWordIsDutch)
			{
				verdict = 1;
			}
			
			if (currentWordIsEnglish)
			{
				verdict = -1;
			}
		}
		
		return verdict;
	}
	
	private String verdictToString(int verdict)
	{
		if (verdict < 0)
		{
			return "english";
		}
		
		if (verdict > 0)
		{
			return "dutch";
		}
		
		return "undetermined";
	}
	
	public void map(LongWritable offset, Text lineText, Context context) throws IOException, InterruptedException 
	{
		String currentFile = ((FileSplit) context.getInputSplit()).getPath().getName();
		
		//Get current line as String 
		String line = lineText.toString();
		
		//Variable for previous word in sentence
		String previousWord = "";
		
		//Variable for current sentence score
		int languageScore = 0;
		
		//Loop over individual words in string
		for (String currentWord : WORD_BOUNDARY.split(line)) 
		{
			//Skip first word in sentence
			if (!previousWord.equals(""))
			{
				//Empty check
				if (!currentWord.isEmpty()) 
				{
					//Add to dutch word matrix
					if (currentFile.equals("dutch.txt"))
					{
						WordCount.dutchLanguageHandler.getMatrix().addWords(previousWord, currentWord);
					}
					
					//Add to english word matrix
					if (currentFile.equals("english.txt"))
					{
						WordCount.englishLanguageHandler.getMatrix().addWords(previousWord, currentWord);
					}
					
					if (currentFile.equals("test.txt"))
					{
						String verdict = "";
						
						int currentWordVerdict = getWordLanguage(currentWord);
						int previousWordVerdict = getWordLanguage(previousWord);
						int total = currentWordVerdict + previousWordVerdict;
						
						verdict = verdictToString(total);
						languageScore += total;
					}
				}
			}
			
			previousWord = currentWord;
		}
		
		context.write(new Text(verdictToString(languageScore)), one);
	}
}
