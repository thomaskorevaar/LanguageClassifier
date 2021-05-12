package LanguageClassifier.matrix;

import java.util.ArrayList;

public class Matrix {
	// All alphabetical characters and a whitespace
	public static final String characters = "abcdefghijklmnopqrstuvwxyz ";

	private ArrayList<Row> rows = new ArrayList<Row>();
	private ArrayList<Column> columns = new ArrayList<Column>();
	private ArrayList<Cell> cells = new ArrayList<Cell>();

	// Get all cells
	public ArrayList<Cell> getCells() {
		return this.cells;
	}

	// Print all cells of the matrix
	public static void print(Matrix mx) {
		for (Cell cell : mx.getCells()) {
			System.out.println(cell.toString() + "\t" + cell.getValue());
		}
	}

	// Create a new matrix for storing characters
	public static Matrix createCharacterMatrix(ArrayList<String> sentences) {
		// Create new matrix instance
		Matrix mx = new Matrix();

		// Loop over individual sentences
		for (String sentence : sentences) {
			// Split sentence into word
			for (String word : sentence.split(" ")) {
				// Holding variable for leading character
				char lead = 0;

				for (char tail : word.toCharArray()) {
					// Attempt to add characters to matrix
					mx.addCharacters(lead, tail);
					// Set lead character to current character
					lead = tail;
				}
			}
		}

		// Normalize the values of the cells
		mx.normalizeValues();

		// Return the new matrix
		return mx;
	}

	// Create a new matrix for storing characters
	public static Matrix createCharacterMatrix() {
		return new Matrix();
	}

	// Create a new matrix for storing characters
	public void addToMatrix(ArrayList<String> sentences) {
		// Loop over individual sentences
		for (String sentence : sentences) {
			// Split sentence into word
			for (String word : sentence.split(" ")) {
				// Holding variable for leading character
				char lead = 0;

				for (char tail : word.toCharArray()) {
					// Attempt to add characters to matrix
					addCharacters(lead, tail);
					// Set lead character to current character
					lead = tail;
				}
			}
		}

		// Normalize the values of the cells
		normalizeValues();
	}

	// Normalize the value of a cell by its occurrences divided by the total
	// occurrences
	private void normalizeValues() {
		for (Cell outerCell : cells) {
			int totalOccurrences = 0;

			// Loop once to get the total amount of occurrences
			for (Cell innerCell : cells) {
				if (outerCell.getRow().x.equals(innerCell.getRow().x)) {
					totalOccurrences += innerCell.getOccurrence();
				}
			}

			// Loop again to set the value
			for (Cell innerCell : cells) {
				if (outerCell.getRow().x.equals(innerCell.getRow().x)) {
					// Cast to double or else it doesnt work
					double value = ((double) outerCell.getOccurrence() / (double) totalOccurrences);
					outerCell.setValue(value);
				}
			}
		}
	}

	// Check if the character is not a space or in the alphabet
	private boolean isSpecialCharacter(char c) {
		for (char character : characters.toCharArray()) {
			if (c == character) {
				return false;
			}
		}

		return true;
	}

	// Null check that allows for ambiguous objects
	private boolean isNull(Object o) {
		if (o == null) {
			return true;
		}

		if (o instanceof Character) {
			if ((char) o == 0) {
				return true;
			}
		}

		return false;
	}

	// Add lead as a row in the matrix
	private Row addLead(Object o) {
		if (isNull(o)) {
			return null;
		}

		Row r = findRow(o);

		if (r == null) {
			r = new Row(o);
			rows.add(r);
		}

		return r;
	}

	// Add tail as a column in the matrix
	private Column addTail(Object o) {
		if (isNull(o)) {
			return null;
		}

		Column c = findColumn(o);

		if (c == null) {
			c = new Column(o);
			columns.add(c);
		}

		return c;
	}

