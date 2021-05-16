package LanguageClassifier.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import LanguageClassifier.WordCount.WordCount;

public class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
	// The '1' digit
	private static final IntWritable ONE = new IntWritable(1);

	// Constants for output
	private static final String DUTCH = "Dutch";
	private static final String ENGLISH = "English";
	private static final String UNKNOWN = "Unknown";
	private static final String SENTENCE_RESULT = "Current sentence: %s\nLanguage: %s";

	// Matching pattern for sentences
	private static final Pattern SENTENCE_BOUNDARY = Pattern.compile("(.*)\\.?");

	// Helper function to get sentences from slab of text
	protected ArrayList<String> getSentenceFromText(String text) {
		ArrayList<String> result = new ArrayList<String>();
		Matcher m = SENTENCE_BOUNDARY.matcher(text);

		while (m.find()) {
			// Get match result as string
			String matchResult = m.group().toString();

			// Skip empty lines
			if (matchResult.isEmpty()) {
				continue;
			}

			// Add to result
			result.add(matchResult);
		}

		return result;
	}

	// Read the testfile sentence by sentence
	// Attempt to recognise the language using the trained matrixes
	// Classify every sentence as dutch, english or unknown
	private void handleClassification(Context context, ArrayList<String> sentences)
			throws IOException, InterruptedException {
		for (String sentence : sentences) {
			double dutchScore = WordCount.dutchMatrix.testSentence(sentence, WordCount.testMatrix);
			double englishScore = WordCount.englishMatrix.testSentence(sentence, WordCount.testMatrix);

			// Compare the scores
			if (dutchScore > englishScore) {
				// Dutch score is higher
				context.write(new Text(String.format(SENTENCE_RESULT, sentence, DUTCH)), ONE);
				context.write(new Text(DUTCH), ONE);
			} else if (englishScore > dutchScore) {
				// English score is higher
				context.write(new Text(String.format(SENTENCE_RESULT, sentence, ENGLISH)), ONE);
				context.write(new Text(ENGLISH), ONE);
			} else {
				// If the score is equal, write unknown
				context.write(new Text(String.format(SENTENCE_RESULT, sentence, UNKNOWN)), ONE);
				context.write(new Text(UNKNOWN), ONE);
			}
		}
	}

	@Override
	public void map(LongWritable offset, Text lineText, Context context) throws IOException, InterruptedException {
		// Get current file name
		String currentFile = ((FileSplit) context.getInputSplit()).getPath().getName();
		// All text from the file
		String text = lineText.toString();
		// All sentences in the file
		ArrayList<String> sentences = getSentenceFromText(text);

		// If the current file is our dutch training file
		// Add to dutch word matrix
		if (currentFile.equals("dutch.txt")) {
			WordCount.dutchMatrix.addToMatrix(sentences);
		}
		// If the current file is our english training file
		// Add to english word matrix
		else if (currentFile.equals("english.txt")) {
			WordCount.englishMatrix.addToMatrix(sentences);
		}
		// If the current file is neither of the training files
		else {
			// Build a matrix for the testfile
			// The test matrix is what we compare against using our trained matrixes
			WordCount.testMatrix.addToMatrix(sentences);
		}
	}

	// cleanup is called after map
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		// Handle the classification
		handleClassification(context, WordCount.testMatrix.sentences);
	}
}
