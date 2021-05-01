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

	public void map(LongWritable offset, Text lineText, Context context) throws IOException, InterruptedException {
		String line = lineText.toString();
		Text currentWord = new Text();
		for (String word : WORD_BOUNDARY.split(line)) {
			if (word.isEmpty()) {
				continue;
			}
			currentWord = new Text(word);
			context.write(currentWord, one);
		}
	}
}
