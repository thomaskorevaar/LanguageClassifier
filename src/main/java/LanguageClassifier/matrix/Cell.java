package LanguageClassifier.matrix;

public class Cell {
	// Row it belongs to
	private Row row;
	// Column it belongs to
	private Column column;
	// Value
	private double value;
	// Occurrences in text
	private int occurrence;

	public Cell(Row row, Column column) {
		this.setRow(row);
		this.setColumn(column);
		this.occurrence = 1;
	}

	public Row getRow() {
		return row;
	}

	private void setRow(Row row) {
		this.row = row;
	}

	public Column getColumn() {
		return column;
	}

	private void setColumn(Column column) {
		this.column = column;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void increment() {
		this.occurrence++;
	}

	public void increment(int value) {
		this.occurrence += value;
	}

	@Override
	public String toString() {
		return "" + row.x + " + " + column.y;
	}

	public int getOccurrence() {
		return occurrence;
	}
}
