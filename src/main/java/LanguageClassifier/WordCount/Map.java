package LanguageClassifier.WordCount;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import LanguageClassifier.matrix.Matrix;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
	private static final IntWritable one = new IntWritable(1);

	private static final Pattern SENTENCE_BOUNDARY = Pattern.compile("(.*)\\.?");
	private static final Pattern WORD_BOUNDARY = Pattern.compile("\\s*\\b\\s*");

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

	public void map(LongWritable offset, Text lineText, Context context) throws IOException, InterruptedException {
		String currentFile = ((FileSplit) context.getInputSplit()).getPath().getName();
		String text = lineText.toString();

		// Add to dutch word matrix
		if (currentFile.equals("dutch.txt")) {
			WordCount.dutchMatrix.addToMatrix(text);
		}

		// Add to english word matrix
		if (currentFile.equals("english.txt")) {
			WordCount.englishMatrix.addToMatrix(text);
		}

		if (currentFile.equals("test.txt")) {
			WordCount.testMatrix.addToMatrix(text);
			
			double dutchScore = WordCount.dutchMatrix.testSentence(text, WordCount.testMatrix);
			double englishScore = WordCount.englishMatrix.testSentence(text, WordCount.testMatrix);

			if (dutchScore > englishScore) {
				context.write(new Text("Dutch"), one);
			} else if (dutchScore < englishScore) {
				context.write(new Text("English"), one);
			}
		}
	}
}
