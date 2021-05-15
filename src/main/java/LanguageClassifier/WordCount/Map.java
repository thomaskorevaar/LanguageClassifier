package LanguageClassifier.WordCount;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
	// The '1' digit
	private static final IntWritable ONE = new IntWritable(1);

	// Matching pattern for sentences
	private static final Pattern SENTENCE_BOUNDARY = Pattern.compile("(.*)\\.?");

	// Constants for output
	private static final String DUTCH = "Dutch";
	private static final String ENGLISH = "English";
	private static final String SENTENCE_RESULT = "Current sentence: %s\nLanguage: %s";

	// Helper function to get sentences from slab of text
	public ArrayList<String> getSentenceFromText(String text) {
		ArrayList<String> result = new ArrayList<String>();
		Matcher m = SENTENCE_BOUNDARY.matcher(text);

		while (m.find()) {
			String matchResult = m.group().toString();

			if (matchResult.isEmpty()) {
				continue;
			}

			result.add(matchResult);
		}

		return result;
	}

	// Mapper function that takes text files from input
	public void map(LongWritable offset, Text lineText, Context context) throws IOException, InterruptedException {
		// Get current file name
		String currentFile = ((FileSplit) context.getInputSplit()).getPath().getName();
		// Current text
		String text = lineText.toString();

		// Get all sentences from the text as an array and add it to the matrix
		// Matrixes are stored in the "main" for use throughout the application

		// Add to dutch word matrix
		if (currentFile.equals("dutch.txt")) {
			WordCount.dutchMatrix.addToMatrix(getSentenceFromText(text));
		}

		// Add to english word matrix
		if (currentFile.equals("english.txt")) {
			WordCount.englishMatrix.addToMatrix(getSentenceFromText(text));
		}

		// The test matrix is what we compare against using our trained matrixes
		if (currentFile.equals("test.txt")) {
			ArrayList<String> sentences = getSentenceFromText(text);
			WordCount.testMatrix.addToMatrix(sentences);

			for (String sentence : sentences) {
				double dutchScore = WordCount.dutchMatrix.testSentence(sentence, WordCount.testMatrix);
				double englishScore = WordCount.englishMatrix.testSentence(sentence, WordCount.testMatrix);

				if (dutchScore > englishScore) {
					context.write(new Text(String.format(SENTENCE_RESULT, sentence, DUTCH)), ONE);
					context.write(new Text(DUTCH), ONE);
				} else if (dutchScore < englishScore) {
					context.write(new Text(String.format(SENTENCE_RESULT, sentence, ENGLISH)), ONE);
					context.write(new Text(ENGLISH), ONE);
				}
			}
		}
	}
}
