package LanguageClassifier.WordCount;

public class LanguageHandler {
	private String language;
	private Matrix matrix;
	
	public LanguageHandler(String language, Matrix matrix)
	{
		this.language = language;
		this.matrix = matrix;
	}

	public String getLanguage() {
		return language;
	}

	public Matrix getMatrix() {
		return matrix;
	}
}
