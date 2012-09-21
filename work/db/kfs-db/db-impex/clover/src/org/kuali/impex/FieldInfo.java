package org.kuali.impex;

public class FieldInfo {
	protected String cloverFieldType;
	protected String columnName;
	protected int columnPosition;
	
	public FieldInfo(String columnName, String cloverFieldType, int columnPosition) {
		this.columnName = columnName;
		this.cloverFieldType = cloverFieldType;
	}

	public String getCloverFieldType() {
		return cloverFieldType;
	}
	public void setCloverFieldType(String cloverFieldType) {
		this.cloverFieldType = cloverFieldType;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getColumnPosition() {
		return columnPosition;
	}

	public void setColumnPosition(int columnPosition) {
		this.columnPosition = columnPosition;
	}
}