	// Add a cell or return the existing one
	private Cell addCell(Row r, Column c) {
		Cell cell = findCell(r, c);

		if (cell == null) {
			cell = new Cell(r, c);
			cells.add(cell);
		}

		return cell;
	}

	// Find a cell, if it exists, increment its occurrence by one
	public Cell findCell(Row row, Column column) {
		for (Cell cell : cells) {
			if (row.x.equals(cell.getRow().x) && column.y.equals(cell.getColumn().y)) {
				cell.increment();
				return cell;
			}
		}

		return null;
	}

	// Get a cell
	public Cell getCell(char lead, char tail) {
		// Get row associated with lead character
		Row row = findRow(lead);
		// Get column associated with tail character
		Column column = findColumn(tail);

		// Cell doesnt exist if row or column dont
		if (row == null || column == null) {
			return null;
		}

		// Get the cell
		for (Cell cell : cells) {
			if (row.x.equals(cell.getRow().x) && column.y.equals(cell.getColumn().y)) {
				return cell;
			}
		}

		// Return null if all previous steps fail
		return null;
	}

	// Find a row
	private Row findRow(Object c) {
		for (Row row : rows) {
			if (row.x.equals(c)) {
				return row;
			}
		}

		return null;
	}

	// Find a column
	private Column findColumn(Object c) {
		for (Column column : columns) {
			if (column.y.equals(c)) {
				return column;
			}
		}

		return null;
	}

	// Find the likelyness of the character combination
	// Likelyness is equal to the normalized value of the cell
	public double findCharacterCombinationLikelyness(char lead, char tail) {
		Cell cell = getCell(lead, tail);

		if (cell == null) {
			return 0;
		}

		return cell.getValue();
	}

	// Add two characters to the matrix
	public void addCharacters(char leadingCharacter, char tailingcharacter) {
		leadingCharacter = Character.toLowerCase(leadingCharacter);
		tailingcharacter = Character.toLowerCase(tailingcharacter);

		// Change the character to an asterisk if it is not an alphabetic character or a
		// whitespace
		if (isSpecialCharacter(leadingCharacter)) {
			leadingCharacter = '*';
		}

		if (isSpecialCharacter(tailingcharacter)) {
			tailingcharacter = '*';
		}

		// Attempt to add a new row or fetch an existing one
		Row r = addLead(leadingCharacter);
		// Attempt to add a new column or fetch an existing one
		Column c = addTail(tailingcharacter);

		// If both the row and column exist, attempt to add a new cell; if it exists,
		// its occurrence is incremented
		if (r != null && c != null) {
			addCell(r, c);
		}
	}

	// Get the current words, character combinations and compare the current
	// matrix's result against another matrix
	private double testWord(String word, Matrix otherMatrix) {
		double result = 0;
		char lead = 0;

		for (char tail : word.toCharArray()) {
			// Guard to skip the first iteration
			if (lead == 0) {
				lead = tail;
				continue;
			}

			// Score from the current matrix
			double score = findCharacterCombinationLikelyness(lead, tail);
			// Score from the other matrix
			double otherScore = otherMatrix.findCharacterCombinationLikelyness(lead, tail);

			// Calculate result by using Maximum Entropy
			result += (score * otherScore);

			// Set the lead to the current character
			lead = tail;
		}

		return result;
	}

	// Test the sentence
	public double testSentence(String sentence, Matrix otherMatrix) {
		double result = 0;

		for (String word : sentence.split(" ")) {
			if (word.isEmpty()) {
				continue;
			}

			// Add result of the test to the total
			result += testWord(word, otherMatrix);
		}

		return result;
	}

	// Test a whole text, split into sentences
	public ArrayList<Double> testSentences(ArrayList<String> sentences, Matrix otherMatrix) {
		ArrayList<Double> result = new ArrayList<Double>();

		for (String sentence : sentences) {
			// Add the scoring result to the result list
			result.add(testSentence(sentence, otherMatrix));
		}

		return result;
	}
}
