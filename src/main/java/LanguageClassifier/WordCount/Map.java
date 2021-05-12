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
	private static final IntWritable one = new IntWritable(1);

	private static final Pattern SENTENCE_BOUNDARY = Pattern.compile("(.*)\\.?");

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
			WordCount.dutchMatrix.addToMatrix(getSentenceFromText(text));
		}

		// Add to english word matrix
		if (currentFile.equals("english.txt")) {
			WordCount.englishMatrix.addToMatrix(getSentenceFromText(text));
		}

		if (currentFile.equals("test.txt")) {
			ArrayList<String> sentences = getSentenceFromText(text);
			WordCount.testMatrix.addToMatrix(sentences);

			for (String sentence : sentences) {
				double dutchScore = WordCount.dutchMatrix.testSentence(sentence, WordCount.testMatrix);
				double englishScore = WordCount.englishMatrix.testSentence(sentence, WordCount.testMatrix);

				if (dutchScore > englishScore) {
					context.write(new Text("Dutch"), one);
				} else if (dutchScore < englishScore) {
					context.write(new Text("English"), one);
				}
			}
		}
	}
}
